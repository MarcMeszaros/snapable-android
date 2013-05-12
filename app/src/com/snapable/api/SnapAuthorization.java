package com.snapable.api;

import org.codegist.crest.config.MethodType;
import org.codegist.crest.param.EncodedPair;
import org.codegist.crest.security.Authorization;
import org.codegist.crest.security.AuthorizationToken;

import java.net.URL;
import java.util.HashMap;

public class SnapAuthorization implements Authorization {

	public AuthorizationToken authorize(MethodType methodType, String url,
			EncodedPair... parameters) throws Exception {
		URL newUrl = new URL(url);
		// Authorization: SNAP key="abc123",signature="sadasd",nonce="abc2",timestamp="1234567890"
		HashMap<String, String> vals = SnapApi.sign(methodType.name(), newUrl.getPath());
		String authString = "key=\""+(String)vals.get("api_key")+"\"" +
				",signature=\""+(String)vals.get("signature")+"\"" +
				",nonce=\""+(String)vals.get("nonce")+"\"" +
				",timestamp=\""+(String)vals.get("timestamp")+"\"";
		AuthorizationToken token = new AuthorizationToken("SNAP", authString);
		return token;
	}

	public void refresh() throws Exception {
		// nothing to refresh
	}

}
