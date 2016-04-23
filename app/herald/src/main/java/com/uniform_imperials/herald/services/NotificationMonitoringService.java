package com.uniform_imperials.herald.services;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.uniform_imperials.herald.MainApplication;
import com.uniform_imperials.herald.model.HistoricalNotification;
import com.uniform_imperials.herald.util.NotificationUtil;

import java.util.Date;

import io.requery.Persistable;
import io.requery.sql.EntityDataStore;

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
    private EntityDataStore<Persistable> dataStore;

    @Override
    public void onCreate() {
        super.onCreate();
        this.mReceiver = new NMSReceiver();
        this.dataStore = ((MainApplication) this.getApplication()).getData();

        IntentFilter f = new IntentFilter();

        this.registerReceiver(this.mReceiver, f);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification notification) {
        Notification n = notification.getNotification();
        NotificationUtil.CapturedNotification cn = NotificationUtil.getData(n);
        if (cn == null) {
            return;
        }

        cn.srcPackage = notification.getPackageName();

        // TODO: Steal the application's notification icon from the app context resources
        HistoricalNotification hn = new HistoricalNotification();
        hn.setReceiveDate(new Date(cn.postedTime).toString());
        hn.setNotificationKey(notification.getKey());
        hn.setNotificationContent(cn.text);
        hn.setSourceApplication(cn.srcPackage);
        hn.setAppIcon(null);
        this.dataStore.insert(hn);
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
