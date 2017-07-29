package ajayverma26.com.ajaylbrfordelete;

import java.io.Serializable;

/**
 * Created by ajay on 24/6/17.
 */

public class Reminder implements Serializable {

    private int id;
    private String title;
    private String address;
    private String type;
    private double radius;
    private double lat,lng;

    public Reminder(){

    }

    public Reminder(String title, String type,double lat, double lng,double radius){

        this.title = title;
        this.type = type;
        this.lat = lat;
        this.lng = lng;
        this.radius = radius;
    }

    public Reminder(int id,String title, String type,double lat, double lng,double radius){

        this.id = id;
        this.title = title;
        this.type = type;
        this.lat = lat;
        this.lng = lng;
        this.radius = radius;
    }
    /*public Reminder(int id, String title, String address, String type, int radius, double lat, double lng) {
        this.id = id;
        this.title = title;
        this.address = address;
        this.type = type;
        this.radius = radius;
        this.lat = lat;
        this.lng = lng;
    }*/

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }
}

