package com.uniform_imperials.herald;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.joshdholtz.sentry.Sentry;
import com.uniform_imperials.herald.activities.MainActivity;
import com.uniform_imperials.herald.fragments.NotificationHistoryFragment;

import butterknife.ButterKnife;

/**
 * Created by Sean Johnson on 3/28/2016.
 *
 * This class is the base activity that all other application activities should extend.
 */
public abstract class BaseActivity extends AppCompatActivity {

    private static final int DB_SCHEMA_VERSION = 1;

    /**
     * Global drawer layout.
     */
    protected DrawerLayout mDrawer;

    /**
     * Global navigation view.
     */
    protected NavigationView mNavView;

    /**
     * Toolbar replacing the ActionBar.
     */
    protected Toolbar mToolbar;

    /**
     * Linked state for the nav icon animation.
     */
    protected ActionBarDrawerToggle mDrawerToggle;

    /**
     * This method is the entry point for a given activity. In this method,
     * we should load up any necessary components that may be used by the subclassing
     * activity, such as the database connection, HTTP connection, etc.
     *
     * @param savedInstanceState Activity's saved instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Wrapper for setContentView that binds Butterknife after setting the current view.
     *
     * @param resId Resource ID to use as view.
     */
    @Override
    public void setContentView(@LayoutRes int resId) {
        super.setContentView(resId);

        ButterKnife.bind(this);
    }

    /**
     * Wrapper for setContentView that binds Butterknife after setting the current view.
     *
     * @param view View to assign.
     */
    @Override
    public void setContentView(View view) {
        super.setContentView(view);

        ButterKnife.bind(this);
    }

    /**
     * Wrapper for setContentView that binds Butterknife after setting the current view.
     *
     * @param view View to assign
     * @param params Parameters for the view
     */
    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);

        ButterKnife.bind(this);
    }

    /**
     * Finds the views and resources associated with creating an inter-Activity navigation
     * drawer.
     */
    protected void createNavDrawer() {
        if (this.mToolbar == null) {
            this.mToolbar = (Toolbar) findViewById(R.id.toolbar);
        }

        if (this.mDrawer == null) {
            this.mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        }

        if (this.mNavView == null) {
            this.mNavView = (NavigationView) findViewById(R.id.nav_view);
        }

        // Override the actionbar with a new MD toolbar.
        setSupportActionBar(this.mToolbar);
        try {
            // Override the navigation icon with a nav drop.
            this.mToolbar.setNavigationIcon(R.drawable.nav_drop);
            this.mToolbar.setTitleTextAppearance(
                    getApplicationContext(), R.style.Subtle_ActionBar_Text
            );
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            Sentry.captureException(e);
        }

        // Set up the linked mDrawer + mToolbar toggle
        mDrawerToggle = setupDrawerToggle();
        this.mDrawer.addDrawerListener(mDrawerToggle);

        // Run the routine to build nav drawer content.
        setupDrawerContent(this.mNavView);
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(
                this,
                this.mDrawer,
                this.mToolbar,
                R.string.drawer_open,
                R.string.drawer_close);
    }

    /**
     * Event for opening the nav drawer.
     *
     * @param item item that was selected.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (this.mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case android.R.id.home:
                this.mDrawer.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Post-create routine.
     *
     * @param savedInstanceState Activity's saved instance state.
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (this.mDrawerToggle != null) {
            this.mDrawerToggle.syncState();
        }
    }

    /**
     * Event listener for Activity configuration change (ie., screen rotation)
     *
     * @param config new Activity config
     */
    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);

        // Pass config changes to the drawer toggle.
        if (this.mDrawerToggle != null) {
            this.mDrawerToggle.onConfigurationChanged(config);
        }
    }

    /**
     * Creates a nav selection listener to select a new drawer item.
     *
     * @param nav active navigation menu.
     */
    private void setupDrawerContent(NavigationView nav) {
        nav.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem item) {
                        selectDrawerItem(item);
                        return true;
                    }
                }
        );
    }

    /**
     * Sets the current displayed fragment based on a clicked nav menu item.
     *
     * @param item Menu item that was selected.
     */
    public void selectDrawerItem(MenuItem item) {
        // Create a fragment to show the activity specified by the nav item.
        Fragment mFrag = null;
        Class cFrag = null;

        switch (item.getItemId()) {
            case R.id.nav_am:  // Application manager pane
                // cFrag = ApplicationManagerActivity.class;
                break;
            case R.id.nav_dm:  // Device manager pane
                // cFrag = DeviceManagerActivity.class;
                break;
            case R.id.nav_nh:  // Notification history pane1
                cFrag = NotificationHistoryFragment.class;
                break;
            case R.id.nav_sp:  // Settings pane
                // cFrag = SettingsActivity.class;
                break;
            case R.id.nav_ap:  // About pane
                // cFrag = AboutPanelActivity.class
                break;
            case R.id.nav_disable_mirror:  // Nav toggle
                return;
            default:
                cFrag = MainActivity.class;
                break;
        }

        try {
            if (cFrag == null) {
                System.out.println("WARNING: Fragment class was null!");
                this.mDrawer.closeDrawer(GravityCompat.START);
            }
            mFrag = (Fragment) cFrag.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Replace the current Fragment.
        FragmentManager mFM = getSupportFragmentManager();
        mFM.beginTransaction()
           .replace(R.id.content_frame, mFrag)
           .commit();

        // Highlight the current fragment in the nav drawer.
        item.setChecked(true);

        // Set toolbar title.
        setTitle(item.getTitle());

        // Close the nav!
        this.mDrawer.closeDrawer(GravityCompat.START);
    }
}
