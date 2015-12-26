package models.eventmedia;

import play.cache.Cache;
import play.libs.F;
import play.libs.F.Promise;
import play.mvc.Controller;

import com.hp.hpl.jena.query.*;

import models.geo.Location;


public class EventMedia extends Controller {
	
	public static F.Promise<EventList> getCachedEvents(Location loc,String profile) {
		// Build Caching-Key
    	String sKey = "events-"+loc.getLat()+loc.getLng()+profile;
    	@SuppressWarnings("unchecked")
    	// Is the result already in the Cache?
    	F.Promise<EventList> eventList = (Promise<EventList>) Cache.get(sKey);
    	// No? -> Retrieve it
		if(eventList == null) eventList = EventMedia.search4Events(loc,profile);
		// Put it into the cache
		Cache.set(sKey, eventList);
		return eventList;
	}
	
	public static F.Promise<EventList> search4Events(Location loc,String profile) {
		return F.Promise.promise(() -> EventMedia.poseEventQuery(loc, profile));
//		final EventList events=EventMedia.poseEventQuery(loc,profile);
//		if (events==null) 	return null;
//		else 				return F.Promise.promise(() -> events);
	}

	/**
	 * Based on the city name of the location retrieves all the Events from EventMedia dataset together with their relavant information
	 * @param location
	 * @param profile
	 * @return
	 */
	public static EventList poseEventQuery(Location location,String profile) {
		
		//String profile="family";// to be implemented in the interface
		String filterCategories=setCategoriesFilter(profile);
				
		String sparqlquery= "PREFIX lode:<http://linkedevents.org/ontology/> \n"
				+ "PREFIX dc:<http://purl.org/dc/elements/1.1/> \n"
				+ "PREFIX vcard:<http://www.w3.org/2006/vcard/ns#> \n"
				+ "PREFIX time:<http://www.w3.org/2006/time#> \n"
				+ "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>"
				+ "PREFIX foaf:<http://xmlns.com/foaf/0.1/>"
				+ "select  \n "
				+ "(group_concat(distinct ?title; separator = ',') AS ?Title)"
				+ "(group_concat(distinct ?address; separator = ',') AS ?Address)"
				+ "(group_concat(distinct ?datetime; separator = ',') AS ?Datetime)"
				+ "(group_concat(distinct ?category; separator = ',') AS ?Categories) \n"
				+ "(group_concat(distinct ?nameAgent; separator = ',') AS ?PeopleInvolved) \n"
				+ "(group_concat(distinct ?img; separator = ',') AS ?Images) \n"
				+ "(group_concat(distinct ?namePlace; separator = ',') AS ?Nameplace) \n"
				+ "where { \n"
				+ "?event a lode:Event. \n"
				+ "?event dc:title ?title. \n"
				+ "?event lode:atPlace ?place. \n"
				+ "?place vcard:adr ?blankplace. \n"
				+ "?blankplace vcard:locality '"+location.getSimpleName()+"'. \n"
				+ "?event lode:hasCategory ?category.\n"
				+ "?place rdfs:label ?namePlace. \n"
				+ "?blankplace vcard:street-address ?address. \n"
				+ "?event lode:atTime ?time. \n"
				+ "?time time:inXSDDateTime ?datetime. \n"
				+ "?event lode:involvedAgent ?agent.\n"
				+ "?agent rdfs:label ?nameAgent. \n"
				+ "FILTER ( "+filterCategories+" ) \n"
				+ "OPTIONAL { \n"
				+ "?agent foaf:depiction ?img. \n"
				+ "} \n"
				+ "} \n"
				+ "ORDER BY DESC(?datetime) \n";

		Query query = QueryFactory.create(sparqlquery);
	    QueryExecution qexec = QueryExecutionFactory.sparqlService("http://eventmedia.eurecom.fr/sparql", query);
	
	    ResultSet results = qexec.execSelect();
	    EventList events = parseResult(results);
	    qexec.close();

	    return events;
	    
	}
	
	/**
	 * Defines the different lode:hasCategory  values for the EventMedia query based on the selected profile
	 * @param profile
	 * @return
	 */
	private static String setCategoriesFilter(String profile) {
		
		 String categories;
		 
		 switch (profile) {
		 case "tourist":  categories = "?category='Musical Concert' || ?category='Visual and Performing Arts' || ?category='Festivals' || ?category='Other and Miscellaneous' || ?category='Comedy' || ?category='Performing Arts' || ?category='Media and Literary' || ?category='Technology' || ?category='Nightlife' || ?category='Museums and Attractions' || ?category='Food'";
		  break;
		 case "newcitizen":  categories = "?category='Musical Concert' || ?category='Social Gathering' || ?category='Visual and Performing Arts' || ?category='Festivals' || ?category='Community' || ?category='Other and Miscellaneous' || ?category='Media and Literary' || ?category='Comedy' || ?category='Performing Arts' || ?category='Nightlife' || ?category='Fundraising and Charity' || ?category='Food'";
		  break;
		 case "family":  categories = "?category='Family and Kids' || ?category='Education' || ?category='Animals' ";
		  break;
		 case "business":  categories = "?category='Commercial and Sales' || ?category='Politics' || ?category='Technology' || ?category='Professional' || ?category='Conferences and Tradeshows' || ?category='Organizations and Meetups'|| ?category='Business and Networking' || ?category='Science'";
		  break;
		 case "sport":  categories = "?category='Sports' || ?category='Outdoors Recreation' || ?category='Health and Wellness'";
		  break;
		// Default take values like tourist
		 default: categories = "?category='Musical Concert' || ?category='Visual and Performing Arts' || ?category='Festivals' || ?category='Other and Miscellaneous' || ?category='Comedy' || ?category='Performing Arts' || ?category='Media and Literary' || ?category='Technology' || ?category='Nightlife' || ?category='Museums and Attractions' || ?category='Food'"; 
		  break;
		 }
		return categories;
	}

	/**
	 * Parses the result of the query and stores the relevant information in a list of Event objects
	 * @param result
	 * @return
	 */
	private static EventList parseResult (ResultSet result){
		
		EventList events = new EventList();
		
		if (result.hasNext())
			while (result.hasNext() ){ 
				QuerySolution soln = result.nextSolution() ;
				
	            Event event =new Event();
	            event.setTitle(soln.getLiteral("?Title").toString());
	            event.setDatetime(soln.getLiteral("?Datetime").toString());
				event.setHallName(soln.getLiteral("?Nameplace").toString());
				event.setAddress(soln.getLiteral("?Address").toString());
				event.setCategory(soln.getLiteral("?Categories").toString());
				event.setInvolvedAgent(soln.getLiteral("?PeopleInvolved").toString());
				event.setImages(soln.getLiteral("?Images").toString());
				
				events.add(event);
			}
		return events;

	}
}
