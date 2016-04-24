package com.uniform_imperials.herald.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.uniform_imperials.herald.MainApplication;

/**
 * Created by Sean Johnson on 4/24/2016.
 *
 * Useful methods for access display properties.
 */
public class DisplayUtil {

    /**
     * Finds the appropriate DisplayMetrics from the devices' WindowManager service.
     *
     * @return DisplayMetrics
     */
    public static DisplayMetrics getDisplayMetrics() {
        DisplayMetrics metrics = new DisplayMetrics();
        Context ctx = MainApplication.getStaticBaseContext()
                .getApplicationContext();

        WindowManager wm = (WindowManager) ctx
                .getSystemService(Context.WINDOW_SERVICE);

        wm.getDefaultDisplay().getMetrics(metrics);

        return metrics;
    }
}
