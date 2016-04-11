package com.uniform_imperials.herald.util;

import com.uniform_imperials.herald.MainApplication;
import com.uniform_imperials.herald.model.AppSettingEntity;

/**
 * Created by Sean Johnson on 4/10/2016.
 *
 * Loads a set of default settings into the on-board database.
 */
public class DefaultSettings {

    public static final String TAG = DefaultSettings.class.getSimpleName();

    /**
     * Default setting keys.
     */
    private static String[] default_keys = {
            // Global settings
            "global.mirroring_disabled",
            "global.quiet_time_begin",
            "global.quiet_time_end",
            // Server settings
            "server.uri",
            "server.force_https",
            "server.channel_uuid",
            // Debug settings
            "debug.allow_event_xmit",
    };

    /**
     * Default values.
     *
     * Must be valid JSON data!
     */
    private static String[] default_values = {
            "false",                                    // global.mirroring_disabled
            "-1",                                       // global.quiet_time_begin
            "-1",                                       // global.quiet_time_end

            "\"https://hpush-pub0.maiome-infra.net\"",  // server.uri
            "true",                                     // server.force_https
            "-1",                                       // server.channel_uuid

            "false",                                    // debug.allow_event_xmit
    };

    /**
     * Iterates through the list of default settings. If they do not exist in the database,
     * they will be created and set to the corresponding default value.
     */
    public static void ensureSettingsExist(MainApplication mApp) {
        for (int i = 0; i < default_keys.length; i++) {
            AppSettingEntity as = new AppSettingEntity();
            as.setKey(default_keys[i]);
            as.setValue(default_values[i]);
            mApp.getData().insert(as);
        }
    }
}
