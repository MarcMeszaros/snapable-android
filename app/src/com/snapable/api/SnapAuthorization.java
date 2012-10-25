package com.snapable.api;

import java.net.URL;
import java.util.HashMap;

import org.codegist.crest.config.MethodType;
import org.codegist.crest.param.EncodedPair;
import org.codegist.crest.security.*;

public class SnapAuthorization implements Authorization {

	public AuthorizationToken authorize(MethodType methodType, String url,
			EncodedPair... parameters) throws Exception {
		URL newUrl = new URL(url);
		// Authorization: SNAP snap_key="abc123",snap_signature="sadasd",snap_nonce="abc2",snap_date="20121001T140823Z"
		HashMap<String, String> vals = SnapApi.sign(methodType.name(), newUrl.getPath());
		String authString = "snap_key=\""+(String)vals.get("api_key")+"\"" +
				",snap_signature=\""+(String)vals.get("signature")+"\"" +
				",snap_nonce=\""+(String)vals.get("x_snap_nonce")+"\"" +
				",snap_date=\""+(String)vals.get("x_snap_date")+"\"";
		AuthorizationToken token = new AuthorizationToken("SNAP", authString);
		return token;
	}

	public void refresh() throws Exception {
		// nothing to refresh
	}

}
