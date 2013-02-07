package com.snapable.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.codegist.crest.serializer.Serializer;

import android.graphics.Bitmap;
import android.util.Log;

public class SnapBitmapSerializer implements Serializer<Bitmap> {

	private static final String TAG = "SnapBitmapSerializer";

	public void serialize(Bitmap value, Charset charset, OutputStream out) throws IOException {
		Log.d(TAG, value.getWidth() + "x" + value.getHeight());
		ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        value.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        out.write(baos.toByteArray());
	}

}
