package com.uniform_imperials.herald;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * Created by Sean Johnson on 3/29/2016.
 */
public abstract class BaseFragment extends Fragment {

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
        View view = this.createView(inflater, container);
        ButterKnife.bind(this, view);

        this.afterBind();

        return view;
    }

    /**
     * Simple method stub to create, prepare, and return the view used by the
     * extending fragment.
     *
     * @return view used by this fragment
     */
    public abstract View createView(LayoutInflater inflater, ViewGroup container);

    /**
     * Actions to perform after a ButterKnife bind is successful.
     */
    public void afterBind() {
        return;
    }

}
