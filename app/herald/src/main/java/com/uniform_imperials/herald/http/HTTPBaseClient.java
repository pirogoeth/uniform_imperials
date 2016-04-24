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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;


/**
 * Created by Matt Humphrieson 4/22/2016.
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
	 * Connection URL
	 */
	private String url;

	/**
	 * String -> String mapping of arguments to
	 */
	private HashMap<String, String> args;

	public HTTPBaseClient(String url, HashMap<String, String> args){
	}

	/**
	 * Instances must be configured with setDoOutput(true) to
	 * include a request body. GET method by default.
	 * Transmit data by writing
	 * to the stream returned by getOutputStream().
	 */
	private get(String URL, HashMap args) {
		HttpURLConnection hc;
		hc.setDoOutput(true);
		URL server;
		InputStream in;
		OutputStream out;
		try {
			server = new URL(url);
			hc = (HttpURLConnection) server.openConnection();
			hc.setChunkedStreamingMode(0);

			writeStream(out);
			readStream(in);

		} catch (Exception e) {
			Sentry.captureException(e);
		} finally {
			hc.disconnect();
		}

	}

	//UE
	private void put(String URL, HashMap args){
		HttpURLConnection hc;
		hc.setDoOutput(true);
		URL server;
		InputStream in;
		OutputStream out;
		try {
			hc = (HttpURLConnection) server.openConnection();
			hc.setRequestMethod(PUT);
			hc.setChunkedStreamingMode(0);

			writeStream(out);
			readStream(in);
			finally {
				hc.disconnect();
			}
		}catch (Exception e) {
			Sentry.captureException(e);
		}
	}

	/**
	 * The response body may be read from the stream
	 * returned by getInputStream(). If the response has
	 * no body, that method returns an empty stream.
	 */
	private void post(String URL, HashMap args){
		HttpURLConnection hc;
		hc.setDoOutput(true);
		URL server;
		InputStream in;
		OutputStream out;
		try {
			hc = (HttpURLConnection) server.openConnection();
			hc.setChunkedStreamingMode(0);
			writeStream(out);
			readStream(in);
			finally {
				hc.disconnect();
			}
		}catch (Exception e) {
			Sentry.captureException(e);
		}
	}

	//QS
	private void delete(URL URL, HashMap args){
		HttpURLConnection hc;
		URL server;
		OutputStream out;
		try {
			hc = (HttpURLConnection) server.openConnection();
			hc.setRequestMethod(DELETE);
			writeStream(out);
			finally {
				hc.disconnect();
			}
		} catch (Exception e) {
			Sentry.captureException(e);
		}
	}

	private void head(String URL, HashMap args) {
		HttpURLConnection hc;
		hc.setDoOutput(true);
		URL server;
		InputStream in;
		OutputStream out;
		try {
			hc = (HttpURLConnection) server.openConnection();
			hc.setRequestMethod(HEAD);
			readStream(in);
		} catch (Exception e) {
			Sentry.captureException(e);
		}
	}
	private void readStream(InputStream streamIn){
		BufferedInputStream in = new BufferedReader(streamIn);
		String inputLn;
		StringBuffer response = new StringBuffer();
		try{
			for(inputLn = in.readLine();inputLn != null; inputLn = in.readLine()){
				response.append(inputLn);
			}
			in.close();
		}catch (Exception e) {
			Sentry.captureException(e);
		}

		System.out.println(response.toString());
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
