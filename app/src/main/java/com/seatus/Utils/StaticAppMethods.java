package com.seatus.Utils;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;

import com.github.tamir7.contacts.Contact;
import com.github.tamir7.contacts.Contacts;
import com.github.tamir7.contacts.PhoneNumber;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.gson.Gson;
import com.seatus.Interfaces.DocumentInterface;
import com.seatus.Interfaces.InvitedUsersIterface;
import com.seatus.Interfaces.WorkCompletedInterface;
import com.seatus.Models.CityItem;
import com.seatus.Models.FireStoreUserDocument;
import com.seatus.Models.SchoolItem;
import com.seatus.Models.SearchFilterDataItem;
import com.seatus.Models.StateItem;
import com.seatus.Models.TripItem;
import com.seatus.Models.UserItem;
import com.seatus.Retrofit.WebResponse;
import com.seatus.Retrofit.WebServiceFactory;
import com.seatus.Utils.database.AppDatabase;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by rohail on 23-Oct-17.
 */

public class StaticAppMethods {

    public static void fetchDocument(DocumentInterface iface) {
        FirebaseFirestore.getInstance().collection(AppConstants.STORE_COLLECTION_USERS).document(getUserItem().user_id).get().addOnCompleteListener((documentTask) -> {
            if (documentTask.isSuccessful() && documentTask.getResult().exists()) {
                FireStoreUserDocument mDocumet = documentTask.getResult().toObject(FireStoreUserDocument.class);
                iface.onDocumentFetchSuccess(mDocumet);
            } else
                iface.onDocumentFetchSuccess(null);
        }).addOnFailureListener(e -> iface.onDocumentFetchSuccess(null));
    }

    public static void fetchValidInvites(Context context, SearchFilterDataItem dataItem, FetchInvitesInterface iface) {

        iface.onTaskCompleted(InviteStatus.noInvites, null);

      /*  fetchDocument(document -> {
            if (document == null) {
                iface.onTaskCompleted(InviteStatus.connectionError, null);
                return;
            }

            if (document.last_req_edited == null || TextUtils.isEmpty(document.invite_type) || !document.invite_type.equals(dataItem.inviteType.name()))
                iface.onTaskCompleted(InviteStatus.noInvites, null);


            else if (document.isExpired()) {
                StaticAppMethods.deleteRequestData(() -> {
                    new AlertDialog.Builder(context).setTitle("Invites Expired").setMessage("Your Previous Invites have Expired, do you want to proceed with the request ?")
                            .setPositiveButton("Yes", (dialogInterface, i) -> {
                                iface.onTaskCompleted(InviteStatus.expiredContinue, null);
                            }).setNegativeButton("No", (dialogInterface, i) -> {
                        iface.onTaskCompleted(InviteStatus.expiredAbort, null);
                    }).show();
                });
            } else {
                StaticAppMethods.fetchAllInvites(documents -> {
                    if (areInvitesPending(documents))
                        iface.onTaskCompleted(InviteStatus.invitesPending, documents);
                    else
                        iface.onTaskCompleted(InviteStatus.invitesAvailaible, documents);
                });
            }
        });*/
    }

    public static void fetchValidInvitesAndDriver(Context context, SearchFilterDataItem dataItem, FetchInvitesWithDriverInterface iface) {
        fetchDocument(document -> {
            if (document == null) {
                iface.onTaskCompleted(InviteStatus.connectionError, null, null);
                return;
            }

            if (document.last_req_edited == null || TextUtils.isEmpty(document.invite_type) || !document.invite_type.equals(dataItem.inviteType.name()))
                iface.onTaskCompleted(InviteStatus.noInvites, null, null);


            else if (document.isExpired()) {
                StaticAppMethods.deleteRequestData(() -> {
                    new AlertDialog.Builder(context).setTitle("Invites Expired").setMessage("Your Previous Invites have Expired, do you want to proceed with the request ?")
                            .setPositiveButton("Yes", (dialogInterface, i) -> {
                                iface.onTaskCompleted(InviteStatus.expiredContinue, null, null);
                            }).setNegativeButton("No", (dialogInterface, i) -> {
                        iface.onTaskCompleted(InviteStatus.expiredAbort, null, null);
                    }).show();
                });
            } else {
                StaticAppMethods.fetchAllInvites(documents -> {

                    if (document.invited_driver == null) {
                        if (areInvitesPending(documents))
                            iface.onTaskCompleted(InviteStatus.invitesPending, documents, null);
                        else
                            iface.onTaskCompleted(InviteStatus.invitesAvailaible, documents, null);
                    } else {
                        if (document.invited_driver.status == null || document.invited_driver.status == 0)
                            iface.onTaskCompleted(InviteStatus.invitesPending, documents, null);
                        else {
                            if (areInvitesPending(documents))
                                iface.onTaskCompleted(InviteStatus.invitesPending, documents, null);
                            else
                                iface.onTaskCompleted(InviteStatus.invitesAvailaible, documents, document.invited_driver);
                        }
                    }
                });
            }
        });
    }

