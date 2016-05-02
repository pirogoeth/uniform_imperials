package com.uniform_imperials.herald.services;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by Sean Johnson on 4/26/2016.
 *
 * Listens for instance id updates from GCM.
 */
public class GcmInstanceIdListener extends InstanceIDListenerService {

    @Override
    public void onTokenRefresh() {
        Intent intent = new Intent(this, UnwindRegistrationService.class);
        startService(intent);
    }

}
