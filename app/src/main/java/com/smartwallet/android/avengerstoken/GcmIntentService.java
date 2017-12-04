package com.smartwallet.android.avengerstoken;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import com.smartwallet.android.R;
import com.smartwallet.android.enums.NotificationStatus;
import com.smartwallet.android.pojos.Notification;
import com.smartwallet.android.services.SharedPreferencesHelper;

public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public GcmIntentService() {
        super("GcmIntentService");
    }
    public static final String TAG = "GCM Demo";

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String gcmMessageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM will be
             * extended in the future with new message types, just ignore any message types you're
             * not interested in, or that you don't recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(gcmMessageType)) {
                // don't care
                Log.d("GCM", "ERROR 1");
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(gcmMessageType)) {
                // don't care
                Log.d("GCM", "ERROR 2");
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(gcmMessageType)) {

                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                // Post notification of received message.

                int messageType = Integer.parseInt(extras.getString(getString(R.string.extra_messageType)));
                String timestamp = extras.getString(getString(R.string.extra_timestamp));
                String accountNumber = extras.getString(getString(R.string.extra_accountNumber));
                String message = timestamp + accountNumber;
                try
                {
                    if (messageType == 0) {
                        double amount = Double.parseDouble(extras.getString(getString(R.string.extra_amount)));
                        String paymentId = extras.getString(getString(R.string.extra_paymentId));
                        Notification notification = new Notification(timestamp, message, accountNumber, NotificationStatus.PENDING, amount, paymentId);
                        int notificationId = SharedPreferencesHelper.addToNotifications(getApplicationContext(), notification);
                        showConfirmTransactionNotification(notificationId, accountNumber, paymentId, message, timestamp, amount);
                    } else if (messageType == 1) {
                        String guid = extras.getString(getString(R.string.extra_guid));
                        String action = extras.getString(getString(R.string.extra_action));
                        message += guid;

                        Notification notification = new Notification(timestamp, message, accountNumber, NotificationStatus.PENDING, guid, action);
                        int notificationId = SharedPreferencesHelper.addToNotifications(getApplicationContext(), notification);
                        showConfirmIdentityNotification(notificationId, accountNumber, message, timestamp, guid, action);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.i(TAG,extras.toString());
            }
        }

        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void showConfirmIdentityNotification(int notificationId, String accountNumber, String message, String timestamp, String guid, String action) {
        Intent intent = new Intent(this, ConfirmIdentityActivity.class);
        intent.putExtra(getString(R.string.extra_notificationId), notificationId);
        intent.putExtra(getString(R.string.extra_message), message);
        intent.putExtra(getString(R.string.extra_timestamp), timestamp);
        intent.putExtra(getString(R.string.extra_accountNumber), accountNumber);
        intent.putExtra(getString(R.string.extra_guid), guid);
        intent.putExtra(getString(R.string.extra_action), action);

        PendingIntent pendingIntent =  PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        showNotification(pendingIntent, getString(R.string.identity_confirmation), message, getString(R.string.confirm_identity));
    }

    private void showConfirmTransactionNotification(int notificationId, String accountNumber, String paymentId, String message, String timestamp, double amount) {
        Intent intent = new Intent(this,ConfirmTransactionActivity.class);
        intent.putExtra(getString(R.string.extra_notificationId), notificationId);
        intent.putExtra(getString(R.string.extra_message), message);
        intent.putExtra(getString(R.string.extra_timestamp), timestamp);
        intent.putExtra(getString(R.string.extra_amount), amount);
        intent.putExtra(getString(R.string.extra_accountNumber), accountNumber);
        intent.putExtra(getString(R.string.extra_paymentId), paymentId);

        PendingIntent pendingIntent =  PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        showNotification(pendingIntent, getString(R.string.purchase_authorization), message, getString(R.string.authorize_purchase, amount));
    }

    private void showNotification(PendingIntent pendingIntent, String title, String message, String content) {

        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_stat_cloud)
                        .setContentTitle(title)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                        .setContentText(content);

        mBuilder.setAutoCancel(true);

        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(notificationSound);

        mBuilder.setContentIntent(pendingIntent);

        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}