    public static int getGenderInt(String gender) {
        switch (gender) {
            case "Male":
                return 1;
            case "Female":
                return 2;
            default:
                return 3;
        }
    }

    public static String getEstimate(double lowerEstimate) {
        // Minimum offer at $5.00
        if( lowerEstimate < 5.00)
            lowerEstimate = 5.00;


        return String.format("$%.2f", lowerEstimate);
    }

    public static String getEstimate(String lowerStr, boolean isRoundtrip) {
        double lowerEstimate = Double.parseDouble(lowerStr);

        // Minimum offer at $5.00
        if( lowerEstimate < 5.00)
        {
            lowerEstimate = 5.00;
        }

        if (isRoundtrip)
            return String.format("$%.2f", lowerEstimate * 2);
        else
            return String.format("$%.2f", lowerEstimate);
    }

    public static String getEstimate(double lowerEstimate, double upperEstimate) {

        if (upperEstimate < 2.5)
            return "$5.50";

        // Minimum offer at $5.00
        if( lowerEstimate < 5.00)
        {
            upperEstimate = upperEstimate + 5.00 - lowerEstimate;
            lowerEstimate = 5.00;
        }

        return String.format("$%.2f - $%.2f", lowerEstimate, upperEstimate);
    }

    public static String getEstimate(String lowerStr, String upperStr, boolean isRoundtrip) {
        double lowerEstimate = Double.parseDouble(lowerStr);
        double upperEstimate = Double.parseDouble(upperStr);

        if (upperEstimate < 2.5)
            return "$5.50";

        // Minimum offer at $5.00
        if( lowerEstimate < 5.00)
        {
            upperEstimate = upperEstimate + 5.00 - lowerEstimate;
            lowerEstimate = 5.00;
        }

        if (isRoundtrip)
            return String.format("$%.2f - $%.2f", lowerEstimate * 2, upperEstimate * 2);
        else
            return String.format("$%.2f - $%.2f", lowerEstimate, upperEstimate);
    }

