package models.geonames.struct;

/**
 * Created by martin on 04.10.14.
 */
public class POI {

    private String resource;
    private String label;
    private String wikilink;
    private double lat;
    private double lon;
    
    @Override
    public boolean equals(Object obj) {
    	if(obj instanceof POI){
    		POI poi2 = (POI)obj;
    		
    		if(poi2.getLabel().equals(getLabel()))
    			return true;
    		else
    			return false;
    		
    	}
    	return super.equals(obj);
    }

    public POI(String resource, String label, String wikilink, double lat, double lon){
        this.resource = resource;
        this.label = label;
        this.setWikilink(wikilink);
        this.lat = lat;
        this.lon = lon;
    }

    public String toString(){
        return "POI: " + this.resource + " " + this.label + " " + this.lat + " " + this.lon;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

	public String getWikilink() {
		return wikilink;
	}

	public void setWikilink(String wikilink) {
		String sOD = "oldid=";
		if(wikilink.contains(sOD))
				wikilink = wikilink.substring(0, wikilink.indexOf(sOD)-1);
		this.wikilink = wikilink;
	}
}
