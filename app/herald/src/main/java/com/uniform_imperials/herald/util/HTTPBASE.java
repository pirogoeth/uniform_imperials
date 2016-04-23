package com.uniform_imperials.herald.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;


/**
 * Created by Matt on 4/22/2016.
 */


/**
 * http://developer.android.com/reference/java/net/HttpURLConnection.html
 *
 * HttpURLConnection uses the GET method by default.
 * It will use POST if setDoOutput(true) has been called.
 * Other HTTP methods (OPTIONS, HEAD, PUT, DELETE and TRACE)
 * can be used with setRequestMethod(String).
 */
abstract class HTTP_BASE {
	private URL url;
	private HashMap<String, String> args;
	public HTTP_BASE(){
		url = new URL("http://suckitbitches.middlefinger");
		HttpURLConnection hc = (HttpURLConnection) URL.openConnection();
		try {
			InputStream in = new BufferedInputStream(urlConnection.getInputStream());
			readStream(in);
			finally{
				urlConnection.disconnect();
			}
		}

	}

	/**
	 * Instances must be configured with setDoOutput(true) to
	 * include a request body. GET method by default.
	 * Transmit data by writing
	 * to the stream returned by getOutputStream().
	 */
	private get(String URL, HashMap args) {
		HttpURLConnection hc;

		try {
			hc = (HttpURLConnection) url.openConnection();

			hc.setDoOutput(true);
			hc.setChunkedStreamingMode(0);

			OutputStream out = new BufferedOutputStream(hc.getOutputStream());
			writeStream(out);

			InputStream in = new BufferedInputStream(hc.getInputStream());
			readStream(in);
			finally {
				hc.disconnect();
			}
		}//TODO CATCH

	}
	//UE
	private put(String URL, HashMap args){
		HttpURLConnection hc;
		hc.setRequestMethod(PUT);
		try {
			hc = (HttpURLConnection) url.openConnection();

			hc.setDoOutput(true);
			hc.setChunkedStreamingMode(0);

			OutputStream out = new BufferedOutputStream(hc.getOutputStream());
			writeStream(out);

			InputStream in = new BufferedInputStream(hc.getInputStream());
			readStream(in);
			finally {
				hc.disconnect();
			}
		}//TODO CATCH
	}

	/**
	 * The response body may be read from the stream
	 * returned by getInputStream(). If the response has
	 * no body, that method returns an empty stream.
	 */
	private post(String URL, HashMap args){
		HttpURLConnection hc = (HttpURLConnection) url.openConnection();
		try {
			hc.setDoOutput(true);
			hc.setChunkedStreamingMode(0);

			OutputStream out = new BufferedOutputStream(hc.getOutputStream());
			writeStream(out);

			InputStream in = new BufferedInputStream(hc.getInputStream());
			readStream(in);
			finally {
				hc.disconnect();
			}
		}//TODO catch
	}

	//QS
	private delete(String URL, HashMap args){
		HttpURLConnection hc;
		hc.setRequestMethod(DELETE);
		hc = (HttpURLConnection) url.openConnection();
	}

	private head(String URL, HashMap args){
		HttpURLConnection hc;
		hc.setRequestMethod(HEAD);
		hc = (HttpURLConnection) url.openConnection();
	}


}
