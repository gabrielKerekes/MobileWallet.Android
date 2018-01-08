package com.mobilewallet.android.ocrahotp;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by GabrielK on 10-Feb-17.
 */

public class HotpCounter {
    private static String HOTP_COUNTER_SHARED_PREFS = "HOTP_COUNTER_SHARED_PREFS";
    private static String HOTP_COUNTER_SHARED_PREFS_KEY = "HOTP_COUNTER_SHARED_PREFS_KEY";

    private static long GetCounter(SharedPreferences sp) {
        return sp.getLong(HOTP_COUNTER_SHARED_PREFS_KEY, 0);
    }

    public static long GetAndIncrementCounter(Context context) {
        SharedPreferences sp = context.getSharedPreferences(HOTP_COUNTER_SHARED_PREFS, Context.MODE_PRIVATE);
        long counter = GetCounter(sp);

        IncrementCounter(sp, counter);

        return counter;
    }

    private static void SaveCounter(SharedPreferences sp, long counter) {
        SharedPreferences.Editor spEditor = sp.edit();
        spEditor.putLong(HOTP_COUNTER_SHARED_PREFS_KEY, counter);
        spEditor.commit();
    }

    public static void IncrementCounter(SharedPreferences sp, long counter) {
        SaveCounter(sp, counter + 1);
    }
}
