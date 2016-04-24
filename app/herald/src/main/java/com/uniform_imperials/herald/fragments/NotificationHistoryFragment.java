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
        View refreshLayout = parent.findViewById(R.id.nh_swipe_refresh_frame);

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

        // TODO: Make the emptyMsgView not swallow the Recycler
        // Display the "empty" content if adapter is empty.
        if (this.nestedView.getAdapter().getItemCount() == 0) {
            View emptyMsgView = parent.findViewById(R.id.nh_empty_container);
            emptyMsgView.setVisibility(View.VISIBLE);
        } else {
            View emptyMsgView = parent.findViewById(R.id.nh_empty_container);
            if (emptyMsgView.getVisibility() == View.VISIBLE) {
                emptyMsgView.setVisibility(View.GONE);
            }
        }

        // Set up the swipe refresh layout
        if (refreshLayout instanceof SwipeRefreshLayout) {
            SwipeRefreshLayout srl = (SwipeRefreshLayout) refreshLayout;

            // Create the swipe-refresh handler
            srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    nestedView.removeAllViewsInLayout();
                    NotificationHistoryAdapter adapter = new NotificationHistoryAdapter(mListener);
                    nestedView.setAdapter(adapter);
                    srl.setRefreshing(false);
                }
            });
        }

        return parent;
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
                NotificationHistoryAdapter adapter = new NotificationHistoryAdapter(mListener);
                nestedView.setAdapter(adapter);
            }
        }
    }
}
