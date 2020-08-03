package com.seatus.Models;

/**
 * Created by saqib on 11/21/2017.
 */

public class RideSeatItem {
    public String status;
    public String passenger_name;
    public String passenger_status;
    public int status_image;
    public int passenger_image;

    public RideSeatItem(int status_image,String status, int passenger_image,String passenger_status,String passenger_name){
        this.status = status;
        this.passenger_name = passenger_name;
        this.passenger_status = passenger_status;
        this.status_image = status_image;
        this.passenger_image = passenger_image;
    }
}
