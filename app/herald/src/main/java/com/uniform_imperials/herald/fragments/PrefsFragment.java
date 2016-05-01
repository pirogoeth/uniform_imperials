package com.uniform_imperials.herald.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uniform_imperials.herald.R;

import butterknife.ButterKnife;

/**
 * Created by Matt Humphries on 4/26/2016.
 *
 */
public class PrefsFragment extends PreferenceFragment {

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PrefsFragment() {
    }

    public static PrefsFragment newInstance() {
        PrefsFragment fragment = new PrefsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from a seperate XML resource
        addPreferencesFromResource(R.xml.preferences);
      //alt method to change settings layout  setContentView(R.layout.alt_settings);
    }

    /**
     * Stub-out for the Fragment's onCreateView event.
     *
     * @param inflater layout inflater
     * @param container view container
     * @param savedInstanceState application's saved instance state
     * @return view used by this fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, view);

        return view;
    }
}


