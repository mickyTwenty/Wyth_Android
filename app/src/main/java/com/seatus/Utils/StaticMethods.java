package com.seatus.Utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DownloadManager;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.seatus.Dialogs.DigitalTimePickerDialog;
import com.seatus.Interfaces.WorkCompletedInterface;
import com.seatus.R;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static android.content.Context.DOWNLOAD_SERVICE;
import static android.provider.DocumentsContract.getDocumentId;

/**
 * Created by rohail on 4/15/2016.
 */
public class StaticMethods {


    public static long convertTimeToUTC(Calendar selectedTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.setTime(selectedTime.getTime());
        return calendar.getTimeInMillis();
    }

    public static long convertTimeToUTC(Date selectedTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.setTime(selectedTime);
        return calendar.getTimeInMillis();
    }

    public static long getUTCTimestamp() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        return calendar.getTimeInMillis();
    }

    public static Uri getUriFromBitmap(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public static float convertSpToPixels(float sp, Context context) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }

    public static void writeLogToFile(String text, String filename) {
        try {
            File logFile = new File(new StringBuilder("sdcard/").append(filename).append(".txt").toString());
            if (!logFile.exists()) {
                try {
                    logFile.createNewFile();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public static void initPreferences(Context context) {
        PreferencesManager.createInstance(context, AppConstants.PreferencesName);
    }

    public static void clearPrefs(Context context) {
        PreferencesManager.putObject(AppConstants.KEY_USER, null);
        PreferencesManager.putObject(AppConstants.KEY_TEMP_FILTERS, null);
    }

    public static void hideSoftKeyboard(Context context) {
        try {
            InputMethodManager inputManager = (InputMethodManager) context
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(((Activity) context).getCurrentFocus()
                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
        }
    }

    public static boolean checkFieldNotEmpty(View fieldStory) {

        if (fieldStory instanceof EditText) {
            EditText editText = (EditText) fieldStory;
            String str = editText.getText().toString().trim();
            if (isEmptyOrNull(str))
                return false;
            else
                return true;
        }

        return false;
    }


    public static void datePopup(Context context, final TextView text, boolean allowPastDates) {

        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        // Launch Date Picker Dialog
        DatePickerDialog dpd = new DatePickerDialog(context,
                (view, year, monthOfYear, dayOfMonth) -> {
                    Calendar cal = Calendar.getInstance();
                    cal.set(year, monthOfYear, dayOfMonth);
                    String formatedServerDate = DateTimeHelper.getFormattedDate(cal.getTime());
                    text.setText(formatedServerDate);
                    text.setTag(cal.getTime());
                }, mYear, mMonth, mDay);

        if (!allowPastDates)
            dpd.getDatePicker().setMinDate(c.getTimeInMillis());

        dpd.show();
    }

    public static void datePopup(Context context, final TextView text, WorkCompletedInterface iface) {

        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        // Launch Date Picker Dialog
        DatePickerDialog dpd = new DatePickerDialog(context,
                (view, year, monthOfYear, dayOfMonth) -> {
                    Calendar cal = Calendar.getInstance();
                    cal.set(year, monthOfYear, dayOfMonth);
                    String formatedServerDate = DateTimeHelper.getFormattedDate(cal.getTime());
                    text.setText(formatedServerDate);
                    text.setTag(cal.getTime());
                    iface.onCompleted();
                }, mYear, mMonth, mDay);

        dpd.getDatePicker().setMinDate(c.getTimeInMillis());

        dpd.show();
    }

    public static void datePopup(Context context, final TextView text, boolean allowPastDates, WorkCompletedInterface iface) {

        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        // Launch Date Picker Dialog
        DatePickerDialog dpd = new DatePickerDialog(context,
                (view, year, monthOfYear, dayOfMonth) -> {
                    Calendar cal = Calendar.getInstance();
                    cal.set(year, monthOfYear, dayOfMonth);
                    String formatedServerDate = DateTimeHelper.getFormattedDate(cal.getTime());
                    text.setText(formatedServerDate);
                    text.setTag(cal.getTime());
                    iface.onCompleted();
                }, mYear, mMonth, mDay);

        if (!allowPastDates)
            dpd.getDatePicker().setMinDate(c.getTimeInMillis());

        dpd.show();
    }

    public static void datePopup(Context context, final TextView text, boolean allowPastDates, boolean allowFutureDates, int initYear) {

        final Calendar c = Calendar.getInstance();
//        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        // Launch Date Picker Dialog
        DatePickerDialog dpd = new DatePickerDialog(context,
                (view, year, monthOfYear, dayOfMonth) -> {
                    Calendar cal = Calendar.getInstance();
                    cal.set(year, monthOfYear, dayOfMonth);
                    String formatedServerDate = DateTimeHelper.getFormattedDate(cal.getTime());
                    text.setText(formatedServerDate);
                    text.setTag(cal.getTime());
                }, initYear, mMonth, mDay);

        if (!allowPastDates)
            dpd.getDatePicker().setMinDate(c.getTimeInMillis());
        if (!allowFutureDates)
            dpd.getDatePicker().setMaxDate(c.getTimeInMillis());

        try {
            dpd.getDatePicker().getTouchables().get(0).performClick();
        } catch (Exception e) {
            e.printStackTrace();
        }

        dpd.show();
    }

    public static void timePopup(Context context, final TextView text) {

        final Calendar c = Calendar.getInstance();
        int Hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);

        TimePickerDialog tpd = new TimePickerDialog(context,
                (timePicker, hour, mins) -> {
                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.HOUR_OF_DAY, hour);
                    cal.set(Calendar.MINUTE, mins);
                    text.setText(DateTimeHelper.getFormattedDate(cal.getTime(), "hh:mm"));
                }, Hour, min, true);
        tpd.show();

    }

    public static void downloadWithManager(Context context, View view, String fileUrl) {


        if (view.getTag() != null && view.getTag().toString().equals("done")) {
//                        makeSnackbar("File already download, please check in downloads folder");
            Intent i = new Intent();
            i.setAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
            context.startActivity(i);
        } else {
            DownloadManager dm = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(
                    Uri.parse(fileUrl));
            request.setDestinationInExternalPublicDir("/Download", URLUtil.guessFileName(fileUrl, null, null));
            dm.enqueue(request);
        }
        view.setTag("done");


//        DownloadManager dm = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
//        DownloadManager.Request request = new DownloadManager.Request(
//                Uri.parse(fileUrl));
//        request.setDestinationInExternalPublicDir("/Download", URLUtil.guessFileName(fileUrl, null, null) );
//        dm.enqueue(request);
    }

    public static void disableForSecond(Handler handler, View view) {
        view.setEnabled(false);
        handler.postDelayed(() -> view.setEnabled(true), 1000);
    }

    public static void initVerticalRecycler(Context context, RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    public static void initVerticalRecycler(Context context, boolean divider, RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        if (divider)
            recyclerView.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
    }

    public static void initHorizontalRecycler(Context context, RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    public static String getEstimateFare(Long meters) {
        try {
            double miles = meters.doubleValue() * 0.000621371;
            return String.format(Locale.US, "$ %.2f", miles * AppConstants.RATE_PER_MILE);
        } catch (Exception e) {
            e.printStackTrace();
            return "Not available";
        }
    }

    public static Estimate getEstimate(Long meters, float rate) {
        double value;
        String text;
        try {
            double miles = meters.doubleValue() * 0.000621371;
            value = miles * rate;
            text = String.format(Locale.US, "$ %.2f", value);
        } catch (Exception e) {
            e.printStackTrace();
            text = "Not available";
            value = 0d;
        }

        return new Estimate(value, text);
    }

    public static String removeSpecialCharacters(String str) {
        return str.replaceAll("[-+.^:,]", "");
    }

    public static class Estimate {
        public double value;
        public String text;

        public Estimate(double value, String text) {
            this.value = value;
            this.text = text;
        }
    }

    public static double getMilesFromMeters(Long meters) {
        return meters.doubleValue() * 0.000621371;
    }

    public static LatLng parseLatLng(String latnlng) {
        try {
            String[] tokens = latnlng.split(",");
            return new LatLng(Double.parseDouble(tokens[0]), Double.parseDouble(tokens[1]));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static LatLngBounds getLatLngBounds(LatLng latlng1, LatLng latlng2) {
        LatLngBounds LatLngbounds = LatLngBounds.builder()
                .include(new LatLng(latlng1.latitude, latlng1.longitude))
                .include(new LatLng(latlng2.latitude, latlng2.longitude)).build();
        return LatLngbounds;
    }

    public interface DateTimeSelectedIface {
        public void onDateTimeSelected(Calendar cal);
    }

    public static void datePopup(Context context, final TextView text, DateTimeSelectedIface iFace) {

        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        // Launch Date Picker Dialog
        DatePickerDialog dpd = new DatePickerDialog(context,
                (view, year, monthOfYear, dayOfMonth) -> {
                    Calendar cal = Calendar.getInstance();
                    cal.set(year, monthOfYear, dayOfMonth);
                    text.setText(DateTimeHelper.getFormattedDate(cal.getTime()));
                    iFace.onDateTimeSelected(cal);
                }, mYear, mMonth, mDay);

        dpd.getDatePicker().setMinDate(c.getTimeInMillis());
        dpd.show();
    }

    public static void timePopup(Context context, final TextView text, DateTimeSelectedIface iFace) {

        final Calendar c = Calendar.getInstance();
        int Hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);

        TimePickerDialog tpd = new TimePickerDialog(context,
                (timePicker, hour, mins) -> {
                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.HOUR_OF_DAY, hour);
                    cal.set(Calendar.MINUTE, mins);
                    text.setText(DateTimeHelper.getFormattedDate(cal.getTime(), "hh:mm"));
                    iFace.onDateTimeSelected(cal);
                }, Hour, min, false);
        tpd.show();
    }

    public static void timePopup(Context context, DateTimeSelectedIface iFace) {

        final Calendar c = Calendar.getInstance();
        int Hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);

        DigitalTimePickerDialog tpd = new DigitalTimePickerDialog(context, android.R.style.Theme_Holo_Light_Dialog,
                (timePicker, hour, mins) -> {
                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.HOUR_OF_DAY, hour);
                    cal.set(Calendar.MINUTE, mins);
                    iFace.onDateTimeSelected(cal);
                }, Hour, min, false);
        tpd.show();
    }

    public static void timePopup(Context context, long defaultDateMillis, DateTimeSelectedIface iFace) {

        final Calendar c = Calendar.getInstance();
        int Hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);

        DigitalTimePickerDialog tpd = new DigitalTimePickerDialog(context, android.R.style.Theme_Holo_Light_Dialog,
                (timePicker, hour, mins) -> {
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(defaultDateMillis);
                    cal.set(Calendar.HOUR_OF_DAY, hour);
                    cal.set(Calendar.MINUTE, mins);
                    iFace.onDateTimeSelected(cal);
                }, Hour, min, false);

        tpd.show();
    }

    public static boolean isEmpty(String str, int count) {
        return (isEmptyOrNull(str) || str.length() < count) ? true : false;
    }


    public static boolean checkFieldNotEmpty(View fieldStory, int count) {

        if (fieldStory instanceof EditText) {


            EditText editText = (EditText) fieldStory;
            String str = editText.getText().toString().trim();
            if (isEmptyOrNull(str) || str.length() < count)
                return false;
            else
                return true;
        }

        return false;
    }

    public static boolean isNull(String _field) {
        if (_field == null)
            return true;
        else
            return false;

    }

    public static boolean isNull(Object _field) {
        if (_field == null)
            return true;
        else
            return false;

    }

    public static String getPlayStoreLink(Context context) {
        try {
            String pkg = context.getPackageName();
            return "http://play.google.com/store/apps/details?id=" + pkg;
        } catch (Exception e) {
            return "";
        }
    }

    public static String getApplicationName(Context context) {
        int stringId = context.getApplicationInfo().labelRes;
        return context.getString(stringId);
    }

    public static String capitalizeFirstChar(String str) {
        try {
            return str.substring(0, 1).toUpperCase() + str.substring(1);
        } catch (Exception e) {
            return "";
        }
    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target)
                && android.util.Patterns.EMAIL_ADDRESS.matcher(target)
                .matches();
    }

    public static boolean isEmptyOrNull(String string) {
        try {
            if (string == null)
                return true;
            return (string.trim().length() <= 0);
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    public static boolean isEmptyOrNull(String string, int min) {
        if (string == null)
            return true;
        return (string.trim().length() < min);
    }

    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "title", null);
            return Uri.parse(path);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap overlayBitmap(Bitmap bmp1, Bitmap bmp2) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, new Matrix(), null);
        canvas.drawBitmap(bmp2, new Matrix(), null);
        return bmOverlay;
    }

    public static Bitmap overlayWaterMark(Bitmap image, Bitmap border, Bitmap watermark, int opacity) {
        Bitmap bmOverlay = Bitmap.createBitmap(image.getWidth(), image.getHeight(), image.getConfig());
        Canvas canvas = new Canvas(bmOverlay);

        canvas.drawBitmap(image, new Matrix(), null);
        canvas.drawBitmap(border, new Matrix(), null);
        Paint alphaPaint = new Paint();
        alphaPaint.setAlpha(50);
        canvas.drawBitmap(watermark, canvas.getWidth() - watermark.getWidth() - 30, canvas.getHeight() - watermark.getHeight() - 30, alphaPaint);
        return bmOverlay;
    }

    public static Bitmap overlayWaterMark(Bitmap image, Bitmap watermark, int opacity) {

        int padding = 50;
        try {
            if (watermark.getWidth() > watermark.getHeight())
                padding = image.getWidth() / 100 * 5;
            else
                padding = image.getHeight() / 100 * 5;
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bitmap bmOverlay = Bitmap.createBitmap(image.getWidth(), image.getHeight(), image.getConfig());
        Canvas canvas = new Canvas(bmOverlay);

        canvas.drawBitmap(image, new Matrix(), null);
        Paint alphaPaint = new Paint();
        Log.e("WaterMark Opacity", opacity + "");
        alphaPaint.setAlpha(255 / 100 * opacity);
        canvas.drawBitmap(watermark, canvas.getWidth() - watermark.getWidth() - padding, padding + 10, alphaPaint);
        return bmOverlay;
    }

    public static boolean isAppInstalled(Context context, String pckStr) {
        try {
            context.getPackageManager().getPackageInfo(pckStr, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
//        bm.recycle();
        return resizedBitmap;
    }


    public static void shareIntent(Activity context) {
        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));
            String strShareMessage = "\nCheck out this cool new app called Wyth - it's a city-to-city carpooling app designed exclusively for college students. Download it now at Google play link.\n\n";
            strShareMessage = strShareMessage + "https://play.google.com/store/apps/details?id=" + context.getPackageName();
            i.putExtra(Intent.EXTRA_TEXT, strShareMessage);
            context.startActivity(Intent.createChooser(i, "Share via"));
        } catch (Exception e) {
            //e.toString();
        }
    }

    public static void openInBrowser(Activity context, String url) {
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(browserIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void openUrlWithPackage(Activity context, String url, String pckg) {
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            browserIntent.setPackage(pckg);
            if (isIntentAvailable(context, browserIntent))
                context.startActivity(browserIntent);
            else
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isIntentAvailable(Context ctx, Intent intent) {
        final PackageManager packageManager = ctx.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    public static File convertBitmapToFile(Context context, Bitmap mBitmap) {
        String filePath = new StringBuilder().append(context.getCacheDir()).append("/postimage_temp.png").toString();
        File f = new File(context.getCacheDir(), "postimage_temp.png");
        try {

            File file = new File("postimage_temp.png");
            if (file.exists()) {
                file.delete();
                file = new File(context.getCacheDir(), "postimage_temp.png");
                Log.e("file exist", "" + file + ",Bitmap = postimage_temp.png");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            // create a file to write bitmap data
            f.createNewFile();

            // Convert bitmap to byte array
            Bitmap bitmap = mBitmap;

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0 /* ignored for PNG */, bos);
            byte[] bitmapdata = bos.toByteArray();

            // write the bytes in file
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return f;

    }

    public static File savebitmap(Context context, Bitmap bitmap) {
        String filename = "shareimg_temp";
        String extStorageDirectory = context.getCacheDir().toString();
        OutputStream outStream = null;

        File file = new File(extStorageDirectory, filename + ".jpg");
        if (file.exists()) {
            file.delete();
            file = new File(extStorageDirectory, filename + ".jpg");
            Log.e("file exist", "" + file + ",Bitmap= " + filename);
        }
        try {
            // make a new bitmap from your file

            outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("file", "" + file);
        return file;

    }

    public static File savebitmap(Context context, Bitmap bitmap, String filename) {
        String extStorageDirectory = context.getCacheDir().toString();
        OutputStream outStream = null;

        File file = new File(extStorageDirectory, filename + ".png");
        if (file.exists()) {
            file.delete();
            file = new File(extStorageDirectory, filename + ".png");
            Log.e("file exist", "" + file + ",Bitmap= " + filename);
        }
        try {
            // make a new bitmap from your file
            outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("file", "" + file);
        return file;

    }

    @SuppressWarnings("rawtypes")
    public static boolean isEmptyOrNull(ArrayList arrayList) {
        if (arrayList == null)
            return true;

        return (arrayList.size() <= 0);
    }

    @SuppressWarnings("rawtypes")
    public static boolean isEmptyOrNull(ArrayList arrayList, int minimum) {
        if (arrayList == null)
            return true;

        return (arrayList.size() <= minimum - 1);
    }

    public static <T> ArrayList<T> getArrayListFromJson(Gson gson, String String, Class<T> clazz) {
        try {
            ArrayList<T> list = new ArrayList<>();
            JsonArray arr = new JsonParser().parse(String).getAsJsonArray();
            for (JsonElement element : arr)
                list.add(gson.fromJson(element, clazz));

            return list;
        } catch (Exception e) {
            return null;
        }
    }

    public static void hideSoftKeyboard(Activity activity, EditText mEdit) {
        try {
            InputMethodManager inputManager = (InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(mEdit.getWindowToken(), 0);
        } catch (Exception e) {
        }
    }

    public static int convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }

    public static int getScreenWidth(Activity context) {
        Display display = context.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        return width;
    }

    public static int getScreenHeight(Activity context) {
        Display display = context.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;
        return height;
    }

    public static String validateEmptyString(String string) {
        return validateEmptyString(string, "");
    }

    public static String validateEmptyString(String string, String defaultValue) {
        if (isEmptyOrNull(string))
            return defaultValue;

        return string;
    }

    public static int getPixelsFromDps(int dp, Context context) {
        Resources r = context.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                r.getDisplayMetrics());
    }

    public static String getFormattedDate(int secondsToAdd, String dateFormat) {
        long time = System.currentTimeMillis();
        time = time + (secondsToAdd * 1000);
        Date date = new Date(time);
        SimpleDateFormat postFormater = new SimpleDateFormat(dateFormat);
        return postFormater.format(date);
    }

    public static Map<String, Typeface> typefaceCache = new HashMap<String, Typeface>();


    @Nullable
    public static RequestBody getRequestBody(String str) {
        try {
            if (StaticMethods.isEmptyOrNull(str))
                return null;
            try {
                return RequestBody.create(MediaType.parse("multipart/form-data"), str);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    public static RequestBody getRequestBody(int integer) {
        if (integer == 0)
            return null;
        else
            return RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(integer));
    }


    public static ArrayList<MultipartBody.Part> getMultiPartBodyList(String paramName, ArrayList<File> files) {

        ArrayList<MultipartBody.Part> partList = new ArrayList<>();

        try {
            if (paramName == null)
                paramName = "image";

            int count = 0;
            for (File file : files) {
                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                partList.add(MultipartBody.Part.createFormData(paramName + "[]", file.getName(), requestFile));
                count++;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        if (partList.isEmpty())
            return null;
        return partList;

    }

    public static ArrayList<MultipartBody.Part> getMultiPartBodyList_Integer(String paramName, ArrayList<Integer> intArray) {

        ArrayList<MultipartBody.Part> partList = new ArrayList<>();
        try {
            int count = 0;
            for (int i : intArray) {
                partList.add(MultipartBody.Part.createFormData(paramName + "[]", String.valueOf(i)));
                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if (partList.isEmpty())
            return null;
        return partList;

    }

    public static MultipartBody.Part getMultiPartBody(String paramName, File file) {
        try {
            if (file == null)
                return null;
            if (paramName == null)
                paramName = "image";
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData(paramName, file.getName(), requestFile);
            return body;
        } catch (Throwable e) {
            return null;
        }
    }

    public static String getFileName(String path) {
        try {
            return path.substring(path.lastIndexOf("/") + 1);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


//    public static void setLinearRecycler(Context context, RecyclerView recyclerCertificate, boolean AddSeperator) {
//        LinearLayoutManager lm = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
//        recyclerCertificate.setLayoutManager(lm);
//        if (AddSeperator)
//            recyclerCertificate.addItemDecoration(new VerticalSpaceItemDecoration(context.getResources().getDimension(R.dimen.listview_vertical_space)));
//    }

    public static String readStringFile(Context context, String filename) {
        String json = "";
        try {
            InputStream is = context.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return json;
    }

    public static String readStringFile(Context context, File file) {
        String json = "";
        try {
            InputStream is = new FileInputStream(file);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return json;
    }


    @SuppressLint("NewApi")
    public static String getRealPath(final Context context, final Uri uri) {

        try {
            final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

            // DocumentProvider
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }

                    // TODO handle non-primary volumes
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {

                    final String id = getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    return getDataColumn(context, contentUri, null, null);
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId = getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{
                            split[1]
                    };

                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
            // MediaStore (and general)
            else if ("content".equalsIgnoreCase(uri.getScheme())) {
                return getDataColumn(context, uri, null, null);
            }
            // File
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {

            if (isExternalStorageDocument(uri)) {// ExternalStorageProvider
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                String storageDefinition;


                if ("primary".equalsIgnoreCase(type)) {

                    return Environment.getExternalStorageDirectory() + "/" + split[1];

                } else {

                    if (Environment.isExternalStorageRemovable()) {
                        storageDefinition = "EXTERNAL_STORAGE";

                    } else {
                        storageDefinition = "SECONDARY_STORAGE";
                    }

                    return System.getenv(storageDefinition) + "/" + split[1];
                }

            } else if (isDownloadsDocument(uri)) {// DownloadsProvider

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);

            } else if (isMediaDocument(uri)) {// MediaProvider
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }

        } else if ("content".equalsIgnoreCase(uri.getScheme())) {// MediaStore (and general)

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);

        } else if ("file".equalsIgnoreCase(uri.getScheme())) {// File
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }


    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }


    public static String getLocationString(Activity activity, Location location) {
        if (location == null)
            return "";
        Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            return addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}
