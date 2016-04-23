package com.uniform_imperials.herald.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uniform_imperials.herald.BaseFragment;
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

    private AppSettingFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SettingFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static SettingFragment newInstance() {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_setting_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.addItemDecoration(new DividerItemDecoration(context));

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
