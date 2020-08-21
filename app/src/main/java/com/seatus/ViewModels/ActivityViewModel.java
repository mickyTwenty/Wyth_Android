package com.seatus.ViewModels;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.akexorcist.googledirection.model.Route;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.seatus.Holders.BankItem;
import com.seatus.Models.BootMeUpResponse;
import com.seatus.Models.CardItem;
import com.seatus.Models.NotificationListingItem;
import com.seatus.Models.PendingRatingItem;
import com.seatus.Models.PopularityResponse;
import com.seatus.Models.SearchPreferenceItem;
import com.seatus.Models.SessionExpireEvent;
import com.seatus.Models.TripItem;
import com.seatus.Models.TripSearchWrapper;
import com.seatus.Models.UserItem;
import com.seatus.Retrofit.WebResponse;
import com.seatus.Retrofit.WebServiceFactory;
import com.seatus.Utils.AppConstants;
import com.seatus.Utils.AppStore;
import com.seatus.Utils.BusProvider;
import com.seatus.Utils.PreferencesManager;
import com.seatus.Utils.StaticAppMethods;
import com.seatus.Utils.StaticMethods;
import com.seatus.Utils.database.AppDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by rohail on 26-Oct-17.
 */

public class ActivityViewModel extends AndroidViewModel {


    private AppDatabase roomDb;
    private MutableLiveData<UserItem> userItem;
    MutableLiveData<Resource<WebResponse<ArrayList<UserItem>>>> friendMutableData = new MutableLiveData<>();

    public MutableLiveData<ArrayList<UserItem>> invitedFriendList;

    public ActivityViewModel(@NonNull Application application) {
        super(application);
    }


