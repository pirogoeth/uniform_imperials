package com.uniform_imperials.herald.model;

/**
 * Created by Sean Johnson on 3/29/2016.
 * Modified by Gustavo Moreira on 4/10/2016.
 */

import android.databinding.Bindable;
import android.databinding.Observable;
import android.os.Parcelable;

import io.requery.Column;
import io.requery.Entity;
import io.requery.Generated;
import io.requery.Key;
import io.requery.Persistable;

@Entity
public interface IHistoricalNotification extends Observable, Parcelable, Persistable {

    @Key
    @Generated
    int getId();

    @Bindable
    String getNotificationTitle();
    void setNotificationTitle(String s);

    @Bindable
    String getNotificationContent();
    void setNotificationContent(String s);

    @Bindable
    @Column(unique = true)
    String getNotificationKey();
    void setNotificationKey(String s);

    @Bindable
    String getSourceApplication();
    void setSourceApplication(String s);

    @Bindable
    String getLargeAppIcon();
    void setLargeAppIcon(String s);

    @Bindable
    String getSmallAppIcon();
    void setSmallAppIcon(String s);

    @Bindable
    String getReceiveDate();
    void setReceiveDate(String s);

    /**
     * Unix TS/Epoch version of receive date.
     * @return
     */
    @Bindable
    long getEpoch();
    void setEpoch(long l);

}
