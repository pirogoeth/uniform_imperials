package com.uniform_imperials.herald.activities;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SwitchCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.joshdholtz.sentry.Sentry;
import com.uniform_imperials.herald.BaseActivity;
import com.uniform_imperials.herald.R;
import com.uniform_imperials.herald.fragments.SettingFragment;
import com.uniform_imperials.herald.model.AppSetting;

public class MainActivity extends BaseActivity
        implements SettingFragment.AppSettingFragmentInteractionListener {

    public final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.global_drawer);

        NavigationView nav = (NavigationView) findViewById(R.id.nav_view);

        // Attach an onClick handler to the menu item.
        Menu menu = nav.getMenu();
        MenuItem item = menu.findItem(R.id.nav_disable_mirror);
        View actionView = MenuItemCompat.getActionView(item);
        final SwitchCompat disableSwitch = (SwitchCompat) actionView.findViewById(R.id.disable_switch);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem mi) {
                try {
                    disableSwitch.invalidate();
                    disableSwitch.performClick();

                    return true;
                } catch (NullPointerException e) {
                    Sentry.captureMessage("Could not toggle mirroring switch.");
                }
                return false;
            }
        });

        disableSwitch.setOnClickListener(new SwitchCompat.OnClickListener() {
            public void onClick(View v) {
                View topView = findViewById(R.id.content_frame);
                if (disableSwitch.isChecked()) { // Notifications are disabled
                    Snackbar.make(topView, R.string.notif_disabled, Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(topView, R.string.notif_enabled, Snackbar.LENGTH_SHORT).show();
                }

                // TODO: Call to the settings model to disable notification pushing.
            }
        });

        this.createNavDrawer();

        // Open the default fragment view.
        this.selectDrawerItem(R.id.nav_nh);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // this.mDrawer.openDrawer(GravityCompat.START);
    }

    public void onAppSettingFragmentInteraction(AppSetting setting) {
        // TODO: Trigger a modification dialog for the appropriate key value.
    }
}
