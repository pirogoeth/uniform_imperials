package com.uniform_imperials.herald.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uniform_imperials.herald.R;
import com.uniform_imperials.herald.model.HistoricalNotification;

import io.requery.Persistable;
import io.requery.rx.SingleEntityStore;

/**
 * Created by Sean Johnson on 3/29/2016.
 */
public class HistoricalNotificationAdapter extends RecyclerView.Adapter<HistoricalNotificationAdapter.ViewHolder> {

    /**
     * Datastore to load from.
     */
    private SingleEntityStore<Persistable> dataStore;

    /**
     * Dataset to display
     */
    private HistoricalNotification[] mDataSet;

    /**
     * Encapsulates all views needed to display a single dataset item
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // TODO: Figure out how items will be displayed in the view.

        public ViewHolder(View v) {
            super(v);
        }
    }

    public HistoricalNotificationAdapter(SingleEntityStore<Persistable> dataStore) {
        this.dataStore = dataStore;
        // TODO: Actually load the dataset from the store.
        this.mDataSet = null;
    }

    /**
     * Creates a viewholder with the view for this recycler view.
     *
     * @param parent ViewGroup parent
     * @param viewType view type
     * @return new ViewHolder
     */
    @Override
    public HistoricalNotificationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                       int viewType) {
        // Create the view
        View v = LayoutInflater.from(parent.getContext())
                               .inflate(R.layout.anh_notification_view, parent, false);

        // TODO: Set the necessary data in the view.

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    /**
     * Binds a single dataset record to a view.
     *
     * @param holder viewholder element
     * @param position dataset position
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get dataset element and replace view contents
        // -- holder.whateverview.setText(this.mDataSet[position]) ...
        return;
    }

    /**
     * Returns dataset length
     *
     * @return dataset length
     */
    @Override
    public int getItemCount() {
        return this.mDataSet.length;
    }

}
