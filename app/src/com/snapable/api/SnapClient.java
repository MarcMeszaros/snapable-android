package com.snapable.api;

import org.codegist.crest.CRest;
import org.codegist.crest.CRestBuilder;
import org.codegist.crest.CRestException;
import org.codegist.crest.security.Authorization;

import ca.hashbrown.snapable.BuildConfig;

import android.graphics.Bitmap;

public class SnapClient {
	
	private static volatile SnapClient instance = null;
	
	// private class variable
	private CRest crest;

	/**
	 * Create the Snapable API client which should be used to instantiate resources.
	 */
	private SnapClient() {
		CRestBuilder builder = new CRestBuilder(); // get a CRestBuilder
		Authorization auth = new SnapAuthorization(); // get our custom auth class
		builder.property(Authorization.class.getName(), auth); // set the auth class to the builder
		builder.bindDeserializer(SnapDeserializer.class, "image/jpeg"); // tell CRest to use our custom image deserializer
		builder.bindSerializer(SnapBitmapSerializer.class, Bitmap.class); // tell CRest how to serialize Bitmap

		// set the endpoint based on build flag
		if (BuildConfig.DEBUG) {
			builder.endpoint("http://devapi.snapable.com");
		} else {
			builder.endpoint("https://api.snapable.com");
		}

        this.crest = builder.build(); // return the CRest object
	}

	public <T> T build(Class<T> interfaze) throws CRestException {
		return this.crest.build(interfaze);
	}
	
	/**
	 * Gets an instance of a Snapable API CRest based client.
	 * @return A handle on the API client.
	 */
	public static SnapClient getInstance() {
		if (instance == null) {
			synchronized (SnapClient.class) {
				if (instance == null) {
					instance = new SnapClient();
				}
			}
		}
		return instance;
	}

}
