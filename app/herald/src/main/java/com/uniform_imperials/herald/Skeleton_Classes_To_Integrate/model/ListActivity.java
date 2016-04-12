import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;



public class ListActivity {

    private OnRefreshListener refreshListener = new OnRefreshListener() {

        @Override
        public void onRefresh() {

        }
    }

    @Override
    protected void onCreate() {
        @Override
        public void onReceive () {

        }
    }

    @Override
    protected void onListItemClick() {

    }

    @Override
    protected void onStart() {
        super.onStart();

        registerReceiver();

    }

    @Override
    protected void onStop() {
        super.onStop();

        unregisterReceiver(receiver);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {


    }

    private void updateList() {
    }

    @Override
    public boolean onCreateOptionsMenu() {

    }

    @Override
    public boolean onOptionsItemSelected() {

    }

    public Context getApplicationContext() {

    }
}
