package com.uniform_imperials.herald.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.uniform_imperials.herald.activities.MainActivity;

/**
 * Created by Sean Johnson on 4/26/2016.
 *
 * Listens to messages from GCM.
 */
public class GcmListenerService extends com.google.android.gms.gcm.GcmListenerService {

    public static final String TAG = GcmListenerService.class.getSimpleName();

    @Override
    public void onMessageReceived(String from, Bundle data) {
        // TODO: Parse data from GCM into a real notification and display it!
    }

    // Sends the notification through the system service manager!
    private void sendNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Replace 0 with request code
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(0)
                .setLargeIcon(null)
                .setContentText(null)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pi);

        NotificationManager notifMan = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Replace 0 with notification id
        notifMan.notify(0, notificationBuilder.build());
    }
}
