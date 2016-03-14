package models.eventful;

import com.fasterxml.jackson.databind.JsonNode;
import models.geo.Location;
import play.Logger.ALogger;
import play.cache.Cache;
import play.libs.F;
import play.libs.F.Promise;
import play.libs.ws.WS;

import java.text.SimpleDateFormat;
import java.util.Iterator;

public class EventfulAPI {
	
	private static final ALogger logger = play.Logger.of(EventfulAPI.class);
    private final static String API_KEY = "";
	/**
	 * Search for events on eventful
	 * @param loc
	 * @return
	 */
	public static Promise<EventList> search4Events(Location loc){
		// Create URL
		String sURL = "http://api.eventful.com/json/events/search?app_key=API_KEY&date=Future&units=km&sort_order=date&where=LAT,LNG&within=15";

        sURL = sURL.replace("API_KEY", API_KEY);
        sURL = sURL.replace("LAT", String.valueOf(loc.getLat()));
        sURL = sURL.replace("LNG", String.valueOf(loc.getLng()));

        logger.debug(sURL);

		// make async web-request
		Promise<EventList> eventListPromise = WS.url(sURL).get().map(response -> {
			// result is json
			JsonNode jsonNode = response.asJson();

			// init event-list
            EventList eventList = new EventList();

			// Get iterator for events
			Iterator<JsonNode> itNode = jsonNode.findValue("events").findValue("event").elements();
			// while there are more events!
			while(itNode.hasNext()){
				JsonNode oneEvent = itNode.next();

				// add event to list
                Event event = new Event();
                event.setId(oneEvent.get("id").asText());
                event.setTitle(oneEvent.get("title").asText());
                event.setUrl(oneEvent.get("url").asText());
                event.setDescription(oneEvent.get("description").asText());
                event.setAll_day(oneEvent.get("all_day").asInt());

                SimpleDateFormat dateFormat;
                if(event.getAll_day() == 1 || event.getAll_day() == 2){ // As stated in the Eventful documentation 1 or 2 means either it is an all day event or the time is missing
                    dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                }else{
                   dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                }

                if(!oneEvent.get("start_time").isNull() && !oneEvent.get("start_time").asText().equals("")){
                    event.setStartTime(dateFormat.parse(oneEvent.get("start_time").asText()));
                }else{
                    event.setStartTime(null);
                }

                if(!oneEvent.get("stop_time").isNull() && !oneEvent.get("stop_time").asText().equals("")){
                    event.setStopTime(dateFormat.parse(oneEvent.get("stop_time").asText()));
                }else{
                    event.setStopTime(null);
                }

                event.setVenue_id(oneEvent.get("venue_id").asText());
                event.setVenue_url(oneEvent.get("venue_url").asText());
                event.setVenue_name(oneEvent.get("venue_name").asText());
                event.setVenue_address(oneEvent.get("venue_address").asText());

                if(!oneEvent.get("image").isNull() && !oneEvent.findValue("image").findValue("medium").get("url").isNull() && !oneEvent.findValue("image").findValue("medium").get("url").asText().equals("")){
                    event.setImage_url(oneEvent.findValue("image").findValue("medium").get("url").asText());
                }else{
                    event.setImage_url(null);
                }

                event.setLatitude(oneEvent.get("latitude").asDouble());
                event.setLongitude(oneEvent.get("longitude").asDouble());

                eventList.add(event);
			}
			return eventList;
		});
		return eventListPromise;
	}

    public static F.Promise<models.eventful.EventList> getCachedEvents(Location loc) {
        // Build Caching-Key
        String sKey = "events-"+loc.getLat()+loc.getLng();
        @SuppressWarnings("unchecked")
        // Is the result already in the Cache?
                F.Promise<models.eventful.EventList> eventList = (Promise<models.eventful.EventList>) Cache.get(sKey);
        // No? -> Retrieve it
        if(eventList == null) eventList = EventfulAPI.search4Events(loc);
        // Put it into the cache
        Cache.set(sKey, eventList);
        return eventList;
    }
	
}
