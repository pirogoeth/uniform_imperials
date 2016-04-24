package com.uniform_imperials.herald.model;

/**
 * Created by Sean Johnson on 3/29/2016.
 * Modified by Gustavo Moreira on 4/10/2016.
 */

import android.databinding.Bindable;
import android.databinding.Observable;
import android.os.Parcelable;

import io.requery.Entity;
import io.requery.Generated;
import io.requery.Key;
import io.requery.Persistable;

@Entity
public interface IHistoricalNotification extends Observable, Parcelable, Persistable {
    @Key
    @Generated
    int getId();

    /**
     * NOTE: Notification title may or may not be used.
     *
     * @return
     */
    @Bindable
    String getNotificationTitle();
    void setNotificationTitle(String s);

    @Bindable
    String getNotificationContent();
    void setNotificationContent(String s);

    @Bindable
    String getNotificationKey();
    void setNotificationKey(String s);

    @Bindable
    String getSourceApplication();
    void setSourceApplication(String s);

    @Bindable
    String getAppIcon();
    void setAppIcon(String s);

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
