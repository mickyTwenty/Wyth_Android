package com.seatus.Utils.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;


import com.seatus.Models.StateItem;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by shakil on 9/27/2017.
 */

@Dao
public interface StateDoa {

    @Query("SELECT COUNT(*) FROM StateItem")
    Integer getTotalCount();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertAll(ArrayList<StateItem> countryItems);

    @Query("SELECT * FROM StateItem")
    List<StateItem> getAll();

    @Query("SELECT * FROM StateItem WHERE country_id = :country_id")
    List<StateItem> getAllStateOnCountry(String country_id);

}
