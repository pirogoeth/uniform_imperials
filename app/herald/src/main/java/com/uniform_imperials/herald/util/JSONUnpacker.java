package com.uniform_imperials.herald.util;

import android.support.annotation.Nullable;

import com.joshdholtz.sentry.Sentry;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sean Johnson on 4/10/2016.
 *
 * Simplifies the unpacking of JSON values from Moshi into native types.
 */
public class JSONUnpacker {

    private static Moshi m = new Moshi.Builder().build();

    /**
     * Unpacks a simple boolean from a JSON string.
     *
     * @param s JSON string
     * @return <boolean> result
     */
    public static boolean unpackBoolean(String s) {
        JsonAdapter<Boolean> ja = m.adapter(Boolean.class);
        try {
            return ja.fromJson(s);
        } catch (IOException e) {
            // TODO: Simple Sentry wrapper that handles cases where we can't log.
            Sentry.captureException(e);
            return false;
        }
    }

    /**
     * Unpacks a simple string from a JSON string.
     *
     * @param s JSON string
     * @return <String> result
     */
    @Nullable
    public static String unpackString(String s) {
        JsonAdapter<String> ja = m.adapter(String.class);
        try {
            return ja.fromJson(s);
        } catch (IOException e) {
            // TODO: Simple Sentry wrapper that handles cases where we can't log.
            Sentry.captureException(e);
            return null;
        }
    }
}
