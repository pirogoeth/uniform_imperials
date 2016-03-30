package com.uniform_imperials.herald.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uniform_imperials.herald.BaseFragment;
import com.uniform_imperials.herald.R;

/**
 * Created by Sean Johnson on 3/29/2016.
 */
public class NotificationHistoryFragment extends BaseFragment {

    public View createView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.activity_notif_history, container, false);

        // TODO: Connect the recycler view to an adapter for notification display

        return view;
    }

}