    public static void fireBaseAnalytics(Context context, String event,Map<String, Object> map) {
        FirebaseAnalytics logger = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        for (String key : map.keySet()) {
            try {
                bundle.putString(key, map.get(key).toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        bundle.remove("_token");
        bundle.putString("token", map.get("_token").toString());
        logger.logEvent(event, bundle);
    }


//    public interface FetchInvitesInterface {
//        void connectionError();
//
//        void noInvites();
//
//        void invitesExpiredContinue();
//
//        void invitesExpiredReturn();
//
//        void invitesAvailabile(List<UserItem> documents);
//
//        void invitesPending(List<UserItem> documents);
//    }

    public interface FetchInvitesInterface {
        void onTaskCompleted(InviteStatus status, List<UserItem> invitedmembers);
    }

    public interface FetchInvitesWithDriverInterface {
        void onTaskCompleted(InviteStatus status, List<UserItem> invitedmembers, UserItem driver);
    }

    public enum InviteStatus {
        connectionError,
        noInvites,
        expiredContinue,
        expiredAbort,
        invitesPending,
        invitesAvailaible
    }

    public static void fetchDocument(String user_id, DocumentInterface iface) {
        FirebaseFirestore.getInstance().collection(AppConstants.STORE_COLLECTION_USERS).document(user_id).get().addOnCompleteListener((documentTask) -> {
            if (documentTask.isSuccessful() && documentTask.getResult().exists()) {
                FireStoreUserDocument mDocumet = documentTask.getResult().toObject(FireStoreUserDocument.class);
                iface.onDocumentFetchSuccess(mDocumet);
            } else
                iface.onDocumentFetchSuccess(null);
        }).addOnFailureListener(e -> iface.onDocumentFetchSuccess(null));
    }


    public static void fetchAllInvites(InvitedUsersIterface iface) {
        FirebaseFirestore.getInstance().collection(AppConstants.STORE_COLLECTION_USERS).document(getUserItem().user_id).collection(AppConstants.STORE_COLLECTION_INVITED_MEMBERS).get().addOnCompleteListener((task) -> {
            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                iface.allInvitedMembers(task.getResult().toObjects(UserItem.class));
            } else iface.allInvitedMembers(null);
        }).addOnFailureListener(e -> {
            iface.allInvitedMembers(null);
        });
    }


    public static Call<WebResponse> syncFriends(JSONArray friendsObjs, String contactStr) throws Exception {

        StringBuilder stringBuilder = new StringBuilder();
        if (friendsObjs != null) {
            for (int i = 0; i < friendsObjs.length(); i++) {
                try {
                    if (i != 0) stringBuilder.append("-,-");
                    stringBuilder.append(friendsObjs.getJSONObject(i).get("id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
//        if (stringBuilder.length() > 0)
        return WebServiceFactory.getInstance().syncFriends(getUserItem().token, stringBuilder.toString(), contactStr);

    }

    public static UserItem getUserItem() {
        UserItem userItem = PreferencesManager.getObject(AppConstants.KEY_USER, UserItem.class);
        return userItem;
    }

    public static void setUserItem(UserItem userItem) {
        try {
            if (userItem.token == null && getUserItem() != null)
                userItem.token = getUserItem().token;
        } catch (Exception e) {
            e.printStackTrace();
        }
        PreferencesManager.putObject(AppConstants.KEY_USER, userItem);
    }

    public static void bindFbAccount(String fbToken, String token) {

        WebServiceFactory.getInstance().bindAccountWithFacebook(fbToken, token).enqueue(new Callback<WebResponse>() {
            @Override
            public void onResponse(Call<WebResponse> call, Response<WebResponse> response) {
                if (response.body().isSuccess()) {
                    UserItem userItem = getUserItem();
                    userItem.has_facebook_integrated = true;
                    setUserItem(userItem);
                }
            }

            @Override
            public void onFailure(Call<WebResponse> call, Throwable t) {

            }
        });

    }

    public static String syncPhoneContacts(Context context) {
        Contacts.initialize(context);
        List<Contact> contacts = Contacts.getQuery().hasPhoneNumber().find();
        StringBuilder stringBuilder = new StringBuilder();
        for (Contact contact : contacts) {
            for (PhoneNumber number : contact.getPhoneNumbers())
                stringBuilder.append(stringBuilder.length() == 0 ? number.getNumber() : "-,-" + number.getNumber());
        }
        return stringBuilder.toString();
//        WebServiceFactory.getInstance().syncFriends(getUserItem().token, null, stringBuilder.toString()).enqueue(null);
    }

    public static void deleteRequestData(WorkCompletedInterface iface) {
        try {
            new StaticAppMethods.DeleteCollectionAsync(iface).execute(FirebaseFirestore.getInstance().collection(AppConstants.STORE_COLLECTION_USERS).document(getUserItem().user_id).collection(AppConstants.STORE_COLLECTION_INVITED_MEMBERS));
            Map<String, Object> map = new HashMap<>();
            map.put("trip_search_data", FieldValue.delete());
            map.put("last_req_edited", FieldValue.delete());
            map.put("invite_type", FieldValue.delete());
            map.put("invited_driver", FieldValue.delete());
            FirebaseFirestore.getInstance().collection(AppConstants.STORE_COLLECTION_USERS).document(getUserItem().user_id).update(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean areInvitesPending(List<UserItem> documents) {

        if (documents == null)
            return false;

        for (UserItem item : documents)
            if (item.status == null || item.status == 0)
                return true;

        return false;

    }

    public static String getAcceptedUserIds(List<UserItem> documents) {
        StringBuilder str = new StringBuilder();

        for (UserItem item : documents)
            if (item.status != null && item.status == 1) {
                if (str.length() == 0) str.append(item.user_id);
                else str.append("-,-").append(item.user_id);
            }

        return str.toString();
    }

    public static String getAcceptedUserCount(List<UserItem> documents) {
        StringBuilder str = new StringBuilder();

        for (UserItem item : documents)
            if (item.status != null && item.status == 1) {
                if (str.length() == 0) str.append(item.user_id);
                else str.append("-,-").append(item.user_id);
            }

        return str.toString();
    }

    public static String normalizePhone(String phone) {
        return phone.replaceAll("[^\\d]", "");
    }

    public static String generateOfferId(String user_id, TripItem tripItem) {
        UserItem otherItem;
        if (tripItem.driver != null)
            otherItem = tripItem.driver;
        else
            otherItem = tripItem.passenger;

        int myId = Integer.parseInt(user_id);
        int otherId = Integer.parseInt(otherItem.user_id);

        if (myId > otherId)
            return String.format("offers_%d_%d", otherId, myId);
        else
            return String.format("offers_%d_%d", myId, otherId);


    }

    public static String generateOfferId(String user_id, String other_id) {

        int myId = Integer.parseInt(user_id);
        int otherId = Integer.parseInt(other_id);

        if (myId > otherId)
            return String.format("offers_%d_%d", otherId, myId);
        else
            return String.format("offers_%d_%d", myId, otherId);

    }

    public static class DeleteCollectionAsync extends AsyncTask<Query, Void, Void> {

        WorkCompletedInterface iface;

        public DeleteCollectionAsync(WorkCompletedInterface iface) {
            this.iface = iface;
        }

        @Override
        protected Void doInBackground(Query... queries) {

            Query query = queries[0];
            try {
                QuerySnapshot querySnapshot = Tasks.await(query.get());
                WriteBatch batch = query.getFirestore().batch();
                for (DocumentSnapshot snapshot : querySnapshot) {
                    batch.delete(snapshot.getReference());
                }
                Tasks.await(batch.commit());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (iface != null)
                iface.onCompleted();
        }
    }

    public static void checkForRecentTrip(UserItem user) {
        

        return;
    }

    public static void preloadStateCityData(Context context) {
        AppDatabase roomDb = AppDatabase.getInstance(context);
        try {
            if (roomDb.stateDoa().getTotalCount() < 1 || roomDb.cityDoa().getTotalCount() < 1) {

                String statesJson = StaticMethods.readStringFile(context, "states.json");
                String cityJson = StaticMethods.readStringFile(context, "cities.json");

                ArrayList<StateItem> listStates = StaticMethods.getArrayListFromJson(new Gson(), statesJson, StateItem.class);
                ArrayList<CityItem> listCity = StaticMethods.getArrayListFromJson(new Gson(), cityJson, CityItem.class);

                roomDb.stateDoa().insertAll(listStates);
                roomDb.cityDoa().insertAll(listCity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void preloadSchoolsData(Activity context) {
        AppDatabase roomDb = AppDatabase.getInstance(context);
        try {
            try {
                int schoolsCount = roomDb.schoolsDao().getTotalCount();
                if (schoolsCount < 1) {
                    String json = StaticMethods.readStringFile(context, "schools.json");
                    ArrayList<SchoolItem> list = StaticMethods.getArrayListFromJson(new Gson(), json, SchoolItem.class);
                    roomDb.schoolsDao().insertAll(list);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            long currTime = System.currentTimeMillis();
            long lastUpdateTime = PreferencesManager.getLong("last_update");

//            if (currTime - lastUpdateTime > 24 * 60 * 60 * 1000) {
            WebServiceFactory.getInstance().getAllSchools().enqueue(new Callback<WebResponse<ArrayList<SchoolItem>>>() {
                @Override
                public void onResponse(Call<WebResponse<ArrayList<SchoolItem>>> call, Response<WebResponse<ArrayList<SchoolItem>>> response) {
                    if (response.isSuccessful() && response.body().isSuccess() && !response.body().body.isEmpty()) {
                        roomDb.schoolsDao().deleteAll();
                        roomDb.schoolsDao().insertAll(response.body().body);
                        PreferencesManager.putLong("last_update", System.currentTimeMillis());
                        Log.v("SchoolList", "Updated");
                    }
                }

                @Override
                public void onFailure(Call<WebResponse<ArrayList<SchoolItem>>> call, Throwable t) {
                    Log.v("SchoolList", "Failed to Update");
                }
            });
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
