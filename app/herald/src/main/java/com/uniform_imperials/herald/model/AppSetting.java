package com.uniform_imperials.herald.model;

import android.databinding.Bindable;
import android.databinding.Observable;
import android.os.Parcelable;

import java.util.Date;

import io.requery.Entity;
import io.requery.Generated;
import io.requery.Index;
import io.requery.Key;
import io.requery.Persistable;

/**
 * Created by Sean Johnson on 4/9/2016.
 */

@Entity
public interface AppSetting extends Observable, Parcelable, Persistable {

    /**
     * Primary setting id.
     *
     * @return int
     */
    @Key
    @Generated
    int getSettingId();

    /**
     * Setting key reference. Can not be changed after initial set.
     *
     * @return String
     */
    @Index
    @Bindable
    String getKey();

    /**
     * Mutable setting value.
     *
     * @return String
     */
    @Bindable
    String getValue();
    void setValue(String value);

    /**
     * Mutable modification date/time.
     *
     * @return Date
     */
    @Bindable
    Date getLastModifiedDate();
    void setLastModifiedDate(Date d);
}
