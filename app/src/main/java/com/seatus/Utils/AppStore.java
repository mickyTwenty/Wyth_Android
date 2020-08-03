package com.seatus.Utils;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.location.Location;

import com.seatus.Models.BootMeUpResponse;
import com.seatus.Models.NotifObject;
import com.seatus.Models.SearchPreferenceItem;
import com.seatus.Models.UserItem;
import com.seatus.Models.SchoolItem;
import com.seatus.Utils.database.AppDatabase;

import java.util.ArrayList;

/**
 * Created by rohail on 30-Jan-17.
 */
public class AppStore {

    private static AppStore ourInstance;

    public static AppStore getInstance() {
        if (ourInstance == null)
            ourInstance = new AppStore();
        return ourInstance;
    }

    public static void clearInstance() {
        ourInstance = null;
    }

    private AppStore() {
    }


    // Singleton Store Logic End

    UserItem tmpRegisterItem;


    public UserItem getTmpRegisterItem() {
        if (tmpRegisterItem == null)
            tmpRegisterItem = new UserItem();
        return tmpRegisterItem;
    }

    public void setTmpRegisterItem(UserItem tmpRegisterItem) {
        this.tmpRegisterItem = tmpRegisterItem;
    }

    BootMeUpResponse bootUpData;

    public ArrayList<SearchPreferenceItem> getPrefsList() {

        try {
            if (bootUpData == null)
                bootUpData = PreferencesManager.getObject(AppConstants.BOOT_ME_UP, BootMeUpResponse.class);

            return bootUpData.preferences;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public BootMeUpResponse getBootUpData() {
        try {
            if (bootUpData == null)
                bootUpData = PreferencesManager.getObject(AppConstants.BOOT_ME_UP, BootMeUpResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bootUpData;
    }

    public void setBootUpData(BootMeUpResponse bootUpData) {
        PreferencesManager.putObject(AppConstants.BOOT_ME_UP, bootUpData);
        this.bootUpData = bootUpData;
    }

    ArrayList<String> vehicleTypeList;

    public ArrayList<String> getVehicleTypeList() {

        try {
            if (vehicleTypeList == null)
                vehicleTypeList = PreferencesManager.getObject(AppConstants.BOOT_ME_UP, BootMeUpResponse.class).vehicle_type;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return vehicleTypeList;
    }

    ArrayList<SchoolItem> schoolsList;

    public ArrayList<SchoolItem> getSchoolsList(Context context) {

        try {
            if (schoolsList == null) {
                schoolsList = new ArrayList<>();
                schoolsList.addAll(AppDatabase.getInstance(context).schoolsDao().getAll());
            }
            return schoolsList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    MutableLiveData<NotifObject> notificationLiveData;

    public MutableLiveData<NotifObject> getNotificationItem() {
        if (notificationLiveData == null)
            notificationLiveData = new MutableLiveData<>();

        return notificationLiveData;
    }


    MutableLiveData<Location> LocationLiveData;

    public MutableLiveData<Location> getLocationLiveData() {
        if (LocationLiveData == null)
            LocationLiveData = new MutableLiveData<>();

        return LocationLiveData;
    }
}
