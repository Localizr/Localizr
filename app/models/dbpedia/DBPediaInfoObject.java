package models.dbpedia;

import java.util.HashMap;

public class DBPediaInfoObject {

	private HashMap<String, String> literalProperties = new HashMap<String, String>();
	private HashMap<String, String> wikiLinks = new HashMap<String,String>();
	
	public HashMap<String,  String> getWikiLinks() {
		return wikiLinks;
	}
	public void setWikiLinks(HashMap<String, String> wikiLinks) {
		this.wikiLinks = wikiLinks;
	}
	public HashMap<String, String> getLiteralProperties() {	
		
		return formProperties(literalProperties);
	}
	public void setLiteralProperties(HashMap<String, String> properties) {
		this.literalProperties = properties;
	}
	
	//takes the initial hashmap and groups the same properties
	private  HashMap<String, String> formProperties(HashMap<String, String> initialProp){
		
		HashMap <String, String> finalProp = new HashMap<String,String>();
		if (initialProp.containsKey("Lcountry"))
			finalProp.put("Country", initialProp.get("Lcountry"));
		if (initialProp.containsKey("Description"))
			finalProp.put("Short Description", initialProp.get("Description"));
		if (initialProp.containsKey("Homepage"))
			finalProp.put("Website", initialProp.get("Homepage"));
		if (initialProp.containsKey("Motto"))
			finalProp.put("Motto of the city", initialProp.get("Motto"));
		if (initialProp.containsKey("AreaMetro2"))
			finalProp.put("Metropolitan Area Size", initialProp.get("AreaMetro2"));
		if (initialProp.containsKey("Area1")){
			if(initialProp.containsKey("Area3"))
				finalProp.put("Total Area Size",
						String.valueOf(Double.parseDouble(initialProp.get("Area1"))+Double.parseDouble(initialProp.get("Area3"))/2));
			else 
				finalProp.put("Total Area Size", initialProp.get("Area1"));
		}
		else if (initialProp.containsKey("Area3"))
			finalProp.put("Total Area Size", initialProp.get("Area3"));
		if (initialProp.containsKey("Population1")){
			if (initialProp.containsKey("Population2")){
				finalProp.put("Population",
						String.valueOf(Double.parseDouble(initialProp.get("Population1"))+Double.parseDouble(initialProp.get("Population2"))/2));
			}
			else 
				finalProp.put("Population",initialProp.get("Population1"));
		}
		else if (initialProp.containsKey("Population2"))
			finalProp.put("Population",initialProp.get("Population2"));
		
		if (initialProp.containsKey("Leader1")){
			if (initialProp.containsKey("Leader2"))
				finalProp.put("Leader", initialProp.get("Leader1")+","+initialProp.get("Leader2"));
		}
			
		if (initialProp.containsKey("Picture"))
			finalProp.put("picture", initialProp.get("Picture"));
		return finalProp;
	}
	

}
