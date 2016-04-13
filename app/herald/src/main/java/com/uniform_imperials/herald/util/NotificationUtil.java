package com.uniform_imperials.herald.util;

import android.app.Notification;
import android.graphics.drawable.Icon;

/**
 * Created by Sean Johnson on 4/11/2016.
 *
 * Utilities for ripping apart notifications.
 */
public class NotificationUtil {

    public static class CapturedNotification {
        public Icon largeIcon;
        public Icon smallIcon;
        public String text;
        public long postedTime;
        public String srcPackage;
    }

    /**
     * Rips all text from the Notification text views.
     *
     * @param notification notif to dig in to
     * @return CapturedNotification
     */
    public static CapturedNotification getData(Notification notification) {
        if (notification == null) return null;

        CapturedNotification cn = new CapturedNotification();
        cn.text = notification.tickerText.toString();
        cn.smallIcon = notification.getSmallIcon();
        cn.largeIcon = notification.getLargeIcon();
        cn.postedTime = notification.when;

        return cn;
    }
}
