package com.snapable.api;

import java.io.OutputStream;
import java.util.List;

import org.codegist.crest.config.ParamType;
import org.codegist.crest.entity.EntityWriter;
import org.codegist.crest.io.Request;
import org.codegist.crest.param.Param;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonEntityWriter implements EntityWriter {

	@Override
	public void writeTo(Request request, OutputStream outputStream) throws Exception {
		outputStream.write(getJsonFromRequest(request).getBytes());
	}

	@Override
	public String getContentType(Request request) {
		return "application/json";
	}

	@Override
	public int getContentLength(Request request) {
		try {
			return getJsonFromRequest(request).length();
		} catch (JSONException e) {
			e.printStackTrace();
			return 0;
		}
	}

	private String getJsonFromRequest(Request request) throws JSONException {
		// get the parameters
		List<Param> params = request.getParams(ParamType.FORM);
		
		// build the json object
		JSONObject result = new JSONObject();
		for (Param param : params) {
			result.put(param.getParamConfig().getName(), param.getValue().toArray()[0]);
		}
		
		// return the actual json string
		return result.toString();
	}
	
	
}