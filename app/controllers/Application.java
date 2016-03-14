package controllers;

import models.dbpedia.DBPedia;
import models.dbpedia.DBPediaInfoObject;
import models.eventful.EventfulAPI;
import models.eventful.EventList;
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
	    	String sQry = request().getQueryString("qry"); 
	    	String sLat = request().getQueryString("lat");
	    	String sLon = request().getQueryString("lon");
	    	String sName = request().getQueryString("name");
	    	
	    	// All parameters have to be set
	    	if(sQry == null || sLat==null || sLon==null || sName == null)
				// No parameters? serve start page
				return ok(views.html.index.render());


	    	Location loc = new Location();
	    	loc.setLat(Double.parseDouble(sLat));
	    	loc.setLng(Double.parseDouble(sLon));
	    	loc.setName(sQry);
	    	loc.setSimpleName(sName);
	    	

            // init
            Promise<POIList> 			PoiListFuture 		= GeoNames.getCachedPOIList(loc);
            Promise<LocPhotoList> 		panoraPhotosFuture	= PanoramioAPI.getCachedPhotos(loc);
            Promise<EventList> 			eventListFuture 	= EventfulAPI.getCachedEvents(loc);
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
                e.printStackTrace();
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
