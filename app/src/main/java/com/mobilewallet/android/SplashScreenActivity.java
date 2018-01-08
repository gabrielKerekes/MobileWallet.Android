package com.mobilewallet.android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;

import com.mobilewallet.android.avengerstoken.AvengersMainActivity;
import com.mobilewallet.android.services.ClientManager;

/***
 * This is a AvengersMainActivity for this application.
 * It decides it he user is using this app for the first time and should registrate or if it is a regular user.
 * Also it checks if a specific permission is set, without it, the application would not work.
 */

public class SplashScreenActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    final int REQUEST_READ_PHONE_STATE = 1;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        sharedPreferences = getApplicationContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        String imei = sharedPreferences.getString("imei",null);
        if (imei == null) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
            } else {
                getImeiAndSetToPrefs();
            }
        } else {
            setPropertiesAndChangeActivity();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_PHONE_STATE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    getImeiAndSetToPrefs();
                }
                break;

            default:
                break;
        }
    }

    private void getImeiAndSetToPrefs() {
        final SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        TelephonyManager telephonyManager = (TelephonyManager)getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        String imei = telephonyManager.getDeviceId();
        if (imei == null) {
            imei = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        editor.putString("imei",imei);
        editor.apply();
        setPropertiesAndChangeActivity();
    }

    private void setPropertiesAndChangeActivity() {
        String username = sharedPreferences.getString("uname", null);
        String email = sharedPreferences.getString("email", null);
        String accountNumber = sharedPreferences.getString("accountNumber", null);
        String bic = sharedPreferences.getString("bic", null);

        if (username != null) {

            ClientManager.userName = username;
            ClientManager.email = email;
            ClientManager.accountNumber = accountNumber;
            ClientManager.BIC = bic;

            Intent myIntent = new Intent(SplashScreenActivity.this, SuperwalletMainActivity.class);
            SplashScreenActivity.this.startActivity(myIntent);

        }
        else {
            Intent myIntent = new Intent(SplashScreenActivity.this, AvengersMainActivity.class);
            SplashScreenActivity.this.startActivity(myIntent);
        }
        SplashScreenActivity.this.finish();
    }
}
