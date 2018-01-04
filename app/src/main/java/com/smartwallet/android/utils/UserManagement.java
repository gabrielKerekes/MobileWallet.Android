package com.mobilewallet.android.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by GabrielK on 06-Jan-17.
 */

public class UserManagement
{
    public static String GetImei(Context context) {

        SharedPreferences sharedPreferences = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        String imei = sharedPreferences.getString("imei", null);

        /* This is now in SplashScreenActivity because of run time requesting permissions
        TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = telephonyManager.getDeviceId();

        if(imei == null) {
            imei = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        */

        return imei;
    }

    public static boolean IsUserLoggedIn(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("uname", null);

        return username != null;
    }

    public static String GetUsername(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("uname", null);

        return username;
    }

    public static String GetAccountNumber(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        String accountNumber = sharedPreferences.getString("accountNumber", null);

        return accountNumber;
    }
}
