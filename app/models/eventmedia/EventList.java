package models.eventmedia;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
import models.geo.Location;

public class EventList {

	private ArrayList<Event> eventlist;

    public EventList(){
    	eventlist = new ArrayList<Event>();
    }

    public String toString(){
        String string = "";
        for(Event e: eventlist){
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

    public void add(Event e){
        eventlist.add(e);
    }

    public ArrayList<Event> get() {
        return eventlist;
    }

    public ArrayList<String> getAllAddresses(){
        ArrayList<String> addresses = new ArrayList<String>();
        for(Event e: eventlist){
            addresses.add(e.getAddress());
        }

        return addresses;
    }

    public HashMap<String,String> getAddressHashmap(){
        HashMap<String,String> events = new HashMap<String,String>();
        for(Event e: eventlist){
            events.put(e.getTitle(),e.getAddress());
        }

        return events;
    }

    public ArrayList<Location> getAddressArrayList(){
        ArrayList<Location> events = new ArrayList<Location>();
        for(Event e: eventlist){
            Location l = new Location();
            l.setName(e.getTitle());
            l.setAddress(e.getAddress());
            events.add(l);
        }

        return events;
    }

}
