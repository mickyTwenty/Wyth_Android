package com.seatus.Utils.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.seatus.Models.CityItem;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by shakil on 10/13/2017.
 */
@Dao
public interface CityDoa {

    @Query("SELECT COUNT(*) FROM CityItem")
    Integer getTotalCount();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertAll(ArrayList<CityItem> cityItems);

    @Query("SELECT * FROM CityItem")
    List<CityItem> getAll();

    @Query("SELECT * FROM CityItem WHERE state_id = :state_id")
    List<CityItem> getAllCityByStateID(String state_id);

}
