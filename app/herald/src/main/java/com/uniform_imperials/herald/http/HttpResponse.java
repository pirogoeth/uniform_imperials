package com.uniform_imperials.herald.http;

/**
 * Created by Sean on 4/23/2016.
 */
public abstract class HttpResponse {

    /**
     * Status code of the HTTP response.
     */
    private int statusCode = -1;

    /**
     * Error message
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
     * Decodes received data from the HTTP request into the IHttpRequest object.
     *
     * @param jsonData JSON data string.
     */
    public abstract void decode(String jsonData);
}
