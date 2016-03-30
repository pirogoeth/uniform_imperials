package com.uniform_imperials.herald;

import android.app.Application;

import com.joshdholtz.sentry.Sentry;

/**
 * Created by Sean Johnson on 3/29/2016.
 */
public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Sentry.init(this.getApplicationContext(),
                    getString(R.string.sentry_url),
                    getString(R.string.sentry_dsn));
    }

}
