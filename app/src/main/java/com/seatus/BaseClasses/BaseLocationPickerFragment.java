package com.seatus.BaseClasses;

import android.content.Intent;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.seatus.Models.BootMeUpResponse;
import com.seatus.Models.MatrixApiResponse;
import com.seatus.Models.SearchFilterDataItem;
import com.seatus.R;
import com.seatus.Retrofit.WebServiceFactory;
import com.seatus.Utils.AppConstants;
import com.seatus.Utils.AppStore;
import com.seatus.Utils.StaticMethods;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

/**
 * Created by rohail on 3/22/2018.
 */

public abstract class BaseLocationPickerFragment extends BaseLocalFiltersFragment {

    private final int ORIGIN_REQUEST = 123;
    private final int DESTINATION_REQUEST = 124;

    public void pickOrigin() {
        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            startActivityForResult(builder.build(getContext()), ORIGIN_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pickDestination() {
        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            startActivityForResult(builder.build(getContext()), DESTINATION_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == ORIGIN_REQUEST) {
                Place place = PlacePicker.getPlace(getContext(), data);
                String txt = place.getAddress().toString();
                String tag = String.format("%s,%s", place.getLatLng().latitude, place.getLatLng().longitude);
                getTempItem().origin_text = txt;
                getTempItem().origin_geo = tag;
                onLocationSelected(true, txt, tag);
                getEstimation();
            } else if (requestCode == DESTINATION_REQUEST) {
                Place place = PlacePicker.getPlace(getContext(), data);
                String txt = place.getAddress().toString();
                String tag = String.format("%s,%s", place.getLatLng().latitude, place.getLatLng().longitude);
                getTempItem().destination_text = txt;
                getTempItem().destination_geo = tag;
                onLocationSelected(false, txt, tag);
                getEstimation();
            }
            updateTempItem();
        }
    }


    protected abstract void onLocationSelected(boolean isOrigin, String address, String geo);

    public void getEstimation() {

        if (getTempItem().origin_geo != null && getTempItem().destination_geo != null) {

            WebServiceFactory.getInstance().distanceMatrixApi(getString(R.string.google_places_key), getTempItem().origin_geo, getTempItem().destination_geo).enqueue(new Callback<MatrixApiResponse>() {
                @Override
                public void onResponse(Call<MatrixApiResponse> call, Response<MatrixApiResponse> response) {
                    if (response.isSuccessful() && response.body().Status.equals("OK")) {
                        try {

                            StaticMethods.Estimate lowerEstimate;
                            StaticMethods.Estimate upperEstimate;
                            BootMeUpResponse bootmeupdata = AppStore.getInstance().getBootUpData();
                            if (bootmeupdata == null) {
                                lowerEstimate = StaticMethods.getEstimate(response.body().Rows.get(0).Elements.get(0).Distance.Value, AppConstants.RATE_PER_MILE / 2);
                                upperEstimate = StaticMethods.getEstimate(response.body().Rows.get(0).Elements.get(0).Distance.Value, AppConstants.RATE_PER_MILE);
                            } else {
                                lowerEstimate = StaticMethods.getEstimate(response.body().Rows.get(0).Elements.get(0).Distance.Value, bootmeupdata.min_estimate);
                                upperEstimate = StaticMethods.getEstimate(response.body().Rows.get(0).Elements.get(0).Distance.Value, bootmeupdata.max_estimate);
                            }


                            SearchFilterDataItem tempItem = getTempItem();
//                            tempItem.estimate_format = lowerEstimate.text + upperEstimate.text;
                            tempItem.estimate_high = upperEstimate.value;
                            tempItem.estimate_low = lowerEstimate.value;
                            updateTempItem();
                            estimateFound(getTempItem().estimate_low, getTempItem().estimate_high);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<MatrixApiResponse> call, Throwable t) {
                }
            });
        }
    }

    protected abstract void estimateFound(double lowerEstimate, double upperEstimate);
}
