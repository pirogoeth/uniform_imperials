package com.uniform_imperials.herald.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.joshdholtz.sentry.Sentry;
import com.uniform_imperials.herald.MainApplication;
import com.uniform_imperials.herald.R;
import com.uniform_imperials.herald.fragments.NotificationHistoryFragment;
import com.uniform_imperials.herald.model.HistoricalNotification;
import com.uniform_imperials.herald.util.DisplayUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.uniform_imperials.herald.util.NotificationUtil.base64DecodeBitmap;

/**
 * Created by Sean Johnson on 3/29/2016.
 * Modified by Gustavo Moreira on 4/22/2016
 */
public class NotificationHistoryAdapter
        extends RecyclerView.Adapter<NotificationHistoryAdapter.HNViewHolder> {

    /**
     * List of Notification Objects
     */
    private ArrayList<HistoricalNotification> mValues;

    /**
     * List of Notification Objects
     */
    private final NotificationHistoryFragment.HistoricalNotificationFragmentInteractionListener mListener;

    public NotificationHistoryAdapter(NotificationHistoryFragment.HistoricalNotificationFragmentInteractionListener mListener) {
        this.mValues = new ArrayList<>();

        this.loadAllNotifications();

        this.mListener = mListener;
    }

    /**
     * Reloads all notification data (up to +limit+) into the data set.
     */
    public void loadAllNotifications() {
        this.mValues.clear();

        this.mValues.addAll(
                MainApplication.getEntitySourceInstance()
                        .select(HistoricalNotification.class)
                        .orderBy(HistoricalNotification.EPOCH.desc())
                        .limit(50)
                        .get()
                        .toList()
        );

        this.notifyDataSetChanged();
    }

    /**
     * Loads newest notifications into the NHFragment's RecyclerView.
     *
     * NOTE: We should really make sure that this doesn't hang everything up -- it has the potential
     * to be very expensive.
     */
    public void loadNewNotifications() {
        // Reloading all of the notifications is moderately inefficient; try to get only the newest.
        // We can do this by using the latest epoch time as a where condition.
        long newestNotif;
        try {
            newestNotif = this.mValues.get(0).getEpoch();
        } catch (IndexOutOfBoundsException exc) {
            newestNotif = 0;
        }

        ArrayList<HistoricalNotification> addedNotifs = new ArrayList<>();
        addedNotifs.addAll(
                MainApplication.getEntitySourceInstance()
                        .select(HistoricalNotification.class)
                        .where(HistoricalNotification.EPOCH.gt(newestNotif))
                        .orderBy(HistoricalNotification.EPOCH.desc())
                        .limit(50)
                        .get()
                        .toList());

        // The new notifications should be pushed on to the front of the adapter's data set.
        // This requires a small loop :(
        for (int i = addedNotifs.size() - 1; i >= 0; i--) {
            this.mValues.add(0, addedNotifs.get(i));
            // Are there consequences to doing this?
            this.notifyDataSetChanged();
        }
    }

    /**
     * Creates a viewholder with the view for this recycler view.
     *
     * @param parent ViewGroup parent
     * @param viewType view type
     * @return new ViewHolder
     */
    @Override
    public HNViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        // Create the view
        View v = LayoutInflater.from(parent.getContext())
                               .inflate(R.layout.anh_notification_view, parent, false);

        return new HNViewHolder(v);
    }

    /**
     * Binds a single dataset record to a view.
     *
     * @param holder viewholder element
     * @param position dataset position
     */
    @Override
    public void onBindViewHolder(HNViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mTitleView.setText(mValues.get(position).getNotificationTitle());
        holder.mDescriptionView.setText(mValues.get(position).getNotificationContent());
        holder.mIconView.setImageBitmap(base64DecodeBitmap(mValues.get(position).getAppIcon()));

        Date d;
        try {
            // TODO: Parse date *properly* from string.
            d = new SimpleDateFormat().parse(
                    mValues.get(position).getReceiveDate()
            );
            holder.mDateView.setText(d.toString());
        } catch (ParseException e) {
            Sentry.captureException(e);
            holder.mDateView.setText(mValues.get(position).getReceiveDate());
        }

        // Do some display math to set the maxEms on mContentView

        // Animate for the list item entry.
        this.runEnterAnimation(holder.mView);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onHistoricalNotificationFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    /**
     * Handles view recycle events
     *
     * @param holder viewholder
     */
    @Override
    public void onViewDetachedFromWindow(HNViewHolder holder) {
        // Animate for the list item entry.
        this.runExitAnimation(holder.mView);
    }

    /**
     * Returns dataset length
     *
     * @return dataset length
     */
    @Override
    public int getItemCount() {
        return this.mValues.size();
    }

    /**
     * Performs a simple sliding entry animation for list view.
     *
     * @param view View to animate
     */
    private void runEnterAnimation(View view) {
        final DisplayMetrics metrics = DisplayUtil.getDisplayMetrics();

        view.setTranslationY(metrics.heightPixels);
        view.animate()
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator(3.f))
                .setDuration(300)
                .start();
    }

    /**
     * Performs a simple sliding entry animation for list view exit.
     *
     * @param view View to animate
     */
    private void runExitAnimation(View view) {
        final DisplayMetrics metrics = DisplayUtil.getDisplayMetrics();

        view.setTranslationX(0);
        view.animate()
                .translationX(metrics.widthPixels)
                .setInterpolator(new DecelerateInterpolator(3.f))
                .setDuration(150)
                .start();
    }

    public class HNViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTitleView;
        public final TextView mDescriptionView;
        public final ImageView mIconView;
        public final TextView mDateView;
        public HistoricalNotification mItem;

        public HNViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = (TextView) view.findViewById(R.id.nh_item_title);
            mDescriptionView = (TextView) view.findViewById(R.id.nh_item_content);
            mIconView = (ImageView) view.findViewById(R.id.nh_item_icon);
            mDateView = (TextView) view.findViewById(R.id.nh_item_date);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitleView.getText() + "'";
        }
    }

}
