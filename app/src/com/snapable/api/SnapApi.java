package com.snapable.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;

import android.util.Log;

public class SnapApi {
	
	private static String TAG = "SnapApi";

	// API versions available
	public static String api_host = "http://devapi.snapable.com"; // no trailing /
	public static String api_version = "private_v1";
	private static String api_key = "abc123";
	private static String api_secret = "123";
	
	public static String getNonce() {
        //$nonce = '';
        //while ($length > 0) {
        //    $nonce .= dechex(mt_rand(0,15));
        //    $length -= 1;
        //}
        return "abc";
    }

    /**
     * Generate a date timestamp to use for API key signing.
     */
    public static String getDate() {
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
    	dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    	return dateFormat.format(new Date());
    }

    /**
     * Create a HMAC signature for an API request and return an array with all
     * the required parts required to build the API call.
     */
    public static LinkedHashMap<String, String> sign(String verb, String path) {
    	path = SnapApi.cleanPath(path);
        
        String x_snap_nonce = SnapApi.getNonce();
        String x_snap_date = SnapApi.getDate();
        StringBuilder enc = new StringBuilder();
        String raw_signature = SnapApi.api_key + verb.toUpperCase() + path + x_snap_nonce + x_snap_date;
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            SecretKeySpec secret = new SecretKeySpec(SnapApi.api_secret.getBytes(),"HmacSHA1");
            mac.init(secret);
            byte[] digest = mac.doFinal(raw_signature.getBytes());
            
            for (byte b : digest) {
            	enc.append(String.format("%02x", b));
			}
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        
        LinkedHashMap<String, String> resp = new LinkedHashMap<String, String>(3);
        resp.put("x_snap_nonce", x_snap_nonce);
        resp.put("x_snap_date", x_snap_date);
        resp.put("signature", enc.toString());
        
        return resp;
    }

    /**
     * Send an API request and return the results in an array.
     */
    public static String[] send(String verb, String path) {//, $params=array(), $headers=array()) {
    	try {
    		// get the signature
    		LinkedHashMap<String, String> sign = (LinkedHashMap<String, String>) SnapApi.sign(verb, path);
    		String fullPath = SnapApi.cleanPath(path);
    		
    		// setup the connection
    		URL requestURL = new URL(SnapApi.api_host + fullPath);
    		HttpURLConnection conn = null;
			if (requestURL.getProtocol().equals("http")) {
				conn = (HttpURLConnection) requestURL.openConnection();
			} else if (requestURL.getProtocol().equals("https")) {
				conn = (HttpsURLConnection) requestURL.openConnection();
			}
			
    		// setup the request
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("x-SNAP-nonce", sign.get("x_snap_nonce"));
			conn.setRequestProperty("x-SNAP-Date", sign.get("x_snap_date"));
			conn.setRequestProperty("Authorization", "SNAP "+SnapApi.api_key+":"+sign.get("signature"));
			
			// connect to the URL
			conn.connect();
			
			// setup the input/output streams
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			
			// get the data
			String output;
			StringBuilder response = new StringBuilder();
			while ((output = br.readLine()) != null) {
				response.append(output);
			}
			
			// build response
			String[] resp = new String[2];
			resp[0] = Integer.toString(conn.getResponseCode());
			resp[1] = response.toString();
			
			// close the connection
			conn.disconnect();
			
			// return
			return resp;
    	} catch (MalformedURLException e) {
			Log.e(TAG, "MalformedURLException", e);
		} catch (IOException e) {
			Log.e(TAG, "IOException", e);
		} catch (Exception e) {
			Log.e(TAG, "Exception", e);
		}
		return null;
    }
    
    private static String cleanPath(String path) {
    	path = (path.substring(0, 1).equals("/")) ? path.substring(1, path.length()-1):path; // remove the leading '/' if it exists
    	path = (path.substring(path.length()-1, path.length()).equals("/")) ? path.substring(0, path.length()-1):path; // remove the trailling '/' if it exists
        path = "/" + SnapApi.api_version + "/" + path + "/"; // build the path with the API version
        return path;
    }

}
