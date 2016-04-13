

import android.annotation.TargetApi;
import android.app.IntentService;
import android.content.Intent;
import android.os.Build;
import android.os.Message;
import android.support.v4.app.NotificationCompat;

public class GcmIntentService extends IntentService {
    private static int NOTIFICATION_ID = 0;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }

    private void sendNotification(Message msg) {

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    private void setPriority(NotificationCompat.Builder mBuilder, Message msg) {
        int priority = msg.getLevel() - 3;
        if(Math.abs(priority) > 2) {
            priority = 0;
        }

        mBuilder.setPriority(priority);
    }
}

