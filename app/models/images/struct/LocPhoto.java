package models.images.struct;

public class LocPhoto {

	private String sTitle;
	private String sPhotoID;
	@SuppressWarnings("unused")
	private String sURL;
	@SuppressWarnings("unused")
	private String sLargeURL;
	private int nWidth;
	private int nHeight;

    // Copyright
    private String owner_name;
    private String owner_url;
	
	// not mandatory (just filled by panoramio atm)
	private double dLat;
	private double dLng;
	
	
	public String getPhotoID() {
		return sPhotoID;
	}

	public LocPhoto setPhotoID(String photoID) {
		sPhotoID = photoID;
		sURL = getURL();
		sLargeURL = getURLOriginalImage();
		return this;
	}

	public double getRatio() {
		return (double)getWidth()/(double)getHeight(); 
	}
	
	public boolean skipPhotoDueToRatio(){
		return !(getRatio()>0.5 && getRatio() < 5);
	}
	
	public double getLat() {
		return dLat;
	}
	public LocPhoto setLat(double lat) {
		this.dLat = lat; return this;
	}
	public double getLng() {
		return dLng;
	}
	public LocPhoto setLng(double lng) {
		this.dLng = lng; return this;
	}
	public int getWidth() {
		return nWidth;
	}
	public LocPhoto setWidth(int width) {
		this.nWidth = width; return this;
	}
	public int getHeight() {
		return nHeight;
	}
	public LocPhoto setHeight(int height) {
		this.nHeight = height; return this;
	}
	public String getTitle() {
		return sTitle;
	}
	public LocPhoto setTitle(String titel) {
		sTitle = titel; return this;
	}
	public String getURLOriginalImage(){
		return "http://static.panoramio.com/photos/original/"+getPhotoID()+".jpg";
	}
	public String getURL() {
		return "http://mw2.google.com/mw-panoramio/photos/medium/"+getPhotoID()+".jpg";
	}

    public String getOwner_name() {
        return owner_name;
    }

    public LocPhoto setOwner_name(String owner_name) {
        this.owner_name = owner_name;
        return this;
    }

    public String getOwner_url() {
        return owner_url;
    }

    public LocPhoto setOwner_url(String owner_url) {
        this.owner_url = owner_url;
        return this;
    }
}
