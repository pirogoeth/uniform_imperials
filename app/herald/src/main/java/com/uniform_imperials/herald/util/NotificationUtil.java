package com.uniform_imperials.herald.util;

import android.app.Notification;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
        try {
            cn.text = notification.tickerText.toString();
            cn.smallIcon = notification.getSmallIcon();
            cn.largeIcon = notification.getLargeIcon();
            cn.postedTime = notification.when;
        } catch (NullPointerException e) {
            // TODO: Wrapper for posting exception traces to Sentry.
            return null;
        }

        return cn;
    }

    public static Bitmap getNotificationIconBitmap(String pack, Context context){
        Context remotePackageContext = null;
        Bitmap bmp = null;
        try {
            remotePackageContext = context.getApplicationContext().createPackageContext(pack, 0);
            Drawable icon = remotePackageContext.getPackageManager().getApplicationIcon(pack);
            if (icon != null) {
                bmp = ((BitmapDrawable) icon).getBitmap();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bmp;
    }
}
