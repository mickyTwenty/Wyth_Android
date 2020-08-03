package com.seatus.Utils.database;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.seatus.Models.CityItem;
import com.seatus.Models.UserItem;
import com.seatus.Retrofit.WebResponse;
import com.seatus.ViewModels.Resource;

import java.util.ArrayList;
import java.util.List;

import retrofit2.http.DELETE;


/**
 * Created by shakil on 10/13/2017.
 */
@Dao
public interface FriendsDao {

    @Query("SELECT COUNT(*) FROM UserItem")
    int getTotalCount();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertAll(ArrayList<UserItem> friends);

    @Insert
    public void insert(UserItem friend);

    @Query("SELECT * FROM UserItem WHERE is_third_party=0")
    LiveData<List<UserItem>> getAllasLiveData();

    @Query("SELECT * FROM UserItem  WHERE is_third_party=0")
    List<UserItem> getAll();

    @Query("SELECT * FROM UserItem")
    List<UserItem> getAllPlusThirdParty();

    @Query("SELECT * FROM UserItem WHERE role_id='driver' AND is_third_party=0")
    List<UserItem> getAllDrivers();

    //Recent Search Stuff
    @Query("SELECT * FROM UserItem WHERE share_count > 0 ORDER BY share_count DESC LIMIT 5")
    List<UserItem> getRecentSearch();

    @Query("UPDATE UserItem SET share_count=share_count+1 WHERE user_id=:userID")
    void setRecentSearch(String userID);

    @Delete
    public void delete(UserItem friend);

    @Query("DELETE from UserItem")
    public void deleteAll();

    @Query("SELECT * FROM UserItem  WHERE phone=:phone OR email=:email")
    UserItem hasLikeUser(String email, String phone);
}
