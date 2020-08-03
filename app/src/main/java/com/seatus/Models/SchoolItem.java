package com.seatus.Models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by rohail on 2/28/2018.
 */

@Entity
public class SchoolItem {

    @NonNull
    @PrimaryKey
    public String school;
    public String state;
    public String city;
}
