package com.uniform_imperials.herald.model;

/**
 * Created by Sean Johnson on 3/29/2016.
 * Modified by Gustavo Moreira on 4/10/2016.
 */

import android.databinding.Bindable;
import android.databinding.Observable;
import android.os.Parcelable;

import java.util.Date;

import io.requery.Entity;
import io.requery.Generated;
import io.requery.Key;
import io.requery.OneToOne;
import io.requery.Persistable;

@Entity
public interface IHistoricalNotification extends Observable, Parcelable, Persistable{
    @Key
    @Generated
    int getId();

    @Bindable
    String getNotificationTitle();
    void setNotificationTitle(String s);

    @Bindable
    String getNotificationSubtext();
    void setNotificationSubtext(String s);

    @Bindable
    String getNotificationContent();
    void setNotificationContent(String s);

    @Bindable
    String getSourceApplication();
    void setSourceApplication(String s);

    @Bindable
    String getAppIcon();
    void setAppIcon(String s);

    @Bindable
    Date getReceiveDate();
    void setReceiveDate(Date d);

}
