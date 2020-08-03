package com.seatus.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.AppCompatRatingBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.FacebookException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.seatus.BaseClasses.BaseOfferFragment;
import com.seatus.BaseClasses.FacebookAuthActivity;
import com.seatus.Fragments.ChatFragment;
import com.seatus.Fragments.Driver.DriverOfferFragment;
import com.seatus.Fragments.NotificationFragment;
import com.seatus.Fragments.Passenger.MessagingFragment;
import com.seatus.Fragments.Passenger.MyTripsFragment;
import com.seatus.Fragments.Passenger.PassengerOfferFragment;
import com.seatus.Fragments.Passenger.PaymentsFragment;
import com.seatus.Fragments.Passenger.SeatDetailsFragment;
import com.seatus.Fragments.ProfileFragment;
import com.seatus.Fragments.RateFragment;
import com.seatus.Fragments.RideDetailFragment;
import com.seatus.Fragments.UpgradeAccountFragment;
import com.seatus.HomeFragment;
import com.seatus.Interfaces.SyncContactInterface;
import com.seatus.Interfaces.WorkCompletedInterface;
import com.seatus.Models.CardItem;
import com.seatus.Models.FireStoreUserDocument;
import com.seatus.Models.NotifObject;
import com.seatus.Models.PendingRatingItem;
import com.seatus.Models.RateItem;
import com.seatus.Models.SessionExpireEvent;
import com.seatus.Models.UserItem;
import com.seatus.R;
import com.seatus.Retrofit.WebResponse;
import com.seatus.Retrofit.WebServiceFactory;
import com.seatus.Services.ForegroundLocationService;
import com.seatus.Utils.AppConstants;
import com.seatus.Utils.AppStore;
import com.seatus.Utils.BusProvider;
import com.seatus.Utils.DialogHelper;
import com.seatus.Utils.Help;
import com.seatus.Utils.PreferencesManager;
import com.seatus.Utils.StaticAppMethods;
import com.seatus.Utils.StaticMethods;
import com.seatus.Utils.permissionutils.PermissionResult;
import com.seatus.Utils.permissionutils.PermissionUtils;
import com.seatus.Views.TitleBar;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.seatus.Utils.AppConstants.onTest;
import static com.seatus.ViewModels.Resource.Status.success;

/**
 * Created by rah on 27-Sep-17.
 */

public class MainActivity extends FacebookAuthActivity {

    private static final String TAG = "MainActivity";
    public static final String ARG_PUSHDATA = "arg_push_data";

    @BindView(R.id.frame_main)
    FrameLayout frameMain;
    @BindView(R.id.btn_drawer_help)
    ImageView btnDrawerHelp;
    @BindView(R.id.txt_drawer_title_usertype)
    TextView txtDrawerTitleUsertype;
    @BindView(R.id.txt_find)
    TextView txtFind;
    @BindView(R.id.txt_create)
    TextView txtCreate;
    @BindView(R.id.txt_mytrips)
    TextView txtMytrips;
    @BindView(R.id.txt_offers)
    TextView txtOffers;
    @BindView(R.id.txt_upcomming_trips)
    TextView txtUpcommingTrips;
    @BindView(R.id.txt_pastt_rips)
    TextView txtPasttRips;
    @BindView(R.id.layout_mytrip_types)
    LinearLayout layoutMytripTypes;
    @BindView(R.id.txt_payments)
    TextView txtPayments;
    @BindView(R.id.txt_chat_btn)
    LinearLayout txtChatBtn;
    @BindView(R.id.txt_chat_badge)
    TextView txtChatBadge;
    @BindView(R.id.txt_promotions)
    TextView txtPromotions;
    @BindView(R.id.txt_invite)
    TextView txtInvite;
    @BindView(R.id.txt_notif_btn)
    LinearLayout txtNotifBtn;
    @BindView(R.id.txt_notif_badge)
    TextView txtNotifBadge;
    @BindView(R.id.txt_logout)
    TextView txtLogout;
    @BindView(R.id.layout_menu)
    LinearLayout layoutMenu;
    @BindView(R.id.checkbox_usertype)
    CheckBox switchUserType;
    @BindView(R.id.view_titlebar)
    public TitleBar titleBar;
    @BindView(R.id.drawer_layout)
    public DrawerLayout drawerLayout;
    @BindView(R.id.img_user)
    CircleImageView imgUser;
    @BindView(R.id.txt_user_name)
    TextView txtUserName;
    @BindView(R.id.txt_user_location)
    TextView txtUserLocation;
    @BindView(R.id.rating_user)
    AppCompatRatingBar ratingUser;

