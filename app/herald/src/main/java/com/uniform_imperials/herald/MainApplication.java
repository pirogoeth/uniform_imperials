package com.uniform_imperials.herald;

import android.app.Application;

import com.joshdholtz.sentry.Sentry;

import com.uniform_imperials.herald.model.Models;
import com.uniform_imperials.herald.util.DefaultSettings;
import static com.uniform_imperials.herald.util.JSONUnpacker.*;

import java.util.List;

import io.requery.Persistable;
import io.requery.android.sqlite.DatabaseSource;
import io.requery.sql.Configuration;
import io.requery.sql.EntityDataStore;
import io.requery.sql.TableCreationMode;

/**
 * Created by Sean Johnson on 3/29/2016.
 *
 * Main application entry point. Sets up the DB ORM, checks settings table,
 * and ensures that we are allowed to send debug information.
 */
public class MainApplication extends Application {

    /**
     * Entity store to use for persistence.
     */
    private EntityDataStore<Persistable> dataStore;

    /**
     * Database schema version
     */
    public static final int DB_SCHEMA_VERSION = 1;

    @Override
    public void onCreate() {
        super.onCreate();

        DefaultSettings.ensureSettingsExist(this);

        Sentry.init(this.getApplicationContext(),
                getString(R.string.sentry_url),
                getString(R.string.sentry_dsn));

    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        this.getData().close();
    }

    public EntityDataStore<Persistable> getData() {
        if (this.dataStore == null) {
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
            this.dataStore = new EntityDataStore<Persistable>(conf);
        }
        return this.dataStore;
    }
}
