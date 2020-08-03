package com.seatus.Models;

import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;


/**
 * Created by rohail on 01-Mar-17.
 */

public class BaseLocationItem {

    @NonNull
    @PrimaryKey
    public String id;

    public String name;

    public BaseLocationItem() {
    }

    public BaseLocationItem(@NonNull String id, String name) {
        this.id = id;
        this.name = name;
    }
}
