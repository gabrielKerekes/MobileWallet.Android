package com.smartwallet.android.services;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by ROCK LEE on 30.12.2016.
 */

public class ToastMaker {

    public static void makeToast(String message, Context context) {
        Toast toast = Toast.makeText(context,message,Toast.LENGTH_SHORT);
        toast.show();
    }
    public static void connectFirstToast(Context context) {
        Toast toast = Toast.makeText(context,"Connect first!",Toast.LENGTH_SHORT);
        toast.show();
    }

}
