package com.mobilewallet.android.services;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.mobilewallet.android.enums.NotificationStatus;
import com.mobilewallet.android.enums.NotificationType;
import com.mobilewallet.android.pojos.Notification;

/**
 * Created by JakubJ on 16.3.2017.
 *
 * This helper was created as kinnd of a database for Notifications (ConfirmTransaction and ConfirmIdentity)
 * It uses gson for serializing and deserializing Notification objects.
 *
 * I didn't want to create a SQLite database just for these notifications, although it would be better for a long run.
 * In the future, this would be better in an actual database, so it could be more easily modified.
 *
 */

public class SharedPreferencesHelper {

    public static int addToNotifications(Context context, Notification notification) {
        Gson gson = new Gson();

        final SharedPreferences sharedPreferences = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> existing = sharedPreferences.getStringSet("notifications", new HashSet<String>());
        Set<String> edited = new HashSet<>(existing);

        int notificationId = existing.size() + 1;
        notification.setId(notificationId);

        edited.add(gson.toJson(notification));
        editor.putStringSet("notifications", edited);
        editor.apply();

        return notificationId;
    }

    public static Notification getNotification(Context context, int notificationId) {
        final SharedPreferences sharedPreferences = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        Set<String> notifications = sharedPreferences.getStringSet("notifications",new HashSet<String>());

        Gson gson = new Gson();
        for (String json : notifications) {
            Notification notification = gson.fromJson(json, Notification.class);
            if (notification.getId() == notificationId)
                return notification;
        }

        return null;
    }

    public static List<Notification> getAllNotifications(Context context) {

        List<Notification> notificationList = new ArrayList<>();
        final SharedPreferences sharedPreferences = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        Set<String> notifications = sharedPreferences.getStringSet("notifications",new HashSet<String>());

        Gson gson = new Gson();
        for (String json : notifications) {
            Notification notification = gson.fromJson(json, Notification.class);
            notificationList.add(notification);
        }

        return notificationList;
    }

    public static void removeFromNotifications(Context context, Notification notification) {


        List<Notification> notificationList = new ArrayList<>();
        final SharedPreferences sharedPreferences = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> existing = sharedPreferences.getStringSet("notifications",new HashSet<String>());
        Set<String> edited = new HashSet<>(existing);
        Gson gson = new Gson();
        if (edited.remove(gson.toJson(notification))) {
            System.out.println("removed notification");
        } else {
            System.out.println("Did not remove notification");
        }
        editor.putStringSet("notifications", edited);
        editor.apply();
    }

    public static void removeAllNotifications(Context context) {
        List<Notification> notificationList = new ArrayList<>();
        final SharedPreferences sharedPreferences = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> existing = sharedPreferences.getStringSet("notifications",new HashSet<String>());
        Set<String> empty = new HashSet<>();
        editor.putStringSet("notifications", empty);
        editor.apply();
    }

    public static void editNotificationStatus(Context context, int notificationId, NotificationStatus newStatus) {
        final SharedPreferences sharedPreferences = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> existing = sharedPreferences.getStringSet("notifications",new HashSet<String>());
        Set<String> edited = new HashSet<>(existing);
        Notification notificationToChange = null;
        Gson gson = new Gson();
        Iterator<String> iterator = edited.iterator();
        while (iterator.hasNext()) {
            String element = iterator.next();
            Notification notification = gson.fromJson(element, Notification.class);
            if (notification.getId() == notificationId) {
                notificationToChange = notification;
                iterator.remove();
                edited.remove(element);
            }
        }
        if (notificationToChange != null) {
            notificationToChange.setStatus(newStatus);
            edited.add(gson.toJson(notificationToChange));
        }

        editor.putStringSet("notifications", edited);
        editor.apply();

        setPendingConfirmIdentityNotificationsToConfirmed(context);
    }

    public static void setPendingConfirmIdentityNotificationsToConfirmed(Context context) {
        final SharedPreferences sharedPreferences = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> existing = sharedPreferences.getStringSet("notifications",new HashSet<String>());
        Set<String> edited = new HashSet<>(existing);
        Set<Notification> toEdit = new HashSet<>();

        Gson gson = new Gson();
        Iterator<String> iterator = edited.iterator();
        while (iterator.hasNext()) {
            String element = iterator.next();
            Notification notification = gson.fromJson(element, Notification.class);
            if (notification.getType() == NotificationType.ConfirmIdentity
                    && notification.getStatus() == NotificationStatus.PENDING) {
                toEdit.add(notification);
                iterator.remove();
                edited.remove(element);
            }
        }

        if (toEdit != null && toEdit.size() > 0) {
            for (Notification n : toEdit) {
                n.setStatus(NotificationStatus.CONFIRMED);
                edited.add(gson.toJson(n));
            }
        }

        editor.putStringSet("notifications", edited);
        editor.apply();
    }

    public static void updateNotifications(Context context) {
        final SharedPreferences sharedPreferences = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        Set<String> existing = sharedPreferences.getStringSet("notifications",new HashSet<String>());
        Set<String> edited = new HashSet<>(existing);
        Set<String> toEdit = new HashSet<>();

        Gson gson = new Gson();
        Iterator<String> iterator = edited.iterator();
        while (iterator.hasNext()) {
            String element = iterator.next();
            Notification notification = gson.fromJson(element, Notification.class);
            if (notification.getStatus() == NotificationStatus.PENDING
                && new Date().getTime() - notification.getDate().getTime() > 300000) {
                notification.setStatus(NotificationStatus.EXPIRED);
                iterator.remove();
                edited.remove(element);
                toEdit.add(gson.toJson(notification));
            }
        }

        edited.addAll(toEdit);

        editor.putStringSet("notifications", edited);
        editor.apply();
    }
}
