package com.uniform_imperials.herald.http;


import com.joshdholtz.sentry.Sentry;

import org.apache.commons.io.IOUtils;

import java.io.BufferedOutputStream;
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
 * Other HTTP methods (OPTIONS, HEAD, PUT, DELETE and TRACE)
 * can be used with setRequestMethod(String).
 */
abstract class HTTPBaseClient {

    /**
     * Base API Url for requests.
     */
	private static URL baseApiUrl;

    /**
     * Returns the string representing the base API url for requests.
     * @return String
     */
    public static String getBaseApiUrl() {
        if (baseApiUrl == null) {
            return null;
        }

        return baseApiUrl.toString();
    }

    /**
     * Sets the base API url for requests.
     * @throws MalformedURLException if the URL given is invalid.
     */
    public static void setBaseApiUrl(String s) throws MalformedURLException {
        URL apiBase = null;

        try {
            apiBase = new URL(s);
        } catch (MalformedURLException exc) {
            throw exc;
        } catch (Exception exc) {
            Sentry.captureException(exc);
        }

        baseApiUrl = apiBase;
    }

    /**
     * Set of executors for requests. Serve as the works for resolving Futures and fun stuff.
     */
    private final ExecutorService httpRequestPool = Executors.newFixedThreadPool(4);

    /**
     * Class to decode received messages in to.
     */
    private Class<? extends HttpResponse> decodingTarget;

    /**
     * Sets the decoding target class for this request.
     *
     * @param c Class to decode into.
     */
    public void setDecodingTarget(Class<? extends HttpResponse> c) {
        this.decodingTarget = c;
    }

    /**
     * Performs an HTTP GET request. GET does not accept a request body, but it does accept
     * arguments in the form of query string arguments at the end of the URL.
     *
     * @param uri URI to request.
     * @param args HashMap of query string arguments.
     * @return Future<IHttpRequest> resolvable future
     */
    public Future<HttpResponse> get(String uri, HashMap<String, String> args) {
        URL requestUrl;

        try {
            requestUrl = this.buildRequestUrl(uri, this.encodeParamString(args));
        } catch (Exception exc) {
            // Shit
            return null;
        }

        return null;
    }

    /**
     * Performs an HTTP POST request. POST accepts text/x-www-form-urlencoded (or other) data.
     *
     * @param uri URI to request.
     * @param args HashMap of args to urlencode.
     * @return Future<IHttpRequest> resolvable future
     */
    public Future<HttpResponse> post(String uri, HashMap<String, String> args) {
        URL requestUrl;
        String requestBody = this.encodeParamString(args);

        try {
            requestUrl = this.buildRequestUrl(uri);
        } catch (Exception exc) {
            // Shit
            return null;
        }

        return null;
    }

    /**
     * Performs an HTTP POST request. POST accepts text/x-www-form-urlencoded (or other) data.
     *
     * @param uri URI to request.
     * @param payload Body payload to send to upstream.
     * @param payloadType HTTP Content-Type of submitted payload.
     * @return Future<IHttpRequest> resolvable future.
     */
    public Future<HttpResponse> post(String uri, String payload, String payloadType) {
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

        return this.httpRequestPool.submit(new Callable<HttpResponse>() {
            @Override
            public HttpResponse call() throws Exception {
                try {
                    InputStream in = conn.getInputStream();
                    String jsonResponse = IOUtils.toString(in, StandardCharsets.UTF_8);

                    HttpResponse resp = decodingTarget.newInstance();
                    resp.decode(jsonResponse);

                    resp.setStatusCode(hc.getResponseCode());
                    resp.setErrorMessage(hc.getResponseMessage());

                    return resp;
                } catch (Exception exc) {
                    HttpResponse resp = decodingTarget.newInstance();

                    resp.setStatusCode(hc.getResponseCode());
                    resp.setErrorMessage(hc.getResponseMessage());

                    return resp;
                }
            }
        });
    }

    /**
     * Performs an HTTP PUT request. PUT accepts data via request body, similar to POST.
     * In our case, we will only submit JSON data via PUT, so an argument URL encoder and HTTP
     * Content-Type specifications are unneeded.
     *
     * @param uri URI to request.
     * @param jsonPayload JSON-encoded payload to send to upstream.
     * @return Future<IHttpRequest> resolvable future.
     */
    public Future<HttpResponse> put(String uri, String jsonPayload) {
        URL requestUrl;

        try {
            requestUrl = this.buildRequestUrl(uri);
        } catch (Exception exc) {
            // Shit
            return null;
        }

        return null;
    }

    /**
     * Performs an HTTP DELETE request. DELETE accepts data via query string params.
     *
     * @param uri URI to request.
     * @param args HashMap of query string arguments to url encode.
     * @return Future<IHttpRequest> resolvable future.
     */
    public Future<HttpResponse> delete(String uri, HashMap<String, String> args) {
        URL requestUrl;

        try {
            requestUrl = this.buildRequestUrl(uri, this.encodeParamString(args));
        } catch (Exception exc) {
            // Shit
            return null;
        }

        return null;
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
        String baseUrl = baseApiUrl.toString();
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

	private void readStream(InputStream streamIn) {
//        BufferedInputStream in = new BufferedReader(streamIn);
//        String inputLn;
//        StringBuffer response = new StringBuffer();
//        try {
//            for (inputLn = in.readLine(); inputLn != null; inputLn = in.readLine()) {
//                response.append(inputLn);
//            }
//            in.close();
//        } catch (Exception e) {
//            Sentry.captureException(e);
//        }
//
//        System.out.println(response.toString());
    }

	private void writeStream(OutputStream write){
		BufferedOutputStream out;
		try {
			out = new BufferedOutputStream(write);
			//TODO finisih writer
			out.close();
		}catch(Exception e){
			Sentry.captureException(e);
		}
	}
}
