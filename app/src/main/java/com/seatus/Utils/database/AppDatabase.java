package com.seatus.Utils.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.seatus.BuildConfig;
import com.seatus.Models.CityItem;
import com.seatus.Models.SchoolItem;
import com.seatus.Models.StateItem;
import com.seatus.Models.UserItem;


/**
 */

@Database(entities = {CityItem.class, StateItem.class, UserItem.class, SchoolItem.class}, version = BuildConfig.VERSION_CODE, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "seatus.db")
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract StateDoa stateDoa();

    public abstract CityDoa cityDoa();

    public abstract FriendsDao friendsDao();

    public abstract SchoolsDao schoolsDao();


}
