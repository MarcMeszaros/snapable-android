package com.snapable.api;

import java.io.OutputStream;
import java.nio.charset.Charset;

import org.codegist.crest.serializer.Serializer;

import android.graphics.Bitmap;

public class SnapBitmapSerializer implements Serializer<Bitmap> {

	public void serialize(Bitmap value, Charset charset, OutputStream out) throws Exception {
		value.compress(Bitmap.CompressFormat.JPEG, 50, out);	
	}

}
