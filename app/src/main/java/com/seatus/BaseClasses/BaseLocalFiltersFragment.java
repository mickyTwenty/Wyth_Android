package com.seatus.BaseClasses;

import android.content.Intent;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.seatus.Models.MatrixApiResponse;
import com.seatus.Models.SearchFilterDataItem;
import com.seatus.R;
import com.seatus.Retrofit.WebServiceFactory;
import com.seatus.Utils.AppConstants;
import com.seatus.Utils.PreferencesManager;
import com.seatus.Utils.StaticMethods;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

/**
 * Created by rah on 20-Nov-17.
 */

public abstract class BaseLocalFiltersFragment extends BaseFragment {


    public SearchFilterDataItem tempItem;
    public boolean isUpdated = false;

    public SearchFilterDataItem getTempItem() {
        if (tempItem == null || isUpdated) {
            tempItem = PreferencesManager.getObject(AppConstants.KEY_TEMP_FILTERS, SearchFilterDataItem.class);
            isUpdated = false;
            if (tempItem == null)
                tempItem = new SearchFilterDataItem();
        }
        return tempItem;
    }

    public void updateTempItem() {
        isUpdated = true;
        PreferencesManager.putObject(AppConstants.KEY_TEMP_FILTERS, tempItem);
    }


    public void clearTempCache() {
        getTempItem().time_range = 0;
        getTempItem().return_time_range = 0;
        getTempItem().seats = 1;
        getTempItem().return_seats = 1;
        getTempItem().preferences = null;
        getTempItem().gender = "Both";
        updateTempItem();
    }

    public void showAllSearch() {
        getTempItem().time_range = 7;
        getTempItem().return_time_range = 7;
        getTempItem().seats = 1;
        getTempItem().return_seats = 1;
        getTempItem().preferences = null;
        getTempItem().gender = "Both";
        updateTempItem();
    }

    public boolean areInitialFieldsValid(boolean isCreate) {

        boolean valid = true;
        StringBuilder emptyFields = new StringBuilder();

        if (getTempItem().origin_geo == null) {
            emptyFields.append("Origin");
            valid = false;
        }

        if (getTempItem().destination_geo == null) {
            if (emptyFields.length() == 0)
                emptyFields.append("Destination");
            else
                emptyFields.append(", ").append("Destination");
            valid = false;
        }

        /*
        try {
            if (getTempItem().origin_geo.equals(getTempItem().destination_geo)) {
                makeSnackbar("Origin and Destination must be different");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        */

        if (getTempItem().getDate() == null) {
            if (emptyFields.length() == 0)
                emptyFields.append("Date");
            else
                emptyFields.append(", ").append("Date");
            valid = false;
        } else {
            Calendar tempTime = Calendar.getInstance();
            tempTime.setTimeInMillis(getTempItem().date);
            tempTime.set(Calendar.MINUTE, 30);

            Calendar currTime = Calendar.getInstance();
            currTime.set(Calendar.SECOND, 1);
            currTime.set(Calendar.HOUR_OF_DAY, 0);
            currTime.set(Calendar.MINUTE, 0);

            if (tempTime.before(currTime)) {
                if (emptyFields.length() == 0)
                    emptyFields.append("Future Date");
                else
                    emptyFields.append(", ").append("Future Date");
                valid = false;
            }
        }

        if (getTempItem().isRoundTrip == 1) {
            if (getTempItem().getReturnDate() == null) {
                if (emptyFields.length() == 0)
                    emptyFields.append("Return Date");
                else
                    emptyFields.append(", ").append("Return Date");
                valid = false;
            } else {
                Calendar tempTime = Calendar.getInstance();
                tempTime.setTimeInMillis(getTempItem().returnDate);
                tempTime.set(Calendar.MINUTE, 30);

                Calendar goingDate = Calendar.getInstance();
                goingDate.setTimeInMillis(getTempItem().date);
                goingDate.set(Calendar.SECOND, 1);
                goingDate.set(Calendar.HOUR_OF_DAY, 0);
                goingDate.set(Calendar.MINUTE, 0);

                if (tempTime.before(goingDate)) {

                    // Set Time to current time if before
                    getTempItem().setDate(tempTime.getTime());
                    updateTempItem();

                    //if (emptyFields.length() == 0)
                    //    emptyFields.append("Valid Return Date");
                    //else
                    //    emptyFields.append(", ").append("Valid Return Date");
                    //valid = false;
                }
            }
        }

        if (getUserItem().getCurrentInterface() && isCreate) {
            if (getTempItem().selectedRoute == null) {
                if (emptyFields.length() == 0)
                    emptyFields.append("Valid Origin, Destination");
                else
                    emptyFields.append(", ").append("Valid Origin, Destination");
                valid = false;
            }
        }

        if (!valid) {
            emptyFields.insert(0, "Please Select ");
            emptyFields.append(" before proceeding");
            makeSnackbar(emptyFields.toString());
        }

        return valid;
    }


