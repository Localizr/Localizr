package models.dbpedia;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;

import models.eventmedia.EventMedia;
import models.geo.Location;
import play.Play;
import play.cache.Cache;
import play.libs.F;
import play.libs.F.Promise;
import play.mvc.Controller;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;


public class DBPedia extends Controller {
	
	public static F.Promise<DBPediaInfoObject> getCachedGeneralInfo (Location loc) {
		// Build Caching-Key
    	String sKey = "dbpedia-"+loc.getLat()+loc.getLng();
    	@SuppressWarnings("unchecked")
    	// Is the result already in the Cache?
		F.Promise<DBPediaInfoObject> infoDBPedia = (Promise<DBPediaInfoObject>) Cache.get(sKey);
    	// No? -> Retrieve it
		if(infoDBPedia == null)
			infoDBPedia = search4GeneralInfo(loc);
		// Put it into the cache
		Cache.set(sKey, infoDBPedia);
		return infoDBPedia;
	}
	
	public static F.Promise<DBPediaInfoObject> search4GeneralInfo (Location loc) {
		return F.Promise.promise(() -> DBPedia.poseInfoQuery(loc));
	}

	/**
	 * Queries DBPedia dataset for info based on the name of the city of the current location
	 * @param location
	 * @return
	 */
	public static DBPediaInfoObject poseInfoQuery(Location location) { 
		String sQuery = "";
		try {
	        java.net.URL url = Play.class.getResource("/dbpedia_sparql.txt");
	        java.nio.file.Path resPath = java.nio.file.Paths.get(url.toURI());
	        sQuery= new String(java.nio.file.Files.readAllBytes(resPath), "UTF8");

            sQuery = sQuery.replace("SIMPLE_NAME", location.getSimpleName());

            Query query = QueryFactory.create(sQuery);
            QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
            ResultSet results = qexec.execSelect();
            // Put result into a DBPediaInfoObject
            DBPediaInfoObject info = parseResult(results);
            qexec.close();

            return info;
		} catch (Exception e) {
			e.printStackTrace();
		}

        return null;
	}	

	/**
	 * Receives the resultset from the SPARQL query and adds the relevant result values to the DBPediaInfoObject
	 * @param result
	 * @return
	 */
	private static DBPediaInfoObject parseResult (ResultSet result){
		
		DBPediaInfoObject info = new DBPediaInfoObject();
		
		if (!result.hasNext()){
			info= null;
		}
		else{
			while (result.hasNext() ){ 
				
				QuerySolution soln = result.nextSolution() ;
				
				HashMap<String, String> properties = new HashMap<String, String>();
				HashMap<String,String> wikiLinks = new HashMap<String,String>();
				
				// Iterates through the variable names 
	            for ( final Iterator<String> varNames = soln.varNames(); varNames.hasNext(); ) {
	                
	            	final String varName = varNames.next();
	            	
                	if (varName.matches("Country") || varName.matches("Leader1") ||  varName.matches("Leader2") ){
	                	if (soln.getLiteral(varName).getValue().toString()!=""){
	                		String values= soln.getLiteral(varName).getValue().toString();
	                		String[] separateValues = values.split(",,");
	                		
	                		// For every separate values of the types defined get the equivalent wikilinks and store them into the created object
	                		for (int i=0; i<separateValues.length ; i++){
	                			String label = getWikiLink(separateValues[i])[0];
	                			String wikiLink = getWikiLink(separateValues[i])[1];
	                			if (!wikiLinks.containsKey(label))
	                				wikiLinks.put(label, wikiLink);
	                		}
	                	}
	                }	                
                	else if (soln.get(varName).isLiteral() && soln.getLiteral(varName).getValue().toString()!="")
                		properties.put(varName, soln.getLiteral(varName).getValue().toString());
                	else if (soln.get(varName).isResource() && soln.getResource(varName).toString()!="")
                		properties.put(varName, soln.getResource(varName).toString());
                
	            }
	            
	            // Store the literal values and the map of wikilinks that connects resources to their wikis
				info.setLiteralProperties(properties);
				info.setWikiLinks(wikiLinks);
				
			}
		}	
		return info;

	}
	
	/**
	 * Based on the resource URI queries DBPedia for the relevant wikilink
	 * @param resource
	 * @return 
	 */
	private static String[] getWikiLink(String resource){
		
		String sparqlquery= "PREFIX foaf:<http://xmlns.com/foaf/0.1/> \n"
				+ "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#> \n"
				+ "select distinct ?wiki ?label \n"
				+ "where \n"
				+ "{?wiki foaf:primaryTopic <"+resource+">. \n"
				+ "{<"+resource+"> rdfs:label ?label }\n"
				+ "FILTER(langMatches(lang(?label), 'EN'))\n"			
				+ "} ";

		Query query = QueryFactory.create(sparqlquery);
	    QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
	    ResultSet results = qexec.execSelect();
	    String wikilink = null;
	    String label = null;
	    
	    // Store the mapped resource and wikilink values 
	    String[] pairValues = new String[2];
	    while (results.hasNext() ){ 			
			QuerySolution soln = results.nextSolution() ;		
			wikilink= soln.getResource("?wiki").toString();
			label=soln.getLiteral("?label").getValue().toString();
	    }
	    qexec.close();
	    pairValues[0]=label;
	    pairValues[1]=wikilink;
	    
	    return pairValues;
		
	}
}
