package com.snapable.api;

import org.codegist.crest.CRest;
import org.codegist.crest.CRestBuilder;
import org.codegist.crest.CRestException;
import org.codegist.crest.security.Authorization;

public class SnapClient {

	// private class variable
	private CRest crest;

	/**
	 * Create the Snapable API client which should be used to instantiate resources.
	 */
	public SnapClient() {
		CRestBuilder builder = new CRestBuilder(); // get a CRestBuilder
		Authorization auth = new SnapAuthorization(); // get our custom auth class
		builder.property(Authorization.class.getName(), auth); // set the auth class to the builder
		builder.bindDeserializer(SnapDeserializer.class, "image/jpeg"); // tell CRest to use our custom image deserializer

        this.crest = builder.build(); // return the CRest object
	}

	public <T> T build(Class<T> interfaze) throws CRestException {
		return this.crest.build(interfaze);
	}

}
