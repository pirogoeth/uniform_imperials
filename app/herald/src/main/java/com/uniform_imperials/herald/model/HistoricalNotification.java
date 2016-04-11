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

public interface HistoricalNotification extends Observable, Parcelable, Persistable{
    @Key
    @Generated
    int getId();

    @Bindable
    String notif_title();

    void notif_title(String title);

    @Bindable
    String notif_subtext();

    void notif_subtext(String subtext);

    @Bindable
    String notif_content();

    void notif_content(String content);

    @Bindable
    String source_application();

    void source_application(String application);

    @Bindable
    String app_icon();

    void app_icon(String icon);

    @Bindable
    Date recv_date();

    void recv_date(Date date);

}
