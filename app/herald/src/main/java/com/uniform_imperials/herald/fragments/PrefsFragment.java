package com.uniform_imperials.herald.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.uniform_imperials.herald.BaseFragment;
import com.uniform_imperials.herald.R;

/**
 * Created by mobie on 4/26/2016.
 */
public class PrefsFragment extends PreferenceFragment, BaseFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
        }
    }


