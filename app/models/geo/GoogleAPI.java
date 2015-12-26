package models.geo;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import play.Logger.ALogger;
import play.cache.Cache;
import play.libs.F.Promise;
import play.libs.XPath;
import play.libs.ws.WS;
import play.mvc.Controller;

public class GoogleAPI extends Controller{
	
	private static final ALogger logger = play.Logger.of(GoogleAPI.class);
	
	private static final String CACHE_GEO = "geo-qry-";
	private static final String CACHE_STRING = "qry-";

	/**
	 * Retrieve location by coordinates
	 * Example URL http://maps.googleapis.com/maps/api/geocode/json?latlng=40.714224,-73.961452&sensor=false&language=en
	 * @param sLon
	 * @param sLat
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static Promise<Location> getLocationByCoordinates(String sLon, String sLat) throws UnsupportedEncodingException{
    	@SuppressWarnings("unchecked")
		Promise<Location> loc = (Promise<Location>) Cache.get(CACHE_GEO+sLat+sLon);
		if(loc == null)
			loc = getLocationByURL("http://maps.googleapis.com/maps/api/geocode/xml?latlng="+sLon+","+sLat+"&sensor=false&language=en");

		Cache.set(CACHE_GEO+sLat+sLon, loc);
		return loc;
	}
	
	/**
	 * Retrieve the location by string
	 * Example URL http://maps.googleapis.com/maps/api/geocode/xml?address=mannheim&sensor=false&language=en
	 * @param sQry
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static Promise<Location> getLocationByString(String sQry) throws UnsupportedEncodingException{
    	@SuppressWarnings("unchecked")
		Promise<Location> loc = (Promise<Location>) Cache.get(CACHE_STRING+sQry);
		if(loc == null){
			loc = getLocationByURL("http://maps.googleapis.com/maps/api/geocode/xml?address="+URLEncoder.encode(sQry, "UTF-8")+"&sensor=false&language=en");
			Cache.set(CACHE_STRING+sQry, loc);
		}
		return loc;
	}
	
	/**
	 * Retrieve location by URL
	 * @param sURL
	 * @return
	 */
	private static Promise<Location> getLocationByURL(String sURL){
		// Create a promise (ASYNC!)
		return WS.url(sURL).get().map(response -> {
			// Parse as XML
			Document doc = response.asXml();
			
			// use XPATH to determine required information
			Node nodeSimpleName = XPath.selectNode("/GeocodeResponse/result/address_component[type=\"locality\"]/long_name", doc);
			Node nodeAdd = XPath.selectNode("/GeocodeResponse/result/formatted_address", doc);
			Node nodeLat = XPath.selectNode("/GeocodeResponse/result/geometry/location/lat", doc);
			Node nodeLng = XPath.selectNode("/GeocodeResponse/result/geometry/location/lng", doc);
			
			// Get Content as String
			String sSimpleName = nodeSimpleName.getTextContent();
			String sAdd = nodeAdd.getTextContent();
			String sLat = nodeLat.getTextContent();
			String sLng = nodeLng.getTextContent();
			
			// Create a Location
			Location loc = new Location();
			loc.setName(sAdd);
			loc.setSimpleName(sSimpleName);
			loc.setLat(Double.parseDouble(sLat));
			loc.setLng(Double.parseDouble(sLng));
			
			logger.info(loc.getName()+" liegt in LAT "+loc.getLat()+", LNG "+loc.getLng());

			return loc;
		});
	}
	
}