    @BindView(R.id.info_overlay)
    FrameLayout layout_info;
    @BindView(R.id.info_image)
    ImageView img_info;
    @BindView(R.id.info_close)
    ImageView img_close;

    @BindView(R.id.txt_driver)
    TextView txtDriverTooltip;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private int REQUEST_CHECK_SETTINGS = 823;

    public WorkCompletedInterface settingsIface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setCloseDialogEnabled(true);
        ButterKnife.bind(this);
        setParentView(drawerLayout);
        BusProvider.getInstance().register(this);
        titleBar.setDrawer(drawerLayout);
        setCloseDialogEnabled(true);

        if (getUserItem() == null) {
            finish();
            return;
        } else
            Log.e("User Item", getUserItem().logString());

        setEvents();
        setProfileOnTitleBar();
        setUnReadMessageObserver();
        refreshUserData();

        if (!getUserItem().has_sync_friends)
            initateFriendSyncProcess(true, null);


        replaceFragment(new HomeFragment());

        getActivityViewModel().refreshFriendDataRoom();

        if (getUserItem().has_pending_ratings)
            showPendingRatingsDialog();

        if (getIntent().hasExtra(ARG_PUSHDATA))
            notificationReceived(new Gson().fromJson(getIntent().getStringExtra(ARG_PUSHDATA), NotifObject.class), true);

