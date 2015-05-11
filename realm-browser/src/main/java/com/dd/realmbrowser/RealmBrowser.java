package com.dd.realmbrowser;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import io.realm.RealmObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class RealmBrowser {

    public static final int NOTIFICATION_ID = 1000;

    private static final RealmBrowser sInstance = new RealmBrowser();
    private List<Class<? extends RealmObject>> mRealmModelList;

    private RealmBrowser() {
        mRealmModelList = new ArrayList<>();
    }

    public List<Class<? extends RealmObject>> getRealmModelList() {
        return mRealmModelList;
    }

    @SafeVarargs
    public final void addRealmModel(Class<? extends RealmObject>... arr) {
        mRealmModelList.addAll(Arrays.asList(arr));
    }

    public static RealmBrowser getInstance() {
        return sInstance;
    }

    public static void startRealmFilesActivity(@NonNull Activity activity) {
        RealmFilesActivity.start(activity);
    }

    public static void startRealmModelsActivity(@NonNull Activity activity, @NonNull String realmFileName) {
        RealmModelsActivity.start(activity, realmFileName);
    }

    public static void showRealmFilesNotification(@NonNull Activity activity) {
        showRealmNotification(activity, RealmFilesActivity.class);
    }

    private static void showRealmNotification(@NonNull Activity activity, @NonNull Class activityClass) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(activity)
                .setSmallIcon(R.drawable.ic_rb)
                .setContentTitle(activity.getString(R.string.rb_title))
                .setContentText(activity.getString(R.string.rb_click_to_launch))
                .setAutoCancel(false);
        Intent notifyIntent = new Intent(activity, activityClass);
        notifyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent notifyPendingIntent =
                PendingIntent.getActivity(activity, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(notifyPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
