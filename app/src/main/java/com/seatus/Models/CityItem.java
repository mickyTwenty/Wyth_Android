package com.seatus.Models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by shakil on 10/13/2017.
 */


@Entity
public class CityItem extends BaseLocationItem {
    public String state_id;
    public String state_name;


    public CityItem(@NonNull String id, String name) {
        super(id, name);
    }
}
