package com.seatus.Retrofit;


import android.widget.ListView;

import com.seatus.Holders.BankItem;
import com.seatus.Models.BootMeUpResponse;
import com.seatus.Models.CardItem;
import com.seatus.Models.MatrixApiResponse;
import com.seatus.Models.NotificationListingItem;
import com.seatus.Models.PendingRatingItem;
import com.seatus.Models.PopularityResponse;
import com.seatus.Models.PromoAmount;
import com.seatus.Models.RateItem;
import com.seatus.Models.SchoolItem;
import com.seatus.Models.TripItem;
import com.seatus.Models.TripSearchWrapper;
import com.seatus.Models.UserItem;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WebService {

    @FormUrlEncoded
    @POST("login")
    Call<WebResponse<UserItem>> login(
            @Field("email") String email,
            @Field("password") String pass,
            @Field("device_token") String device_token,
            @Field("device_type") String device_type);

    @FormUrlEncoded
    @POST("fb-login")
    Call<WebResponse<UserItem>> fbLogin(
            @Field("facebook_token") String fb_token,
            @Field("device_token") String device_token,
            @Field("device_type") String device_type);

    @FormUrlEncoded
    @POST("reset-password")
    Call<WebResponse> resetPassword(
            @Field("email") String email);

    @FormUrlEncoded
    @POST("verify-account")
    Call<WebResponse<UserItem>> verifyAccount(
            @Field("code") String code);

    @FormUrlEncoded
    @POST("resend-verification-email")
    Call<WebResponse> resendVerificationCode(
            @Field("email") String email);

    @FormUrlEncoded
    @POST("logout")
    Call<WebResponse> logout(
            @Field("_token") String token);

    @FormUrlEncoded
    @POST("account/delete")
    Call<WebResponse> deleteAccount(
            @Field("_token") String token);

    @FormUrlEncoded
    @POST("register")
    Call<WebResponse> register(
            @Field("first_name") String first_name,
            @Field("last_name") String last_name,
            @Field("gender") String gender,
            @Field("school_name") String school_name,
            @Field("email") String email,
            @Field("phone") String phone,
            @Field("student_organization") String student_organization,
            @Field("state") String state,
            @Field("city") String city,
            @Field("postal_code") String zip,
            @Field("graduation_year") String graduation_year,
            @Field("password") String password,
            @Field("user_type") String user_type,
            @Field("reference_source") String reference_source,
            @Field("device_token") String device_token,
            @Field("device_type") String device_type,
            @Field("facebook_token") String facebook_token);

    @FormUrlEncoded
    @POST("account/sync-friends")
    Call<WebResponse> syncFriends(
            @Field("_token") String token,
            @Field("facebook_ids") String facebook_ids,
            @Field("numbers") String phone_numbers);

    @FormUrlEncoded
    @PUT("account/facebook")
    Call<WebResponse> bindAccountWithFacebook(
            @Field("_token") String token,
            @Field("facebook_token") String facebook_token);

    @FormUrlEncoded
    @POST("account/list/followings")
    Call<WebResponse<ArrayList<UserItem>>> getFriends(
            @Field("_token") String token,
            @Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("{role}/payment/history")
    Call<WebResponse<ArrayList<CardItem>>> getPaymentHistory(
            @Path("role") String role,
            @Field("_token") String token,
            @Field("limit") String limit);

    @FormUrlEncoded
    @POST("account/list/notifications")
    Call<WebResponse<ArrayList<NotificationListingItem>>> getNotifications(
            @Field("user_type") String role,
            @Field("_token") String token,
            @Field("limit") int limit,
            @Field("page") int page);

    @FormUrlEncoded
    @POST("driver/rate/passengers")
    Call<WebResponse> ratePassengers(
            @Field("_token") String token,
            @Field("rating_data") String list);

    @FormUrlEncoded
    @POST("passenger/rate/drivers")
    Call<WebResponse> rateDrivers(
            @Field("_token") String token,
            @Field("rating_data") String list);

    @FormUrlEncoded
    @POST("account/list/pending-ratings")
    Call<WebResponse<PendingRatingItem>> getPendingRatings(
            @Field("_token") String token);

    @FormUrlEncoded
    @POST("passenger/get/credit-card")
    Call<WebResponse<ArrayList<CardItem>>> getCreditCard(
            @Field("_token") String token);

    @FormUrlEncoded
    @POST("passenger/remove/credit-card")
    Call<WebResponse<CardItem>> removeCard(
            @Field("_token") String token,
            @Field("card_id") String card_id);

    @FormUrlEncoded
    @POST("passenger/default/credit-card")
    Call<WebResponse<CardItem>> setDefaultCard(
            @Field("_token") String token,
            @Field("card_id") String card_id);

    @FormUrlEncoded
    @POST("passenger/add/credit-card")
    Call<WebResponse<CardItem>> addCreditCard(
            @Field("_token") String token,
            @Field("card_token") String card_token,
            @Field("last_digits") String last_digits);

    @FormUrlEncoded
    @POST("driver/bank-details/read")
    Call<WebResponse<String>> getBankDetail(
            @Field("_token") String token);

    @FormUrlEncoded
    @POST("driver/bank-details/update")
    Call<WebResponse<String>> postBankDetail(
            @Field("_token") String token,
            @Field("body") String body);

    @Multipart
    @POST("account/update")
    Call<WebResponse<UserItem>> updateProfile(
            @Part("_token") RequestBody token,
            @Part MultipartBody.Part profile_pic,
            @Part("birth_date") RequestBody birth_date,
            @Part("gender") RequestBody gender,
            @Part("phone") RequestBody phone,
            @Part("state") RequestBody state,
            @Part("city") RequestBody city,
            @Part("postal_code") RequestBody postal_code,
            @Part("school_name") RequestBody school_name,
            @Part("student_organization") RequestBody school_organization,
            @Part("graduation_year") RequestBody graduation_year,
            //Driver Fields
            @Part("driving_license_no") RequestBody driving_license_no,
            //vehicle
            @Part("vehicle_type") RequestBody vehicle_type,
            @Part("vehicle_id_number") RequestBody vehicle_id_number,
            @Part("vehicle_make") RequestBody vehicle_make,
            @Part("vehicle_model") RequestBody vehicle_model,
            @Part("vehicle_year") RequestBody vehicle_year);

    @FormUrlEncoded
    @POST("account/me")
    Call<WebResponse<UserItem>> getMyProfile(
            @Field("_token") String token);

    @FormUrlEncoded
    @POST("account/profile_me")
    Call<WebResponse<UserItem>> removeProfilePicture(
            @Field("_token") String token,
            @Field("profile_picture") String profile_pic);

    @FormUrlEncoded
    @POST("account/update")
    Call<WebResponse<UserItem>> updateProfile(
            @Field("_token") String token,
            @Field("old_pwd") String oldpass,
            @Field("password") String newpass);

    @Multipart
    @POST("account/upgrade/driver")
    Call<WebResponse> upgradeToDriver(
            @Part("_token") RequestBody token,
            @Part("driving_license_no") RequestBody driving_license_no,
            //vehicle
            @Part("vehicle_type") RequestBody vehicle_type,
            @Part("vehicle_id_number") RequestBody vehicle_id_number,
            @Part("vehicle_make") RequestBody vehicle_make,
            @Part("vehicle_model") RequestBody vehicle_model,
            @Part("vehicle_year") RequestBody vehicle_year);


    @FormUrlEncoded
    @POST("{path}/trips")
    Call<WebResponse<ArrayList<TripItem>>> getMyTrips(
            @Path(value = "path", encoded = true) String upcoming,
            @Field("_token") String token,
            @Field("latitude") String latitude,
            @Field("longitude") String longitude,
            @Field("date") String date,
            @Field("limit") String limit);

    @FormUrlEncoded
    @POST("passenger/subscribe/ride")
    Call<WebResponse> subscribeRide(
            @Field("_token") String token,
            @Field("is_roundtrip") int is_roundtrip,
            @Field("origin_latitude") double origin_latitude,
            @Field("origin_longitude") double origin_longitude,
            @Field("origin_title") String origin_title,
            @Field("destination_latitude") double destination_latitude,
            @Field("destination_longitude") double destination_longitude,
            @Field("destination_title") String destination_title
    );


    // For Messaging
    @FormUrlEncoded
    @POST("passenger/upcoming/trips")
    Call<WebResponse<ArrayList<TripItem>>> getPassengerUpComingTrips(
            @Field("_token") String token);

    @FormUrlEncoded
    @POST("driver/upcoming/trips")
    Call<WebResponse<ArrayList<TripItem>>> getDriverUpComingTrips(
            @Field("_token") String token);

    @FormUrlEncoded
    @POST("account/me")
    Call<WebResponse<UserItem>> getProfile(
            @Field("_token") String token);

    @FormUrlEncoded
    @POST("account/view/{user_id}")
    Call<WebResponse<UserItem>> getProfile(
            @Field("_token") String token,
            @Path("user_id") String user_id);

    @GET("boot-me-up")
    Call<WebResponse<BootMeUpResponse>> bootMeUp();

    @GET("list/schools")
    Call<WebResponse<ArrayList<SchoolItem>>> getAllSchools();

    @FormUrlEncoded
    @POST("ride/driver/popularity")
    Call<WebResponse<PopularityResponse>> getRoutePopulatiry(
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("ride/create/public")
    Call<WebResponse> createRideDriver(
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("passenger/create/ride")
    Call<WebResponse> createRidePassenger(
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("ride/search")
    Call<WebResponse<ArrayList<TripItem>>> findRide(
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("driver/list/subscribed-routes")
    Call<WebResponse<ArrayList<TripItem>>> searchRiders(
            @Field("_token") String token,
            @Field("origin_latitude") double origin_latitude,
            @Field("origin_longitude") double origin_longitude,
            @Field("destination_latitude") double destination_latitude,
            @Field("destination_longitude") double destination_longitude
    );

    @FormUrlEncoded
    @POST("ride/requests")
    Call<WebResponse<ArrayList<TripItem>>> searchRide(
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("ride/{path}")
    Call<WebResponse<ArrayList<TripItem>>> findRide(
            @Path("path") String path,
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("{role}/ride/detail/{trip_id}")
    Call<WebResponse<TripItem>> tripDetails(
            @Field("_token") String token,
            @Path("role") String role,
            @Path("trip_id") String trip_id,
            @Field("fetch_return") boolean fetchReturn);

    @FormUrlEncoded
    @POST("{role}/offer/detail/{trip_id}")
    Call<WebResponse<TripItem>> offerDetail(
            @Path("role") String role,
            @Path("trip_id") String trip_id,
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("{role}/offers")
    Call<WebResponse<ArrayList<TripItem>>> getOffers(
            @Path("role") String role,
            @Field("_token") String token);

    @FormUrlEncoded
    @POST("{role}/make/offer")
    Call<WebResponse<TripItem>> makeOffer(
            @Path("role") String role,
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("passenger/book-now")
    Call<WebResponse<TripItem>> bookNow(
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("passenger/trip/payment")
    Call<WebResponse> completePayment(
            @Field("_token") String token,
            @Field("trip_id") String trip_id);

    @FormUrlEncoded
    @POST("{role}/trip/cancel")
    Call<WebResponse<TripItem>> cancelRide(
            @Path("role") String role,
            @Field("_token") String token,
            @Field("trip_id") String trip_id);

    @FormUrlEncoded
    @POST("{role}/accept/offer")
    Call<WebResponse<TripItem>> acceptOffer(
            @Path("role") String role,
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("passenger/apply-promo")
    Call<WebResponse<PromoAmount>> applyPromo(
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("passenger/reject/offer")
    Call<WebResponse<TripItem>> rejectOffer(
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("driver/ride/eliminate")
    Call<WebResponse> kickPassenger(
            @Field("_token") String token,
            @Field("trip_id") String trip_id,
            @Field("passenger_id") String passenger_id);

    @FormUrlEncoded
    @POST("driver/preschedule/ride/time")
    Call<WebResponse> preScheduleRideTime(
            @Field("_token") String token,
            @Field("trip_id") String trip_id,
            @Field("pickup_time") long pickup_time);

    @FormUrlEncoded
    @POST("driver/schedule/ride/time")
    Call<WebResponse> scheduleRideTime(
            @Field("_token") String token,
            @Field("trip_id") String trip_id,
            @Field("pickup_time") long pickup_time);

    @FormUrlEncoded
    @POST("driver/schedule/ride/time/forcefully")
    Call<WebResponse> scheduleRideTimeForceFully(
            @Field("_token") String token,
            @Field("trip_id") String trip_id,
            @Field("pickup_time") long pickup_time);

    @FormUrlEncoded
    @POST("driver/start/trip")
    Call<WebResponse<TripItem>> startRide(
            @Field("_token") String token,
            @Field("trip_id") String trip_id);


    @FormUrlEncoded
    @POST("driver/resume/trip")
    Call<WebResponse<TripItem>> resumeRide(
            @Field("_token") String token,
            @Field("trip_id") String trip_id);

    @FormUrlEncoded
    @POST("driver/mark/{action}")
    Call<WebResponse<TripItem>> markPickDrop(
            @Path("action") String action,
            @Field("_token") String token,
            @Field("trip_id") String trip_id,
            @Field("passenger_ids") String passenger_ids,
            @Field("coordinates") String coordinates);

    @FormUrlEncoded
    @POST("driver/end/trip")
    Call<WebResponse<TripItem>> endRide(
            @Field("_token") String token,
            @Field("trip_id") String trip_id);

    @FormUrlEncoded
    @POST("driver/ride/update-seats/{trip_id}")
    Call<WebResponse> editSeats(
            @Field("_token") String token,
            @Path("trip_id") String trip_id,
            @Field("seats") int seats);


    @FormUrlEncoded
    @POST("{role}/share-itenerary")
    Call<WebResponse> shareItinerary(
            @Path("role") String role,
            @Field("_token") String token,
            @Field("trip_id") String trip_id,
            @Field("invitee") String invitee);



    /*
    Google Apis
     */

    @GET("https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial")
    Call<MatrixApiResponse> distanceMatrixApi(
            @Query("key") String key,
            @Query("origins") String orign,
            @Query("destinations") String destination);
}