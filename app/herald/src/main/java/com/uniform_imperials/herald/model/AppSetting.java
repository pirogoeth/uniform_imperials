package com.uniform_imperials.herald.model;

import android.databinding.Bindable;
import android.databinding.Observable;
import android.os.Parcelable;

import java.util.Date;

import io.requery.Column;
import io.requery.Entity;
import io.requery.Generated;
import io.requery.Index;
import io.requery.Key;
import io.requery.Persistable;

/**
 * Created by Sean Johnson on 4/9/2016.
 *
 * Represents each app setting in the form of a key->value pair.
 */
@Entity
public interface AppSetting extends Observable, Parcelable, Persistable {

    /**
     * The unique identifier representing an AppSetting object.
     *
     * @return <int> Record id.
     */
    @Key
    @Generated
    int getId();

    /**
     * String representing the settings key.
     */
    @Bindable
    @Index
    @Column(unique = true)
    String getKey();

    /**
     * String representing a settings value.
     */
    @Bindable
    String getValue();
    void setValue(String s);

    /**
     * Last modified time for a settings value.
     */
    @Bindable
    Date getLastModifiedDate();
    void setLastModifiedDate(Date d);
}