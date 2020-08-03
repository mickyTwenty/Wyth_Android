package com.seatus.Models;

import android.support.annotation.Nullable;

import com.google.firebase.firestore.FieldValue;
import com.seatus.Utils.AppConstants;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rah on 14-Nov-17.
 */

public class FireStoreUserDocument {

    public Date last_req_edited;
    public String invite_type;
    public int unread_chats;
    public int unread_notifications;
    public UserItem invited_driver;


    public FireStoreUserDocument() {
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("last_req_edited", FieldValue.serverTimestamp());
        return map;
    }

    public boolean isExpired() {
        if (last_req_edited == null)
            return false;
        Calendar currTime = Calendar.getInstance();
        currTime.add(Calendar.MINUTE, -30);
        return last_req_edited.before(currTime.getTime());
    }
}