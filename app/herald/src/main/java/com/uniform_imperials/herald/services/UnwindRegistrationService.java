package com.uniform_imperials.herald.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.uniform_imperials.herald.R;

import java.io.IOException;

/**
 * Created by Sean Johnson on 4/26/2016.
 */
public class UnwindRegistrationService extends IntentService {

    private static final String TAG = UnwindRegistrationService.class.getSimpleName();

    private static boolean tokenSubmitted = false;

    public UnwindRegistrationService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            InstanceID instId = InstanceID.getInstance(this);
            String token = instId.getToken(
                    getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE,
                    null
            );

            Log.d(TAG, "GCM Instance Token: " + token);

            // TODO: Push updated instance token to device registration endpoint on Unwind.
            this.putTokenToUnwind(token);

            tokenSubmitted = true;
        } catch (IOException exc) {
            Log.w(TAG, "Could not submit GCM token to Unwind: " + exc.getMessage());
            tokenSubmitted = false;
        }

        // TODO: Notify things that registration process completes?
    }

    private void putTokenToUnwind(String token) {
        // TODO: Use http client to put token to server.
    }

}
