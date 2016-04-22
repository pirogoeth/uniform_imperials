package com.uniform_imperials.herald.util.settings;

import android.view.View;

import com.uniform_imperials.herald.model.AppSetting;

/**
 * Created by Sean Johnson on 4/22/2016.
 *
 * Abstracts away logic for modifying and displaying setting views.
 */
public interface ISettingWrapper {

    /**
     * Key of the setting key we are representing.
     */
    String settingKey = null;

    /**
     * Database entry for this key.
     */
    AppSetting settingInstance = null;

    /**
     * View to open in the setting modification dialog.
     */
    View modificationView = null;

    /**
     * View used in displaying the app setting.
     */
    View displayView = null;



}
