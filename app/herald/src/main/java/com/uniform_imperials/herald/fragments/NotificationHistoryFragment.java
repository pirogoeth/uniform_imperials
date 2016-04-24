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
import com.uniform_imperials.herald.adapters.NotificationHistoryAdapter;
import com.uniform_imperials.herald.model.HistoricalNotification;
import com.uniform_imperials.herald.services.NotificationMonitoringService;
import com.uniform_imperials.herald.util.NotificationUtil;

/**
 * Created by Sean Johnson on 3/29/2016.
 * Modified by Gustavo Moreira on 4/22/2016
 */
public class NotificationHistoryFragment
        extends BaseFragment
        implements NotificationMonitoringService.INMSListener {

    private HistoricalNotificationFragmentInteractionListener mListener;

    private RecyclerView nestedView = null;

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
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.activity_notif_history, container, false);

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
     * Implementation of the receiver for NMS onNotificationRecv event
     *
     * @param cn captured notification data
     */
    public void onNotificationReceived(NotificationUtil.CapturedNotification cn) {
        RecyclerView.Adapter adapter = this.nestedView.getAdapter();
        if (adapter instanceof NotificationHistoryAdapter) {
            NotificationHistoryAdapter nha = (NotificationHistoryAdapter) adapter;
            nha.reloadNotifications();
        }
    }

    /**
     * Implementation of the receiver for NMS onNotificationRmvd event
     *
     * @param cn captured notification data
     */
    public void onNotificationRemoved(NotificationUtil.CapturedNotification cn) {
        // no-op
    }

    /**
     * Implementation of the receiver for NMS onNMSDisable event
     */
    public void onNMSDisable() {
        NotificationMonitoringService nms = this.getActivity().getSystemService(NotificationMonitoringService.class);
        nms.unregisterHandler(this);
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

}
