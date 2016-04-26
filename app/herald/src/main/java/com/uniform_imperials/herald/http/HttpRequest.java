package com.uniform_imperials.herald.http;

/**
 * Created by Sean Johnson 4/25/2016.
 */
public interface HttpRequest<T> {

    /**
     * Encodes a Java object into a JSON string.
     *
     * @param T object to encode
     * @return String
     */
    String encode(T object);
}
