package com.snapable.api;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.codegist.common.io.IOs;
import org.codegist.crest.serializer.Serializer;

import android.graphics.Bitmap;
import android.util.Log;

public class SnapBitmapSerializer implements Serializer<Bitmap> {

	private static final String TAG = "SnapBitmapSerializer";

	public void serialize(Bitmap value, Charset charset, OutputStream out) throws Exception {
		Log.d(TAG, value.getWidth() + "x" + value.getHeight());
		byte[] imageData = null;
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        value.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        imageData = baos.toByteArray();
        
        Log.d(TAG, "length: "+imageData.length);
        
        out.write(imageData);
	}

}
