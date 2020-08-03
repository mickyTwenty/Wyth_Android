package com.seatus.Models;

/**
 * Created by saqib on 1/29/2018.
 */

public class NotificationListingItem {

    public Payload payload;
    public String unix_timestamp;

    public class Payload{
        public NotifObject data;
    }
}
