package ca.hashbrown.snapable.api;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;

/**
 * Helper methods used to generate the various parts of the authentication
 * header required when calling the Snapable API.
 *
 * @author Marc Meszaros (marc@snapable.com)
 */
public class SnapApi {

	// API information
	public static final String api_version = "private_v1";
	private static String api_key = "key123"; // default: key123
	private static String api_secret = "sec123"; // default: sec123

	// information used to generate signature
	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
	private static final SecureRandom rand = new SecureRandom();

	/**
	 * Generate a random nonce string.
	 *
	 * @param length The length the generated nonce should be.
	 * @return a string containing a random nonce
	 */
	public static String getNonce(int length) {
		// generate random bytes
		length = (length < 16) ? 16 : length;
		byte[] randBytes = new byte[length / 2];
		rand.nextBytes(randBytes);

		// convert the byte array to a string
		StringBuilder enc = new StringBuilder();
		for (byte b : randBytes) {
			enc.append(String.format("%02x", b));
		}
		return enc.toString();
	}

	/**
	 * Generate a date timestamp to use for API key signing.
	 *
	 * @return a string containing the current datetime
	 */
	public static String getDate() {
        return String.valueOf(new Date().getTime()/1000);
	}

	/**
	 * Create a HMAC signature for an API request and return an array with all
	 * the required parts required to build the API call.
	 *
	 * @param verb The HTTP verb for the request.
	 * @param path The HTTP path of the request.
	 * @return a HashMap containing the signature parts
	 * @see SnapApi#sign(String, String, String, String)
	 */
	public static HashMap<String, String> sign(String verb, String path) {
		return sign(verb, path, null, null);
	}

	/**
	 * Create a HMAC signature for an API request and return an array with all
	 * the required parts required to build the API call.
	 *
	 * @param verb The HTTP verb for the request.
	 * @param path The HTTP path of the request.
	 * @param nonce The nonce that should be used in the request.
	 * @param timestamp The timestamp string that should be used in the request.
	 * @return a HashMap containing the signature parts
	 */
	public static HashMap<String, String> sign(String verb, String path, String nonce, String timestamp) {
		// build the string to sign
		String snap_nonce = (nonce != null) ? nonce : SnapApi.getNonce(16);
		String snap_timestamp = (timestamp != null) ? timestamp : SnapApi.getDate();
		StringBuilder enc = new StringBuilder();
		String raw_signature = SnapApi.api_key + verb.toUpperCase() + path + snap_nonce + snap_timestamp;
		try {
			// generate the HMAC signature
			Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
			SecretKeySpec secret = new SecretKeySpec(
					SnapApi.api_secret.getBytes(), HMAC_SHA1_ALGORITHM);
			mac.init(secret);
			byte[] digest = mac.doFinal(raw_signature.getBytes());

			// build the signature
			for (byte b : digest) {
				enc.append(String.format("%02x", b));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// build the response hashmap
		HashMap<String, String> resp = new HashMap<String, String>(3);
		resp.put("nonce", snap_nonce);
		resp.put("timestamp", snap_timestamp);
		resp.put("api_key", SnapApi.api_key);
		resp.put("signature", enc.toString());

		return resp;
	}

	/**
	 * Helper method to set the API key and secret.
	 *
	 * @param key the API key
	 * @param secret the API secret
	 */
	public static void setApiKeySecret(String key, String secret) {
		SnapApi.api_key = key;
		SnapApi.api_secret = secret;
	}

}
