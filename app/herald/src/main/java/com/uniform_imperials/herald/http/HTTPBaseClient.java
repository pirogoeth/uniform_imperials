package com.uniform_imperials.herald.http;


import com.joshdholtz.sentry.Sentry;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
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
     * @return
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

    public Future<IHttpResponse> get(String uri, HashMap<String, String> args) {
        URL requestUrl;

        try {
            requestUrl = this.buildRequestUrl(uri, this.encodeParamString(args));
        } catch (Exception exc) {
            // Shit
            return null;
        }

        return null;
    }

    public Future<IHttpResponse> post(String uri, HashMap<String, String> args) {
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

    public Future<IHttpResponse> put(String uri, HashMap<String, String> args) {
        return null;
    }

    public Future<IHttpResponse> delete(String uri, HashMap<String, String> args) {
        return null;
    }

    public Future<IHttpResponse> head(String uri, HashMap<String, String> args) {
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
