package com.uniform_imperials.herald.services;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.uniform_imperials.herald.MainApplication;
import com.uniform_imperials.herald.model.HistoricalNotification;
import com.uniform_imperials.herald.util.IntentUtil;
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

    /**
     * Logging tag.
     */
    private static final String TAG = NotificationMonitoringService.class.getSimpleName();

    /**
     * Notification access enabled flag.
     */
    private static boolean notificationAccessEnabled = false;

    /**
     * Allows use of notificationAccessEnabled flag to determine if notification hooking has
     * been granted by the user.
     *
     * @returns boolean Notification listener was successfully bound.
     */
    public static boolean isNotificationAccessEnabled() {
        return notificationAccessEnabled == true;
    }

    /**
     * Receiver variable. Initialized in onCreate().
     */
    private NMSReceiver mReceiver;

    /**
     * Database access variable.
     */
    private EntityDataStore<Persistable> dataStore;

    /**
     * Perform the typical onCreate tasks of setting up the service, database, etc.
     */
    @Override
    public void onCreate() {
        super.onCreate();

        this.mReceiver = new NMSReceiver();
        this.dataStore = MainApplication.getEntitySourceInstance();

        // TODO: Add a "self" filter to prevent notification rebroadcast.
        IntentFilter f = new IntentFilter();

        this.registerReceiver(this.mReceiver, f);
    }

    /**
     * Make sure the broadcast receiver is unregistered on service destruction, perform
     * service cleanup tasks.
     */
    @Override
    public void onDestroy() {
        this.unregisterReceiver(this.mReceiver);
    }

    /**
     * Perform listener onBind tasks, like marking the notification access flag.
     *
     * @param mIntent binding intent
     * @return IBinder
     */
    @Override
    public IBinder onBind(Intent mIntent) {
        IBinder binder = super.onBind(mIntent);

        // Mark the notification access flag so we don't bother the user.
        notificationAccessEnabled = true;

        return binder;
    }

    /**
     * Perform listener onUnbind tasks, like marking the notification access flag and
     * unregistering the broadcast receiver.
     *
     * @param mIntent binding intent.
     * @return boolean
     */
    @Override
    public boolean onUnbind(Intent mIntent) {
        boolean res = super.onUnbind(mIntent);

        // Make sure the receiver is unregistered on unbind.
        this.unregisterReceiver(this.mReceiver);

        // Unmark the notification access flag to warn the user.
        notificationAccessEnabled = false;

        return res;
    }

    /**
     * When a notification is posted, grab as much info off the SBN instance as possible and
     * then persist it to the database.
     *
     * @param notification StatusBarNotification posted
     */
    @Override
    public void onNotificationPosted(StatusBarNotification notification) {
        Notification n = notification.getNotification();

        NotificationUtil.CapturedNotification cn = NotificationUtil.getData(n);
        if (cn == null) {
            return;
        }

        cn.srcPackage = notification.getPackageName();

        HistoricalNotification hn = new HistoricalNotification();
        hn.setEpoch(cn.postedTime);
        hn.setReceiveDate(new Date(cn.postedTime).toString());
        hn.setNotificationKey(notification.getKey());
        hn.setNotificationContent(cn.text);
        hn.setNotificationTitle(NotificationUtil.resolveApplicationName(cn.srcPackage));
        hn.setSourceApplication(cn.srcPackage);
        hn.setAppIcon(cn.largeIcon);

        this.dataStore.insert(hn);

        // Fire off a broadcast so the NHF reloads its data set.
        sendBroadcast(new Intent(IntentUtil.NHF_ACTION_RELOAD));
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification notification) {
        // no-op
    }

    /**
     * Broadcast receiver class.
     */
    class NMSReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            return;
        }
    }
}
