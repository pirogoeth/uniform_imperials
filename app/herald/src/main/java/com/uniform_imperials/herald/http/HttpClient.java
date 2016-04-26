package com.uniform_imperials.herald.http;

import com.joshdholtz.sentry.Sentry;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Matt Humphries 4/22/2016.
 */

/**
 * http://developer.android.com/reference/java/net/HttpURLConnection.html
 *
 * HttpURLConnection uses the GET method by default.
 * It will use POST if setDoOutput(true) has been called.
 * Other HTTP methods (OPTIONS, HEAD, and TRACE)
 * can be used with setRequestMethod(String).
 */
public class HttpClient {

    /**
     * Set of executors for requests. Serve as the works for resolving Futures and fun stuff.
     */
    private final ExecutorService httpRequestPool = Executors.newFixedThreadPool(4);

    /**
     * Class to decode received messages in to.
     */
    private Class<? extends AbstractHttpResponse> decodingTarget;

    /**
     * Base API Url for requests.
     */
    private URL baseApiUrl;

    public HttpClient(String baseApiUrl) {
        try {
            this.setBaseApiUrl(baseApiUrl);
        } catch (MalformedURLException exc) {
            exc.printStackTrace();
        }
    }

    /**
     * Returns the string representing the base API url for requests.
     * @return String
     */
    public String getBaseApiUrl() {
        if (this.baseApiUrl == null) {
            return null;
        }

        return this.baseApiUrl.toString();
    }

    /**
     * Sets the base API url for requests.
     * @throws MalformedURLException if the URL given is invalid.
     */
    public void setBaseApiUrl(String s) throws MalformedURLException {
        URL apiBase = null;

        try {
            apiBase = new URL(s);
        } catch (MalformedURLException exc) {
            throw exc;
        } catch (Exception exc) {
            Sentry.captureException(exc);
        }

        this.baseApiUrl = apiBase;
    }

    /**
     * Sets the decoding target class for this request.
     *
     * @param c Class to decode into.
     */
    public void setDecodingTarget(Class<? extends AbstractHttpResponse> c) {
        this.decodingTarget = c;
    }

    /**
     * Performs an HTTP GET request without any arguments.
     * @param uri
     * @return
     */
    public Future<AbstractHttpResponse> get(String uri) {
        return this.get(uri, null);
    }

    /**
     * Performs an HTTP GET request. GET does not accept a request body, but it does accept
     * arguments in the form of query string arguments at the end of the URL.
     *
     * @param uri URI to request.
     * @param args HashMap of query string arguments.
     * @return Future<IHttpRequest> resolvable future
     */
    public Future<AbstractHttpResponse> get(String uri, HashMap<String, String> args) {
        URL requestUrl;

        try {
            if (args != null) {
                requestUrl = this.buildRequestUrl(uri, this.encodeParamString(args));
            } else {
                requestUrl = this.buildRequestUrl(uri, null);
            }
        } catch (Exception exc) {
            // Shit
            return null;
        }

        URLConnection conn;
        try {
            conn = requestUrl.openConnection();
        } catch (IOException exc) {
            // Shiiiit
            return null;
        }

        conn.setRequestProperty("Accept-Charset", "UTF-8");

        HttpURLConnection hc = (HttpURLConnection) conn;
        try {
            hc.setRequestMethod("GET");
        } catch (ProtocolException exc) {
            // Shit
            return null;
        }

        return this.httpRequestPool.submit(this.generateHttpResponseReader(conn));
    }

    /**
     * Performs an HTTP POST request with an empty request body.
     *
     * @param uri URI to request.
     * @return Future<AbstractHttpRequest> resolvable future
     */
    public Future<AbstractHttpResponse> post(String uri) {
        return this.post(uri, (HashMap) null);
    }

    /**
     * Performs an HTTP POST request. POST accepts text/x-www-form-urlencoded (or other) data.
     *
     * @param uri URI to request.
     * @param args HashMap of args to urlencode.
     * @return Future<AbstractHttpRequest> resolvable future
     */
    public Future<AbstractHttpResponse> post(String uri, HashMap<String, String> args) {
        URL requestUrl;

        String requestBody;
        if (args != null) {
            requestBody = this.encodeParamString(args);
        } else {
            requestBody = null;
        }

        return this.post(uri, requestBody, "application/x-www-form-urlencoded");
    }

