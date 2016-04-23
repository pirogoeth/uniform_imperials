package com.uniform_imperials.herald;

import android.app.Application;

import com.joshdholtz.sentry.Sentry;

import com.uniform_imperials.herald.model.Models;
import com.uniform_imperials.herald.util.DefaultSettings;

import io.requery.Persistable;
import io.requery.android.sqlite.DatabaseSource;
import io.requery.sql.Configuration;
import io.requery.sql.EntityDataStore;
import io.requery.sql.TableCreationMode;

import android.content.Context;
import android.util.Log;

/**
 * Created by Sean Johnson on 3/29/2016.
 *
 * Main application entry point. Sets up the DB ORM, checks settings table,
 * and ensures that we are allowed to send debug information.
 */
public class MainApplication extends Application {

    /**
     * String tag used for log messages.
     */
    public static final String TAG = MainApplication.class.getSimpleName();

    /**
     * Entity store to use for persistence.
     */
    private static EntityDataStore<Persistable> dataStore = null;

    /**
     * Base context instance.
     */
    private static Context baseContext = null;

    /**
     * Database schema version
     */
    public static final int DB_SCHEMA_VERSION = 2;

    @Override
    public void onCreate() {
        super.onCreate();

        this.getData();
        baseContext = this.getBaseContext();

        DefaultSettings.ensureSettingsExist();

        Sentry.init(this.getApplicationContext(),
                getString(R.string.sentry_url),
                getString(R.string.sentry_dsn));

    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        this.getData().close();
    }

    /**
     * Returns the statically assigned dataStore instance.
     * If the stored instance is null, logs an error for debugging purposes.
     *
     * @return dataStore
     */
    public static EntityDataStore<Persistable> getEntitySourceInstance() {
        if (dataStore == null) {
            Log.w(TAG, "getEntitySourceInstance returns null -- dataStore uninitialized");
        }

        return dataStore;
    }

    /**
     * Returns the statically created baseContext instance.
     * If the stored context is null, logs an error for debugging purposes.
     *
     * @return baseContext
     */
    public static Context getStaticBaseContext() {
        if (baseContext == null) {
            Log.w(TAG, "getStaticBaseContext returns null -- baseContext uninitialized");
        }

        return baseContext;
    }
    public EntityDataStore<Persistable> getData() {
        if (dataStore == null) {
            DatabaseSource source = new DatabaseSource(this, Models.DEFAULT, DB_SCHEMA_VERSION);

            // Release-based table modes
            if (BuildConfig.DEBUG) {
                source.setTableCreationMode(TableCreationMode.DROP_CREATE);
            } else {
                source.setTableCreationMode(TableCreationMode.CREATE_NOT_EXISTS);
            }

            // Configuration stuffs
            source.setLoggingEnabled(true);
            source.setWriteAheadLoggingEnabled(true);
            Configuration conf = source.getConfiguration();

            // Create the reactive data source
            dataStore = new EntityDataStore<>(conf);
        }
        return dataStore;
    }
}
