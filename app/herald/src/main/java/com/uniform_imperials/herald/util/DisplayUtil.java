package com.uniform_imperials.herald.util;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
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

    /**
     * Gustavo's magic formula for calculating density-dependent text size in Ems.
     *
     * @param view View to measure
     * @param boundingView View to use as a side-bounding block.
     * @return int size in ems.
     */
    public static int calculateMaxEms(View view, View boundingView) {
        Context ctx =  MainApplication.getStaticBaseContext()
                .getApplicationContext();

        WindowManager wm = (WindowManager) ctx
                .getSystemService(MainApplication.WINDOW_SERVICE);

        Display display = wm.getDefaultDisplay();

        view.measure(0, 0);
        boundingView.measure(0, 0);

        Point size = new Point();
        display.getSize(size);
        int totalWidth = size.x;
        int width = totalWidth - boundingView.getMeasuredWidth();

        float density = getDisplayMetrics().density;

        return (int) (((double) width - 25) / (15 * (density + 0.9)));
    }
}
