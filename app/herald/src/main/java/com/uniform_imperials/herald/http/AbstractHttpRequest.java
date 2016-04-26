package com.uniform_imperials.herald.http;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

/**
 * Created by Sean Johnson on 4/25/2016.
 *
 * Wraps the encode() method to try to automatically encode a dataset.
 */
public abstract class AbstractHttpRequest<T> {

    /**
     * Wrapper around the encode(Class<T>, T) function.
     * Usage:
     *
     *      @Override
     *      public String encode(T o) {
     *          return this.encode(T.class, o);
     *      }
     *
     * @param o Object of type T to encode to.
     * @return String JSON-encoded data
     */
    public abstract String encode(T o);

    /**
     * Performs the true encoding from class instance to JSON.
     *
     * @param encodeClass class type to encode from.
     * @param o Type object
     * @return String JSON-encoded data
     */
    public String encode(Class<T> encodeClass, T o) {
        if (o == null) {
            return null;
        }

        Moshi m = new Moshi.Builder().build();
        JsonAdapter<T> ja = m.adapter(encodeClass);

        return ja.toJson(o);
    }
}
