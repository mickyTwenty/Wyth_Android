package com.seatus.Utils.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.seatus.Models.SchoolItem;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by shakil on 10/13/2017.
 */
@Dao
public interface SchoolsDao {

    @Query("SELECT COUNT(*) FROM SchoolItem")
    int getTotalCount();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertAll(ArrayList<SchoolItem> friends);

    @Insert
    public void insert(SchoolItem friend);

    @Query("SELECT * FROM SchoolItem")
    List<SchoolItem> getAll();

    @Delete
    public void delete(SchoolItem friend);

    @Query("DELETE from SchoolItem")
    public void deleteAll();
}
