package com.smartwallet.android.pojos;

import android.graphics.Color;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.smartwallet.android.enums.NotificationStatus;
import com.smartwallet.android.enums.NotificationType;

/**
 * Created by JakubJ on 22.2.2017.
 */

public class Notification {
    // this properties has every notification
    private int id;
    private String timestampString;
    private Date date;
    private String content;
    private String accountNumber;
    private String paymentId;
    private NotificationStatus status;
    private NotificationType type;

    // type == ConfirmTransaction has amount as an extra properties
    private double amount;

    //type == ConfirmIdentity
    private String guid;
    private String action;

    public static final Map<String,Integer> notificationColor = new HashMap<>();
    static {
        notificationColor.put(NotificationStatus.PENDING.toString(), Color.BLUE);
        notificationColor.put(NotificationStatus.CONFIRMED.toString(), Color.GREEN);
        notificationColor.put(NotificationStatus.EXPIRED.toString(), Color.RED);
        notificationColor.put(NotificationStatus.REJECTED.toString(), Color.RED);
    }

    // constructor for ConfirmIdentity Notification
    public Notification(String timestampString, String content, String accountNumber, NotificationStatus status, String guid, String action) {
        this.type = NotificationType.ConfirmIdentity;
        this.timestampString = timestampString;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            this.date = sdf.parse(timestampString);
        } catch (Exception e) {
            Log.d("NSTL", "date", e);
        }
        this.content = content;
        this.accountNumber = accountNumber;
        this.status = status;
        this.guid = guid;
        this.action = action;
    }

    // constructor for ConfirmTransaction Notification
    public Notification(String timestampString, String content, String accountNumber, NotificationStatus status, double amount, String paymentId) {
        this.type = NotificationType.ConfirmTransaction;
        this.timestampString = timestampString;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            this.date = sdf.parse(timestampString);
        } catch (Exception e) {
            Log.d("NSTL", "date", e);
        }
        this.content = content;
        this.accountNumber = accountNumber;
        this.status = status;
        this.amount = amount;
        this.paymentId = paymentId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTimestampString() { return timestampString; }

    public void setTimestampString(String timestampString) { this.timestampString = timestampString; }

    public Date getDate() {

        //TODO: refine Date to return ddMMYYYY

        return date;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public void setStatus(NotificationStatus status) {
        this.status = status;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPaymentId() { return paymentId; }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getGuid() { return guid; }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getAction() { return action; }

    public void setAction(String action) {
        this.action = action;
    }
}
