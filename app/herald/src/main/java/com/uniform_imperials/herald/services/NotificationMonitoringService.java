package com.uniform_imperials.herald.services;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.uniform_imperials.herald.util.NotificationUtil;

/**
 * Created by Sean Johnson on 4/8/2016.
 *
 * NotificationMonitoringService is the Android intent plugin that captures notifications
 * and does several things:
 * - Logs them in the SQLite database (track notification history)
 * - Passes them through the routing chain (what notifications go where)
 */
public class NotificationMonitoringService extends NotificationListenerService {

    private static final String TAG = NotificationMonitoringService.class.getSimpleName();
    private NMSReceiver mReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        this.mReceiver = new NMSReceiver();

        IntentFilter f = new IntentFilter();

        this.registerReceiver(this.mReceiver, f);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification notification) {
        Notification n = notification.getNotification();
        NotificationUtil.CapturedNotification cn = NotificationUtil.getData(n);
        cn.srcPackage = notification.getPackageName();

        Log.i(TAG, "NOTIFICATION POSTED: " + cn.text);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification notification) {

    }

    class NMSReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            return;
        }
    }

}
