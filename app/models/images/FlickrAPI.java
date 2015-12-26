package models.images;
import java.util.Collections;

import models.geo.Location;
import models.images.struct.LocPhoto;
import models.images.struct.LocPhotoList;
import play.libs.F.Promise;
import play.mvc.Controller;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotoList;
import com.flickr4java.flickr.photos.SearchParameters;
import com.flickr4java.flickr.photos.Size;
import com.flickr4java.flickr.places.Place;
import com.flickr4java.flickr.places.PlacesList;
public class FlickrAPI extends Controller{
	
	
	/** 
	 * ##################################################################
	 * ##################################################################
	 * ##################################################################
	 * ##################################################################
	 * This class only has an archiving purpose.
	 * Flickr is not used any more as the results are not very good
	 * and panoramio has much better photos
	 * ##################################################################
	 * ##################################################################
	 * ##################################################################
	 * ##################################################################
	 */

	private final static String  PRIVATE_KEY = "YOUR_PRIVATE_KEY";
	private final static String  KEY         = "YOUR_KEY";
	private final static Flickr  flkr        = new Flickr(KEY, PRIVATE_KEY, new REST());

    /**
     * Search by promise for photos on flickr
     * @param loc
     * @return
     */
    public static Promise<LocPhotoList> search4Photos(Location loc) {
    	return Promise.promise(() -> FlickrAPI.getFlickrPhotos(loc));
	}
	
	/**
	 * @param loc
	 * @return
	 * @throws Exception
	 */
	private static LocPhotoList getFlickrPhotos(Location loc) throws Exception{
		// init photolist
		LocPhotoList photoList = new LocPhotoList();

		// 2DO: Decide which technique should be used
//		PhotoList<Photo> listPhoto =  flickr.getPhotosInterface().search(searchByGeo(loc), 20, 0);
		PhotoList<Photo> listPhoto =  flkr.getPhotosInterface().search(searchByPlaceID(loc), 20, 0);
//		PhotoList<Photo> listPhoto =  flickr.getPhotosInterface().search(searchByFulltext(loc), 20, 0);
		
		// Iterate over all found photos
		for(Photo p:listPhoto){
			Size biggest = null;
			// Look for the biggest picture
			for(Size size:flkr.getPhotosInterface().getSizes(p.getId()))
				if(biggest==null || size.getWidth() > biggest.getWidth())
					biggest = size;
				
			// Add picture to photo-list
			photoList.addPhoto(
				new LocPhoto()
				.setHeight(biggest.getHeight())
				.setWidth(biggest.getWidth())
				.setTitle(p.getTitle())
				//.setURL(biggest.getSource())
			);
			
		}
		// Reverse to get the best picture on first place
		Collections.reverse(photoList.getPhotos());
		
		return photoList;
	}
    

    
//    private static SearchParameters searchByFulltext(Location loc){
//		SearchParameters params = new SearchParameters();
//		params.setSort(com.flickr4java.flickr.photos.SearchParameters.RELEVANCE);
//		params.setText(loc.getSimpleName());
//		return params;
//    }
//    private static SearchParameters searchByGeo(Location loc){
//		SearchParameters params = new SearchParameters();
//		params.setSort(com.flickr4java.flickr.photos.SearchParameters.RELEVANCE);
//		params.setLongitude(String.valueOf(loc.getLng()));
//		params.setLatitude(String.valueOf(loc.getLat()));
//		params.setAccuracy(6);
//		// Without tag there would be only the results of last 12 hours
//		// See https://www.flickr.com/services/api/flickr.photos.search.html#yui_3_11_0_1_1412423010742_254
//		params.setTags(new String[]{loc.getSimpleName()});
//		return params;
//    }
    
    private static SearchParameters searchByPlaceID(Location loc) throws FlickrException{
		PlacesList<Place> listPlaces = flkr.getPlacesInterface().findByLatLon(loc.getLat(), loc.getLng(), 8);
		SearchParameters params = new SearchParameters();
		params.setSort(com.flickr4java.flickr.photos.SearchParameters.RELEVANCE);
		params.setPlaceId(listPlaces.get(0).getPlaceId());
		// Without tag there would be only the results of last 12 hours
		// See https://www.flickr.com/services/api/flickr.photos.search.html#yui_3_11_0_1_1412423010742_254
		params.setTags(new String[]{loc.getSimpleName()});
		return params;
    }
}
