package models.geonames.struct;

import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by martin on 04.10.14.
 */
public class POIList {

    private ArrayList<POI> listPOIs;

    public POIList(){
        listPOIs = new ArrayList<POI>();
    }

    public String toString(){
        String string = "";
        for(POI p: listPOIs){
            string = string + "POI: " + p.getResource() + " " + p.getLabel() + " " + p.getLat() + " " + p.getLon() + "\n";
        }
        return string;
    }

    public String getJSON(){
        Gson gson = new Gson();

        return gson.toJson(this);
    }

    public int size(){
        return listPOIs.size();
    }

    public void add(POI poi){
        listPOIs.add(poi);
    }

    public ArrayList<POI> get() {
        return listPOIs;
    }
}
