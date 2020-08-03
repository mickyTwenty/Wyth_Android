package com.seatus.Utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.seatus.Adapters.LocationAdapter;
import com.seatus.Adapters.SchoolAdapter;
import com.seatus.Adapters.StringSearchableAdapter;
import com.seatus.Adapters.VehicleMakeAdapter;
import com.seatus.BaseClasses.BaseActivity;
import com.seatus.Dialogs.AddCardDialog;
import com.seatus.Dialogs.ChangePasswordDialog;
import com.seatus.Dialogs.DriverInvitationDialog;
import com.seatus.Dialogs.InviteItineraryDialog;
import com.seatus.Dialogs.RateDialog;
import com.seatus.Dialogs.SeatUsDialog;
import com.seatus.Dialogs.SeatsEditDialog;
import com.seatus.Dialogs.VerificationDialog;
import com.seatus.Dialogs.VerifyOfferDialog;
import com.seatus.Holders.PickDropHolder;
import com.seatus.Interfaces.PickDropSaveInterface;
import com.seatus.Interfaces.RateInterface;
import com.seatus.Interfaces.VerificationSuccessInterface;
import com.seatus.Models.BaseLocationItem;
import com.seatus.Models.FireStoreUserDocument;
import com.seatus.Models.NotifObject;
import com.seatus.Models.PromoAmount;
import com.seatus.Models.SchoolItem;
import com.seatus.Models.TripItem;
import com.seatus.Models.UserItem;
import com.seatus.Models.VehicleMake;
import com.seatus.R;
import com.seatus.Retrofit.WebResponse;
import com.seatus.Retrofit.WebServiceFactory;
import com.seatus.Views.RippleView;
import com.shawnlin.numberpicker.NumberPicker;
import com.skocken.efficientadapter.lib.adapter.EfficientRecyclerAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by rohail on 09-Aug-16.
 */
public class DialogHelper {

    public static Dialog showForgotPasswordDialog(Context context, SeatUsDialog.SeatUsDialogInterface iface) {

        return new SeatUsDialog.Builder(context, R.style.BounceDialog)
                .setHeaderImg(R.drawable.dialog_header_forgot)
                .setTitleImg(R.drawable.dialog_title_forgotpass)
                .setMessage(R.string.dialog_message_forgotpass)
                .setFieldDrawable(R.drawable.icon_email_dark)
                .setFieldInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
                .setFieldHint("Email")
                .setPositiveButton("Submit", iface)
                .setNegativeButton("Cancel")
                .show();

    }

    public static Dialog showLogoutDialog(Context context, SeatUsDialog.SeatUsDialogInterface iface) {

        return new SeatUsDialog.Builder(context, R.style.BounceDialog)
                .setHeaderImg(R.drawable.dialog_header_logout)
                .setTitleImg(R.drawable.dialog_title_logout)
                .setMessage(R.string.dialog_message_logout)
                .setPositiveButton("Yes", iface)
                .setNegativeButton("No")
                .show();
    }