    public LiveData<Resource<WebResponse<ArrayList<UserItem>>>> getFriendsApi() {

        WebServiceFactory.getInstance().getFriends(getUserItem().token, null).enqueue(new Callback<WebResponse<ArrayList<UserItem>>>() {
            @Override
            public void onResponse(Call<WebResponse<ArrayList<UserItem>>> call, Response<WebResponse<ArrayList<UserItem>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess())
                        friendMutableData.setValue(Resource.response(Resource.Status.success, response.body()));
                    else if (response.body().isExpired())
                        BusProvider.getInstance().post(new SessionExpireEvent());
                    else
                        friendMutableData.setValue(Resource.response(Resource.Status.error, response.body()));
                } else
                    friendMutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }

            @Override
            public void onFailure(Call<WebResponse<ArrayList<UserItem>>> call, Throwable t) {
                friendMutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }
        });
        return friendMutableData;
    }


    public LiveData<Resource<WebResponse>> resendVerificationCode(String email) {

        if(email.isEmpty())
            email = PreferencesManager.getString("user");

        MutableLiveData<Resource<WebResponse>> mutableData = new MutableLiveData<>();
        mutableData.setValue(Resource.loading());
        WebServiceFactory.getInstance().resendVerificationCode(email).enqueue(new Callback<WebResponse>() {
            @Override
            public void onResponse(Call<WebResponse> call, Response<WebResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess())
                        mutableData.setValue(Resource.response(Resource.Status.success, response.body()));
                    else if (response.body().isExpired())
                        BusProvider.getInstance().post(new SessionExpireEvent());
                    else
                        mutableData.setValue(Resource.response(Resource.Status.error, response.body()));
                } else
                    mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }

            @Override
            public void onFailure(Call<WebResponse> call, Throwable t) {
                mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }
        });
        return mutableData;
    }

    public LiveData<Resource> refreshFriendDataRoom() {

        MutableLiveData<Resource> mutableLiveData = new MutableLiveData<>();

        WebServiceFactory.getInstance().getFriends(getUserItem().token, null).enqueue(new Callback<WebResponse<ArrayList<UserItem>>>() {
            @Override
            public void onResponse(Call<WebResponse<ArrayList<UserItem>>> call, Response<WebResponse<ArrayList<UserItem>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        getRoomDb().friendsDao().insertAll(response.body().body);
                        mutableLiveData.setValue(Resource.response(Resource.Status.success, null));
                    } else if (response.body().isExpired())
                        BusProvider.getInstance().post(new SessionExpireEvent());
                    else
                        mutableLiveData.setValue(Resource.response(Resource.Status.error, response.body()));
                } else
                    mutableLiveData.setValue(Resource.response(Resource.Status.connection_error, null));
            }

            @Override
            public void onFailure(Call<WebResponse<ArrayList<UserItem>>> call, Throwable t) {
                mutableLiveData.setValue(Resource.response(Resource.Status.connection_error, null));
            }
        });
        return mutableLiveData;
    }

    public MediatorLiveData<Resource<List<UserItem>>> getFriendsDataBothSources() {
        MediatorLiveData<Resource<List<UserItem>>> liveDataMerger = new MediatorLiveData<>();
        liveDataMerger.setValue(Resource.loading());

        LiveData<List<UserItem>> liveDataRoom = getRoomDb().friendsDao().getAllasLiveData();
        LiveData<Resource<WebResponse<ArrayList<UserItem>>>> retrofitData = getFriendsApi();

        liveDataMerger.addSource(liveDataRoom, value -> {
            if (value != null && !value.isEmpty())
                liveDataMerger.setValue(Resource.response(Resource.Status.success, value));
        });
        liveDataMerger.addSource(retrofitData, responseResource -> {
            switch (responseResource.status) {
                case success:
                    if (responseResource.data.body.size() > 0)
                        new Thread(() -> getRoomDb().friendsDao().insertAll(responseResource.data.body)).start();
                    else
                        liveDataMerger.setValue(Resource.response(Resource.Status.success, responseResource.data.body));
                    break;
                default:
                    liveDataMerger.setValue(Resource.response(Resource.Status.error, null));
                    break;
            }
        });
        return liveDataMerger;
    }


    public LiveData<Resource<WebResponse<UserItem>>> login(String email, String pass, String deviceToken) {

        MutableLiveData<Resource<WebResponse<UserItem>>> loginMutableData = new MutableLiveData<>();
        loginMutableData.setValue(Resource.loading());
        WebServiceFactory.getInstance().login(email, pass, deviceToken, "android").enqueue(new Callback<WebResponse<UserItem>>() {
            @Override
            public void onResponse(Call<WebResponse<UserItem>> call, Response<WebResponse<UserItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess())
                        loginMutableData.setValue(Resource.response(Resource.Status.success, response.body()));
                    else if (response.body().error_code.equals(WebResponse.ERROR_EMAIL_VERIFICATION))
                        loginMutableData.setValue(Resource.response(Resource.Status.action_validation, response.body()));
                    else
                        loginMutableData.setValue(Resource.response(Resource.Status.error, response.body()));
                } else
                    loginMutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }

            @Override
            public void onFailure(Call<WebResponse<UserItem>> call, Throwable t) {
                loginMutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }
        });
        return loginMutableData;
    }


    public LiveData<Resource<WebResponse<UserItem>>> fbLogin(String token, String deviceToken) {

        MutableLiveData<Resource<WebResponse<UserItem>>> loginMutableData = new MutableLiveData<>();
        loginMutableData.setValue(Resource.loading());
        WebServiceFactory.getInstance().fbLogin(token, deviceToken, "android").enqueue(new Callback<WebResponse<UserItem>>() {
            @Override
            public void onResponse(Call<WebResponse<UserItem>> call, Response<WebResponse<UserItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess())
                        loginMutableData.setValue(Resource.response(Resource.Status.success, response.body()));
                    else {
                        if (response.body().error_code.equals(WebResponse.ERROR_EMAIL_VERIFICATION))
                            loginMutableData.setValue(Resource.response(Resource.Status.action_validation, response.body()));
                        else if (response.body().error_code.equals(WebResponse.ERROR_ACTION_SIGNUP))
                            loginMutableData.setValue(Resource.response(Resource.Status.action_need_signup, response.body()));
                        else
                            loginMutableData.setValue(Resource.response(Resource.Status.error, response.body()));
                    }
                } else
                    loginMutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }

            @Override
            public void onFailure(Call<WebResponse<UserItem>> call, Throwable t) {
                loginMutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }
        });
        return loginMutableData;
    }


    public LiveData<Resource<WebResponse>> register(String first_name, String last_name, String gender, String school_name, String email, String phone, String student_organization, String stateid, String cityid, String zip, String selectedYearOfGrad, String pass, String user_type, String reference_source, String device_token, String facebookToken) {

        MutableLiveData<Resource<WebResponse>> mutableData = new MutableLiveData<>();
        mutableData.setValue(Resource.loading());
        WebServiceFactory.getInstance().register(first_name, last_name, gender, school_name, email, phone, student_organization, stateid, cityid, zip, selectedYearOfGrad, pass, user_type, reference_source, device_token, "android", facebookToken).enqueue(new Callback<WebResponse>() {
            @Override
            public void onResponse(Call<WebResponse> call, Response<WebResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess())
                        mutableData.setValue(Resource.response(Resource.Status.success, response.body()));
                    else
                        mutableData.setValue(Resource.response(Resource.Status.error, response.body()));
                } else
                    mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }

            @Override
            public void onFailure(Call<WebResponse> call, Throwable t) {
                mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }
        });

        return mutableData;
    }


    public LiveData<Resource<WebResponse<UserItem>>> getProfile() {

        MutableLiveData<Resource<WebResponse<UserItem>>> mutableData = new MutableLiveData<>();
        mutableData.setValue(Resource.loading());
        WebServiceFactory.getInstance().getProfile(getUserItem().token).enqueue(new Callback<WebResponse<UserItem>>() {
            @Override
            public void onResponse(Call<WebResponse<UserItem>> call, Response<WebResponse<UserItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess())
                        mutableData.setValue(Resource.response(Resource.Status.success, response.body()));
                    else if (response.body().isExpired())
                        BusProvider.getInstance().post(new SessionExpireEvent());
                    else
                        mutableData.setValue(Resource.response(Resource.Status.error, response.body()));
                } else
                    mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }

            @Override
            public void onFailure(Call<WebResponse<UserItem>> call, Throwable t) {
                mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }
        });

        return mutableData;
    }

    public LiveData<Resource<WebResponse<UserItem>>> getProfile(String user_id) {

        MutableLiveData<Resource<WebResponse<UserItem>>> mutableData = new MutableLiveData<>();
        mutableData.setValue(Resource.loading());
        WebServiceFactory.getInstance().getProfile(getUserItem().token, user_id).enqueue(new Callback<WebResponse<UserItem>>() {
            @Override
            public void onResponse(Call<WebResponse<UserItem>> call, Response<WebResponse<UserItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess())
                        mutableData.setValue(Resource.response(Resource.Status.success, response.body()));
                    else if (response.body().isExpired())
                        BusProvider.getInstance().post(new SessionExpireEvent());
                    else
                        mutableData.setValue(Resource.response(Resource.Status.error, response.body()));
                } else
                    mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }

            @Override
            public void onFailure(Call<WebResponse<UserItem>> call, Throwable t) {
                mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }
        });

        return mutableData;
    }

    public LiveData<Resource<WebResponse<UserItem>>> getMyProfile(String token) {

        MutableLiveData<Resource<WebResponse<UserItem>>> mutableData = new MutableLiveData<>();
        mutableData.setValue(Resource.loading());
        WebServiceFactory.getInstance().getMyProfile(getUserItem().token).enqueue(new Callback<WebResponse<UserItem>>() {
            @Override
            public void onResponse(Call<WebResponse<UserItem>> call, Response<WebResponse<UserItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess())
                        mutableData.setValue(Resource.response(Resource.Status.success, response.body()));
                    else if (response.body().isExpired())
                        BusProvider.getInstance().post(new SessionExpireEvent());
                    else
                        mutableData.setValue(Resource.response(Resource.Status.error, response.body()));
                } else
                    mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }

            @Override
            public void onFailure(Call<WebResponse<UserItem>> call, Throwable t) {
                mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }
        });

        return mutableData;
    }


    public LiveData<Resource<WebResponse<UserItem>>> updateProfile(MultipartBody.Part profile_pic, RequestBody dob, RequestBody gender, RequestBody phone, RequestBody state, RequestBody city, RequestBody zip, RequestBody school_name, RequestBody student_org, RequestBody yearofgrad, RequestBody licence, RequestBody vehicle_type, RequestBody vehicleid, RequestBody make, RequestBody model, RequestBody year) {

        MutableLiveData<Resource<WebResponse<UserItem>>> mutableData = new MutableLiveData<>();
        mutableData.setValue(Resource.loading());
        WebServiceFactory.getInstance().updateProfile(StaticMethods.getRequestBody(getUserItem().token), profile_pic, dob, gender, phone, state, city, zip, school_name, student_org, yearofgrad, licence, vehicle_type, vehicleid, make, model, year).enqueue(new Callback<WebResponse<UserItem>>() {
            @Override
            public void onResponse(Call<WebResponse<UserItem>> call, Response<WebResponse<UserItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess())
                        mutableData.setValue(Resource.response(Resource.Status.success, response.body()));
                    else if (response.body().isExpired())
                        BusProvider.getInstance().post(new SessionExpireEvent());
                    else
                        mutableData.setValue(Resource.response(Resource.Status.error, response.body()));
                } else
                    mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }

            @Override
            public void onFailure(Call<WebResponse<UserItem>> call, Throwable t) {
                mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }
        });

        return mutableData;
    }

    public LiveData<Resource<WebResponse<ArrayList<CardItem>>>> getPaymentHistory(String role) {

        MutableLiveData<Resource<WebResponse<ArrayList<CardItem>>>> mutableData = new MutableLiveData<>();
        mutableData.setValue(Resource.loading());
        WebServiceFactory.getInstance().getPaymentHistory(role, getUserItem().token, "99999").enqueue(new Callback<WebResponse<ArrayList<CardItem>>>() {
            @Override
            public void onResponse(Call<WebResponse<ArrayList<CardItem>>> call, Response<WebResponse<ArrayList<CardItem>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess())
                        mutableData.setValue(Resource.response(Resource.Status.success, response.body()));
                    else if (response.body().isExpired())
                        BusProvider.getInstance().post(new SessionExpireEvent());
                    else
                        mutableData.setValue(Resource.response(Resource.Status.error, response.body()));
                } else
                    mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }

            @Override
            public void onFailure(Call<WebResponse<ArrayList<CardItem>>> call, Throwable t) {
                mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }
        });
        return mutableData;
    }

    public LiveData<Resource<WebResponse<ArrayList<NotificationListingItem>>>> getNotifications(String role, int limit, int page) {

        MutableLiveData<Resource<WebResponse<ArrayList<NotificationListingItem>>>> mutableData = new MutableLiveData<>();
        mutableData.setValue(Resource.loading());
        WebServiceFactory.getInstance().getNotifications(role, getUserItem().token, limit, page).enqueue(new Callback<WebResponse<ArrayList<NotificationListingItem>>>() {
            @Override
            public void onResponse(Call<WebResponse<ArrayList<NotificationListingItem>>> call, Response<WebResponse<ArrayList<NotificationListingItem>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess())
                        mutableData.setValue(Resource.response(Resource.Status.success, response.body()));
                    else if (response.body().isExpired())
                        BusProvider.getInstance().post(new SessionExpireEvent());
                    else
                        mutableData.setValue(Resource.response(Resource.Status.error, response.body()));
                } else
                    mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }

            @Override
            public void onFailure(Call<WebResponse<ArrayList<NotificationListingItem>>> call, Throwable t) {
                mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }
        });
        return mutableData;
    }

    public LiveData<Resource<WebResponse>> ratePassengers(String list) {

        MutableLiveData<Resource<WebResponse>> mutableData = new MutableLiveData<>();
        mutableData.setValue(Resource.loading());
        WebServiceFactory.getInstance().ratePassengers(getUserItem().token, list).enqueue(new Callback<WebResponse>() {
            @Override
            public void onResponse(Call<WebResponse> call, Response<WebResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess())
                        mutableData.setValue(Resource.response(Resource.Status.success, response.body()));
                    else if (response.body().isExpired())
                        BusProvider.getInstance().post(new SessionExpireEvent());
                    else
                        mutableData.setValue(Resource.response(Resource.Status.error, response.body()));
                } else
                    mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }

            @Override
            public void onFailure(Call<WebResponse> call, Throwable t) {
                mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }
        });
        return mutableData;
    }

    public LiveData<Resource<WebResponse>> rateDrivers(String list) {

        MutableLiveData<Resource<WebResponse>> mutableData = new MutableLiveData<>();
        mutableData.setValue(Resource.loading());
        WebServiceFactory.getInstance().rateDrivers(getUserItem().token, list).enqueue(new Callback<WebResponse>() {
            @Override
            public void onResponse(Call<WebResponse> call, Response<WebResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess())
                        mutableData.setValue(Resource.response(Resource.Status.success, response.body()));
                    else if (response.body().isExpired())
                        BusProvider.getInstance().post(new SessionExpireEvent());
                    else
                        mutableData.setValue(Resource.response(Resource.Status.error, response.body()));
                } else
                    mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }

            @Override
            public void onFailure(Call<WebResponse> call, Throwable t) {
                mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }
        });
        return mutableData;
    }


    public LiveData<Resource<WebResponse<PendingRatingItem>>> getPendingRatings() {

        MutableLiveData<Resource<WebResponse<PendingRatingItem>>> mutableData = new MutableLiveData<>();
        mutableData.setValue(Resource.loading());
        WebServiceFactory.getInstance().getPendingRatings(getUserItem().token).enqueue(new Callback<WebResponse<PendingRatingItem>>() {
            @Override
            public void onResponse(Call<WebResponse<PendingRatingItem>> call, Response<WebResponse<PendingRatingItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess())
                        mutableData.setValue(Resource.response(Resource.Status.success, response.body()));
                    else if (response.body().isExpired())
                        BusProvider.getInstance().post(new SessionExpireEvent());
                    else
                        mutableData.setValue(Resource.response(Resource.Status.error, response.body()));
                } else
                    mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }

            @Override
            public void onFailure(Call<WebResponse<PendingRatingItem>> call, Throwable t) {
                mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }
        });
        return mutableData;
    }

    public LiveData<Resource<WebResponse<ArrayList<CardItem>>>> getCreditCard() {

        MutableLiveData<Resource<WebResponse<ArrayList<CardItem>>>> mutableData = new MutableLiveData<>();
        mutableData.setValue(Resource.loading());
        WebServiceFactory.getInstance().getCreditCard(getUserItem().token).enqueue(new Callback<WebResponse<ArrayList<CardItem>>>() {
            @Override
            public void onResponse(Call<WebResponse<ArrayList<CardItem>>> call, Response<WebResponse<ArrayList<CardItem>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess())
                        mutableData.setValue(Resource.response(Resource.Status.success, response.body()));
                    else if (response.body().isExpired())
                        BusProvider.getInstance().post(new SessionExpireEvent());
                    else
                        mutableData.setValue(Resource.response(Resource.Status.error, response.body()));
                } else
                    mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }

            @Override
            public void onFailure(Call<WebResponse<ArrayList<CardItem>>> call, Throwable t) {
                mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }
        });
        return mutableData;
    }


    public LiveData<Resource<WebResponse<CardItem>>> addCreditCard(String card_token, String lastDigits) {

        MutableLiveData<Resource<WebResponse<CardItem>>> mutableData = new MutableLiveData<>();
        mutableData.setValue(Resource.loading());
        WebServiceFactory.getInstance().addCreditCard(getUserItem().token, card_token, lastDigits).enqueue(new Callback<WebResponse<CardItem>>() {
            @Override
            public void onResponse(Call<WebResponse<CardItem>> call, Response<WebResponse<CardItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess())
                        mutableData.setValue(Resource.response(Resource.Status.success, response.body()));
                    else if (response.body().isExpired())
                        BusProvider.getInstance().post(new SessionExpireEvent());
                    else
                        mutableData.setValue(Resource.response(Resource.Status.error, response.body()));
                } else
                    mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }

            @Override
            public void onFailure(Call<WebResponse<CardItem>> call, Throwable t) {
                mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }
        });

        return mutableData;
    }

    public LiveData<Resource<WebResponse<CardItem>>> setDefaultCard(String card_id) {

        MutableLiveData<Resource<WebResponse<CardItem>>> mutableData = new MutableLiveData<>();
        mutableData.setValue(Resource.loading());
        WebServiceFactory.getInstance().setDefaultCard(getUserItem().token, card_id).enqueue(new Callback<WebResponse<CardItem>>() {
            @Override
            public void onResponse(Call<WebResponse<CardItem>> call, Response<WebResponse<CardItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess())
                        mutableData.setValue(Resource.response(Resource.Status.success, response.body()));
                    else if (response.body().isExpired())
                        BusProvider.getInstance().post(new SessionExpireEvent());
                    else
                        mutableData.setValue(Resource.response(Resource.Status.error, response.body()));
                } else
                    mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }

            @Override
            public void onFailure(Call<WebResponse<CardItem>> call, Throwable t) {
                mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }
        });

        return mutableData;
    }

    public LiveData<Resource<WebResponse<CardItem>>> removeCard(String card_id) {

        MutableLiveData<Resource<WebResponse<CardItem>>> mutableData = new MutableLiveData<>();
        mutableData.setValue(Resource.loading());
        WebServiceFactory.getInstance().removeCard(getUserItem().token, card_id).enqueue(new Callback<WebResponse<CardItem>>() {
            @Override
            public void onResponse(Call<WebResponse<CardItem>> call, Response<WebResponse<CardItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess())
                        mutableData.setValue(Resource.response(Resource.Status.success, response.body()));
                    else if (response.body().isExpired())
                        BusProvider.getInstance().post(new SessionExpireEvent());
                    else
                        mutableData.setValue(Resource.response(Resource.Status.error, response.body()));
                } else
                    mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }

            @Override
            public void onFailure(Call<WebResponse<CardItem>> call, Throwable t) {
                mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }
        });

        return mutableData;
    }

    public LiveData<Resource<WebResponse<String>>> getBankDetail() {

        MutableLiveData<Resource<WebResponse<String>>> mutableData = new MutableLiveData<>();
        mutableData.setValue(Resource.loading());
        WebServiceFactory.getInstance().getBankDetail(getUserItem().token).enqueue(new Callback<WebResponse<String>>() {
            @Override
            public void onResponse(Call<WebResponse<String>> call, Response<WebResponse<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess())
                        mutableData.setValue(Resource.response(Resource.Status.success, response.body()));
                    else if (response.body().isExpired())
                        BusProvider.getInstance().post(new SessionExpireEvent());
                    else
                        mutableData.setValue(Resource.response(Resource.Status.error, response.body()));
                } else
                    mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }

            @Override
            public void onFailure(Call<WebResponse<String>> call, Throwable t) {
                mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }
        });
        return mutableData;
    }

    public LiveData<Resource<WebResponse<String>>> postBankDetail(String body) {

        MutableLiveData<Resource<WebResponse<String>>> mutableData = new MutableLiveData<>();
        mutableData.setValue(Resource.loading());
        WebServiceFactory.getInstance().postBankDetail(getUserItem().token, body).enqueue(new Callback<WebResponse<String>>() {
            @Override
            public void onResponse(Call<WebResponse<String>> call, Response<WebResponse<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess())
                        mutableData.setValue(Resource.response(Resource.Status.success, response.body()));
                    else if (response.body().isExpired())
                        BusProvider.getInstance().post(new SessionExpireEvent());
                    else
                        mutableData.setValue(Resource.response(Resource.Status.error, response.body()));
                } else
                    mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }

            @Override
            public void onFailure(Call<WebResponse<String>> call, Throwable t) {
                mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }
        });

        return mutableData;
    }

    public LiveData<Resource<WebResponse<UserItem>>> removeProfilePic() {

        MutableLiveData<Resource<WebResponse<UserItem>>> mutableData = new MutableLiveData<>();
        mutableData.setValue(Resource.loading());
        WebServiceFactory.getInstance().removeProfilePicture(getUserItem().token, "").enqueue(new Callback<WebResponse<UserItem>>() {
            @Override
            public void onResponse(Call<WebResponse<UserItem>> call, Response<WebResponse<UserItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess())
                        mutableData.setValue(Resource.response(Resource.Status.success, response.body()));
                    else if (response.body().isExpired())
                        BusProvider.getInstance().post(new SessionExpireEvent());
                    else
                        mutableData.setValue(Resource.response(Resource.Status.error, response.body()));
                } else
                    mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }

            @Override
            public void onFailure(Call<WebResponse<UserItem>> call, Throwable t) {
                mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }
        });

        return mutableData;
    }

    public LiveData<Resource<WebResponse>> upgradeToDriver(RequestBody licence, RequestBody vehicle_type, RequestBody vehicleid, RequestBody make, RequestBody model, RequestBody year) {

        MutableLiveData<Resource<WebResponse>> mutableData = new MutableLiveData<>();
        mutableData.setValue(Resource.loading());
        WebServiceFactory.getInstance().upgradeToDriver(StaticMethods.getRequestBody(getUserItem().token), licence, vehicle_type, vehicleid, make, model, year).enqueue(new Callback<WebResponse>() {
            @Override
            public void onResponse(Call<WebResponse> call, Response<WebResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess())
                        mutableData.setValue(Resource.response(Resource.Status.success, response.body()));
                    else if (response.body().isExpired())
                        BusProvider.getInstance().post(new SessionExpireEvent());
                    else
                        mutableData.setValue(Resource.response(Resource.Status.error, response.body()));
                } else
                    mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }

            @Override
            public void onFailure(Call<WebResponse> call, Throwable t) {
                mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }
        });

        return mutableData;
    }


    public MutableLiveData<Resource<WebResponse<PopularityResponse>>> getRoutesPopularity(ArrayList<Route> list) {

        Gson gson = new Gson();
        HashMap<String, Object> params = new HashMap<>();
        params.put("_token", getUserItem().token);
        for (int i = 0; i < list.size(); i++)
            params.put("route[" + i + "]", list.get(i).getOverviewPolyline().getRawPointList());

        MutableLiveData<Resource<WebResponse<PopularityResponse>>> mutableData = new MutableLiveData<>();
        mutableData.setValue(Resource.loading());
        WebServiceFactory.getInstance().getRoutePopulatiry(params).enqueue(new Callback<WebResponse<PopularityResponse>>() {
            @Override
            public void onResponse(Call<WebResponse<PopularityResponse>> call, Response<WebResponse<PopularityResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess())
                        mutableData.setValue(Resource.response(Resource.Status.success, response.body()));
                    else if (response.body().isExpired())
                        BusProvider.getInstance().post(new SessionExpireEvent());
                    else
                        mutableData.setValue(Resource.response(Resource.Status.error, response.body()));
                } else
                    mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }

            @Override
            public void onFailure(Call<WebResponse<PopularityResponse>> call, Throwable t) {
                mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }
        });

        return mutableData;
    }


    public MutableLiveData<Resource<WebResponse>> bootMeUp(Context context) {

        MutableLiveData<Resource<WebResponse>> mutableData = new MutableLiveData<>();
        mutableData.setValue(Resource.loading());
        WebServiceFactory.getInstance().bootMeUp().enqueue(new Callback<WebResponse<BootMeUpResponse>>() {
            @Override
            public void onResponse(Call<WebResponse<BootMeUpResponse>> call, Response<WebResponse<BootMeUpResponse>> response) {
                if (response.isSuccessful() && response.body().isSuccess() && response.body().body != null) {
                    ArrayList<SearchPreferenceItem> mList = response.body().body.preferences;
                    AppStore.getInstance().setBootUpData(response.body().body);
                    mutableData.setValue(Resource.response(Resource.Status.success, response.body()));
                } else
                    getBackupBootMeUp(context);
            }

            @Override
            public void onFailure(Call<WebResponse<BootMeUpResponse>> call, Throwable t) {
                mutableData.setValue(Resource.response(Resource.Status.error, null));
                getBackupBootMeUp(context);
            }
        });
        return mutableData;
    }

    private void getBackupBootMeUp(Context context) {
        try {
            if (AppStore.getInstance().getBootUpData() == null) {
                BootMeUpResponse bootupdata = new Gson().fromJson(StaticMethods.readStringFile(context, "bootmeup.json"), BootMeUpResponse.class);
                AppStore.getInstance().setBootUpData(bootupdata);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public LiveData<Resource<WebResponse<UserItem>>> changeProfile(String oldPass, String newPass) {

        MutableLiveData<Resource<WebResponse<UserItem>>> mutableData = new MutableLiveData<>();
        mutableData.setValue(Resource.loading());
        WebServiceFactory.getInstance().updateProfile(getUserItem().token, oldPass, newPass).enqueue(new Callback<WebResponse<UserItem>>() {
            @Override
            public void onResponse(Call<WebResponse<UserItem>> call, Response<WebResponse<UserItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess())
                        mutableData.setValue(Resource.response(Resource.Status.success, response.body()));
                    else if (response.body().isExpired())
                        BusProvider.getInstance().post(new SessionExpireEvent());
                    else
                        mutableData.setValue(Resource.response(Resource.Status.error, response.body()));
                } else
                    mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }

            @Override
            public void onFailure(Call<WebResponse<UserItem>> call, Throwable t) {
                mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }
        });

        return mutableData;
    }

    public LiveData<Resource<WebResponse<ArrayList<TripItem>>>> getMyTrips(String path, String latitude, String longitude, String date) {

        MutableLiveData<Resource<WebResponse<ArrayList<TripItem>>>> mutableData = new MutableLiveData<>();
        mutableData.setValue(Resource.loading());
        WebServiceFactory.getInstance().getMyTrips(path, getUserItem().token, latitude, longitude, date, "9999").enqueue(new Callback<WebResponse<ArrayList<TripItem>>>() {
            @Override
            public void onResponse(Call<WebResponse<ArrayList<TripItem>>> call, Response<WebResponse<ArrayList<TripItem>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess())
                        mutableData.setValue(Resource.response(Resource.Status.success, response.body()));
                    else if (response.body().isExpired())
                        BusProvider.getInstance().post(new SessionExpireEvent());
                    else
                        mutableData.setValue(Resource.response(Resource.Status.error, response.body()));
                } else
                    mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }

            @Override
            public void onFailure(Call<WebResponse<ArrayList<TripItem>>> call, Throwable t) {
                mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }
        });
        return mutableData;
    }

    public LiveData<Resource<WebResponse>> subscribeRide(int isRoundTrip, double origin_lat, double origin_lng, String origin_title, double dest_lat, double dest_lng, String dest_title) {

        MutableLiveData<Resource<WebResponse>> mutableData = new MutableLiveData<>();
        mutableData.setValue(Resource.loading());
        WebServiceFactory.getInstance().subscribeRide(getUserItem().token, isRoundTrip, origin_lat, origin_lng, origin_title, dest_lat, dest_lng, dest_title).enqueue(new Callback<WebResponse>() {
            @Override
            public void onResponse(Call<WebResponse> call, Response<WebResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess())
                        mutableData.setValue(Resource.response(Resource.Status.success, response.body()));
                    else if (response.body().isExpired())
                        BusProvider.getInstance().post(new SessionExpireEvent());
                    else
                        mutableData.setValue(Resource.response(Resource.Status.error, response.body()));
                } else
                    mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }

            @Override
            public void onFailure(Call<WebResponse> call, Throwable t) {
                mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }
        });
        return mutableData;
    }


    public LiveData<Resource<WebResponse>> makeOffer(String role, Map fieldMap) {
        MutableLiveData<Resource<WebResponse>> mutableData = new MutableLiveData<>();
        mutableData.setValue(Resource.loading());
        WebServiceFactory.getInstance().makeOffer(role, fieldMap).enqueue(new Callback<WebResponse>() {
            @Override
            public void onResponse(Call<WebResponse> call, Response<WebResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess())
                        mutableData.setValue(Resource.response(Resource.Status.success, response.body()));
                    else if (response.body().error_code.equals(WebResponse.ERROR_NO_CARD))
                        mutableData.setValue(Resource.response(Resource.Status.action_card_not_added, response.body()));
                    else if (response.body().error_code.equals(WebResponse.ERROR_NO_BANK_DETAILS))
                        mutableData.setValue(Resource.response(Resource.Status.add_bank_account, response.body()));
                    else if (response.body().isExpired())
                        BusProvider.getInstance().post(new SessionExpireEvent());
                    else
                        mutableData.setValue(Resource.response(Resource.Status.error, response.body()));
                } else
                    mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }

            @Override
            public void onFailure(Call<WebResponse> call, Throwable t) {
                mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }
        });
        return mutableData;
    }

    public LiveData<Resource<WebResponse>> acceptOffer(String role, Map fieldMap) {
        MutableLiveData<Resource<WebResponse>> mutableData = new MutableLiveData<>();
        mutableData.setValue(Resource.loading());
        WebServiceFactory.getInstance().acceptOffer(role, fieldMap).enqueue(new Callback<WebResponse>() {
            @Override
            public void onResponse(Call<WebResponse> call, Response<WebResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess())
                        mutableData.setValue(Resource.response(Resource.Status.success, response.body()));
                    else if (response.body().isExpired())
                        BusProvider.getInstance().post(new SessionExpireEvent());
                    else
                        mutableData.setValue(Resource.response(Resource.Status.error, response.body()));
                } else
                    mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }

            @Override
            public void onFailure(Call<WebResponse> call, Throwable t) {
                mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }
        });
        return mutableData;
    }


    public LiveData<Resource<WebResponse<TripItem>>> getOfferDetails(String role, String trip_id, Map fieldMap) {
        MutableLiveData<Resource<WebResponse<TripItem>>> mutableData = new MutableLiveData<>();
        mutableData.setValue(Resource.loading());
        WebServiceFactory.getInstance().offerDetail(role, trip_id, fieldMap).enqueue(new Callback<WebResponse<TripItem>>() {
            @Override
            public void onResponse(Call<WebResponse<TripItem>> call, Response<WebResponse<TripItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess())
                        mutableData.setValue(Resource.response(Resource.Status.success, response.body()));
                    else if (response.body().isExpired())
                        BusProvider.getInstance().post(new SessionExpireEvent());
                    else
                        mutableData.setValue(Resource.response(Resource.Status.error, response.body()));
                } else
                    mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }

            @Override
            public void onFailure(Call<WebResponse<TripItem>> call, Throwable t) {
                mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }
        });
        return mutableData;
    }

    public LiveData<Resource<WebResponse>> bookNow(Map fieldMap) {
        MutableLiveData<Resource<WebResponse>> mutableData = new MutableLiveData<>();
        mutableData.setValue(Resource.loading());
        WebServiceFactory.getInstance().bookNow(fieldMap).enqueue(new Callback<WebResponse>() {
            @Override
            public void onResponse(Call<WebResponse> call, Response<WebResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess())
                        mutableData.setValue(Resource.response(Resource.Status.success, response.body()));
                    else if (response.body().isExpired())
                        BusProvider.getInstance().post(new SessionExpireEvent());
                    else
                        mutableData.setValue(Resource.response(Resource.Status.error, response.body()));
                } else
                    mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }

            @Override
            public void onFailure(Call<WebResponse> call, Throwable t) {
                mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }
        });
        return mutableData;
    }


    public LiveData<Resource<WebResponse<ArrayList<TripItem>>>> getOffers(String role) {

        MutableLiveData<Resource<WebResponse<ArrayList<TripItem>>>> mutableData = new MutableLiveData<>();
        mutableData.setValue(Resource.loading());
        WebServiceFactory.getInstance().getOffers(role, getUserItem().token).enqueue(new Callback<WebResponse<ArrayList<TripItem>>>() {
            @Override
            public void onResponse(Call<WebResponse<ArrayList<TripItem>>> call, Response<WebResponse<ArrayList<TripItem>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess())
                        mutableData.setValue(Resource.response(Resource.Status.success, response.body()));
                    else if (response.body().isExpired())
                        BusProvider.getInstance().post(new SessionExpireEvent());
                    else
                        mutableData.setValue(Resource.response(Resource.Status.error, response.body()));
                } else
                    mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }

            @Override
            public void onFailure(Call<WebResponse<ArrayList<TripItem>>> call, Throwable t) {
                mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }
        });
        return mutableData;
    }


    public LiveData<Resource<WebResponse<TripItem>>> getTripDetails(String role, String trip_id, boolean searchReturn) {

        MutableLiveData<Resource<WebResponse<TripItem>>> mutableData = new MutableLiveData<>();
        mutableData.setValue(Resource.loading());
        WebServiceFactory.getInstance().tripDetails(getUserItem().token, role, trip_id, searchReturn).enqueue(new Callback<WebResponse<TripItem>>() {
            @Override
            public void onResponse(Call<WebResponse<TripItem>> call, Response<WebResponse<TripItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess())
                        mutableData.setValue(Resource.response(Resource.Status.success, response.body()));
                    else if (response.body().isExpired())
                        BusProvider.getInstance().post(new SessionExpireEvent());
                    else
                        mutableData.setValue(Resource.response(Resource.Status.error, response.body()));
                } else
                    mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }

            @Override
            public void onFailure(Call<WebResponse<TripItem>> call, Throwable t) {
                mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }
        });
        return mutableData;
    }


    MutableLiveData<Resource<WebResponse<ArrayList<TripItem>>>> passengerUpComingTripData;

    public LiveData<Resource<WebResponse<ArrayList<TripItem>>>> getPassengerUpComingTripData() {
        if (passengerUpComingTripData == null) {
            passengerUpComingTripData = new MutableLiveData<>();
            //   customerUpComingTripData.setValue(Resource.loading());
        }
        refreshPassengerUpComingTripData();
        return passengerUpComingTripData;
    }

    public LiveData<Resource<WebResponse<ArrayList<TripItem>>>> refreshPassengerUpComingTripData() {

        if (passengerUpComingTripData.getValue() == null)
            passengerUpComingTripData.setValue(Resource.loading());
        WebServiceFactory.getInstance().getPassengerUpComingTrips(getUserItem().token).enqueue(new Callback<WebResponse<ArrayList<TripItem>>>() {
            @Override
            public void onResponse(Call<WebResponse<ArrayList<TripItem>>> call, Response<WebResponse<ArrayList<TripItem>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess())
                        passengerUpComingTripData.setValue(Resource.response(Resource.Status.success, response.body()));
                    else if (response.body().isExpired())
                        BusProvider.getInstance().post(new SessionExpireEvent());
                    else
                        passengerUpComingTripData.setValue(Resource.response(Resource.Status.error, response.body()));
                } else
                    passengerUpComingTripData.setValue(Resource.response(Resource.Status.connection_error, null));
            }

            @Override
            public void onFailure(Call<WebResponse<ArrayList<TripItem>>> call, Throwable t) {
                passengerUpComingTripData.setValue(Resource.response(Resource.Status.connection_error, null));
            }
        });
        return passengerUpComingTripData;
    }


    MutableLiveData<Resource<WebResponse<ArrayList<TripItem>>>> driverUpComingTripData;

    public LiveData<Resource<WebResponse<ArrayList<TripItem>>>> getDriverUpComingTripData() {
        if (driverUpComingTripData == null) {
            driverUpComingTripData = new MutableLiveData<>();
            //   customerUpComingTripData.setValue(Resource.loading());
        }
        refreshDriverUpComingTripData();
        return driverUpComingTripData;
    }

    public LiveData<Resource<WebResponse<ArrayList<TripItem>>>> refreshDriverUpComingTripData() {

        if (driverUpComingTripData.getValue() == null)
            driverUpComingTripData.setValue(Resource.loading());
        WebServiceFactory.getInstance().getDriverUpComingTrips(getUserItem().token).enqueue(new Callback<WebResponse<ArrayList<TripItem>>>() {
            @Override
            public void onResponse(Call<WebResponse<ArrayList<TripItem>>> call, Response<WebResponse<ArrayList<TripItem>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess())
                        driverUpComingTripData.setValue(Resource.response(Resource.Status.success, response.body()));
                    else if (response.body().isExpired())
                        BusProvider.getInstance().post(new SessionExpireEvent());
                    else
                        driverUpComingTripData.setValue(Resource.response(Resource.Status.error, response.body()));
                } else
                    driverUpComingTripData.setValue(Resource.response(Resource.Status.connection_error, null));
            }

            @Override
            public void onFailure(Call<WebResponse<ArrayList<TripItem>>> call, Throwable t) {
                driverUpComingTripData.setValue(Resource.response(Resource.Status.connection_error, null));
            }
        });
        return driverUpComingTripData;
    }

    public LiveData<Resource<WebResponse>> createRideDriver(Map fieldMap) {

        MutableLiveData<Resource<WebResponse>> mutableData = new MutableLiveData<>();
        mutableData.setValue(Resource.loading());
        WebServiceFactory.getInstance().createRideDriver(fieldMap).enqueue(new Callback<WebResponse>() {
            @Override
            public void onResponse(Call<WebResponse> call, Response<WebResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess())
                        mutableData.setValue(Resource.response(Resource.Status.success, response.body()));
                    else if (response.body().error_code.equals(WebResponse.ERROR_NO_BANK_DETAILS))
                        mutableData.setValue(Resource.response(Resource.Status.add_bank_account, response.body()));
                    else if (response.body().isExpired())
                        BusProvider.getInstance().post(new SessionExpireEvent());
                    else
                        mutableData.setValue(Resource.response(Resource.Status.error, response.body()));
                } else
                    mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }

            @Override
            public void onFailure(Call<WebResponse> call, Throwable t) {
                mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }
        });
        return mutableData;
    }

    public LiveData<Resource<WebResponse>> createRide(Map fieldMap, boolean isPassenger) {

        MutableLiveData<Resource<WebResponse>> mutableData = new MutableLiveData<>();
        mutableData.setValue(Resource.loading());

        Call call = null;
        if (isPassenger)
            call = WebServiceFactory.getInstance().createRidePassenger(fieldMap);
        else
            call = WebServiceFactory.getInstance().createRideDriver(fieldMap);

        call.enqueue(new Callback<WebResponse>() {
            @Override
            public void onResponse(Call<WebResponse> call, Response<WebResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess())
                        mutableData.setValue(Resource.response(Resource.Status.success, response.body()));
                    else if (response.body().isExpired())
                        BusProvider.getInstance().post(new SessionExpireEvent());
                    else
                        mutableData.setValue(Resource.response(Resource.Status.error, response.body()));
                } else
                    mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }

            @Override
            public void onFailure(Call<WebResponse> call, Throwable t) {
                mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }
        });
        return mutableData;
    }

    public LiveData<Resource<WebResponse>> kickPassenger(String trip_id, String passenger_id) {

        MutableLiveData<Resource<WebResponse>> mutableData = new MutableLiveData<>();
        mutableData.setValue(Resource.loading());
        WebServiceFactory.getInstance().kickPassenger(getUserItem().token, trip_id, passenger_id).enqueue(new Callback<WebResponse>() {
            @Override
            public void onResponse(Call<WebResponse> call, Response<WebResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess())
                        mutableData.setValue(Resource.response(Resource.Status.success, response.body()));
                    else if (response.body().isExpired())
                        BusProvider.getInstance().post(new SessionExpireEvent());
                    else
                        mutableData.setValue(Resource.response(Resource.Status.error, response.body()));
                } else
                    mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }

            @Override
            public void onFailure(Call<WebResponse> call, Throwable t) {
                mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }
        });
        return mutableData;
    }

    public LiveData<Resource<WebResponse<ArrayList<TripItem>>>> findRideOld(Map fieldMap) {

        MutableLiveData<Resource<WebResponse<ArrayList<TripItem>>>> mutableData = new MutableLiveData<>();
        mutableData.setValue(Resource.loading());
        WebServiceFactory.getInstance().findRide(fieldMap).enqueue(new Callback<WebResponse<ArrayList<TripItem>>>() {
            @Override
            public void onResponse(Call<WebResponse<ArrayList<TripItem>>> call, Response<WebResponse<ArrayList<TripItem>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess())
                        mutableData.setValue(Resource.response(Resource.Status.success, response.body()));
                    else if (response.body().isExpired())
                        BusProvider.getInstance().post(new SessionExpireEvent());
                    else
                        mutableData.setValue(Resource.response(Resource.Status.error, response.body()));
                } else
                    mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }

            @Override
            public void onFailure(Call<WebResponse<ArrayList<TripItem>>> call, Throwable t) {
                mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }
        });
        return mutableData;
    }

    public LiveData<Resource<WebResponse<ArrayList<TripItem>>>> searchRide(Map fieldMap) {

        MutableLiveData<Resource<WebResponse<ArrayList<TripItem>>>> mutableData = new MutableLiveData<>();
        mutableData.setValue(Resource.loading());
        WebServiceFactory.getInstance().searchRide(fieldMap).enqueue(new Callback<WebResponse<ArrayList<TripItem>>>() {
            @Override
            public void onResponse(Call<WebResponse<ArrayList<TripItem>>> call, Response<WebResponse<ArrayList<TripItem>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess())
                        mutableData.setValue(Resource.response(Resource.Status.success, response.body()));
                    else if (response.body().isExpired())
                        BusProvider.getInstance().post(new SessionExpireEvent());
                    else
                        mutableData.setValue(Resource.response(Resource.Status.error, response.body()));
                } else
                    mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }

            @Override
            public void onFailure(Call<WebResponse<ArrayList<TripItem>>> call, Throwable t) {
                mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }
        });
        return mutableData;
    }

    public LiveData<Resource<WebResponse<ArrayList<TripItem>>>> findRide(Map fieldMap) {

        MutableLiveData<Resource<WebResponse<ArrayList<TripItem>>>> mutableData = new MutableLiveData<>();
        mutableData.setValue(Resource.loading());
        WebServiceFactory.getInstance().findRide(getUserItem().getCurrentInterface() ? "search" : "requests", fieldMap).enqueue(new Callback<WebResponse<ArrayList<TripItem>>>() {
            @Override
            public void onResponse(Call<WebResponse<ArrayList<TripItem>>> call, Response<WebResponse<ArrayList<TripItem>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess())
                        mutableData.setValue(Resource.response(Resource.Status.success, response.body()));
                    else if (response.body().isExpired())
                        BusProvider.getInstance().post(new SessionExpireEvent());
                    else
                        mutableData.setValue(Resource.response(Resource.Status.error, response.body()));
                } else
                    mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }

            @Override
            public void onFailure(Call<WebResponse<ArrayList<TripItem>>> call, Throwable t) {
                mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }
        });
        return mutableData;
    }

    public LiveData<Resource<WebResponse<ArrayList<TripItem>>>> searchRiders(double origin_lat, double origin_lng,double dest_lat, double dest_lng) {

        MutableLiveData<Resource<WebResponse<ArrayList<TripItem>>>> mutableData = new MutableLiveData<>();
        mutableData.setValue(Resource.loading());
        WebServiceFactory.getInstance().searchRiders(getUserItem().token,origin_lat,origin_lng,dest_lat,dest_lng).enqueue(new Callback<WebResponse<ArrayList<TripItem>>>() {
            @Override
            public void onResponse(Call<WebResponse<ArrayList<TripItem>>> call, Response<WebResponse<ArrayList<TripItem>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess())
                        mutableData.setValue(Resource.response(Resource.Status.success, response.body()));
                    else if (response.body().isExpired())
                        BusProvider.getInstance().post(new SessionExpireEvent());
                    else
                        mutableData.setValue(Resource.response(Resource.Status.error, response.body()));
                } else
                    mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }

            @Override
            public void onFailure(Call<WebResponse<ArrayList<TripItem>>> call, Throwable t) {
                mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }
        });
        return mutableData;
    }

    public LiveData<Resource<WebResponse>> preScheduleTripTime(String trip_id, long pickuptime) {

        MutableLiveData<Resource<WebResponse>> mutableData = new MutableLiveData<>();
        mutableData.setValue(Resource.loading());
        Call<WebResponse> call;
        call = WebServiceFactory.getInstance().preScheduleRideTime(getUserItem().token, trip_id, pickuptime);

        call.enqueue(new Callback<WebResponse>() {
            @Override
            public void onResponse(Call<WebResponse> call, Response<WebResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess())
                        mutableData.setValue(Resource.response(Resource.Status.success, response.body()));
                    else if (response.body().isExpired())
                        BusProvider.getInstance().post(new SessionExpireEvent());
                    else
                        mutableData.setValue(Resource.response(Resource.Status.error, response.body()));
                } else
                    mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }

            @Override
            public void onFailure(Call<WebResponse> call, Throwable t) {
                mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }
        });
        return mutableData;
    }

    public LiveData<Resource<WebResponse>> scheduleTripTime(boolean isforcefull, String trip_id, long pickuptime) {

        MutableLiveData<Resource<WebResponse>> mutableData = new MutableLiveData<>();
        mutableData.setValue(Resource.loading());
        Call<WebResponse> call;
        if (isforcefull)
            call = WebServiceFactory.getInstance().scheduleRideTimeForceFully(getUserItem().token, trip_id, pickuptime);
        else
            call = WebServiceFactory.getInstance().scheduleRideTime(getUserItem().token, trip_id, pickuptime);

        call.enqueue(new Callback<WebResponse>() {
            @Override
            public void onResponse(Call<WebResponse> call, Response<WebResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess())
                        mutableData.setValue(Resource.response(Resource.Status.success, response.body()));
                    else if (response.body().isExpired())
                        BusProvider.getInstance().post(new SessionExpireEvent());
                    else
                        mutableData.setValue(Resource.response(Resource.Status.error, response.body()));
                } else
                    mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }

            @Override
            public void onFailure(Call<WebResponse> call, Throwable t) {
                mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }
        });
        return mutableData;
    }

    public LiveData<Resource<WebResponse>> editSeats(String trip_id, int seats) {

        MutableLiveData<Resource<WebResponse>> mutableData = new MutableLiveData<>();
        mutableData.setValue(Resource.loading());
        WebServiceFactory.getInstance().editSeats(getUserItem().token, trip_id, seats).enqueue(new Callback<WebResponse>() {
            @Override
            public void onResponse(Call<WebResponse> call, Response<WebResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess())
                        mutableData.setValue(Resource.response(Resource.Status.success, response.body()));
                    else if (response.body().isExpired())
                        BusProvider.getInstance().post(new SessionExpireEvent());
                    else
                        mutableData.setValue(Resource.response(Resource.Status.error, response.body()));
                } else
                    mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }

            @Override
            public void onFailure(Call<WebResponse> call, Throwable t) {
                mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }
        });
        return mutableData;
    }


    public LiveData<Resource<WebResponse<TripItem>>> startRide(String trip_id) {

        MutableLiveData<Resource<WebResponse<TripItem>>> mutableData = new MutableLiveData<>();
        mutableData.setValue(Resource.loading());
        WebServiceFactory.getInstance().startRide(getUserItem().token, trip_id).enqueue(new Callback<WebResponse<TripItem>>() {
            @Override
            public void onResponse(Call<WebResponse<TripItem>> call, Response<WebResponse<TripItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess())
                        mutableData.setValue(Resource.response(Resource.Status.success, response.body()));
                    else if (response.body().isExpired())
                        BusProvider.getInstance().post(new SessionExpireEvent());
                    else
                        mutableData.setValue(Resource.response(Resource.Status.error, response.body()));
                } else
                    mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }

            @Override
            public void onFailure(Call<WebResponse<TripItem>> call, Throwable t) {
                mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }
        });
        return mutableData;
    }

    public LiveData<Resource<WebResponse<TripItem>>> markPickDrop(String action, String tripId, String passengers_id, String coordinates) {

        MutableLiveData<Resource<WebResponse<TripItem>>> mutableData = new MutableLiveData<>();
        mutableData.setValue(Resource.loading());
        WebServiceFactory.getInstance().markPickDrop(action, getUserItem().token, tripId, passengers_id, coordinates).enqueue(new Callback<WebResponse<TripItem>>() {
            @Override
            public void onResponse(Call<WebResponse<TripItem>> call, Response<WebResponse<TripItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess())
                        mutableData.setValue(Resource.response(Resource.Status.success, response.body()));
                    else if (response.body().isExpired())
                        BusProvider.getInstance().post(new SessionExpireEvent());
                    else
                        mutableData.setValue(Resource.response(Resource.Status.error, response.body()));
                } else
                    mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }

            @Override
            public void onFailure(Call<WebResponse<TripItem>> call, Throwable t) {
                mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }
        });
        return mutableData;
    }

    public LiveData<Resource<WebResponse<TripItem>>> resumeRide(String trip_id) {

        MutableLiveData<Resource<WebResponse<TripItem>>> mutableData = new MutableLiveData<>();
        mutableData.setValue(Resource.loading());
        WebServiceFactory.getInstance().resumeRide(getUserItem().token, trip_id).enqueue(new Callback<WebResponse<TripItem>>() {
            @Override
            public void onResponse(Call<WebResponse<TripItem>> call, Response<WebResponse<TripItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess())
                        mutableData.setValue(Resource.response(Resource.Status.success, response.body()));
                    else if (response.body().isExpired())
                        BusProvider.getInstance().post(new SessionExpireEvent());
                    else
                        mutableData.setValue(Resource.response(Resource.Status.error, response.body()));
                } else
                    mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }

            @Override
            public void onFailure(Call<WebResponse<TripItem>> call, Throwable t) {
                mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }
        });
        return mutableData;
    }

    public LiveData<Resource<WebResponse<TripItem>>> endRide(String trip_id) {

        MutableLiveData<Resource<WebResponse<TripItem>>> mutableData = new MutableLiveData<>();
        mutableData.setValue(Resource.loading());
        WebServiceFactory.getInstance().endRide(getUserItem().token, trip_id).enqueue(new Callback<WebResponse<TripItem>>() {
            @Override
            public void onResponse(Call<WebResponse<TripItem>> call, Response<WebResponse<TripItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess())
                        mutableData.setValue(Resource.response(Resource.Status.success, response.body()));
                    else if (response.body().isExpired())
                        BusProvider.getInstance().post(new SessionExpireEvent());
                    else
                        mutableData.setValue(Resource.response(Resource.Status.error, response.body()));
                } else
                    mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }

            @Override
            public void onFailure(Call<WebResponse<TripItem>> call, Throwable t) {
                mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }
        });
        return mutableData;
    }

    public LiveData<Resource<WebResponse>> completePayment(String trip_id) {

        MutableLiveData<Resource<WebResponse>> mutableData = new MutableLiveData<>();
        mutableData.setValue(Resource.loading());
        WebServiceFactory.getInstance().completePayment(getUserItem().token, trip_id).enqueue(new Callback<WebResponse>() {
            @Override
            public void onResponse(Call<WebResponse> call, Response<WebResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess())
                        mutableData.setValue(Resource.response(Resource.Status.success, response.body()));
                    else if (response.body().isExpired())
                        BusProvider.getInstance().post(new SessionExpireEvent());
                    else
                        mutableData.setValue(Resource.response(Resource.Status.error, response.body()));
                } else
                    mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }

            @Override
            public void onFailure(Call<WebResponse> call, Throwable t) {
                mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }
        });
        return mutableData;
    }

    public LiveData<Resource<WebResponse<TripItem>>> cancelRide(String role, String trip_id) {

        MutableLiveData<Resource<WebResponse<TripItem>>> mutableData = new MutableLiveData<>();
        mutableData.setValue(Resource.loading());
        WebServiceFactory.getInstance().cancelRide(role, getUserItem().token, trip_id).enqueue(new Callback<WebResponse<TripItem>>() {
            @Override
            public void onResponse(Call<WebResponse<TripItem>> call, Response<WebResponse<TripItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess())
                        mutableData.setValue(Resource.response(Resource.Status.success, response.body()));
                    else if (response.body().isExpired())
                        BusProvider.getInstance().post(new SessionExpireEvent());
                    else
                        mutableData.setValue(Resource.response(Resource.Status.error, response.body()));
                } else
                    mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }

            @Override
            public void onFailure(Call<WebResponse<TripItem>> call, Throwable t) {
                mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }
        });
        return mutableData;
    }

    public LiveData<Resource<WebResponse>> shareItinerary(String role, String trip_id, String invitee) {

        MutableLiveData<Resource<WebResponse>> mutableData = new MutableLiveData<>();
        mutableData.setValue(Resource.loading());
        WebServiceFactory.getInstance().shareItinerary(role, getUserItem().token, trip_id, invitee).enqueue(new Callback<WebResponse>() {
            @Override
            public void onResponse(Call<WebResponse> call, Response<WebResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess())
                        mutableData.setValue(Resource.response(Resource.Status.success, response.body()));
                    else if (response.body().isExpired())
                        BusProvider.getInstance().post(new SessionExpireEvent());
                    else
                        mutableData.setValue(Resource.response(Resource.Status.error, response.body()));
                } else
                    mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }

            @Override
            public void onFailure(Call<WebResponse> call, Throwable t) {
                mutableData.setValue(Resource.response(Resource.Status.connection_error, null));
            }
        });
        return mutableData;
    }


    // User Object Access
    public AppDatabase getRoomDb() {
        if (roomDb == null)
            roomDb = AppDatabase.getInstance(getApplication());
        return roomDb;
    }

    public UserItem getUserItem() {
        if (userItem == null) {
            userItem = new MutableLiveData<>();
            userItem.setValue(PreferencesManager.getObject(AppConstants.KEY_USER, UserItem.class));
        }
        return userItem.getValue();
    }

    public MutableLiveData<UserItem> getUserLiveData() {
        if (userItem == null) {
            userItem = new MutableLiveData<>();
            userItem.setValue(PreferencesManager.getObject(AppConstants.KEY_USER, UserItem.class));
        }
        return userItem;
    }

    public void setUserItem(UserItem userItem) {
        try {
            if (userItem != null && userItem.token == null && getUserItem() != null)
                userItem.token = getUserItem().token;
        } catch (Exception e) {
            e.printStackTrace();
        }

        PreferencesManager.putObject(AppConstants.KEY_USER, userItem);
        if (userItem != null && this.userItem != null)
            this.userItem.setValue(userItem);
    }

    public void postUserItem(UserItem userItem) {
        try {
            if (userItem != null && userItem.token == null && getUserItem() != null)
                userItem.token = getUserItem().token;
        } catch (Exception e) {
            e.printStackTrace();
        }
        PreferencesManager.putObject(AppConstants.KEY_USER, userItem);
        if (userItem != null && this.userItem != null)
            this.userItem.postValue(userItem);

        Log.e("user updated", "user updated");
    }

}
