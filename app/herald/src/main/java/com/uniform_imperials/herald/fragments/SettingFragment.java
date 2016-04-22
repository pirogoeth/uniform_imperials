package com.uniform_imperials.herald.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uniform_imperials.herald.BaseFragment;
import com.uniform_imperials.herald.MainApplication;
import com.uniform_imperials.herald.R;
import com.uniform_imperials.herald.adapters.AppSettingAdapter;
import com.uniform_imperials.herald.drawables.DividerItemDecoration;
import com.uniform_imperials.herald.model.AppSetting;

/**
 * A fragment representing a list of Items.
 * <p>
 * Activities containing this fragment MUST implement the {@link AppSettingFragmentInteractionListener}
 * interface.
 */
public class SettingFragment extends BaseFragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private AppSettingFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SettingFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static SettingFragment newInstance(int columnCount) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            this.mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_setting_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            // To get main app, traverse to the parent context (MainActivity), and then get the app ctx.
            MainApplication appContext = (MainApplication) this.getContext().getApplicationContext();
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;

            // Boilerplate for switching between grid and linear layouts.
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, this.mColumnCount));
            }

            // Attach the item decorator for the divider
            recyclerView.addItemDecoration(new DividerItemDecoration(this.getActivity()));

            // Create the adapter and attach it to the view.
            recyclerView.setAdapter(new AppSettingAdapter(this.mListener));
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AppSettingFragmentInteractionListener) {
            mListener = (AppSettingFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface AppSettingFragmentInteractionListener {
        // TODO: Update argument type and name
        void onAppSettingFragmentInteraction(AppSetting setting);
    }
}
