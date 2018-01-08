package com.mobilewallet.android.avengerstoken;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
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

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

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

public class ConfirmTransactionActivity extends AppCompatActivity implements PinDialogResultListener, ServiceRequestListener {
    private final String ConfirmedString = "CONFIRMED";
    private final String RejectedString = "REJECTED";
    private final String ExpiredString = "EXPIRED";

    private Context context;

    private int notificationId;
    private String msg;
    private String timestamp;
    private String answer;
    private String accountNumber;
    private String paymentId;
    private double amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_transaction);

        context = getApplicationContext();

        setPropertiesFromExtras();

        TextView msgView = (TextView) findViewById(R.id.message_textView);
        Button confirmTransactionButton = (Button) findViewById(R.id.confirm_transaction_button);
        Button rejectTransactionButton = (Button) findViewById(R.id.reject_transaction_button);
        TextView notificationStatusTextView = (TextView) findViewById(R.id.notification_status_text_view);

        msgView.setGravity(Gravity.CENTER);
        msgView.setText(getString(R.string.authorize_purchase, amount));

        Toolbar myToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(myToolbar);

        // check notification
        Notification notification = SharedPreferencesHelper.getNotification(this, notificationId);
        if (notification == null) {
            return;
        }

        if (notification.getStatus() == NotificationStatus.PENDING) {
            confirmTransactionButton.setVisibility(View.VISIBLE);
            rejectTransactionButton.setVisibility(View.VISIBLE);
        } else {
            confirmTransactionButton.setVisibility(View.GONE);
            rejectTransactionButton.setVisibility(View.GONE);
        }

        String notificationStatus = notification.getStatus().toString();
        notificationStatusTextView.setText(notificationStatus);
        notificationStatusTextView.setTextColor(Notification.notificationColor.get(notificationStatus));
    }

    private void setPropertiesFromExtras() {
        try {
            Intent intent = getIntent();
            Bundle extras = intent.getExtras();

            notificationId = extras.getInt(getString(R.string.extra_notificationId));
            msg = extras.getString(getString(R.string.extra_message));
            timestamp = extras.getString(getString(R.string.extra_timestamp));
            amount = extras.getDouble(getString(R.string.extra_amount));
            accountNumber = extras.getString(getString(R.string.extra_accountNumber));
            paymentId = extras.getString(getString(R.string.extra_paymentId));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onClick(View v) {
        answer = getAnswerAccordingToButtonPressed(v);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.addToBackStack(null);

        // Create and show the dialog.
        PinDialogFragment newFragment = PinDialogFragment.newInstance(this);
        newFragment.show(ft, "dialog");
    }

    private String getAnswerAccordingToButtonPressed(View v) {
        switch (v.getId()) {
            case R.id.confirm_transaction_button:
                return ConfirmedString;
            case R.id.reject_transaction_button:
                return RejectedString;
        }

        return "";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_online_token2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    private void sendMessage(String username, String answer, String message, String ocra, double amount) {
        try {
            JSONObject data = new JSONObject();

            data.put("username", username);
            data.put("answer", answer);
            data.put("message", message);
            data.put("ocra", ocra);
            data.put("timestamp", timestamp);
            data.put("amount", amount);
            data.put("accountNumber", accountNumber);
            data.put("paymentId", paymentId);

            ServiceRequest serviceRequest = new ServiceRequest(this);
            serviceRequest.doPostJsonRequest("confirmTransactionResponse", data);
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
            if (answer.equals(ConfirmedString)) {
                SharedPreferencesHelper.editNotificationStatus(this, notificationId, NotificationStatus.CONFIRMED);
                Toast.makeText(context, getString(R.string.transaction_confirmed), Toast.LENGTH_LONG).show();
            } else if (answer.equals(RejectedString)) {
                SharedPreferencesHelper.editNotificationStatus(this, notificationId, NotificationStatus.REJECTED);
                Toast.makeText(context, getString(R.string.transaction_confirmed), Toast.LENGTH_LONG).show();
            }

            finish();
        }
        else {
            String message = (String) map.get("message");
            Toast.makeText(context, "ERROR: " + message, Toast.LENGTH_LONG).show();
            if (message.equals(ExpiredString)) {
                finish();
            }
        }
    }

    @Override
    public void onPinDialogOkButtonClicked(String pin) {
        String imei = UserManagement.GetImei(this);

        SharedPreferences sharedPreferences = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("uname", "");

        String originalMessage = msg;

        msg = answer + ":" + msg;

        String stringForOcra = paymentId + timestamp;

        try {
            long counter = HotpCounter.GetAndIncrementCounter(this);
            String otp = Hotp.GenerateOTP(imei, pin, counter, 6, false, 65535);

            String hexStringForOcra = String.format("%040x", new BigInteger(1, stringForOcra.getBytes()));
            String ocraMessage = Ocra.GenerateOCRA(imei, pin, otp, hexStringForOcra);

            if (ocraMessage != null || ocraMessage.length() > 0) {
                sendMessage(username, answer, originalMessage, ocraMessage, amount);
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
    public void onPinDialogCancelButtonClicked() {
        Toast t = Toast.makeText(ConfirmTransactionActivity.this, R.string.operation_cancelled, Toast.LENGTH_SHORT);
        t.show();
    }

    public class NullHostNameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            Log.i("RestUtilImpl", "Approving certificate for " + hostname);
            return true;
        }

    }
}
