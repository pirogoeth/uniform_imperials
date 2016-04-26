package com.uniform_imperials.herald.http;

/**
 * Created by Sean Johnson 4/25/2016.
 */
public interface HttpResponse<T> {

    /**
     * Decodes a JSON string into a Java object.
     *
     * @param jsonString JSON-encoded String
     * @return T decoded object
     */
    T decode(String jsonString);
}