        AppStore.getInstance().getNotificationItem().observe(this, notifObject -> {
            notificationReceived(notifObject, false);
        });
    }

    private void refreshUserData() {
        getActivityViewModel().getMyProfile(getUserItem().token).observe(this, webResponseResource -> {
            if (webResponseResource.status == success && webResponseResource.data.body != null)
                getActivityViewModel().setUserItem(webResponseResource.data.body);
        });
    }

    public void startForegroundService() {
        Intent serviceIntent = new Intent(context, ForegroundLocationService.class);
        serviceIntent.setAction(ForegroundLocationService.START);
        context.startService(serviceIntent);
    }

    public void stopForegroundService() {
        Intent serviceIntent = new Intent(context, ForegroundLocationService.class);
        serviceIntent.setAction(ForegroundLocationService.STOP);
        context.startService(serviceIntent);
    }

    private void showPendingRatingsDialog() {

        getActivityViewModel().getPendingRatings().observe(this, webResponseResource -> {
            switch (webResponseResource.status) {
                case loading:
                    showLoader();
                    break;
                case success:
                    hideLoader();
                    PendingRatingItem item = webResponseResource.data.body;
                    if (item.driver.size() > 0 || item.passenger.size() > 0) {
                        new android.support.v7.app.AlertDialog.Builder(context, R.style.CustomDialog)
                                .setTitle("Pending Ratings")
                                .setMessage("You currently have a pending rating. Would you like to compelete it now?")
                                .setPositiveButton("Yes", (dialogInterface, i) -> replaceFragmentWithBackstack(RateFragment.newInstance(item)))
                                .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss())
                                .show();
                    }
                    break;
                default:
                    hideLoader();
                    makeSnackbar(webResponseResource.data);
                    break;
            }
        });
    }

    private void setUnReadMessageObserver() {
        FirebaseFirestore.getInstance().collection(AppConstants.STORE_COLLECTION_USERS).document(getUserItem().user_id).addSnapshotListener(this, (documentSnapshot, e) -> {
            try {

                if (e != null)
                    return;

                FireStoreUserDocument userDocument = documentSnapshot.toObject(FireStoreUserDocument.class);
                if (userDocument.unread_chats > 0) {
                    txtChatBadge.setVisibility(View.VISIBLE);
                    txtChatBadge.setText(userDocument.unread_chats + "");
                } else
                    txtChatBadge.setVisibility(View.GONE);

                if (userDocument.unread_notifications > 0) {
                    txtNotifBadge.setVisibility(View.VISIBLE);
                    txtNotifBadge.setText(userDocument.unread_notifications + "");
                } else
                    txtNotifBadge.setVisibility(View.GONE);

            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
    }

    private void notificationReceived(NotifObject notifObject, boolean openedFromPush) {
        if (notifObject == null || notifObject.getAction() == null)
            return;
        else
            AppStore.getInstance().getNotificationItem().postValue(null);

        resetDelay();

        Log.e("Action", notifObject.getAction());
        switch (notifObject.getAction()) {
            case NotifObject.ACTION_NEW_INVITE:
                DialogHelper.showInviteDialog(context, getUserItem(), notifObject);
                break;
            case NotifObject.ACTION_NEW_DRIVER_INVITE:
                DialogHelper.showInviteForDriverDialog(context, getUserItem(), notifObject);
                break;
            case NotifObject.ACTION_NEW_OFFER:
                if (openedFromPush) {
                    String otherUserId = notifObject.getOtherUserId();
                    if (!isOnOffer())
                        if (notifObject.isForPassenger())
                            replaceFragmentWithBackstack(PassengerOfferFragment.newInstance(notifObject.trip_id, otherUserId));
                        else
                            replaceFragmentWithBackstack(DriverOfferFragment.newInstance(notifObject.trip_id, otherUserId));
                }
                break;
            case NotifObject.ACTION_ACCEPTED_DRIVER:
                // DialogHelper.showPassengerVerifyOffer(context, getUserItem(), notifObject);
                getCreditCards(notifObject);
                break;
            case NotifObject.ACTION_NEW_MESSAGE:
                if (openedFromPush && !(getCurrentFragment() instanceof ChatFragment))
                    replaceFragmentWithBackstack(ChatFragment.newInstance(notifObject.trip_id));
                break;
            case NotifObject.ACTION_UPDATE_TIME:
                if (openedFromPush && !isOnDetail(notifObject))
                    replaceFragmentWithBackstack(RideDetailFragment.newInstance(notifObject.trip_id));
                break;
            case NotifObject.ACTION_NEW_TRIP_SUGGESTION:
                if (openedFromPush && !isOnDetail(notifObject))
                    replaceFragmentWithBackstack(RideDetailFragment.newInstance(notifObject.trip_id));
                break;
            case NotifObject.ACTION_PASSENGER_DROPED:
                getUserItem().has_pending_ratings = true;
                PreferencesManager.putObject(AppConstants.KEY_USER, getUserItem());
                DialogHelper.showRateSingleDriverDialog(context, notifObject.driver_name, notifObject.date, (rate, comment, dialog) -> {
                    JSONArray jsonArray = new JSONArray();
                    RateItem rateItem = new RateItem();
                    rateItem.setUser_id(notifObject.driver_id);
                    rateItem.setTrip_id(notifObject.trip_id);
                    rateItem.setRating(Double.valueOf(rate));
                    rateItem.setFeedback(comment);
                    jsonArray.put(rateItem.toJsonObject());
                    getActivityViewModel().rateDrivers(jsonArray.toString()).observe(this, webResponseResource -> {
                        getUserItem().has_pending_ratings = false;
                        PreferencesManager.putObject(AppConstants.KEY_USER, getUserItem());
                        switch (webResponseResource.status) {
                            case loading:
                                showLoader();
                                break;
                            case success:
                                hideLoader();
                                makeSnackbar(webResponseResource.data.message);
                                break;
                            case error:
                                hideLoader();
                                makeToast(webResponseResource.data.message);
                                break;
                            default:
                                hideLoader();
                                break;
                        }
                    });
                });
                break;
            case NotifObject.ACTION_TRIP_CANCELED:
                Log.e("TripCanceled", "Canceled");
                break;
            default:
                if (openedFromPush && notifObject.isTripDetailNotif())
                    replaceFragmentWithBackstack(RideDetailFragment.newInstance(notifObject.trip_id));
                break;
        }
    }

    private void getCreditCards(NotifObject object) {

        getActivityViewModel().getCreditCard().observe(this, webResponseResource -> {
            switch (webResponseResource.status) {
                case loading:
                    showLoader();
                    break;
                case success:
                    hideLoader();
                    List<CardItem> mList = webResponseResource.data.body;
                    for (int a = 0; a < mList.size(); a++) {
                        CardItem cardItem = mList.get(a);
                        if (cardItem.is_default) {
                            object.card_number = "*** **** " + cardItem.last_digits;
                            DialogHelper.showPassengerVerifyOffer(context, getUserItem(), object);
                        }
                    }
                    break;
                default:
                    hideLoader();
                    makeSnackbar(webResponseResource.data);
                    break;
            }
        });
    }

    private boolean isOnOffer(NotifObject notifObject, boolean isUser) {
        Fragment frag = getCurrentFragment();
        if (isUser)
            if (frag instanceof PassengerOfferFragment) {
                PassengerOfferFragment passFrag = (PassengerOfferFragment) frag;
                if ((passFrag.trip_id.equals(notifObject.trip_id) && passFrag.other_id.equals(notifObject.getOtherUserId())))
                    return true;
            } else if (frag instanceof DriverOfferFragment) {
                DriverOfferFragment driverFrag = (DriverOfferFragment) frag;
                if ((driverFrag.trip_id.equals(notifObject.trip_id) && driverFrag.other_id.equals(notifObject.getOtherUserId())))
                    return true;
            }


        return false;
    }

    private boolean isOnDetail(NotifObject notifObject) {
        Fragment frag = getCurrentFragment();
        if (frag instanceof RideDetailFragment) {
            RideDetailFragment passFrag = (RideDetailFragment) frag;
            if (passFrag.trip_id.equals(notifObject.trip_id))
                return true;
        } else if (frag instanceof SeatDetailsFragment) {
            SeatDetailsFragment seatFrag = (SeatDetailsFragment) frag;
            if (seatFrag.tripItem.trip_id.equals(notifObject.trip_id))
                return true;
        }

        return false;
    }

    private boolean isOnOffer() {
        if (getCurrentFragment() instanceof BaseOfferFragment)
            return true;
        else
            return false;
    }


    private void setProfileOnTitleBar() {

        getUserLiveData().observe(this, userItem -> {
            txtUserName.setText(userItem.getFull_name());
            txtUserLocation.setText(String.format("%s, %s", userItem.city_text, userItem.state_text));

            if (imgUser != null && !TextUtils.isEmpty(userItem.profile_picture))
                Picasso.with(context).load(userItem.profile_picture).fit().centerCrop().into(imgUser);
            try {
                ratingUser.setRating(userItem.rating.floatValue());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        switchUserType.setChecked(getCurrentInterface());
    }

    private void setEvents() {

        layout_info.setOnClickListener(v -> Log.v("InfoWindow", "No Action"));
        titleBar.enableMenu().setMenuListener(view -> drawerLayout.openDrawer(Gravity.LEFT));
        titleBar.setInfoListener(v -> HelpActivity.launchInstance(context, getUserItem().getRoleString(), titleBar.sub_link));

        img_close.setOnClickListener(v -> {
            layout_info.setVisibility(View.GONE);
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        });
        titleBar.setBackListener(view -> actionBack());

        switchUserType.setOnCheckedChangeListener((compoundButton, b) -> {

            if (!b && getUserItem().getUser_type() == UserItem.UserType.normal) {
                drawerLayout.closeDrawer(Gravity.LEFT);
                switchUserType.setChecked(true);
                DialogHelper.showUpgradeDialog(context, dialog -> {
                    dialog.dismiss();
                    replaceFragmentWithBackstack(new UpgradeAccountFragment());
                });
                return;
            }

            setCurrentInterface(b);
            UserItem item = getUserItem();
            getActivityViewModel().setUserItem(item);
            StaticMethods.disableForSecond(handler, switchUserType);
            layoutMenu.setVisibility(View.GONE);
            txtDrawerTitleUsertype.setVisibility(View.GONE);
            drawerLayout.closeDrawer(Gravity.LEFT);

            handler.postDelayed(() -> {
                replaceFragment(new HomeFragment());
//                if (b)
//                    replaceFragment(new FindRideFragmentOld());
//                else
//                    replaceFragment(new PostTripDriverFragment());

                titleBar.setBackgroundColor(b ? ContextCompat.getColor(context, R.color.colorAccent) : ContextCompat.getColor(context, R.color.black));
                titleBar.enableDriver(!b);

                txtFind.setText(b ? "Find Ride" : "Post Ride");
//                txtCreate.setText(b ? "Create Trip" : "Post Trip"); // No longer visible
                txtDrawerTitleUsertype.setText(b ? "PASSENGER MENU" : "DRIVER MENU");
                txtDrawerTitleUsertype.setVisibility(View.VISIBLE);
                layoutMenu.setVisibility(View.VISIBLE);

            }, 300);
        });
    }

    @Override
    public void showLoader() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoader() {
        progressBar.setVisibility(View.GONE);
    }

    @Subscribe
    public void sessionExpired(SessionExpireEvent obj) {
        makeToast("Session Expired, Please login agian");
        actionLogout();
    }

    @Override
    protected int getFrameLayoutId() {
        return R.id.frame_main;
    }

    @OnClick({R.id.txt_contactus, R.id.btn_drawer_help, R.id.btn_profile, R.id.txt_find, R.id.txt_notif_btn, R.id.txt_create, R.id.txt_offers, R.id.txt_upcomming_trips, R.id.txt_pastt_rips, R.id.layout_mytrip_types, R.id.txt_payments, R.id.txt_chat_btn, R.id.txt_chat_badge, R.id.txt_promotions, R.id.txt_invite, R.id.txt_logout})
    public void onDrawerItemClicked(View view) {
        drawerLayout.closeDrawer(Gravity.LEFT);
        switch (view.getId()) {
            case R.id.btn_profile:
                replaceFragmentWithBackstack(new ProfileFragment(), 200);
                break;
            case R.id.txt_find:
                replaceFragment(new HomeFragment(), 200);
                break;
            case R.id.txt_create:
                // Button hidden
                break;
            case R.id.txt_mytrips:
                if (layoutMytripTypes.isShown()) layoutMytripTypes.setVisibility(View.GONE);
                else layoutMytripTypes.setVisibility(View.VISIBLE);
                break;
            case R.id.txt_offers:
                replaceFragmentWithBackstack(MyTripsFragment.newInstance(0), 200);
                break;
            case R.id.txt_upcomming_trips:
                replaceFragmentWithBackstack(MyTripsFragment.newInstance(1), 200);
                break;
            case R.id.txt_pastt_rips:
                replaceFragmentWithBackstack(MyTripsFragment.newInstance(2), 200);
                break;
            case R.id.layout_mytrip_types:
                break;
            case R.id.txt_payments:
                replaceFragmentWithBackstack(new PaymentsFragment(), 200);
                break;
            case R.id.txt_chat_btn:
                replaceFragmentWithBackstack(new MessagingFragment(), 200);
                break;
            case R.id.txt_chat_badge:
                break;
            case R.id.txt_promotions:
                break;
            case R.id.txt_invite:
                StaticMethods.shareIntent(context);
                break;
            case R.id.txt_notif_btn:
                replaceFragmentWithBackstack(new NotificationFragment(), 200);
                break;
            case R.id.txt_contactus:
                ContactUsActivity.launchInstance(context, "https://gowyth.com/contact/");
                break;
            case R.id.txt_logout:
                DialogHelper.showLogoutDialog(context, dialog -> {
                    dialog.startProgress();
                    WebServiceFactory.getInstance().logout(getUserItem().token).enqueue(new Callback<WebResponse>() {
                        @Override
                        public void onResponse(Call<WebResponse> call, Response<WebResponse> response) {
                            dialog.stopProgress();
                            dialog.dismiss();
                            actionLogout();
                        }

                        @Override
                        public void onFailure(Call<WebResponse> call, Throwable t) {
                            dialog.stopProgress();
                            dialog.dismiss();
                            actionLogout();
                        }
                    });
                });
                break;
            case R.id.btn_drawer_help:
                HelpActivity.launchInstance(context, getUserItem().getRoleString(), Help.Menu);
                break;
        }
    }

    @OnClick({R.id.txt_mytrips})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.txt_mytrips:
                if (layoutMytripTypes.isShown()) layoutMytripTypes.setVisibility(View.GONE);
                else layoutMytripTypes.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void actionLogout() {

        try {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("/topics/android");
            FirebaseMessaging.getInstance().unsubscribeFromTopic(String.format("/topics/user_%s", getUserItem().user_id));
            getActivityViewModel().getRoomDb().friendsDao().deleteAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            StaticMethods.clearPrefs(context);
            getActivityViewModel().setUserItem(null);
            context.startActivity(new Intent(context, AccountsActivity.class));
            context.finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        BusProvider.getInstance().unregister(this);
        super.onDestroy();
    }

    /*
    true for User false for Driver
     */
    public boolean getCurrentInterface() {
        boolean isLastNormalInterface = PreferencesManager.getBoolean(AppConstants.KEY_INTERFACE);

        if (isLastNormalInterface)
            return true;

        if (getUserItem().getUser_type() == UserItem.UserType.normal)
            return true;
        else
            return false;
    }

    public void setCurrentInterface(boolean isNormal) {
        PreferencesManager.putBoolean(AppConstants.KEY_INTERFACE, isNormal);
    }


    public void initateFriendSyncProcess(boolean isFirst, SyncContactInterface iface) {
        initFaceBook();
        if (isFirst)
            new AlertDialog.Builder(context, R.style.CustomDialog).setTitle("Sync Contacts").setMessage("Would you like to add your friends who are using " + getString(R.string.app_name) + " ?").setNegativeButton("No", null)
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        askCompactPermission(Manifest.permission.READ_CONTACTS, new PermissionResult() {
                            @Override
                            public void permissionGranted() {
                                //  initiateFbSync(iface);
                                new FetchContactsTask(iface).execute(new JSONArray());
                            }

                            @Override
                            public void permissionDenied() {
                                //   initiateFbSync(iface);
                                new FetchContactsTask(iface).execute(new JSONArray());
                            }

                            @Override
                            public void permissionForeverDenied() {
                                // initiateFbSync(iface);
                                new FetchContactsTask(iface).execute(new JSONArray());
                            }
                        });
                    }).setNegativeButton("No", (dialog, which) -> {
                try {
                    StaticAppMethods.syncFriends(null, "").enqueue(new Callback<WebResponse>() {
                        @Override
                        public void onResponse(Call<WebResponse> call, Response<WebResponse> response) {
                            handler.post(() -> {
                                try {
                                    UserItem userItem = getUserItem();
                                    userItem.has_sync_friends = true;
                                    getActivityViewModel().postUserItem(userItem);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<WebResponse> call, Throwable t) {
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).show();
        else
            askCompactPermission(Manifest.permission.READ_CONTACTS, new PermissionResult() {
                @Override
                public void permissionGranted() {
                    //initiateFbSync(iface);
                    new FetchContactsTask(iface).execute(new JSONArray());
                }

                @Override
                public void permissionDenied() {
                    //initiateFbSync(iface);
                    new FetchContactsTask(iface).execute(new JSONArray());
                }

                @Override
                public void permissionForeverDenied() {
                    // initiateFbSync(iface);
                    new FetchContactsTask(iface).execute(new JSONArray());
                }
            });
    }

    protected void initiateFbSync(SyncContactInterface iface) {
        loginWithFaceBook((loginObj, loginResponse) -> getFacebookFriends((friendsObjs, friendsResponse) -> {
            if (!getUserItem().has_facebook_integrated)
                StaticAppMethods.bindFbAccount(AccessToken.getCurrentAccessToken().getToken(), getUserItem().token);
            new FetchContactsTask(iface).execute(friendsObjs);

        }), new fbLoginFailureIface() {
            @Override
            public void loginCanceled() {
                new FetchContactsTask(iface).execute(new JSONArray());
            }

            @Override
            public void loginFailed(FacebookException e) {
                new FetchContactsTask(iface).execute(new JSONArray());
            }
        });
    }

    class FetchContactsTask extends AsyncTask<JSONArray, Void, Void> {

        private final SyncContactInterface iface;

        public FetchContactsTask(SyncContactInterface syncContactInterface) {
            this.iface = syncContactInterface;
        }

        @Override
        protected Void doInBackground(JSONArray... friendsObjs) {
            String contactStr = StaticAppMethods.syncPhoneContacts(context);
            try {
                Response<WebResponse> response = StaticAppMethods.syncFriends(friendsObjs[0], contactStr).execute();
                if (response.isSuccessful() && response.body().isSuccess()) {
                    UserItem userItem = getUserItem();
                    userItem.has_sync_friends = true;
                    getActivityViewModel().postUserItem(userItem);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (iface != null)
                iface.onContactSyncComplete();
        }
    }

    public void checkLocationEnabled(WorkCompletedInterface iface, boolean openRessolver) {
        askCompactPermission(PermissionUtils.Manifest_ACCESS_FINE_LOCATION, new PermissionResult() {


            @Override
            public void permissionGranted() {
                {
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
                    LocationRequest mLocationRequest = new LocationRequest();
                    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    builder.addLocationRequest(mLocationRequest);
                    SettingsClient client = LocationServices.getSettingsClient(context);

                    Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
                    task.addOnSuccessListener(context, locationSettingsResponse -> {
                        iface.onCompleted();
                    });
                    if (openRessolver)
                        task.addOnFailureListener(context, e -> {
                            if (e instanceof ResolvableApiException) {
                                try {
                                    settingsIface = iface;
                                    ResolvableApiException resolvable = (ResolvableApiException) e;
                                    resolvable.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (Exception sendEx) {
                                    sendEx.printStackTrace();
                                }
                            }
                        });
                }
            }

            @Override
            public void permissionDenied() {

            }

            @Override
            public void permissionForeverDenied() {
                DialogHelper.showAlertDialog(context, "You have not permitted WYTH Location access. Would you like to be navigated to the Setting now?");
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS)
            checkLocationEnabled(settingsIface, false);

    }
}