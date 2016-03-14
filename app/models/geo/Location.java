package models.geo;

import java.util.ArrayList;
import java.util.List;

public class Location {

	// e.g. Mannheim, Germany
	private String Name;
	
	// e.g. Mannheim
	private String SimpleName;
	private double lng;
	private double lat;
	private List<Location> listAlternatives = new ArrayList<Location>();
	
	/**
	 * Get the bounding coordinates from a location
	 * @param dDistance
	 * @return
	 */
	public double[][] getBoundingCoordinates(double dDistance) {
		GeoHelper geo = GeoHelper.fromDegrees(getLat(), getLng());
		// Search within a distance (dDistance) of kilometres on earth (=6371.01)
		GeoHelper[] bounding = geo.boundingCoordinates(dDistance, 6371.01);
		
		double[][] dArr = new double[2][2];
		dArr[0][0] = bounding[0].getLongitudeInDegrees();	// MIN_X = MIN_LONGITUDE
		dArr[0][1] = bounding[0].getLatitudeInDegrees();	// MIN_Y = MIN_LATITUDE
		dArr[1][0] = bounding[1].getLongitudeInDegrees();	// MAX_X = MAX_LATITUDE
		dArr[1][1] = bounding[1].getLatitudeInDegrees();	// MAX_Y = MAX_LONGITUDE
		return dArr;
	}
	
	
	public String getSimpleName() {
		return SimpleName;
	}
	public void setSimpleName(String simpleName) {
		SimpleName = simpleName;
	}
	public void addAlternative(Location loc){
		listAlternatives.add(loc);
	}
	public List<Location> getAlternatives(){
		return listAlternatives;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public double getLng() {
		return lng;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}

}