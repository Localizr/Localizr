package models.geonames;

import java.util.List;

import models.geo.Location;
import models.geonames.struct.POI;
import models.geonames.struct.POIList;
import play.Logger.ALogger;
import play.cache.Cache;
import play.libs.F;
import play.libs.F.Promise;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Resource;

import controllers.Application;

public class GeoNames {

	private static final ALogger logger = play.Logger.of(GeoNames.class);

	
	public static F.Promise<POIList> getCachedPOIList(Location loc) {
		// Build Caching-Key
    	String sCategories = "";
    	String sKey = "poilist-"+loc.getLat()+loc.getLng();
    	// Is the result already in the Cache?
    	@SuppressWarnings("unchecked")
    	F.Promise<POIList> poiList = (Promise<POIList>) Cache.get(sKey);
    	// No? -> Retrieve it
		if(poiList == null) poiList = GeoNames.async(loc);
		// Put it into the cache
		Cache.set(sKey, poiList);
		return poiList;
	}
    public static F.Promise<POIList> async(Location loc) {
    	return F.Promise.promise(() -> GeoNames.retriveData(loc));
    }

	/**
	 * Queries factforge endpoint for POIs  based on the coordinates and profile of the user
	 * @param location
	 * @param profile
	 * @param categories
	 * @return
	 */
	public static POIList retriveData(Location location){

		String sparqlquery= "PREFIX geo-pos: <http://www.w3.org/2003/01/geo/wgs84_pos#> \n"
				+ "PREFIX omgeo: <http://www.ontotext.com/owlim/geo#> \n"
				+ "PREFIX dbpedia: <http://dbpedia.org/resource/> \n"
				+ "PREFIX dbp-ont: <http://dbpedia.org/ontology/> \n"
				+ "PREFIX ff: <http://factforge.net/> \n"
				+ "PREFIX om: <http://www.ontotext.com/owlim/> \n"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
				+ "PREFIX fb: <http://rdf.freebase.com/ns/> \n"
				+ "PREFIX foaf: <http://xmlns.com/foaf/0.1/> \n"
				+ "PREFIX prov:<http://www.w3.org/ns/prov#> \n"
				+ "SELECT distinct ?label ?poi ?wikipoi ?lat ?lng ?description ?page ?pic \n "				
				+ "where { \n"
				+ "?poi omgeo:nearby(\""+ String.valueOf(location.getLat())+"\"" + "\"" +String.valueOf(location.getLng())+ "\"" +"\"10km\"). \n"
				+ "?poi rdfs:label ?label . \n"
				+ "?poi prov:wasDerivedFrom ?wikipoi. \n"
				+ "?poi rdfs:comment ?description. \n"
				+ "?poi fb:type.object.type ?type. \n"
				+ "?poi geo-pos:lat ?lat . \n"
				+ "?poi geo-pos:long ?lng .\n"
				+ "FILTER(langMatches(lang(?label), 'EN'))\n"
				+ "OPTIONAL { \n"
				+ "?poi foaf:homepage ?page . \n"
				+ "} \n"
				+ "OPTIONAL { \n"
				+ "?poi foaf:depiction ?pic . \n"
				+ "} \n"
				+ "} \n"
				+ "ORDER BY ASC(?label)  \n";
		
		Query query = QueryFactory.create(sparqlquery);
		long l1 = System.currentTimeMillis();
        QueryExecution qexec = QueryExecutionFactory.sparqlService("http://factforge.net/sparql", query);
        ResultSet results = qexec.execSelect();
        long l2 = System.currentTimeMillis()-l1;
//        logger.error("Time for query "+l2+"\n"+sparqlquery);
        POIList list = new POIList();

        // Parse the retrieved result and create a list of POIs
        while (results.hasNext()) {
            QuerySolution soln = results.nextSolution();
            Resource resource = soln.getResource("poi");
            Resource wikipoi = soln.getResource("wikipoi");
            Literal label = soln.getLiteral("label");
            Literal lat = soln.getLiteral("lat");
            Literal lng = soln.getLiteral("lng");
            POI poi = new POI(resource.toString(), label.getValue().toString(), wikipoi.toString(), Double.valueOf(lat.getValue().toString()), Double.valueOf(lng.getValue().toString()));
            list.add(poi);
        }
        

        qexec.close() ;

        return list;
    }

}
