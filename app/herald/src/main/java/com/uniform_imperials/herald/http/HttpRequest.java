package com.uniform_imperials.herald.http;

/**
 * Created by Sean Johnson 4/25/2016.
 */
public interface HttpRequest<T> {

    /**
     * Encodes a Java object into a JSON string.
     *
     * @param Class<T> class to encode to
     * @param T object to encode
     * @return String
     */
    String encode(Class<T> encodeTarget, T o);
}
