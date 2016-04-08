package com.uniform_imperials.herald.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.service.notification.NotificationListenerService;

/**
 * Created by Sean Johnson on 4/8/2016.
 *
 * NotificationMonitoringService is the Android intent plugin that captures notifications
 * and does several things:
 * - Logs them in the SQLite database (track notification history)
 * - Passes them through the routing chain (what notifications go where)
 */
public class NotificationMonitoringService extends NotificationListenerService {

    private final String TAG = this.getClass().getSimpleName();
    private NMSReceiver mReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        this.mReceiver = new NMSReceiver();

        IntentFilter f = new IntentFilter();

        this.registerReceiver(this.mReceiver, f);
    }

    class NMSReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            return;
        }
    }

}
