package com.uniform_imperials.herald.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.uniform_imperials.herald.MainApplication;
import com.uniform_imperials.herald.R;
import com.uniform_imperials.herald.fragments.NotificationHistoryFragment;
import com.uniform_imperials.herald.model.HistoricalNotification;
import static com.uniform_imperials.herald.util.NotificationUtil.base64DecodeBitmap;

import java.util.List;

/**
 * Created by Sean Johnson on 3/29/2016.
 * Modified by Gustavo Moreira on 4/22/2016
 */
public class NotificationHistoryAdapter
        extends RecyclerView.Adapter<NotificationHistoryAdapter.HNViewHolder> {

    /**
     * List of Notification Objects
     */
    private final List<HistoricalNotification> mValues;

    /**
     * List of Notification Objects
     */
    private final NotificationHistoryFragment.HistoricalNotificationFragmentInteractionListener mListener;

    public NotificationHistoryAdapter(NotificationHistoryFragment.HistoricalNotificationFragmentInteractionListener mListener) {
        this.mValues = MainApplication.getEntitySourceInstance()
                .select(HistoricalNotification.class)
                .limit(50)
                .get()
                .toList();

        this.mListener = mListener;
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
        holder.mTitleView.setText(mValues.get(position).getSourceApplication());
        holder.mDescriptionView.setText(mValues.get(position).getNotificationContent());
        holder.mIconView.setImageBitmap(base64DecodeBitmap(mValues.get(position).getAppIcon()));
        holder.mDateView.setText(mValues.get(position).getReceiveDate().toString());

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
        return;
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
