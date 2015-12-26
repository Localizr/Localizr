package models.geonames;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import models.geonames.struct.POI;
import models.geonames.struct.POIList;
import play.libs.F;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Resource;

public class GeoNamesRecommendation {

    public static F.Promise<POIList> async(String resource) {
    	final POIList pois=GeoNamesRecommendation.retrieveRecommendedData(resource);
    	
		if (pois==null) return null;
		else 			return F.Promise.promise(() -> pois);
    }
    
    /**
     * Receives a POI and returns all recommended POIs based on the supertype of the input POI
     * @param resource
     * @return
     */
    public static POIList retrieveRecommendedData(String resource){
    	
    	List<Resource> abstractClasses = new ArrayList<Resource>();
    	
    	// Get all the supertypes of the defined resource
    	abstractClasses= retrieveHierarchyTwo(resource);
    	// Get the geolocation information from DBPedia
    	Literal geolocation = getGeoLocation(resource);
    	String listOfClasses= "";
    	int iterator=0;
    	
    	// Create a list of superclasses that the recommended POIs belong
    	for (Resource askedClass:abstractClasses){
    		if (iterator>0)
    			listOfClasses+="UNION";
    		listOfClasses += "{?poi a <"+askedClass+">.}";
    		iterator++;
    	}
    	// Get all POIs of the superclasses in a radius of 25km from the defined location
    	String sparqlquery= "PREFIX geo:<http://www.w3.org/2003/01/geo/wgs84_pos#> \n"
    			+ "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#> \n"
				+ "PREFIX foaf:<http://xmlns.com/foaf/0.1/> \n"
				+ "PREFIX bif:<bif:>"
				+ "select distinct ?poi ?poilabel ?lat ?lon ?wikilink where { \n"
    			+listOfClasses
				+ "?poi geo:geometry ?poi_geo .\n"
				+ "?poi rdfs:label ?poilabel. \n"
				+ "?poi geo:lat ?lat. \n"
				+ "?poi geo:long ?lon.\n"
				+ "?poi foaf:isPrimaryTopicOf ?wikilink.\n"
				+ " FILTER(langMatches(lang(?poilabel), 'EN'))"
				+ "FILTER (bif:st_intersects(?poi_geo, '"+geolocation.getLexicalForm()+"'^^<http://www.openlinksw.com/schemas/virtrdf#Geometry>, 25) )}";
    	Query query = QueryFactory.create(sparqlquery);
	    QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
	    ResultSet results = qexec.execSelect();
    	
	    POIList list = new POIList();

        while (results.hasNext()) {
            QuerySolution soln = results.nextSolution();
            Resource poiuri = soln.getResource("poi");
            Resource wikipoi = soln.getResource("wikilink");
            Literal label = soln.getLiteral("poilabel");
            Literal lat = soln.getLiteral("lat");
            Literal lng = soln.getLiteral("lon");
            POI poi = new POI(poiuri.toString(), label.getValue().toString(), wikipoi.toString(), Double.valueOf(lat.getValue().toString()), Double.valueOf(lng.getValue().toString()));
            if (poiuri.toString().equals(resource))
            	continue;
            list.add(poi);
        }
    	
        	return list;
    }
    
    /**
     * Gets and returns the geolocation of a POI
     * @param resource
     * @return
     */
    private static Literal getGeoLocation(String resource){
    	
    	Literal geoLocation;
    	
    	String sparqlquery= "PREFIX geo:<http://www.w3.org/2003/01/geo/wgs84_pos#> \n"			
							+ "select distinct ?geolocation where {" 
							+ "<"+resource+"> geo:geometry ?geolocation.}\n"
							+ "LIMIT 1 ";
    	Query query = QueryFactory.create(sparqlquery);
	    QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
	    ResultSet results = qexec.execSelect();
	    if (results.hasNext() ){ 				
			QuerySolution soln = results.nextSolution();
			geoLocation = soln.getLiteral("geolocation");
		    qexec.close();
		    return geoLocation;
	    }
	    else {
		    qexec.close();
	    	return null;
	    }
    }
    
    /**
     * Queries for the supertypes of the given resource
     * @param resource
     * @return
     */
    private static List<Resource> retrieveHierarchyTwo(String resource){
    	
    	String sparqlquery= "PREFIX dbpedia-owl:<http://dbpedia.org/ontology/> \n"
				+ "PREFIX geo:<http://www.w3.org/2003/01/geo/wgs84_pos#> \n"
				+ "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#> \n"
				+ "PREFIX foaf:<http://xmlns.com/foaf/0.1/> \n"
				+ "PREFIX xsd:<http://www.w3.org/2001/XMLSchema#> \n"
				+ "select distinct ?super ?subclass where {" 
				+ "<"+resource+"> a ?super.\n"
				+ "<"+resource+">  a ?subclass.\n"
				+ "?subclass rdfs:subClassOf ?super.\n"
				+ "FILTER (?subclass!=?super)}";
    	
    	Query query = QueryFactory.create(sparqlquery);
	    QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
	    ResultSet results = qexec.execSelect();
	    
	    // Parse the result to avoid transitivity and thus repetition of types
	    List<Resource> hierarchy_two = parseResult(results);
	    qexec.close();
	    
	    return hierarchy_two;

    }
    
    
	/**
	 * Parses the resultset from the query and creates two list of classes.
	 * Retrieves a list of the 1st hierarchy classes by finding the junction between all classes and all superclasses defined
	 * @param result
	 * @return
	 */
	private static List<Resource> parseResult(ResultSet result) {
		
		
		// Maps every supclass to a list of its superclasses
		HashMap<Resource, List<Resource>> classMappings= new HashMap<Resource, List<Resource>>();
		
		List<Resource> superclasses = new ArrayList<Resource>();
		List<Resource> hierarchy_twoClasses = new ArrayList<Resource>();
		
		if (result.hasNext()){
				
			while (result.hasNext() ){ 				
				QuerySolution soln = result.nextSolution();
				Resource superName = soln.getResource("super");
				Resource subName = soln.getResource("subclass");
				superclasses.add(superName);
				// Subclass is found for the first time - put it in the map
	            if (classMappings.containsKey(subName)){
	            	classMappings.get(subName).add(superName);
	            }
	            // Subclass is already in the hashmap - simply add the superclass at the list of values
	            else {
	            	List<Resource> resources= new ArrayList<Resource>();
	            	resources.add(superName);
	            	classMappings.put(subName, resources);	            	
	            }
			}
		}
		// Add a class in the superclass list only if it is not also defined as class of level 1
		for (Resource class_hone:classMappings.keySet()){
			if(!superclasses.contains(class_hone)){
				for (Resource class_htwo:classMappings.get(class_hone)){
					if (!(hierarchy_twoClasses.contains(class_htwo)))
						hierarchy_twoClasses.add(class_htwo);
				}
			}
		}
		
		return hierarchy_twoClasses;
	}

	
	
	

	

}
