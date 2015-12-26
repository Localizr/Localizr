package models.eventmedia;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date; 

public class Event {
	
	//member
	private String title;
	private String categories;
	private String address;
	private String city;
	private double lon;
	private double lat;
	private String involvedAgents;
	private String datetime;
	private String images;
	private String hallName;
	
	
	// helper classes
	
	private String shortenString(String s, int nLength){
		if(s.length()>nLength)
			return s.substring(0, nLength)+"...";
		return s;
	}
	
	public String getFormattedDate() throws Exception{
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
		Date result =  df.parse(getDatetime());
		
		DateFormat dfDate = new SimpleDateFormat("dd.MM.yyyy");
		String sDate = dfDate.format(result);
		
		DateFormat dfTime = new SimpleDateFormat("kk:mm:ss");
		String sTime = dfTime.format(result);
		
		return "<span class='date'>"+sDate+"</span><span class='time'>"+sTime+"</span>";
		
	}
	
	// regular getter&setter
	
	public String getShortArtist(){
		return shortenString(getInvolvedAgent(), 50);
	}
	public String getShortTitle(){
		return shortenString(getTitle(), 50);
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCategory() {
		return categories;
	}
	public void setCategory(String category) {
		this.categories = category;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public double getLon() {
		return lon;
	}
	public void setLon(double lon) {
		this.lon = lon;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public String getInvolvedAgent() {
		return involvedAgents;
	}
	public void setInvolvedAgent(String involvedAgent) {
		this.involvedAgents = involvedAgent;
	}
	public String getDatetime() {
		return datetime;
	}
	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}
	public boolean hasImage(){
		return !getImages().isEmpty();
	}
	public String getFirstImage(){
		return getImages().split(",")[0];
	}
	public String getImages() {
		return images;
	}
	public void setImages(String images) {
		this.images = images;
	}
	public String getHallName() {
		return hallName;
	}
	public void setHallName(String hallName) {
		this.hallName = hallName;
	}
}
