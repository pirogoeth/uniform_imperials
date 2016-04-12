
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import java.util.List;

public class SettingsActivity extends PreferenceActivity {

    private static final String GCM_REGISTER_URL = "";

    private static Preference.OnPreferenceClickListener sBindOnPreferenceClickListener
            = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            String key = preference.getKey();
            final Context context = preference.getContext();
            final DBHandler db = new DBHandler(context);
            if (key.equalsIgnoreCase("Delete All")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("Delete all messages?");
                builder.setMessage("Do you really want to do this?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int userInput) {
                        db.truncateMessages();
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int userInput) {
                        dialog.dismiss();
                    }
                }

                builder.create().show();

                return true;
            }

    }
    /**
     * updates the preference's to reflect new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    }



    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the subscriptioner to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the subscriptioner immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    public static String getRegisterUrl(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean useCustom = preferences.getBoolean("server_use_custom", false);

        if (useCustom) {
            String url = preferences.getString("server_custom_url", DEFAULT_GCM_URL);
            return url.replaceAll("/+$", "");
        } else {
            return DEFAULT_GCM_URL;
        }
    }

    public static String getSenderId(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean useCustom = preferences.getBoolean("server_use_custom", false);

        if (useCustom) {
            return preferences.getString("server_custom_sender_id", DEFAULT_SENDER_ID);
        } else {
            return DEFAULT_SENDER_ID;
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        setupSimplePreferencesScreen();
    }

    private void setupSimplePreferencesScreen() {
        if (!isSimplePreferences(this)) {
            return;

  }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static class ServerPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_server);

            bindPreferenceSummaryToValue(findPreference("server_custom_url"));
            bindPreferenceSummaryToValue(findPreference("server_custom_sender_id"));
        }
    }
}
