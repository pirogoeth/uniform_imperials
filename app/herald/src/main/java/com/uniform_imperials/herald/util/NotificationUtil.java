package com.uniform_imperials.herald.util;

import android.app.Notification;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.util.Base64;

import com.uniform_imperials.herald.MainApplication;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;

/**
 * Created by Sean Johnson on 4/11/2016.
 *
 * Utilities for ripping apart notifications.
 */
public class NotificationUtil {

    public static class CapturedNotification {
        public String largeIcon;
        public String smallIcon;
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
            cn.smallIcon = base64EncodeIcon(notification.getSmallIcon());
            cn.largeIcon = base64EncodeIcon(notification.getLargeIcon());
            cn.postedTime = notification.when;
        } catch (NullPointerException e) {
            // TODO: Wrapper for posting exception traces to Sentry.
            return null;
        }

        return cn;
    }

    /**
     * Retrieve a Bitmap icon of a source application's notification icon.
     *
     * @param pack name of application package to find
     * @param context application context
     * @return Bitmap
     */
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

    public static String base64EncodeIcon(Icon i) {
        if (i == null) {
            return null;
        }

        Drawable d = i.loadDrawable(MainApplication.getStaticBaseContext());
        Bitmap bmp = ((BitmapDrawable) d).getBitmap();

        return base64EncodeBitmap(bmp);
    }

    public static String base64EncodeBitmap(Bitmap b) {
        if (b == null) {
            return null;
        }

        byte[] ba = convertBitmapToBytes(b);
        if (ba == null) {
            return null;
        }

        String s = base64EncodeBytes(ba);
        if (s == null) {
            return null;
        }

        return s;
    }

    public static Bitmap base64DecodeBitmap(String s) {
        if (s == null) {
            return null;
        }

        byte[] ba = base64DecodeBytes(s);
        if (ba == null) {
            return null;
        }

        Bitmap b = convertBytesToBitmap(ba);
        if (b == null) {
            return null;
        }

        return b;
    }

    /**
     * Converts a Bitmap object to a byte array.
     *
     * @param b Bitmap object
     * @return byte array (byte[])
     */
    public static byte[] convertBitmapToBytes(Bitmap b) {
        if (b == null) {
            return new byte[]{};
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG, 100, baos);

        return baos.toByteArray();
    }

    /**
     * Converts a byte array to a Bitmap object.
     *
     * @param ba byte array (byte[])
     * @return Bitmap object.
     */
    public static Bitmap convertBytesToBitmap(byte[] ba) {
        if (ba == null || ba.length == 0) {
            return null;
        }

        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        return BitmapFactory.decodeStream(bais);
    }

    /**
     * Encodes a byte array string to a base64 encoded string.
     *
     * @param ba byte array
     * @return base64-encoded string
     */
    public static String base64EncodeBytes(byte[] ba) {
        if (ba == null || ba.length == 0) {
            return null;
        }

        return Base64.encodeToString(ba, Base64.DEFAULT);
    }

    /**
     * Decodes a base64-encoded string to a byte array.
     *
     * @param s base64-encoded string
     * @return byte array
     */
    public static byte[] base64DecodeBytes(String s) {
        if (s == null || s.length() == 0) {
            return null;
        }

        return Base64.decode(s, Base64.DEFAULT);
    }
}
