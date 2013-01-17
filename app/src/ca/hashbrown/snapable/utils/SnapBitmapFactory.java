package ca.hashbrown.snapable.utils;

import java.io.IOException;
import java.util.Formatter.BigDecimalLayoutForm;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

public class SnapBitmapFactory extends BitmapFactory {

	private static final String TAG = "SnapBitmapFactory";
	
	public static Bitmap decodeFileRotate(String pathName) {
		return SnapBitmapFactory.decodeFileRotate(pathName, null);
	}

	public static Bitmap decodeFileRotate(String pathName, Options opts) {

		try {
			ExifInterface exif = new ExifInterface(pathName);
			int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
			int rotation = 0;
			
			switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					rotation = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					rotation = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					rotation = 270;
					break;
			}
			
			Bitmap bm = BitmapFactory.decodeFile(pathName, opts);
			if (rotation != 0) {
				bm = bm.copy(Bitmap.Config.RGB_565, false);
				Matrix matrix = new Matrix();
				matrix.setRotate(rotation, bm.getWidth()/2, bm.getHeight()/2);
				bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
			}
			return bm;
		} catch (IOException e) {
			Log.e(TAG, "there was an IO error", e);
			return null;
		} catch (OutOfMemoryError e) {
			Log.e(TAG, "we ran out of memory", e);
			return null;
		}
	}

}