    public static void showInviteDialog(Context context, UserItem userItem, NotifObject notifObject) {

        FirebaseFirestore.getInstance().collection(AppConstants.STORE_COLLECTION_USERS).document(notifObject.inviter_id).collection(AppConstants.STORE_COLLECTION_INVITED_MEMBERS).document(userItem.user_id).get().addOnCompleteListener(task -> {

            if (task.isSuccessful() && task.getResult().exists()) {
                UserItem inviteItem = task.getResult().toObject(UserItem.class);
                if (inviteItem != null && inviteItem.status != 0)
                    return;
            }

            StringBuilder message = new StringBuilder();
            message.append(notifObject.inviter_name)
                    .append(" Has invited you for a Trip")
                    .append("\nDate: ")
                    .append(DateTimeHelper.getDateToShow(notifObject.getDate()))
                    .append("\nFrom: ").append(notifObject.origin_text).append("\n To: ")
                    .append(notifObject.destination_text);

            new SeatUsDialog.Builder(context, R.style.BounceDialog)
                    .setHeaderImg(R.drawable.dialog_header_accept_invitation)
                    .setTitleImg(R.drawable.dialog_title_accept_invite)
                    .setMessage(message.toString())
                    .setMessageBold()
                    .setPositiveButton("Accept", dialog -> {
                        dialog.startProgress();
                        StaticAppMethods.fetchDocument(notifObject.inviter_id, document -> {

                            if (document == null) {
                                makeToast(context, "Connection Timeout!");
                                return;
                            }

                            if (document.isExpired()) {
                                makeToast(context, "Invite Expired");
                                return;
                            }

                            dialog.dismiss();

//                        FirebaseFirestore.getInstance().collection(AppConstants.STORE_COLLECTION_USERS).document(notifObject.inviter_id).collection(AppConstants.STORE_COLLECTION_INVITED_MEMBERS).document(userItem.user_id).update("status", 1);
                            FirebaseFirestore.getInstance().collection(AppConstants.STORE_COLLECTION_USERS).document(notifObject.inviter_id).collection(AppConstants.STORE_COLLECTION_INVITED_MEMBERS).document(userItem.user_id).update("status", 1);
                        });
                    })
                    .setNegativeButton("Reject", dialog -> {
                        dialog.dismiss();
                        FirebaseFirestore.getInstance().collection(AppConstants.STORE_COLLECTION_USERS).document(notifObject.inviter_id).collection(AppConstants.STORE_COLLECTION_INVITED_MEMBERS).document(userItem.user_id).update("status", -1);
                    })
                    .show();

        });
    }

    public static void showInviteForDriverDialog(Context context, UserItem userItem, NotifObject notifObject) {

        FirebaseFirestore.getInstance().collection(AppConstants.STORE_COLLECTION_USERS).document(notifObject.inviter_id).get().addOnCompleteListener(task -> {

            if (task.isSuccessful() && task.getResult().exists()) {
                UserItem inviteItem = task.getResult().toObject(FireStoreUserDocument.class).invited_driver;
                if (inviteItem == null || inviteItem.status != 0)
                    return;
            }

            StringBuilder message = new StringBuilder();
            message.append(notifObject.inviter_name)
                    .append(" Has invited you to Drive for a Trip")
                    .append("\nDate: ")
                    .append(DateTimeHelper.getDateToShow(notifObject.getDate()))
                    .append("\nFrom: ").append(notifObject.origin_text).append("\n To: ")
                    .append(notifObject.destination_text);

            new DriverInvitationDialog.Builder(context, R.style.BounceDialog)
                    .setMessage(message.toString())
                    .setMessageBold()
                    .showReturnSeats(notifObject.is_round_trip.equals("1"))
                    .setPositiveButton((dialog, seats, returnseats) -> {

                        dialog.startProgress();
                        StaticAppMethods.fetchDocument(notifObject.inviter_id, document -> {

                            if (document == null) {
                                makeToast(context, "Connection Timeout!");
                                return;
                            }

                            if (document.isExpired()) {
                                makeToast(context, "Invite Expired");
                                return;
                            }

                            dialog.dismiss();
                            Map<String, Object> map = new HashMap<>();
                            map.put("invited_driver.status", 1);
                            map.put("invited_driver.seats", seats);
                            map.put("invited_driver.seats_returning", returnseats);
                            FirebaseFirestore.getInstance().collection(AppConstants.STORE_COLLECTION_USERS).document(notifObject.inviter_id).update(map);

                        });
                    })
                    .setNegativeButton(v -> FirebaseFirestore.getInstance().collection(AppConstants.STORE_COLLECTION_USERS).document(notifObject.inviter_id).update("invited_driver.status", -1))
                    .show();

        });
    }

