package com.mobilewallet.android.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by GabrielK on 03-Nov-16.
 */

public class ServiceIp
{
    public static final String SCHOOL_SERVICE_IP = "147.175.98.16";

    private static final String IP_SHARED_PREFS = "serviceIp";
    private static final String IP_SHARED_PREFS_KEY = "serviceIpKey";
    private static final String URI_DEFAULT_VALUE = "https://147.175.98.16:8443/service";

    public static void SaveIp(Context context, String ip)
    {
        SharedPreferences sp = context.getSharedPreferences(IP_SHARED_PREFS, Context.MODE_PRIVATE);

        SharedPreferences.Editor spEditor = sp.edit();
        spEditor.putString(IP_SHARED_PREFS_KEY, ip);
        spEditor.commit();
    }

    public static String GetIp(Context context)
    {
        SharedPreferences sp = context.getSharedPreferences(IP_SHARED_PREFS, Context.MODE_PRIVATE);
        return sp.getString(IP_SHARED_PREFS_KEY, URI_DEFAULT_VALUE);
    }
}
