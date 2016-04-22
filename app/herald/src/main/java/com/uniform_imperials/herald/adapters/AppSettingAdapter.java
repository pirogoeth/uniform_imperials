package com.uniform_imperials.herald.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.uniform_imperials.herald.MainApplication;
import com.uniform_imperials.herald.R;
import com.uniform_imperials.herald.fragments.SettingFragment.AppSettingFragmentInteractionListener;
import com.uniform_imperials.herald.model.AppSetting;
import com.uniform_imperials.herald.util.DefaultSettings;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link AppSetting} and makes a call to the
 * specified {@link AppSettingFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class AppSettingAdapter extends RecyclerView.Adapter<AppSettingAdapter.ASViewHolder> {

    private final List<AppSetting> mValues;
    private final AppSettingFragmentInteractionListener mListener;

    public AppSettingAdapter(AppSettingFragmentInteractionListener listener) {
        this.mValues = MainApplication.getEntitySourceInstance()
                                      .select(AppSetting.class)
                                      .get()
                                      .toList();
        this.mListener = listener;
    }

    @Override
    public ASViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_setting, parent, false);
        return new ASViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ASViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mKeyView.setText(mValues.get(position).getKey());
        holder.mDescriptionView.setText(DefaultSettings.getKeyDescription(holder.mItem.getKey()));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onAppSettingFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ASViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mKeyView;
        public final TextView mDescriptionView;
        public AppSetting mItem;

        public ASViewHolder(View view) {
            super(view);
            mView = view;
            mKeyView = (TextView) view.findViewById(R.id.appsetting_key);
            mDescriptionView = (TextView) view.findViewById(R.id.appsetting_description);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mKeyView.getText() + "'";
        }
    }
}
