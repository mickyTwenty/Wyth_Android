package com.seatus.Utils;

import android.text.TextUtils;
import android.text.format.DateUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by rohail on 27-Mar-17.
 */

public class DateTimeHelper {

    public static String DATE_TO_SHOW_FORMAT = "MM/dd/yyyy";
    public static String DATE_SERVER_FORMAT = "yyyy-MM-dd hh:mm:ss";
//    String DATE_TO_SHOW_FORMAT = "dd-MM-yyyy";

    public static String getFormattedDate(Date mDate) {
//        SimpleDateFormat daysFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        SimpleDateFormat daysFormat = new SimpleDateFormat(DATE_TO_SHOW_FORMAT, Locale.US);
        return daysFormat.format(mDate);
    }

    public static String getForServerDate(Date mDate) {
        try {
            SimpleDateFormat daysFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            return daysFormat.format(mDate);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date parseDate(String date) {
        try {
            SimpleDateFormat daysFormat = new SimpleDateFormat(DATE_TO_SHOW_FORMAT, Locale.US);
            return daysFormat.parse(date);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String parseServerDate(String date) {
        try {
            SimpleDateFormat daysFormat = new SimpleDateFormat(DATE_SERVER_FORMAT, Locale.US);
            Date date1 =  daysFormat.parse(date);
            return getDateToShow(date1);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date parseServerDateDate(String date) {
        try {
            SimpleDateFormat daysFormat = new SimpleDateFormat(DATE_SERVER_FORMAT, Locale.US);
            return daysFormat.parse(date);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date parseDate(String date, String format) {
        try {
            SimpleDateFormat daysFormat = new SimpleDateFormat(format, Locale.US);
            return daysFormat.parse(date);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getForServerTime(Date mDate) {
        SimpleDateFormat daysFormat = new SimpleDateFormat("HH:mm", Locale.US);
        return daysFormat.format(mDate);
    }

    public static String getFormattedDate(Date mDate, String format) {
        SimpleDateFormat daysFormat = new SimpleDateFormat(format, Locale.US);
        return daysFormat.format(mDate);
    }

    public static String getDateToShow(String date_time) {
        if (TextUtils.isEmpty(date_time))
            return "";
        String dateStr = "";
        SimpleDateFormat serverFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try {
            Date date = serverFormat.parse(date_time);
            SimpleDateFormat showFormat = new SimpleDateFormat(DATE_TO_SHOW_FORMAT, Locale.US);
            dateStr = showFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateStr;
    }

    public static String getDateToShow(Date date_time) {
        String dateStr = "";
        try {
            SimpleDateFormat showFormat = new SimpleDateFormat(DATE_TO_SHOW_FORMAT, Locale.US);
            dateStr = showFormat.format(date_time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateStr;
    }

    public static String getDateTimeToShow(String date_time) {
        String dateStr = "";
        SimpleDateFormat serverFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        try {
            Date date = serverFormat.parse(date_time);
            SimpleDateFormat showFormat = new SimpleDateFormat(DATE_TO_SHOW_FORMAT + " h:mm a", Locale.US);
            dateStr = showFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateStr;
    }

    public static String getDateTimeToShow(Date date_time) {
        try {
            SimpleDateFormat showFormat = new SimpleDateFormat(DATE_TO_SHOW_FORMAT + " h:mm a", Locale.US);
            return showFormat.format(date_time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getTimeToShow(long date_time) {
        try {
            Date date = new Date(date_time);
            SimpleDateFormat showFormat = new SimpleDateFormat("h:mm a", Locale.US);
            return showFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static long convertFromUTC(long date_time) {
        try {
            int offset = TimeZone.getDefault().getRawOffset() + TimeZone.getDefault().getDSTSavings();
            return offset + date_time;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0l;
    }

    public static String getChatDateTime(Date date1) {
        try {
            if (DateUtils.isToday(date1.getTime())) {

                SimpleDateFormat showFormat = new SimpleDateFormat("h:mm a", Locale.US);
                return showFormat.format(date1);

            } else {
                SimpleDateFormat showFormat = new SimpleDateFormat(DATE_TO_SHOW_FORMAT + " h:mm a", Locale.US);
                return showFormat.format(date1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    public static boolean isEndDateIsGreater(String sDate, String eDate) {
        try {

            SimpleDateFormat daysFormat = new SimpleDateFormat(DATE_TO_SHOW_FORMAT, Locale.US);
            Date date1 = daysFormat.parse(sDate);
            Date date2 = daysFormat.parse(eDate);

            if (date1.compareTo(date2) < 0 || date1.compareTo(date2) == 0) {
                return true;
            }

            return false;
        } catch (ParseException e1) {
            e1.printStackTrace();
            return false;
        }
    }

    public static boolean isEndDateTimeIsGreater(String sDate, String eDate) {
        try {

            SimpleDateFormat daysFormat = new SimpleDateFormat(DATE_SERVER_FORMAT, Locale.US);
            Date date1 = daysFormat.parse(sDate);
            Date date2 = daysFormat.parse(eDate);

            if (date1.compareTo(date2) < 0 || date1.compareTo(date2) == 0) {
                return true;
            }

            return false;
        } catch (ParseException e1) {
            e1.printStackTrace();
            return false;
        }
    }

    public static Date getNonUTCTimeStamp(Date tag) {
        Calendar rightNow = Calendar.getInstance();
        long offset = rightNow.get(Calendar.ZONE_OFFSET) +
                rightNow.get(Calendar.DST_OFFSET);
        long millis = tag.getTime() + offset;
        tag.setTime(millis);
        return tag;
    }

    public static String currentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();

        return dateFormat.format(date);
    }

    public static String currentDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date = new Date();

        return dateFormat.format(date);
    }

    public static Calendar toCalendar(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }
}
