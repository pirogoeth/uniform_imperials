package com.uniform_imperials.herald;

import android.app.Application;

import com.joshdholtz.sentry.Sentry;

import com.uniform_imperials.herald.model.AppSetting;
import com.uniform_imperials.herald.model.Models;
import com.uniform_imperials.herald.util.DefaultSettings;

import io.requery.Persistable;
import io.requery.android.sqlite.DatabaseSource;
import io.requery.rx.RxSupport;
import io.requery.rx.SingleEntityStore;
import io.requery.sql.Configuration;
import io.requery.sql.EntityDataStore;
import io.requery.sql.TableCreationMode;
import rx.Observable;

/**
 * Created by Sean Johnson on 3/29/2016.
 *
 * Main application entry point. Sets up the DB ORM, checks settings table,
 * and ensures that we are allowed to send debug information.
 */
public class MainApplication extends Application {

    private SingleEntityStore<Persistable> dataStore;

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

    public SingleEntityStore<Persistable> getData() {
        if (this.dataStore == null) {
            DatabaseSource source = new DatabaseSource(this, Models.DEFAULT, 1);

            // Release-based table modes
            if (BuildConfig.DEBUG) {
                System.out.println("DEBUG | Setting CREATE_NOT_EXISTS table policy");
                source.setTableCreationMode(TableCreationMode.DROP_CREATE);
            } else {
                source.setTableCreationMode(TableCreationMode.CREATE_NOT_EXISTS);
            }

            // Configuration stuffs
            source.setLoggingEnabled(true);
            source.setWriteAheadLoggingEnabled(true);
            Configuration conf = source.getConfiguration();

            // Create the reactive data source
            this.dataStore = RxSupport.toReactiveStore(
                    new EntityDataStore<Persistable>(conf)
            );
        }
        return this.dataStore;
    }
}
