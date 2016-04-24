package com.uniform_imperials.herald.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uniform_imperials.herald.BaseFragment;
import com.uniform_imperials.herald.R;
import com.uniform_imperials.herald.adapters.NotificationHistoryAdapter;
import com.uniform_imperials.herald.model.HistoricalNotification;
import com.uniform_imperials.herald.util.IntentUtil;

/**
 * Created by Sean Johnson on 3/29/2016.
 * Modified by Gustavo Moreira on 4/22/2016
 */
public class NotificationHistoryFragment
        extends BaseFragment {

    /**
     * Interaction listener.
     */
    private HistoricalNotificationFragmentInteractionListener mListener;

    /**
     * Internal recycler view instance.
     */
    private RecyclerView nestedView = null;

    private NMSActionBroadcastReceiver mABRecv = null;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NotificationHistoryFragment() {
    }

    public static NotificationHistoryFragment newInstance() {
        NotificationHistoryFragment fragment = new NotificationHistoryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        // Create the broadcast receiver.
//        this.mABRecv = new NMSActionBroadcastReceiver();
//        IntentFilter mIf = new IntentFilter(IntentUtil.NHF_ACTION_RELOAD);
//        this.getActivity().registerReceiver(this.mABRecv, mIf);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (this.mABRecv != null) {
            this.getActivity().unregisterReceiver(this.mABRecv);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (this.mABRecv == null) {
            this.mABRecv = new NMSActionBroadcastReceiver();
            IntentFilter mIf = new IntentFilter(IntentUtil.NHF_ACTION_RELOAD);
            this.getActivity().registerReceiver(this.mABRecv, mIf);
        }
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container) {
        View parent = inflater.inflate(R.layout.activity_notif_history, container, false);
        View view = parent.findViewById(R.id.nh_list);

        // Set the adapter
        if (view instanceof RecyclerView) {
            this.nestedView = (RecyclerView) view;

            // To get main app, traverse to the parent context (MainActivity), and then get the app ctx.
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            // Create the adapter and attach it to the view.
            recyclerView.setAdapter(new NotificationHistoryAdapter(this.mListener));
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HistoricalNotificationFragmentInteractionListener) {
            mListener = (HistoricalNotificationFragmentInteractionListener) context;
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
    public interface HistoricalNotificationFragmentInteractionListener {
        // TODO: Update argument type and name
        void onHistoricalNotificationFragmentInteraction(HistoricalNotification notification);
    }

    public class NMSActionBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context mContext, Intent mIntent) {
            if (mIntent.getAction().equals(IntentUtil.NHF_ACTION_RELOAD)) {
                RecyclerView.Adapter adapter = nestedView.getAdapter();
                if (adapter instanceof NotificationHistoryAdapter) {
                    NotificationHistoryAdapter nha = (NotificationHistoryAdapter) adapter;
                    nha.reloadNotifications();
                }
            }
        }
    }
}
