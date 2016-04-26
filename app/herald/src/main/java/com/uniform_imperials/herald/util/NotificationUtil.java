package com.uniform_imperials.herald.util;

import android.app.Notification;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.graphics.drawable.VectorDrawable;
import android.service.notification.StatusBarNotification;
import android.util.Base64;

import com.joshdholtz.sentry.Sentry;
import com.uniform_imperials.herald.MainApplication;
import com.uniform_imperials.herald.R;
import com.uniform_imperials.herald.model.HistoricalNotification;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Locale;

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
     * @param sbn notif to dig in to
     * @return CapturedNotification
     */
    public static CapturedNotification getData(StatusBarNotification sbn, Context ctx) {
        Notification notification = sbn.getNotification();
        if (notification == null) return null;

        CapturedNotification cn = new CapturedNotification();
        try {
            cn.text = notification.tickerText.toString();
            cn.postedTime = notification.when;

            try {
                cn.smallIcon = base64EncodeImage(notification.getSmallIcon());
                cn.largeIcon = base64EncodeImage(notification.getLargeIcon());
            } catch (NoSuchMethodError e) {
                // API version < 23, use getNotificationIconBitmap
                Bitmap b = getNotificationIconBitmap(sbn.getPackageName(), ctx);
                cn.smallIcon = null;
                cn.largeIcon = base64EncodeBitmap(b);
            }
        } catch (NullPointerException e) {
            // TODO: Wrapper for posting exception traces to Sentry.
            return null;
        }

        return cn;
    }

    /**
     * Generates a unique key for each notification.
     *
     * @param sbn StatusBarNotification instance to generate a key for
     * @return String
     */
    public static String getNotificationKey(StatusBarNotification sbn) {
        return String.format(
                Locale.getDefault(),
                "%s|%s|%d|%s|%b",
                sbn.getId(),
                sbn.getPackageName(),
                sbn.getPostTime(),
                sbn.getTag(),
                sbn.isOngoing()
        );
    }

    /**
     * Tries several methods to get the proper icon for a notification:
     * - 1. Tries to decode notification's largeIcon
     * - 2. Tries to decode notification's smallIcon
     * - 3. Tries to find and decode notification's source app icon
     *
     * If none of the above processes works, falls back to a simple drawable resource included in
     * this app.
     *
     * If that fails, just returns null. Holy fallbacks, Batman.
     *
     * @param hn notification data
     * @return Bitmap
     */
    public static Bitmap getStoredNotificationIcon(HistoricalNotification hn) {
        if (hn == null) {
            return null;
        }

        // Try to use the large app icon first.
        if (hn.getLargeAppIcon() != null) {
            Bitmap bmp = base64DecodeBitmap(hn.getLargeAppIcon());
            if (bmp != null) {
                return bmp;
            }
        }

        // Next, try to use the small app icon.
        if (hn.getSmallAppIcon() != null) {
            Bitmap bmp = base64DecodeBitmap(hn.getSmallAppIcon());
            if (bmp != null) {
                return bmp;
            }
        }

        // If we could not use either app image provided by the notification, use the app icon from
        // system.
        Bitmap appIconBmp = getNotificationIconBitmap(
                hn.getSourceApplication(),
                null
        );
        if (appIconBmp != null) {
            return appIconBmp;
        }

        // If we couldn't get the system icon, just use a default unknown icon.
        Context ctx = MainApplication.getStaticBaseContext();
        Drawable d;
        try {
            Resources.Theme t = ctx.getResources().newTheme();
            d = ctx.getResources().getDrawable(R.drawable.unknown_icon, t);
        } catch (NoSuchMethodError exc) {
            d = ctx.getResources().getDrawable(R.drawable.unknown_icon);
        }

        if (d != null) {
            return ((BitmapDrawable) d).getBitmap();
        } else {
            return null;
        }
    }

    /**
     * Retrieve a Bitmap icon of a source application's notification icon.
     *
     * @param pack name of application package to find
     * @param context application context
     * @return Bitmap
     */
    public static Bitmap getNotificationIconBitmap(String pack, Context context) {
        if (pack == null) {
            return null;
        }

        if (context == null) {
            context = MainApplication.getStaticBaseContext();
        }

        Context remotePackageContext;
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

    /**
     * Resolves an application package to the proper application name.
     *
     * @param packageUri application package URI
     * @return String
     */
    public static String resolveApplicationName(String packageUri) {
        PackageManager pm = MainApplication.getStaticBaseContext().getPackageManager();
        String applicationName;

        try {
            applicationName = (String) pm.getApplicationLabel(
                    pm.getApplicationInfo(
                            packageUri, PackageManager.GET_META_DATA
                    )
            );
        } catch (PackageManager.NameNotFoundException exc) {
            // Could not find the name :(
            Sentry.captureException(exc);
            return packageUri;
        }

        return applicationName;
    }

    /**
     * Accepts any of the following:
     *  - VectorDrawable
     *  - Icon
     *  - Drawable
     *  - Bitmap
     *
     * And converts them to their proper upstream class for conversion to a Drawable and then
     * encodes the resulting Drawable as a base64-encoded bitmap.
     *
     * @param o Object to convert, encode, etc.
     * @return String
     */
    public static String base64EncodeImage(Object o) {
        if (o == null) {
            return null;
        }

        if (o instanceof VectorDrawable) {
            return base64EncodeVectorDrawable((VectorDrawable) o);
        } else if (o instanceof AnimationDrawable) {
            return base64EncodeAnimationDrawable((AnimationDrawable) o);
        } else if (o instanceof Icon) {
            return base64EncodeIcon((Icon) o);
        } else if (o instanceof Drawable) {
            return base64EncodeDrawable((Drawable) o);
        } else if (o instanceof Bitmap) {
            return base64EncodeBitmap((Bitmap) o);
        } else {
            return null;
        }
    }

    /**
     * Mutates a VectorDrawable instance and encodes the resulting BitmapDrawable to base64 .
     *
     * @param v VectorDrawable to convert and encode.
     * @return String encoded VectorDrawable.
     */
    public static String base64EncodeVectorDrawable(VectorDrawable v) {
        if (v == null) {
            return null;
        }

        Drawable d;
        try {
            d = v.mutate();
        } catch (NoSuchMethodError e) {
            return null;
        }

        Bitmap bmp = ((BitmapDrawable) d).getBitmap();

        return base64EncodeBitmap(bmp);
    }

    /**
     * Mutates an AnimationDrawable instance and encodes the resulting BitmapDrawable to base64 .
     *
     * @param a AnimationDrawable to convert and encode.
     * @return base64 String encoded AnimationDrawable.
     */
    public static String base64EncodeAnimationDrawable(AnimationDrawable a) {
        if (a == null) {
            return null;
        }

        Drawable d;
        try {
            d = a.mutate();
        } catch (NoSuchMethodError e) {
            return null;
        }

        Bitmap bmp = ((BitmapDrawable) d).getBitmap();

        return base64EncodeBitmap(bmp);
    }

    /**
     * Converts an Icon instance to a BitmapDrawable and perform base64 encoding.
     * @param i Icon instance to convert and encode.
     * @return String
     */
    public static String base64EncodeIcon(Icon i) {
        if (i == null) {
            return null;
        }

        Drawable d;
        try {
            d = i.loadDrawable(MainApplication.getStaticBaseContext());
        } catch (NoSuchMethodError e) {
            return null;
        }

        return base64EncodeImage(d);
    }

    /**
     * Encodes a BitmapDrawable into a base64-encoded string.
     *
     * @param d Drawable to encode.
     * @return String
     */
    public static String base64EncodeDrawable(Drawable d) {
        if (d == null) {
            return null;
        }

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
