package controllers;

import models.geonames.GeoNamesRecommendation;
import models.geonames.struct.POIList;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import com.fasterxml.jackson.databind.JsonNode;

public class Recommendation extends Controller {

	@BodyParser.Of(BodyParser.Json.class)
	public static Result index() {
		try {
			// retrieve request
			JsonNode json = request().body().asJson();
			
			// get ressource-uri
			String resource = json.findPath("name").textValue();	
			
			// Workaroudn: deal with the doubles quotes from json string
			resource=resource.substring(1, resource.length()-1);

			// retrieve POI
			POIList list = GeoNamesRecommendation.retrieveRecommendedData(resource);
			
			// serve
			return ok(list.getJSON());
			
		} catch (Exception e) {
			return badRequest(e.getMessage());
		}
	}

}
