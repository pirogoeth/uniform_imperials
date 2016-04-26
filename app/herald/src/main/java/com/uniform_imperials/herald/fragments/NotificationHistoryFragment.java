package com.uniform_imperials.herald.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
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
     * "Parent" view layout for this set of views.
     */
    private View parentView = null;

    /**
     * Internal recycler view instance.
     */
    private RecyclerView nestedView = null;

    /**
     * Broadcast receiver for reload notifications.
     */
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

        if (this.mABRecv == null) {
            this.mABRecv = new NMSActionBroadcastReceiver();
            IntentFilter mIf = new IntentFilter(IntentUtil.NHF_ACTION_RELOAD);
            this.getActivity().registerReceiver(this.mABRecv, mIf);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (this.mABRecv != null) {
            this.getActivity().unregisterReceiver(this.mABRecv);
            this.mABRecv = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (this.mABRecv != null) {
            this.getActivity().unregisterReceiver(this.mABRecv);
            this.mABRecv = null;
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
        this.parentView = inflater.inflate(R.layout.activity_notif_history, container, false);
        View view = this.parentView.findViewById(R.id.nh_list);
        View refreshLayout = this.parentView.findViewById(R.id.nh_swipe_refresh_frame);

        // Set the adapter
        if (view instanceof RecyclerView) {
            this.nestedView = (RecyclerView) view;

            // To get main app, traverse to the parent context (MainActivity), and then get the app ctx.
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            // Set the event handler for adapter updates.
            NotificationHistoryAdapter nha = new NotificationHistoryAdapter(this.mListener);

            // Create the adapter and attach it to the view.
            recyclerView.setAdapter(nha);

            updateEmptyDataView();
        }

        // Set up the swipe refresh layout
        if (refreshLayout instanceof SwipeRefreshLayout) {
            SwipeRefreshLayout srl = (SwipeRefreshLayout) refreshLayout;

            // Create the swipe-refresh handler
            srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    NotificationHistoryAdapter nha = (NotificationHistoryAdapter) nestedView.getAdapter();
                    nha.loadAllNotifications();
                    updateEmptyDataView();

                    srl.setRefreshing(false);
                }
            });
        }

        return this.parentView;
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
     * Updates the "nh list empty" view with the proper visibility setting.
     */
    private void updateEmptyDataView() {
        // Display the "empty" content if adapter is empty.
        if (this.nestedView.getAdapter().getItemCount() == 0) {
            View emptyMsgView = this.parentView.findViewById(R.id.nh_empty);
            emptyMsgView.setVisibility(View.VISIBLE);
        } else {
            View emptyMsgView = this.parentView.findViewById(R.id.nh_empty);
            if (emptyMsgView.getVisibility() == View.VISIBLE) {
                emptyMsgView.setVisibility(View.GONE);
            }
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface HistoricalNotificationFragmentInteractionListener {
        // TODO: Update argument type and name
        void onHistoricalNotificationFragmentInteraction(HistoricalNotification notification);
    }

    public class NMSActionBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context mContext, Intent mIntent) {
            if (mIntent.getAction().equals(IntentUtil.NHF_ACTION_RELOAD)) {
                NotificationHistoryAdapter nha = (NotificationHistoryAdapter) nestedView.getAdapter();
                nha.loadNewNotifications();
                updateEmptyDataView();
            }
        }
    }
}
