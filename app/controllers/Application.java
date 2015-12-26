package controllers;

import java.util.List;
import java.util.Map;

import models.dbpedia.DBPedia;
import models.dbpedia.DBPediaInfoObject;
import models.eventmedia.EventList;
import models.eventmedia.EventMedia;
import models.geo.GoogleAPI;
import models.geo.Location;
import models.geonames.GeoNames;
import models.geonames.struct.POIList;
import models.images.PanoramioAPI;
import models.images.struct.LocPhotoList;
import play.Logger.ALogger;
import play.libs.F.Promise;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.ergebnis;
import views.html.index;
import controllers.constant.RequestHandler;
import controllers.constant.RequestType;

public class Application extends Controller {

	private static final ALogger logger = play.Logger.of(Application.class);
	

    /**
     * Serve all request from '/'
     * @return
     */
    public static Result index() {
    	try {
			// Retrieve possible query parameters
    		// Two possible modes: query by name or position (GPS)
	    	String sQry = request().getQueryString("qry");  // Query by name
	    	String sLat = request().getQueryString("lat");	// Query by latitude & longitude
	    	String sLon = request().getQueryString("lon");	// Query by latitude & longitude
            String type = request().getQueryString("type"); // What type of search?
	    	String profile = request().getQueryString("profile");	// Read radio button value for profile

	    	// What type of request do we have?
	    	RequestType reqType = RequestHandler.getType(sQry, type, sLat, sLon);
	    	

	    	// init the promise for a location
	    	Promise<Location> promLoc = null;
	    	
	    	// there is a given query?
	    	if(reqType == RequestType.STRING_SEARCH){
	    		// 'name'-mode. Determine the Location-object by string, e.g. 'Mannheim'
    			promLoc = GoogleAPI.getLocationByString(sQry);
    			
	    	} else if(reqType == RequestType.GEO_SEARCH){
				// 'position'-mode. Determine the Location-object by coordinates, e.g. lat=40.714224 lng=-73.961452
    			promLoc = GoogleAPI.getLocationByCoordinates(sLat, sLon);
    			
	    	} else
				// No parameters? serve start page
				return ok(index.render());

	    	// Determine the categories (if given) as a list
            Map<String,String[]> queryString = request().queryString();
            List<String> categoryList = RequestHandler.getCategories(queryString);


	    	// Wait for the location because we need it for further processing!
	    	Location loc = null;
            try{
                loc = promLoc.get(200000);
            }catch(Exception e){
                e.printStackTrace();
                return internalServerError("Location could not be retrieved! Exception: " + e.toString());
            }

            // init
            Promise<POIList> 			PoiListFuture 		= GeoNames.getCachedPOIList(loc,profile,categoryList);
            Promise<LocPhotoList> 		panoraPhotosFuture	= PanoramioAPI.getCachedPhotos(loc);
            Promise<EventList> 			eventListFuture 	= EventMedia.getCachedEvents(loc,profile);
            Promise<DBPediaInfoObject> 	infoDBPediaFuture 	= DBPedia.getCachedGeneralInfo(loc);
            
            POIList poiList;
            LocPhotoList panoraPhotos;
            EventList eventList;
            DBPediaInfoObject infoDBPedia;
            Boolean error = false;
            int nTimeout = 40000;
            
            // ############# POINT OF INTERESTS ###############################
            try{poiList = PoiListFuture.get(nTimeout*2);}
            catch(Exception e){
            	logger.error("Error retrieving POIList: "+e.getLocalizedMessage());
                poiList = new POIList();
                error = true;
            }

            
            // ############# EVENTS ###############################
            try{eventList = eventListFuture.get(nTimeout);}
            catch(Exception e){
            	logger.error("Error retrieving Events: "+e.getLocalizedMessage());
                eventList = new EventList();
                error = true;
            }

            
            // ############# PHOTOS ###############################
            try{panoraPhotos = panoraPhotosFuture.get(nTimeout);}
            catch(Exception e){
            	logger.error("Error retrieving Panoramio: "+e.getLocalizedMessage());
                panoraPhotos = new LocPhotoList();
                error = true;
            }

            
            // ############# GENERAL INFO #######################
            try{infoDBPedia = infoDBPediaFuture.get(nTimeout);}
            catch(Exception e){
            	logger.error("Error retrieving DBpedia: "+e.getLocalizedMessage());
                infoDBPedia = new DBPediaInfoObject();
                error = true;
            }

            // Serve the result!
			return ok(ergebnis.render(loc, poiList, eventList, infoDBPedia, panoraPhotos, error));
			
		// Errors?
    	} catch (Exception e) {
            e.printStackTrace();
    		logger.error(e.toString());
            return internalServerError("Exception: " + e.toString());
    	}
    }
    
}
