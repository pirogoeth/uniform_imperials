package com.uniform_imperials.herald;

import android.app.Application;
import android.os.StrictMode;

import com.joshdholtz.sentry.Sentry;
import com.uniform_imperials.herald.model.AppSetting;
import com.uniform_imperials.herald.model.Models;
import com.uniform_imperials.herald.util.DefaultSettings;

import java.util.Iterator;
import java.util.List;

import io.requery.Persistable;
import io.requery.android.sqlite.DatabaseSource;
import io.requery.rx.RxSupport;
import io.requery.rx.SingleEntityStore;
import io.requery.sql.Configuration;
import io.requery.sql.EntityDataStore;
import io.requery.sql.TableCreationMode;

import static com.uniform_imperials.herald.util.JSONUnpacker.unpackBoolean;

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

        StrictMode.enableDefaults();

        DefaultSettings.ensureSettingsExist();

        Iterator<AppSetting> appSettingIterator = AppSetting.findAll(AppSetting.class);
        while (appSettingIterator.hasNext()) {
            AppSetting a = appSettingIterator.next();
            if (a == null) {
                System.out.println("NULL DATABASE OBJECT");
            } else {
                System.out.println(String.format("{key=%s,value=%s}", a.getKey(), a.getValue()));
            }
        }

        try {
            List<AppSetting> res = AppSetting.find(
                    AppSetting.class,
                    "key = ?",
                    getString(R.string.debug_xmit_setting));
            AppSetting as = res.get(0);
            boolean debug_xmit = unpackBoolean(as.getValue());

            // TODO: Clarify this. Simple wrapper, maybe?

            if (debug_xmit) {
                Sentry.init(this.getApplicationContext(),
                        getString(R.string.sentry_url),
                        getString(R.string.sentry_dsn));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    SingleEntityStore<Persistable> getData() {
        if (this.dataStore == null) {
            DatabaseSource source = new DatabaseSource(this, Models.DEFAULT, 1);
            if (BuildConfig.DEBUG) {
                source.setTableCreationMode(TableCreationMode.DROP_CREATE);
            }
            Configuration conf = source.getConfiguration();
            this.dataStore = RxSupport.toReactiveStore(
                    new EntityDataStore<Persistable>(conf)
            );
        }
    }
}
