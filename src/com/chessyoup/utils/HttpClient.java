package com.chessyoup.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.util.Log;

public class HttpClient {

	public static final HttpClient client = new HttpClient();

	static final String TAG = "HTTPClient";

	private HttpClient() {

	}

	public static HttpClient getInstance() {
		return HttpClient.client;
	}
	
	public HttpClientResponse readEntity(String entityUrl,String authorization) throws IOException {
		
		Log.d(TAG, "Reading '" + entityUrl);
		
		URL url;
		try {
			url = new URL(entityUrl);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("invalid url: " + entityUrl);
		}
		
		
		
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setUseCaches(false);			
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Authorization",authorization);
			
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));			
			StringBuffer reponseBody = new StringBuffer();
			String line = null;
			
			while( (line  = br.readLine() )!= null ){
				reponseBody.append(line);
			}
			
			br.close();
			
			int status = conn.getResponseCode();
			if (status != 200) {
				throw new IOException("Post failed with error code " + status);
			}
			
			HttpClientResponse response = new HttpClientResponse();
			response.setStatus(status);
			response.setBody(reponseBody.toString());
			
			return response;
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}		
	}
	
	public HttpClientResponse post(String endpoint, Map<String, String> params)
			throws IOException {
		URL url;
		try {
			url = new URL(endpoint);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("invalid url: " + endpoint);
		}
		StringBuilder bodyBuilder = new StringBuilder();
		Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
		// constructs the POST body using the parameters
		while (iterator.hasNext()) {
			Entry<String, String> param = iterator.next();
			bodyBuilder.append(param.getKey()).append('=')
					.append(param.getValue());
			if (iterator.hasNext()) {
				bodyBuilder.append('&');
			}
		}
		String body = bodyBuilder.toString();
		Log.d(TAG, "Posting '" + body + "' to " + url);
		byte[] bytes = body.getBytes();
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setFixedLengthStreamingMode(bytes.length);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded;charset=UTF-8");
			// post the request
			OutputStream out = conn.getOutputStream();
			out.write(bytes);
			out.close();
			
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));			
			StringBuffer reponseBody = new StringBuffer();
			String line = null;
			
			while( (line  = br.readLine() )!= null ){
				reponseBody.append(line);
			}
			
			br.close();
			
			// handle the response
			int status = conn.getResponseCode();
			if (status != 200) {
				throw new IOException("Post failed with error code " + status);
			}
			
			HttpClientResponse response = new HttpClientResponse();
			response.setStatus(status);
			response.setBody(reponseBody.toString());
			
			return response;
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
	}

}
