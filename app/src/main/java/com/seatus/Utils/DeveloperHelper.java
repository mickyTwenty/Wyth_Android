package com.seatus.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DeveloperHelper {


    public static void logTokens(Activity context) {

        // Add code to print out the key hash
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getApplicationContext().getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA-1");
                md.update(signature.toByteArray());
                Log.d("SHA1-KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                Log.d("SHA1-KeyHash:::",md.toString());

                md = MessageDigest.getInstance("MD5");
                md.update(signature.toByteArray());
                Log.d("MD5-KeyHash:::",
                        Base64.encodeToString(md.digest(), Base64.DEFAULT));

                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("SHA-Hex-From-KeyHash:::", bytesToHex(md.digest()));
                md.update(signature.toByteArray());
                Log.d("KeyHash Facebook:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
        }
    }

    final protected static char[] hexArray = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String getSecureId(Activity context) {
        return Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
    }

//    public static String getImeiNumber(Activity context) {
//        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//        String imei = telephonyManager.getDeviceId();
//        return imei;
//    }
//
//
//    public static boolean isImeiAllowed(Context ctx) {
//
//        boolean isAllowed;
//        TelephonyManager telephonyManager = (TelephonyManager) ctx.getSystemService(ctx.TELEPHONY_SERVICE);
//        String imei = telephonyManager.getDeviceId();
//        logString.d("DEVICE_IMEI", imei + "");
//
//        if (imei == null)
//            imei = "";
//
//        logString.d("DEVICE ", android.os.Build.DEVICE + "");
//        logString.d("MODEL ", android.os.Build.MODEL + "");
//        logString.d("DISPLAY ", android.os.Build.DISPLAY + "");
//
//        if (imei.contains("359617040644088")  /* Galaxy Nexus Dev */
//                || imei.contains("355537050148222") /* CLIENT’S DEVICE IMEI */
//                || imei.contains("355537/05/014822/2") /* CLIENT’S DEVICE IMEI */
//                || imei.contains("355537050148222") /* CLIENT’S DEVICE IMEI */
//                || imei.contains("357138054650004") /* Testing Device Samsung s3 */
//                || imei.contains("354436053870075") /* Testing Device HTC one */
//                || imei.contains("359153053653188") /* Testing s2 */
//                ) {
//            isAllowed = true;
//        } else {
//            isAllowed = false;
//
//        }
//        return isAllowed;
//    }

}
