package models.images;

import java.util.Iterator;

import models.geo.Location;
import models.images.struct.LocPhoto;
import models.images.struct.LocPhotoList;
import play.Logger.ALogger;
import play.cache.Cache;
import play.libs.F.Promise;
import play.libs.ws.WS;

import com.fasterxml.jackson.databind.JsonNode;

public class PanoramioAPI {
	
	private static final ALogger logger = play.Logger.of(PanoramioAPI.class);
	
	public static Promise<LocPhotoList> getCachedPhotos(Location loc){
		// Caching-Key
    	String sKey = "pano-"+loc.getLat()+loc.getLng();
    	// Is the result already in the Cache?
    	@SuppressWarnings("unchecked")
		Promise<LocPhotoList> panoraPhotos = (Promise<LocPhotoList>) Cache.get(sKey);
    	// No? -> Retrieve it
		if(panoraPhotos == null) panoraPhotos = PanoramioAPI.search4Photos(loc);
		// Put it into the cache
		Cache.set(sKey, panoraPhotos);
		return panoraPhotos;
	}

	/**
	 * Search for photos on panoramio on promise!
	 * @param loc
	 * @return
	 */
	public static Promise<LocPhotoList> search4Photos(Location loc){
		// Retrieve bounding for 15km distance
		double[][] dBounding = loc.getBoundingCoordinates(15);
		
		// Create URL
		String sURL = "http://www.panoramio.com/map/get_panoramas.php?set=public&from=0&to=50&minx=MIN_X&miny=MIN_Y&maxx=MAX_X&maxy=MAX_Y&size=medium&mapfilter=true";
		// Set bounding
		sURL = sURL.replace("MIN_X", String.valueOf(dBounding[0][0]));
		sURL = sURL.replace("MIN_Y", String.valueOf(dBounding[0][1]));
		sURL = sURL.replace("MAX_X", String.valueOf(dBounding[1][0]));
		sURL = sURL.replace("MAX_Y", String.valueOf(dBounding[1][1]));
		
		logger.debug(sURL);
		
		// make async web-request
		Promise<LocPhotoList> photoPromise = WS.url(sURL).get().map(response -> {
			// result is json
			JsonNode jsonNode = response.asJson();
			
			// init photo-list
			LocPhotoList photoList = new LocPhotoList();
			
			// Get iterator for photos
			Iterator<JsonNode> itNode = jsonNode.findValue("photos").elements();
			// while there are more photos!
			while(itNode.hasNext()){
				JsonNode onePhoto = itNode.next();
				
				// add photo to list
				photoList.addPhoto(
					new LocPhoto()
					.setHeight	(onePhoto.get("height"		).asInt())
					.setWidth	(onePhoto.get("width"		).asInt())
					.setTitle	(onePhoto.get("photo_title"	).asText())
					.setLat		(onePhoto.get("latitude"	).asDouble())
					.setLng		(onePhoto.get("longitude"	).asDouble())
					.setPhotoID	(onePhoto.get("photo_id"	).asText())
                    .setOwner_name(onePhoto.get("owner_name").asText())
                    .setOwner_url(onePhoto.get("owner_url").asText())
				);
			}
			return photoList;
		});
		return photoPromise;
	}
	
	
	
}