    /**
     * Performs an HTTP POST request. POST accepts application/json (or other) data.
     *
     * @param uri URI to request.
     * @param request AbstractHttpRequest object to encode and send.
     * @return Future<AbstractHttpRequest> resolvable future.
     */
    public Future<AbstractHttpResponse> post(String uri, AbstractHttpRequest request) {
        return this.post(uri, request.encode(request), "application/json");
    }

    /**
     * Performs an HTTP POST request. POST accepts application/x-www-form-urlencoded (or other) data.
     *
     * @param uri URI to request.
     * @param payload Body payload to send to upstream.
     * @param payloadType HTTP Content-Type of submitted payload.
     * @return Future<AbstractHttpRequest> resolvable future.
     */
    public Future<AbstractHttpResponse> post(String uri, String payload, String payloadType) {
        URL requestUrl;

        try {
            requestUrl = this.buildRequestUrl(uri);
        } catch (Exception exc) {
            // Shit
            return null;
        }

        URLConnection conn;
        try {
            conn = requestUrl.openConnection();
        } catch (IOException exc) {
            // Shiiiiit
            return null;
        }

        conn.setDoOutput(true);
        conn.setRequestProperty("Accept-Charset", "UTF-8");
        conn.setRequestProperty("Content-Type", payloadType);

        HttpURLConnection hc = (HttpURLConnection) conn;
        try {
            hc.setRequestMethod("POST");
        } catch (ProtocolException exc) {
            // SHIT.
            return null;
        }

        try {
            OutputStream out = conn.getOutputStream();
            out.write(payload.getBytes("UTF-8"));
        } catch (IOException exc) {
            // Couldn't grab the output stream.
            System.out.println("Could not open or write HTTP OutputStream");
        }

        return this.httpRequestPool.submit(this.generateHttpResponseReader(conn));
    }

    /**
     * Performs an HTTP PUT request. PUT accepts data via request body, similar to POST.
     * In our case, we will only submit JSON data via PUT, so an argument URL encoder and HTTP
     * Content-Type specifications are unneeded.
     *
     * @param uri URI to request.
     * @param jsonPayload JSON-encoded payload to send to upstream.
     * @return Future<AbstractHttpRequest> resolvable future.
     */
    public Future<AbstractHttpResponse> put(String uri, String jsonPayload) {
        URL requestUrl;

        try {
            requestUrl = this.buildRequestUrl(uri);
        } catch (Exception exc) {
            // Shit
            return null;
        }

        URLConnection conn;
        try {
            conn = requestUrl.openConnection();
        } catch (IOException exc) {
            // Shiiiiit
            return null;
        }

        conn.setDoOutput(true);
        conn.setRequestProperty("Accept-Charset", "UTF-8");
        conn.setRequestProperty("Content-Type", "application/json");

        HttpURLConnection hc = (HttpURLConnection) conn;
        try {
            hc.setRequestMethod("PUT");
        } catch (ProtocolException exc) {
            // SHIT.
            return null;
        }

        try {
            OutputStream out = conn.getOutputStream();
            out.write(jsonPayload.getBytes("UTF-8"));
        } catch (IOException exc) {
            // Couldn't grab the output stream.
            System.out.println("Could not open or write HTTP OutputStream");
        }

        return this.httpRequestPool.submit(this.generateHttpResponseReader(conn));
    }

    /**
     * Performs an HTTP PUT request. PUT accepts data via request body, similar to POST.
     * In our case, we will only submit JSON data via PUT, so an argument URL encoder and HTTP
     * Content-Type specifications are unneeded.
     *
     * @param uri URI to request.
     * @param request Request object to encode and send.
     * @return Future<AbstractHttpRequest> resolvable future.
     */
    public Future<AbstractHttpResponse> put(String uri, AbstractHttpRequest request) {
        return this.put(uri, request.encode(request));
    }

