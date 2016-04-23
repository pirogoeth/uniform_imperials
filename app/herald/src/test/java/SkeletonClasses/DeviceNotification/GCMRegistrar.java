
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GCMRegistrar {
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "app_version";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    protected String TAG = "GCM";
    private GoogleCloudMessaging gcm;
    private Context msgContext;

    public GCMRegistrar(Context context) {
        this.msgContext = context;
        gcm = GoogleCloudMessaging.getInstance(context);
    }

    public boolean checkPlayServices(android.app.Activity self) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(msgContext);
        if (resultCode != ConnectionResult.SUCCESS) {
            Log.e(TAG, "This device does not support GCM");
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode) && self != null) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, self, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(msgContext, "Sorry, you need to have the Google Play services installed ", Toast.LENGTH_SHORT).show();
            }
            return false;
        } else {
            Log.i(TAG, "device registered msg");
        }
        return true;
    }

    private void storeRegistrationId(String regId) {
        final SharedPreferences prefs = getGcmPreferences(msgContext);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, getAppVersion());
        editor.apply();
    }

    public String getRegistrationId() {
        final SharedPreferences prefs = getGcmPreferences(msgContext);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty() || registrationId.equals(""))
            return "";

        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        if (registeredVersion != getAppVersion())
            return "";
        return registrationId;
    }

    public int getAppVersion() {
        try {
            PackageInfo packageInfo = msgContext.getPackageManager()
                    .getPackageInfo(msgContext.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Could not open package name: " + e);
        }
    }

    private SharedPreferences getGcmPreferences(Context context) {
        return context.getSharedPreferences(GCMRegistrar.class.getSimpleName(), Context.MODE_PRIVATE);
    }

    public model.DeviceNotification.GCMRegistrar.RegistrarSync registerInBackground() {

        return this.registerInBackground(false);
    }

    public model.DeviceNotification.GCMRegistrar.RegistrarSync registerInBackground(boolean force) {
        model.DeviceNotification.GCMRegistrar.RegistrarSync task
                = new model.DeviceNotification.GCMRegistrar.RegistrarSync();
        task.execute(force);
        return task;
    }

    public boolean shouldRegister() {

        return getRegistrationId().equals("");
    }

    public void DeleteRegistration() {
        final SharedPreferences prefs = getGcmPreferences(msgContext);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(PROPERTY_REG_ID, "");
        editor.putInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        editor.commit();
    }

    private static boolean asyncRunning = false;
    public class RegistrarSync extends AsyncTask<Boolean, Void, Void> {

        @Override
        protected Void doInBackground(Boolean... params) {
            if(asyncRunning) {
                return null;
            }

            asyncRunning = true;
            boolean force = params.length > 0 && params[0];
            if (!force && shouldRegister()) {
                Log.i(TAG, "Device already registered");
                asyncRunning = false;
                return null;
            }

            String url = SettingsActivity.getRegisterUrl(msgContext) + "";
            String senderId = SettingsActivity.getSenderId(msgContext);
            Looper.prepare();

            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(msgContext);
                }
                String registerID = gcm.register(senderId);

                Map<String, String> data = new HashMap<String, String>();
                data.put("registerId", registerID);
                data.put("uuid", new DeviceUuid(msgContext).getDeviceUuid().toString());

                storeRegistrationId(registerID);
            } catch (IOException ignore) {
                Toast.makeText(msgContext, "Could not register with GCM server", Toast.LENGTH_SHORT).show();
            }

            Log.i(TAG, "Deveice Registration Complete");
            if (force) {
                Toast.makeText(msgContext, "Device Registered to " + url, Toast.LENGTH_SHORT).show();
            }

            Log.i(TAG, "Registered to " + url);
            asyncRunning = false;
            return null;
        }
    }
}
