package com.mobilewallet.android.avengerstoken;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import com.mobilewallet.android.R;
import com.mobilewallet.android.avengerstoken.PinDialog.PinDialogFragment;
import com.mobilewallet.android.avengerstoken.PinDialog.PinDialogResultListener;
import com.mobilewallet.android.enums.NotificationStatus;
import com.mobilewallet.android.http.ServiceRequest;
import com.mobilewallet.android.http.ServiceRequestListener;
import com.mobilewallet.android.ocrahotp.Hotp;
import com.mobilewallet.android.ocrahotp.HotpCounter;
import com.mobilewallet.android.ocrahotp.Ocra;
import com.mobilewallet.android.pojos.Notification;
import com.mobilewallet.android.services.SharedPreferencesHelper;
import com.mobilewallet.android.utils.UserManagement;

public class ConfirmIdentityActivity extends AppCompatActivity implements PinDialogResultListener, ServiceRequestListener {
    private static final String PROPERTY_APP_VERSION = "appVersion";

    private Context context;
    private int notificationId;
    private String message;
    private String timestamp;
    private String accountNumber;
    private String guid;
    private String action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_identity);

        context = getApplicationContext();
        try {
            Bundle extras = getIntent().getExtras();

            notificationId = extras.getInt(getString(R.string.extra_notificationId));
            message = extras.getString(getString(R.string.extra_message));
            timestamp = extras.getString(getString(R.string.extra_timestamp));
            accountNumber = extras.getString(getString(R.string.extra_accountNumber));
            guid = extras.getString(getString(R.string.extra_guid));
            action = extras.getString(getString(R.string.extra_action));
        }
        catch (Exception e) {
            Log.e("err", e.toString());
        }

        TextView msgView = (TextView) findViewById(R.id.message_textView);
        Button confirmIdentityButton = (Button) findViewById(R.id.confirm_identity_button);
        TextView notificationStatusTextView = (TextView) findViewById(R.id.notification_status_text_view);

        // check notification
        Notification notification = SharedPreferencesHelper.getNotification(this, notificationId);
        if (notification == null) {
            return;
        }

        if (notification.getStatus() == NotificationStatus.PENDING) {
            confirmIdentityButton.setVisibility(View.VISIBLE);
        } else {
            confirmIdentityButton.setVisibility(View.GONE);
        }

        notificationStatusTextView.setText(notification.getStatus().toString());
    }

    public void confirmButtonClick(View v) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.addToBackStack(null);

        PinDialogFragment newFragment = PinDialogFragment.newInstance(this);
        newFragment.show(ft, "dialog");
    }

    private void sendMessage(String username, String message, String ocra) {
        try {
            JSONObject data = new JSONObject();

            data.put("username", username);
            data.put("message", message);
            data.put("ocra", ocra);
            data.put("timestamp", timestamp);
            data.put("accountNumber", accountNumber);
            data.put("guid", guid);
            data.put("action", action);

            ServiceRequest serviceRequest = new ServiceRequest(this);
            serviceRequest.doPostJsonRequest("confirmIdentityResponse", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResponse(int responseCode, String responseString) {
        Gson gson = new Gson();
        Map<String, Object> map = new HashMap<String, Object>();
        map = gson.fromJson(responseString, map.getClass());
        boolean success = (boolean) map.get("success");

        if (success) {
            SharedPreferencesHelper.editNotificationStatus(this, notificationId, NotificationStatus.CONFIRMED);
            Toast.makeText(context, getString(R.string.identity_confirmed), Toast.LENGTH_LONG).show();

            finish();
        } else {
            String message = (String) map.get("message");
            Toast.makeText(context, "ERROR: " + message, Toast.LENGTH_LONG).show();
        }
    }

    public void onPinDialogButtonClick(String answer, String pin) {
        String imei = UserManagement.GetImei(this);

        SharedPreferences sharedPreferences = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("uname", "");

        String originalMessage = message;

        try {
            long counter = HotpCounter.GetAndIncrementCounter(this);
            String otp = Hotp.GenerateOTP(imei, pin, counter, 6, false, 65535);

            String hexMessage = String.format("%040x", new BigInteger(1, message.getBytes()));
            String ocraMessage = Ocra.GenerateOCRA(imei, pin, otp, hexMessage);

            if (ocraMessage != null || ocraMessage.length() > 0) {
                sendMessage(username, originalMessage, ocraMessage);
            }
            else {
                throw new Exception("OCRA is empty!");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPinDialogOkButtonClicked(String pin) {
        onPinDialogButtonClick("", pin);
    }

    @Override
    public void onPinDialogCancelButtonClicked() {
        // do nothing
    }
}
