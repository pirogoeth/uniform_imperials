package com.uniform_imperials.herald.activities;

import android.os.Bundle;
import android.support.design.widget.NavigationView;

import com.uniform_imperials.herald.BaseActivity;
import com.uniform_imperials.herald.R;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.global_drawer);

        NavigationView nav = (NavigationView) findViewById(R.id.nav_view);

        this.createNavDrawer();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // this.mDrawer.openDrawer(GravityCompat.START);
    }
}
