package com.snapable.api;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import org.codegist.crest.serializer.Deserializer;

public class SnapDeserializer implements Deserializer {

	@SuppressWarnings("unchecked")
	public <T> T deserialize(Class<T> type, Type genericType,
			InputStream stream, Charset charset) throws Exception {
		
		// handle if an Android Bitmap should be returned
		if (type.getName().equals("android.graphics.Bitmap")) {
			return (T) android.graphics.BitmapFactory.decodeStream(stream);			
		}
		
		return null;
	}

}
