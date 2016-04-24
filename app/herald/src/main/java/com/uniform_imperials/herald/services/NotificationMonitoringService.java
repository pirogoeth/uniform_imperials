package com.uniform_imperials.herald.services;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.uniform_imperials.herald.MainApplication;
import com.uniform_imperials.herald.model.HistoricalNotification;
import com.uniform_imperials.herald.util.NotificationUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
     * List of handlers implementing the INMSListener class for notifications.
     */
    private List<INMSListener> mNMSHandlers = new ArrayList<>();

    /**
     * Registers a NMS handler for watching notification post and remove events.
     *
     * @param l INMSListener-implementing class instance
     * @return boolean registration successful
     */
    public boolean registerHandler(INMSListener l) {
        if (l == null) {
            return false;
        }

        if (this.mNMSHandlers.contains(l)) {
            return false;
        }

        boolean added = this.mNMSHandlers.add(l);
        if (added) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Unregisters a NMS handler.
     *
     * @param l INMSListener-implementing class instance
     * @return boolean de-registration successful
     */
    public boolean unregisterHandler(INMSListener l) {
        if (l == null) {
            return false;
        }

        if (this.mNMSHandlers.contains(l)) {
            boolean removed = this.mNMSHandlers.remove(l);
            if (removed) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

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

        // Make sure there are no more NMS listeners *leaking*
        for (INMSListener l : this.mNMSHandlers) {
            Log.w(
                    TAG,
                    String.format(
                            "Listener %s leaking -- unregister needed when onNMSDisable()",
                            l.getClass().getCanonicalName()
                    )
            );
        }
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

        // Send the onNMSDisable event to NMS listeners
        for (INMSListener l : this.mNMSHandlers) {
            if (l == null) {
                Log.w(TAG, "NMSListener was null -- forcible addition?");
            }

            l.onNMSDisable();
        }

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
        hn.setReceiveDate(new Date(cn.postedTime).toString());
        hn.setNotificationKey(notification.getKey());
        hn.setNotificationContent(cn.text);
        hn.setNotificationTitle(NotificationUtil.resolveApplicationName(cn.srcPackage));
        hn.setSourceApplication(cn.srcPackage);
        hn.setAppIcon(cn.largeIcon);

        // TODO: Fire INMSListener.onNotificationReceived handlers.
        for (INMSListener l : this.mNMSHandlers) {
            if (l == null) {
                Log.w(TAG, "NMSListener was null -- forcible addition?");
            }

            l.onNotificationReceived(cn);
        }

        this.dataStore.insert(hn);
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

    /**
     * Interface for app chunks that want to register for notification receiver updates.
     */
    public interface INMSListener {
        void onNotificationReceived(NotificationUtil.CapturedNotification mNotif);
        void onNotificationRemoved(NotificationUtil.CapturedNotification mNotif);
        void onNMSDisable();
    }
}
