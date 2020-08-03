package com.seatus.Models;

import com.google.firebase.firestore.FieldValue;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by saqib on 12/7/2017.
 */

public class ChatItem {

    public String first_name;
    public String last_name;
    public String message_id;
    public String message_text;
    public String user_id;
    public Date timestamp;


    public Map<String,Object> toMap(){
        Map<String,Object> map = new HashMap<>();
        map.put("first_name",first_name);
        map.put("last_name",last_name);
        map.put("user_id",user_id);
        map.put("message_id",message_id);
        map.put("message_text",message_text);
        map.put("timestamp", FieldValue.serverTimestamp());
        return map;
    }
}
