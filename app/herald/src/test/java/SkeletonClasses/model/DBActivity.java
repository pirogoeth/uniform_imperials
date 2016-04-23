

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


import java.util.ArrayList;
import java.util.Arrays;

public class DBActivity extends ListActivity {

	@Override
	public void onRefresh() {

		refreshServices();
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_subscriptions);
		this.refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
		this.refreshLayout.setEnabled(true);
		this.refreshLayout.setOnRefreshListener(refreshListener);

		this.api = new DeviceSync(getApplicationContext(), SettingsActivity.getRegisterUrl(this));
		this.db = new DatabaseHandler(getApplicationContext());

		adapter = new SubscriptionsAdapter(this);
		setListAdapter(adapter);

		adapter.upDateEntries(new ArrayList<Service>(Arrays.asList(db.getAllServices())));
		registerForContextMenu(findViewById(android.R.id.list));

		Uri uri = getIntent().getData();
		if (uri != null) {
			try {
				String host = uri.getHost();
				Log.d("Service", "Host: " + host);
				parseTokenOrUri(host);
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
			} catch (NullPointerException ignore) {
			}


			if (adapter.getCount() == 0 && !this.refreshLayout.isRefreshing()) {
				refreshServices();
			}

			receiver = new BroadcastReceiver();
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		adapter.upDateEntries(new ArrayList<Service>(Arrays.asList(db.getAllServices())));

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
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.subscriptions_context_menu, menu);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		openContextMenu(v);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.subscriptions, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
			case R.id.actions_new:
				final String[] items = new String[]{"Scan QR", "Enter token"};
				final Activity thisActivity = this;
		}
	}

	@Override
	public void onClick(DialogInterface dialogInterface, int i) {
		if (i == 0) {
			new IntentIntegrator(thisActivity).initiateScan(IntentIntegrator.QR_CODE_TYPES);
		}
		if (i == 1) {
			AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
			builder.setTitle("token");
			final EditText input = new EditText(thisActivity);

			input.setInputType(InputType.TYPE_CLASS_TEXT);
			builder.setView(input);
			builder.setPositiveButton("ok", new DialogInterface.OnClickListener()
		}
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		try {
			parseTokenOrUri(input.getText().toString());
		} catch (java.lang.Exception e) {
			Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
		} catch (NullPointerException ignore) {
		}

		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {

	}

	private void parseTokenOrUri(String token) throws java.lang.Exception {

	}

	// Used for parsing the QR code scanner result
	public void onActivityResult(int request, int result, Intent intent) {
		try {
			IntentResult scanResult = IntentIntegrator.parseActivityResult(request, result, intent);
			if (scanResult == null)
				return;
			parseTokenOrUri(scanResult.getContents().trim());
		} catch (java.lang.Exception e) {
			Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
		} catch (NullPointerException ignore) {
		}
	}

	private void refreshServices() {
		UpdateServiceCallback callback = new UpdateServiceCallback()
	}

	@Override
	public void onComplete(Service[] services) {
		adapter.upDateEntries(new ArrayList<Service>(Arrays.asList(services)));
		refreshLayout.setRefreshing(false);

		UpdateService refresh = new UpdateService(api, db);
		refresh.setCallback(callback);
		refreshLayout.setRefreshing(true);
		refresh.execute();
	}
}