package controllers.constant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class RequestHandler {
	
	/**
	 * Determine the RequestType by the Query-Parameters
	 * @param sQry
	 * @param type
	 * @param sLat
	 * @param sLon
	 * @return
	 */
	public static RequestType getType(String sQry, String type, String sLat, String sLon) {
		if(sQry != null && !sQry.isEmpty() && type.equals("query"))
			return RequestType.STRING_SEARCH;
    	else if(sLat != null && !sLat.isEmpty() && sLon != null && !sLon.isEmpty() && type.equals("gps"))
    		return RequestType.GEO_SEARCH;
    	else
    		return RequestType.INDEX;
	}
	
	/**
	 * Determine the Categories as List by an Array
	 * @param queryString
	 * @return
	 */
	public static List<String> getCategories(Map<String,String[]> queryString) {
		ArrayList<String> categoryList = new ArrayList<String>();

        if(queryString.containsKey("categories") && queryString.get("categories") != null && queryString.get("categories").length != 0){
            String[] categories = queryString.get("categories");

            for(String s: categories){
                String[] result = s.split(",");

                for(String r: result){
                    categoryList.add(r);
                }
            }
        }
        return categoryList;
	}
	
	

}
