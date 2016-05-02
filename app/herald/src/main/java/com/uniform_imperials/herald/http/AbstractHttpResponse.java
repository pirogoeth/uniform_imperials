package com.uniform_imperials.herald.http;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;

/**
 * Created by Sean Johnson on 4/25/2016.
 *
 * Abstracts out a HttpResponse implementation with generics and decode() simple impl.
 * and stub.
 */
public abstract class AbstractHttpResponse<T> implements HttpResponse<T> {

    /**
     * Status code of the HTTP response.
     */
    private int statusCode = -1;

    /**
     * Status message returned by the HTTP upstream.
     */
    private String statusMessage = null;

    /**
     * Error message from any exceptions thrown during Future resolution.
     */
    private String errorMessage = null;

    /**
     * Sets the Response's status code.
     * @param i status code
     */
    public void setStatusCode(int i) {
        if (this.statusCode != -1) {
            throw new UnsupportedOperationException("Can not overwrite a set status code");
        }

        this.statusCode = i;
    }

    /**
     * Returns the status code.
     * @return int
     */
    public int getStatusCode() {
        return this.statusCode;
    }

    /**
     * Sets the Response's HTTP status message as returned by the HTTP upstream.
     */
    public void setStatusMessage(String statusMessage) {
        if (this.statusMessage != null) {
            throw new UnsupportedOperationException("Can not overwrite a set status message");
        }

        this.statusMessage = statusMessage;
    }

    /**
     * Returns the status message.
     * @return String
     */
    public String getStatusMessage() {
        return statusMessage;
    }

    /**
     * Sets the Response's error message, if something prevented the request from being fulfilled.
     * @param s String message
     */
    public void setErrorMessage(String s) {
        if (this.errorMessage != null) {
            throw new UnsupportedOperationException("Can not overwrite a set error message");
        }

        this.errorMessage = s;
    }

    /**
     * Returns the error message.
     * @return String
     */
    public String getErrorMessage() {
        return this.errorMessage;
    }

    /**
     * This wrapper will do the passing and auto decode for any subclasses.
     * Usage:
     *
     *      @Override
     *      public T decode(String jsonString) {
     *          return this.decode(T.class, jsonString);
     *      }
     *
     * @param jsonString JSON-encoded String
     * @return T
     */
    public abstract T decode(String jsonString);

    /**
     * Returns a JSON-decoded, populated version of T.
     *
     * @param decodeTarget Class to decode into.
     * @param jsonString JSON data to decode.
     * @return T
     */
    public T decode(Class<T> decodeTarget, String jsonString) {
        Moshi m = new Moshi.Builder().build();
        JsonAdapter<T> ja = m.adapter(decodeTarget);

        try {
            return ja.fromJson(jsonString);
        } catch (IOException exc) {
            // Could not decode JSON string into ChannelJson class.
            return null;
        }
    }
}