    public boolean areFieldsValid(boolean checkSeats) {

        boolean valid = true;
        StringBuilder emptyFields = new StringBuilder();

        if (getTempItem().origin_geo == null) {
            emptyFields.append("Origin");
            valid = false;
        }

        if (getTempItem().destination_geo == null) {
            if (emptyFields.length() == 0)
                emptyFields.append("Destination");
            else
                emptyFields.append(", ").append("Destination");
            valid = false;
        }

        try {
            if (getTempItem().origin_geo.equals(getTempItem().destination_geo)) {
                makeSnackbar("Origin and Destination must be different");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (getTempItem().getDate() == null) {
            if (emptyFields.length() == 0)
                emptyFields.append("Date");
            else
                emptyFields.append(", ").append("Date");
            valid = false;
        } else {
            Calendar tempTime = Calendar.getInstance();
            tempTime.setTimeInMillis(getTempItem().date);
            tempTime.set(Calendar.MINUTE, 30);

            Calendar currTime = Calendar.getInstance();
            currTime.set(Calendar.HOUR_OF_DAY, 0);
            currTime.set(Calendar.MINUTE, 0);
            currTime.set(Calendar.SECOND, 1);
            currTime.set(Calendar.MILLISECOND, 0);

            if (tempTime.before(currTime)) {
                if (emptyFields.length() == 0)
                    emptyFields.append("Future Date");
                else
                    emptyFields.append(", ").append("Future Date");
                valid = false;
            }

            tempTime.set(Calendar.HOUR_OF_DAY, 0);
            tempTime.set(Calendar.MINUTE, 0);
            tempTime.set(Calendar.SECOND, 1);
            tempTime.set(Calendar.MILLISECOND, 0);

            if (tempTime.compareTo(currTime) == 0) {
                currTime = Calendar.getInstance();
                int hour = currTime.get(Calendar.HOUR_OF_DAY);

                boolean timePassed = false;

                if ((getTempItem().time_range & 1) != 0)
                    if (hour > 11)
                        getTempItem().time_range -= 1;
                if ((getTempItem().time_range & 2) != 0)
                    if (hour > 17)
                        getTempItem().time_range -= 2;

                if (timePassed) {
                    //makeSnackbar("The selected time of the day has passed.  Please select an another time of the day.");
                    //return false;
                    updateTempItem();
                }
            }
        }

        if (getTempItem().time_range == 0) {
            if (emptyFields.length() == 0)
                emptyFields.append("Time Of Day");
            else
                emptyFields.append(", ").append("Time Of Day");
            valid = false;
        }


        if (getTempItem().isRoundTrip == 1) {
            if (tempItem.return_time_range == 0) {
                if (emptyFields.length() == 0)
                    emptyFields.append("Return Time Of Day");
                else
                    emptyFields.append(", ").append("Return Time Of Day");
                valid = false;
            }
        }

        if (checkSeats && getTempItem().seats == 0) {
            if (emptyFields.length() == 0)
                emptyFields.append("No of Seats");
            else
                emptyFields.append(", ").append("No of Seats");
            valid = false;
        }

        if (getTempItem().isRoundTrip == 1) {
            if (checkSeats && tempItem.return_seats == 0) {
                if (emptyFields.length() == 0)
                    emptyFields.append("Return No of Seats");
                else
                    emptyFields.append(", ").append("Return No of Seats");
                valid = false;
            }
        }

        if (!valid) {
            emptyFields.insert(0, "Please Select ");
            emptyFields.append(" before proceeding");
            makeSnackbar(emptyFields.toString());
        }

//        if (txtDate.getTag() == null) {
//            if (emptyFields.length() == 0)
//                emptyFields.append("Date");
//            else
//                emptyFields.append(", ").append("Date");
//            valid = false;
//        }
        return valid;
    }

    public boolean areFieldsValid(SearchFilterDataItem tempItem, boolean checkSeats) {

        boolean valid = true;
        StringBuilder emptyFields = new StringBuilder();

        if (tempItem.origin_geo == null) {
            emptyFields.append("Origin");
            valid = false;
        }

        if (tempItem.destination_geo == null) {
            if (emptyFields.length() == 0)
                emptyFields.append("Destination");
            else
                emptyFields.append(", ").append("Destination");
            valid = false;
        }

        try {
            if (tempItem.origin_geo.equals(tempItem.destination_geo)) {
                makeSnackbar("Origin and Destination must be different");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (tempItem.getDate() == null) {
            if (emptyFields.length() == 0)
                emptyFields.append("Date");
            else
                emptyFields.append(", ").append("Date");
            valid = false;
        } else {
            Calendar tempTime = Calendar.getInstance();
            tempTime.setTimeInMillis(tempItem.date);
            tempTime.set(Calendar.MINUTE, 30);

            Calendar currTime = Calendar.getInstance();
            currTime.set(Calendar.SECOND, 1);
            currTime.set(Calendar.HOUR, 0);
            currTime.set(Calendar.MINUTE, 0);

            if (tempTime.before(currTime)) {
                if (emptyFields.length() == 0)
                    emptyFields.append("Future Date");
                else
                    emptyFields.append(", ").append("Future Date");
                valid = false;
            }
        }

        if (tempItem.time_range == 0) {
            if (emptyFields.length() == 0)
                emptyFields.append("Time Of Day");
            else
                emptyFields.append(", ").append("Time Of Day");
            valid = false;
        }

        if (getTempItem().isRoundTrip == 1) {
            if (tempItem.return_time_range == 0) {
                if (emptyFields.length() == 0)
                    emptyFields.append("Return Time Of Day");
                else
                    emptyFields.append(", ").append("Return Time Of Day");
                valid = false;
            }
        }

        if (checkSeats && tempItem.seats == 0) {
            if (emptyFields.length() == 0)
                emptyFields.append("No of Seats");
            else
                emptyFields.append(", ").append("No of Seats");
            valid = false;
        }

        if (getTempItem().isRoundTrip == 1) {
            if (checkSeats && tempItem.return_seats == 0) {
                if (emptyFields.length() == 0)
                    emptyFields.append("Return No of Seats");
                else
                    emptyFields.append(", ").append("Return No of Seats");
                valid = false;
            }
        }

        if (!valid) {
            emptyFields.insert(0, "Please Select ");
            emptyFields.append(" before proceeding");
            makeSnackbar(emptyFields.toString());
        }

        return valid;
    }

}
