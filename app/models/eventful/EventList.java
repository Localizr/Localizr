package models.eventful;

import com.google.gson.Gson;
import models.eventful.Event;
import models.geo.Location;

import java.util.ArrayList;
import java.util.HashMap;

public class EventList {

	private ArrayList<models.eventful.Event> eventlist;

    public EventList(){
    	eventlist = new ArrayList<models.eventful.Event>();
    }

    public String toString(){
        String string = "";
        for(models.eventful.Event e: eventlist){
            string = string + "EVENTS: " + e.getTitle() + "\n";
        }
        return string;
    }

    public String getJSON(){
        Gson gson = new Gson();

        return gson.toJson(this);
    }

    public int size(){
        return eventlist.size();
    }

    public void add(models.eventful.Event e){
        eventlist.add(e);
    }

    public ArrayList<models.eventful.Event> get() {
        return eventlist;
    }

    public ArrayList<String> getAllAddresses(){
        ArrayList<String> addresses = new ArrayList<String>();
        for(models.eventful.Event e: eventlist){
            addresses.add(e.getVenue_address());
        }

        return addresses;
    }

    public HashMap<String,String> getAddressHashmap(){
        HashMap<String,String> events = new HashMap<String,String>();
        for(models.eventful.Event e: eventlist){
            events.put(e.getTitle(),e.getVenue_address());
        }

        return events;
    }

    public ArrayList<Location> getAddressArrayList(){
        ArrayList<Location> events = new ArrayList<Location>();
        for(Event e: eventlist){
            Location l = new Location();
            l.setName(e.getTitle());
            l.setSimpleName(e.getVenue_address());
            l.setLat(e.getLatitude());
            l.setLng(e.getLongitude());
            events.add(l);
        }

        return events;
    }

}