    public static Dialog showPassengerVerifyOffer(Context context, UserItem userItem, NotifObject notifObject) {


        float trip_fee = Float.parseFloat(notifObject.proposed_amount);
        float transaction_fee = (trip_fee > 5 ? AppStore.getInstance().getBootUpData().transaction_fee : AppStore.getInstance().getBootUpData().transaction_fee_local);
        float total_cost = transaction_fee + trip_fee;

        StringBuilder message = new StringBuilder();
        message.append("Driver has accepted your offer.")
                .append("\n\nDriver Name: ")
                .append(notifObject.driver_name)
                .append("\nOrigin: ")
                .append(notifObject.origin_text)
                .append("\nDestination: ")
                .append(notifObject.destination_text);

        message.append("\nDate: ").append(DateTimeHelper.parseServerDate(notifObject.date));
        if (notifObject.return_date != null && !notifObject.return_date.equals(""))
            message.append("\nReturn Date: ").append(DateTimeHelper.parseServerDate(notifObject.return_date));

        message.append("\n\nConfirm Transcation Details:")
                .append("\nTrip Fee: ")
                .append("$" + trip_fee)
                .append("\nTransaction Fee: ")
                .append("$" + transaction_fee)
                .append("\nTotal: ")
                .append("$" + total_cost)
                .append("\nCredit Card Number: ")
                .append(notifObject.card_number)
                .append("\n\nNote: Transaction fees are assessed as follows: One-way = $" + transaction_fee + " Round-Trip = $" + transaction_fee * 2)
                .append("\n\nCHARGE CARD?");


        return new VerifyOfferDialog.Builder(context, R.style.BounceDialog)
                .setHeaderImg(R.drawable.dialog_header_offer_accepted)
                .setTitleImg(R.drawable.dialog_title_accept_offer)
                .setMessage(message.toString())
                .setMessageBold()
                .setFieldInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .setFieldDrawable(R.drawable.icon_price_dark)
                .setFieldHint("Promo Code (Optional)")
                .setApplyButton(dialog -> {

                    if (dialog.getField().getText().length() == 0) {
                        dialog.getField().setError("Promo code is required");
                        return;
                    } else dialog.getField().setError(null);

                    dialog.startProgress();

                    Map<String, Object> map = new HashMap<>();
                    map.put("_token", userItem.token);
                    map.put("amount", trip_fee);
                    map.put("promo_code", dialog.getField().getText().toString());

                    WebServiceFactory.getInstance().applyPromo(map).enqueue(new Callback<WebResponse<PromoAmount>>() {
                        @Override
                        public void onResponse(Call<WebResponse<PromoAmount>> call, Response<WebResponse<PromoAmount>> response) {
                            try {
                                dialog.stopProgress();
                                if (response.isSuccessful() && response.body().isSuccess()) {
                                    PromoAmount promoAmount = response.body().body;
                                    dialog.getCostField().setText("Final Cost: $" + (promoAmount.after_discount + transaction_fee));
                                } else if (response != null && !TextUtils.isEmpty(response.body().message))
                                    DialogHelper.showAlertDialog(context, response.body().message);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<WebResponse<PromoAmount>> call, Throwable t) {
                            try {
                                dialog.dismiss();
                                makeToast(context, context.getString(R.string.error_connection));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                })
                .setPositiveButton("Yes", dialog -> {
                    dialog.startProgress();

                    Map<String, Object> map = new HashMap<>();
                    map.put("_token", userItem.token);
                    map.put("trip_id", notifObject.trip_id);
                    map.put("driver_id", notifObject.driver_id);
                    map.put("price", notifObject.proposed_amount);
                    map.put("promo_code", dialog.getField().getText().toString());

                    WebServiceFactory.getInstance().acceptOffer("passenger", map).enqueue(new Callback<WebResponse<TripItem>>() {
                        @Override
                        public void onResponse(Call<WebResponse<TripItem>> call, Response<WebResponse<TripItem>> response) {
                            try {
                                dialog.dismiss();
                                if (response.isSuccessful() && response.body().isSuccess())
                                    makeToast(context, response.body().message);
                                else if (response != null && !TextUtils.isEmpty(response.body().message))
                                    DialogHelper.showAlertDialog(context, response.body().message);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<WebResponse<TripItem>> call, Throwable t) {
                            try {
                                dialog.dismiss();
                                makeToast(context, context.getString(R.string.error_connection));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                })
                .setNegativeButton("No", dialog -> {

                    dialog.startProgress();

                    Map<String, Object> map = new HashMap<>();
                    map.put("_token", userItem.token);
                    map.put("trip_id", notifObject.trip_id);
                    map.put("driver_id", notifObject.driver_id);
                    map.put("price", notifObject.proposed_amount);

                    WebServiceFactory.getInstance().rejectOffer(map).enqueue(new Callback<WebResponse<TripItem>>() {
                        @Override
                        public void onResponse(Call<WebResponse<TripItem>> call, Response<WebResponse<TripItem>> response) {
                            try {
                                dialog.dismiss();
                                if (response.isSuccessful() && response.body().isSuccess())
                                    makeToast(context, response.body().message);
                                else if (response != null && !TextUtils.isEmpty(response.body().message))
                                    DialogHelper.showAlertDialog(context, response.body().message);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<WebResponse<TripItem>> call, Throwable t) {
                            try {
                                dialog.dismiss();
                                makeToast(context, context.getString(R.string.error_connection));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                })
                .show();
    }

    public static void makeToast(Context context, String str) {
        Toast.makeText(context, str, Toast.LENGTH_LONG).show();

    }

    public static Dialog showChangePasswordDialog(Context context, ChangePasswordDialog.ChangePassInterface iface) {
        return new ChangePasswordDialog.Builder(context, R.style.BounceDialog)
                .setPositiveButton(iface).show();
    }


    public static Dialog showEmailValidationDialog(Context context, VerificationSuccessInterface iface) {

        return new VerificationDialog.Builder(context, R.style.BounceDialog)
                .setHeaderImg(R.drawable.dialog_header_verifiction)
                .setTitleImg(R.drawable.dialog_title_verifiction)
                .setMessage(R.string.dialog_message_verification)
                .setFieldDrawable(R.drawable.icon_verification)
                .setFieldInputType(InputType.TYPE_CLASS_NUMBER)
                .setFieldHint("Validation Code")
                .setResendListener(dialog -> {
                    iface.onResend(dialog);
                })
                .setPositiveButton("Submit", dialog -> {
                    if (dialog.getField().getText().toString().length() > 6) {
                        dialog.setError(context.getString(R.string.error_validation_empty));
                        return;
                    }
                    StaticMethods.hideSoftKeyboard(context);
                    dialog.startProgress();
                    WebServiceFactory.getInstance().verifyAccount(dialog.getField().getText().toString()).enqueue(new Callback<WebResponse<UserItem>>() {
                        @Override
                        public void onResponse(Call<WebResponse<UserItem>> call, Response<WebResponse<UserItem>> response) {
                            dialog.stopProgress();
                            if (response.body().isSuccess()) {
                                dialog.dismiss();
                                ((BaseActivity) context).makeSnackbar(response.body());
                                if (response.body().body != null)
                                    iface.userVerified(response.body().body);
                            } else
                                dialog.setError(response.body().message);
                        }

                        @Override
                        public void onFailure(Call<WebResponse<UserItem>> call, Throwable t) {
                            dialog.setError(context.getString(R.string.error_connection));
                            dialog.stopProgress();
                        }
                    });
                })
                .setNegativeButton("Cancel")
                .show();
    }


    public static Dialog showNumberPickerDialog(Context context, NumberSelected iface) {
        Calendar cal = Calendar.getInstance();
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = li.inflate(R.layout.dialog_yearpicker, null, false);
        NumberPicker numberPicker = dialogView.findViewById(R.id.number_picker);
        numberPicker.setMinValue(cal.get(Calendar.YEAR));
        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.ListDialog);
        builder.setView(dialogView).setCancelable(true).setNegativeButton("Cancel", null).setPositiveButton("Done", (dialogInterface, i) -> {
            iface.numberSelected(numberPicker.getValue());
        });
        return builder.show();
    }

    public static Dialog showCarYearPicker(Context context, NumberSelected iface) {
        Calendar cal = Calendar.getInstance();
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = li.inflate(R.layout.dialog_yearpicker, null, false);
        NumberPicker numberPicker = dialogView.findViewById(R.id.number_picker);
        numberPicker.setMinValue(cal.get(Calendar.YEAR) - 15);
        numberPicker.setMaxValue(cal.get(Calendar.YEAR));
        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.ListDialog);
        builder.setView(dialogView).setCancelable(true).setNegativeButton("Cancel", null).setPositiveButton("Done", (dialogInterface, i) -> {
            iface.numberSelected(numberPicker.getValue());
        });

        return builder.show();
    }


    public static Dialog showLocationPickerDialog(Context context, Object data, FilterDialogIface iface) {

        ArrayList<BaseLocationItem> list = null;
        try {
            list = (ArrayList<BaseLocationItem>) data;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Invalid Data Type", Toast.LENGTH_LONG).show();
        }

        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = li.inflate(R.layout.dialog_searchablelist, null, false);


        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.ListDialog);
        builder.setView(dialogView).setCancelable(true).setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        ListView listView = dialogView.findViewById(R.id.listItems);
        SearchView searchView = dialogView.findViewById(R.id.search);
        TextView searchText = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchText.setTextColor(ContextCompat.getColor(context, R.color.white));
        searchView.setIconified(false);

        LocationAdapter adp = new LocationAdapter(context, list);
        listView.setAdapter(adp);

        listView.setOnItemClickListener((adapterView, view, poss, l) -> {
            iface.onItemSelected(adp.getItem(poss));
            dialog.dismiss();
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adp.getFilter().filter(newText);
                return false;
            }
        });

        dialog.show();
        return dialog;
    }

    public static Dialog showGenderDialog(Context context, DialogIface iface) {

        List<String> genderList = new ArrayList<>();
        genderList.add("Male");
        genderList.add("Female");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.item_text, genderList);

        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = li.inflate(R.layout.dialog_list, null, false);

        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.ListDialog);
        builder.setView(dialogView).setCancelable(true).setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();

        ListView listView = dialogView.findViewById(R.id.listItems);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((adapterView, view, poss, l) -> {
            iface.onItemSelected(adapter.getItem(poss));
            dialog.dismiss();
        });

        dialog.show();
        return dialog;
    }

    public static Dialog showPaymentTypeDialog(Context context, DialogIface iface) {

        List<String> genderList = new ArrayList<>();
        genderList.add("Standard");
        genderList.add("Expedited");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.item_text, genderList);

        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = li.inflate(R.layout.dialog_list, null, false);

        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.ListDialog);
        builder.setView(dialogView).setCancelable(true).setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();

        ListView listView = dialogView.findViewById(R.id.listItems);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((adapterView, view, poss, l) -> {
            iface.onItemSelected(adapter.getItem(poss));
            dialog.dismiss();
        });

        dialog.show();
        return dialog;
    }

    public static Dialog showSimpleListDialog(Context context, ArrayList<String> data, DialogIface iface) {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.item_text, data);

        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = li.inflate(R.layout.dialog_list, null, false);

        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.ListDialog);
        builder.setView(dialogView).setCancelable(true).setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();

        ListView listView = dialogView.findViewById(R.id.listItems);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((adapterView, view, poss, l) -> {
            iface.onItemSelected(adapter.getItem(poss));
            dialog.dismiss();
        });

        dialog.show();
        return dialog;
    }

    public static Dialog showVehicleMakeDialog(Context context, VehicleMakeDialog iface) {

        ArrayList<VehicleMake> list = new ArrayList<>();
        if (AppStore.getInstance().getBootUpData().make != null)
            list.addAll(AppStore.getInstance().getBootUpData().make);
        VehicleMakeAdapter adapter = new VehicleMakeAdapter(context, list);

        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = li.inflate(R.layout.dialog_searchablelist, null, false);

        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.ListDialog);
        builder.setView(dialogView).setCancelable(true).setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        ListView listView = dialogView.findViewById(R.id.listItems);
        SearchView searchView = dialogView.findViewById(R.id.search);
        TextView searchText = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchText.setTextColor(ContextCompat.getColor(context, R.color.white));
        searchView.setIconified(false);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener((adapterView, view, poss, l) -> {
            iface.onVehicleMakeSelected(adapter.getItem(poss));
            dialog.dismiss();
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        dialog.show();
        return dialog;

    }

    public static void showTermsDialog(Context context, UserItem.UserType userType, DialogInterface.OnClickListener iface) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("User Agreement")
//                .setMessage(Html.fromHtml(agreement_html))
                .setNegativeButton("Reject", null)
                .setPositiveButton("Accept", iface);
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = li.inflate(R.layout.dialog_agreement, null, false);
//        WebView webView = new WebView(context);
        WebView webView = dialogView.findViewById(R.id.webview);
        builder.setView(dialogView).show();
//        webView.loadUrl("file:///android_asset/agreement_" + ((userType == UserItem.UserType.driver) ? "driver" : "user") + ".html");
        webView.loadUrl(AppConstants.ServerUrl + "/agreement/" + ((userType == UserItem.UserType.driver) ? "driver" : "passenger"));


    }

    public interface VehicleMakeDialog {
        void onVehicleMakeSelected(VehicleMake make);
    }

    public static Dialog showSearchableListDialog(Context context, ArrayList<String> list, DialogIface iface) {

        StringSearchableAdapter adapter = new StringSearchableAdapter(context, list);

        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = li.inflate(R.layout.dialog_searchablelist, null, false);

        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.ListDialog);
        builder.setView(dialogView).setCancelable(true).setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        ListView listView = dialogView.findViewById(R.id.listItems);
        SearchView searchView = dialogView.findViewById(R.id.search);
        TextView searchText = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchText.setTextColor(ContextCompat.getColor(context, R.color.white));
        searchView.setIconified(false);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener((adapterView, view, poss, l) -> {
            iface.onItemSelected(adapter.getItem(poss));
            dialog.dismiss();
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        dialog.show();
        return dialog;

    }


    public static void showSchoolsDialog(Context context, ArrayList<SchoolItem> schools, DialogIface iface) {

        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = li.inflate(R.layout.dialog_searchablelist, null, false);


        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.ListDialog);
        builder.setView(dialogView).setCancelable(true).setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        ListView listView = dialogView.findViewById(R.id.listItems);
        SearchView searchView = dialogView.findViewById(R.id.search);
        TextView searchText = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchText.setTextColor(ContextCompat.getColor(context, R.color.white));
        searchView.setIconified(false);

        SchoolAdapter adp = new SchoolAdapter(context, schools);
        listView.setAdapter(adp);

        listView.setOnItemClickListener((adapterView, view, poss, l) -> {
            iface.onItemSelected(adp.getItem(poss).school);
            dialog.dismiss();
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adp.getFilter().filter(newText);
                return false;
            }
        });

        dialog.show();

    }


    public static Dialog showUpgradeDialog(Context context, SeatUsDialog.SeatUsDialogInterface iface) {

        return new SeatUsDialog.Builder(context, R.style.BounceDialog)
                .setHeaderImg(R.drawable.dialog_header_upgrade)
                .setTitleImg(R.drawable.dialog_title_upgrade)
                .setMessage(R.string.dialog_message_upgrade)
                .setMessageBold()
                .setPositiveButton("Proceed", iface)
                .setNegativeButton("Cancel")
                .show();
    }

    public static Dialog addCardDialog(Context context, AddCardDialog.AddCardInterface iface) {
        return new AddCardDialog.Builder(context, R.style.BounceDialog)
                .setPositiveButton(iface).show();
    }

    public static Dialog showRateDialog(Context context, RateInterface iface) {
        return new RateDialog.Builder(context, R.style.BounceDialog)
                .setPositiveButton(iface).show();
    }

    public static Dialog showRateDialog(Context context, String name, String date, RateInterface iface) {
        return new RateDialog.Builder(context, R.style.BounceDialog)
                .setDate(date)
                .setUserName(name)
                .setPositiveButton(iface).show();
    }

    public static Dialog showRateSingleDriverDialog(Context context, String name, String date, RateInterface iface) {
        return new RateDialog.Builder(context, R.style.BounceDialog)
                .setMessage("Please Rate the Driver for your Trip")
                .setMessageBold()
                .setDate(date)
                .setUserName(name)
                .setPositiveButton(iface).show();
    }

    public static Dialog showInviteItineraryDialog(Context context, InviteItineraryDialog.UserAddedInterface iface) {
        return new InviteItineraryDialog.Builder(context, R.style.BounceDialog)
                .setPositiveButton(iface).show();
    }

    public static Dialog showPickUpDropOffDialog(Context context, ArrayList<UserItem> passengers, String title, PickDropSaveInterface iface) {


        if (passengers.isEmpty())
            return null;

        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = li.inflate(R.layout.dialog_pick_drop, null, false);

//        Dialog dialog = new Dialog(context);

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView).setCancelable(true);
        AlertDialog dialog = builder.create();

        RecyclerView recyclerView = dialogView.findViewById(R.id.recycler);
        RippleView btnSave = dialogView.findViewById(R.id.btn_save);
        RippleView btnCancel = dialogView.findViewById(R.id.btn_cancel);
        TextView txtTitle = dialogView.findViewById(R.id.txt_title);

        txtTitle.setText(title);

        ArrayList<UserItem> list = new ArrayList<>();
        list.addAll(passengers);

        StaticMethods.initVerticalRecycler(context, true, recyclerView);
        recyclerView.setAdapter(new EfficientRecyclerAdapter(R.layout.item_pickdrop, PickDropHolder.class, list));


        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            StringBuilder str = new StringBuilder();
            for (UserItem user : list) {
                if (user.checked) {
                    if (str.length() == 0)
                        str.append(user.user_id);
                    else
                        str.append("-,-").append(user.user_id);
                }
            }
            dialog.dismiss();
            iface.onSave(str.toString());
        });

        dialog.show();
        return dialog;

    }

    public static Dialog showEditSeatsDialog(Context context, int initialSeats, SeatsEditDialog.EditSeatsInterface iface) {
        return new SeatsEditDialog.Builder(context, R.style.BounceDialog)
                .setInitialSeats(initialSeats)
                .setPositiveButton(iface).show();
    }


    public static Dialog showAlertDialog(Context context, String message) {
        return new AlertDialog.Builder(context).setMessage(message).setPositiveButton("Ok", null).show();
    }
//    public static Dialog showAlertDialog(Context context, String message,DialogInterface.OnCancelListener iface) {
//        return new AlertDialog.Builder(context).setMessage(message).setPositiveButton("Ok", iface).show();
//    }

    public static Dialog showAlertDialog(Context context, WebResponse body) {
        String message = "";
        if (body == null || body.message == null)
            message = context.getString(R.string.error_connection);
        else
            message = body.message;

        return new AlertDialog.Builder(context).setMessage(message).setPositiveButton("Ok", null).show();
    }

    public static Dialog showAlertDialog(Context context, String message, DialogInterface.OnClickListener onClickListener) {
        return new AlertDialog.Builder(context).setMessage(message).setNegativeButton("No", null).setPositiveButton("Yes", onClickListener).show();
    }

    // Interfaces for Dialogs

    public interface FilterDialogIface {
        void onItemSelected(BaseLocationItem item);
    }

    public interface NumberSelected {
        void numberSelected(int value);
    }

    public interface DialogIface {
        void onItemSelected(String item);
    }


}