    /**
     * Performs an HTTP DELETE request. DELETE accepts data via query string params.
     *
     * @param uri URI to request.
     * @param args HashMap of query string arguments to url encode.
     * @return Future<AbstractHttpRequest> resolvable future.
     */
    public Future<AbstractHttpResponse> delete(String uri, HashMap<String, String> args) {
        URL requestUrl;

        try {
            requestUrl = this.buildRequestUrl(uri, this.encodeParamString(args));
        } catch (Exception exc) {
            // Shit
            return null;
        }

        URLConnection conn;
        try {
            conn = requestUrl.openConnection();
        } catch (IOException exc) {
            // Shiiiit
            return null;
        }

        conn.setRequestProperty("Accept-Charset", "UTF-8");

        HttpURLConnection hc = (HttpURLConnection) conn;
        try {
            hc.setRequestMethod("DELETE");
        } catch (ProtocolException exc) {
            // Shit
            return null;
        }

        return this.httpRequestPool.submit(this.generateHttpResponseReader(conn));
    }

    private URL buildRequestUrl(String uri) throws Exception {
        return this.buildRequestUrl(uri, null);
    }

    private URL buildRequestUrl(String uri, String params) throws Exception {
        if (uri == null) {
            return null;
        }

        StringBuilder requestUrl = new StringBuilder();

        // First, process the base url.
        String baseUrl;
        try {
            baseUrl = getBaseApiUrl().toString();
        } catch (NullPointerException exc) {
            baseUrl = null;
        }
        if (baseUrl == null) {
            throw new Exception("Must provide a base URL before making a request.");
        }

        if (baseUrl.charAt(baseUrl.length() - 1) == '/') {
            // Trim off the trailing slash
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }

        // Push the base url in the first position of the request url.
        requestUrl.append(baseUrl);

        if (uri.charAt(0) != '/') {
            uri = "/" + uri;
        }

        // Push the request uri on top of the base url
        requestUrl.append(uri);

        if (params != null) {
            // Make sure the params string begins with a '?', if present.
            if (params.charAt(0) != '?') {
                params = "?" + params;
            }

            // Make sure the params string does not end with &
            if (params.charAt(params.length() - 1) == '&') {
                params = params.substring(0, params.length() - 1);
            }
        }

        URL fullRequestUrl;
        try {
            fullRequestUrl = new URL(requestUrl.toString());
        } catch (MalformedURLException exc) {
            // What alternatives do we have?
            Sentry.captureException(exc);
            throw exc;
        } catch (Exception exc) {
            // No other exceptions should be thrown, but be careful.
            Sentry.captureException(exc);
            Sentry.captureMessage("Last exception captured was unexpected!");

            return null;
        }

        return fullRequestUrl;
    }

    private String encodeParamString(HashMap<String, String> params) {
        StringBuilder sb = new StringBuilder();
        sb.append("?");

        for (Map.Entry<String, String> ent : params.entrySet()) {
            try {
                sb.append(
                        String.format(
                                "%s=%s&",
                                URLEncoder.encode(ent.getKey(), "UTF-8"),
                                URLEncoder.encode(ent.getValue(), "UTF-8")
                        )
                );
            } catch (UnsupportedEncodingException exc) {
                // Figure out some way to handle this.
                Sentry.captureException(exc);
            } catch (Exception exc) {
                // This should not issue any other exceptions, but be cautious
                Sentry.captureException(exc);
                Sentry.captureMessage("Last exception captured was unexpected!");

                return null;
            }
        }

        String paramString = sb.toString();
        if (paramString.charAt(paramString.length() - 1) == '&') {
            // substring is inclusive -> exclusive
            paramString = paramString.substring(0, paramString.length() - 1);
        }

        return paramString;
    }

    private Callable<AbstractHttpResponse> generateHttpResponseReader(URLConnection conn) {
        HttpURLConnection hc = (HttpURLConnection) conn;

        return new Callable<AbstractHttpResponse>() {
            @Override
            public AbstractHttpResponse call() throws Exception {
                try {
                    InputStream in = conn.getInputStream();
                    String jsonResponse = IOUtils.toString(in, StandardCharsets.UTF_8);

                    AbstractHttpResponse resp = decodingTarget.newInstance();
                    resp.decode(jsonResponse);

                    resp.setStatusCode(hc.getResponseCode());
                    resp.setStatusMessage(hc.getResponseMessage());

                    return resp;
                } catch (Exception exc) {
                    AbstractHttpResponse resp = decodingTarget.newInstance();

                    resp.setStatusCode(hc.getResponseCode());
                    resp.setStatusMessage(hc.getResponseMessage());
                    resp.setErrorMessage(exc.getLocalizedMessage());

                    return resp;
                }
            }
        };
    }
}
