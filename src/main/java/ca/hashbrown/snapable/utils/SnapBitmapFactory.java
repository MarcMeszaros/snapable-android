package ca.hashbrown.snapable.utils;

import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

public class SnapBitmapFactory extends BitmapFactory {

	private static final String TAG = "SnapBitmapFactory";
	
	public static Bitmap decodeFileRotate(String pathName) {
		return decodeFileRotate(pathName, null);
	}

    /**
     * Decode a file on disk and rotate upright.
     *
     * @param pathName The path to image to decode.
     * @param opts The bitmap options to use when rotating.
     * @return a {@link android.graphics.Bitmap} object
     */
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

			Bitmap bm = decodeFile(pathName, opts);
			if (rotation != 0) {
                Matrix matrix = new Matrix();
                matrix.postRotate(rotation);
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

    /**
     * Decode a file from disk and return a sample downed version based on the required height
     * and width.
     *
     * @param path The path to image to decode.
     * @param reqWidth The target required width (in pixel).
     * @param reqHeight The target required height (in pixel).
     * @return a {@link android.graphics.Bitmap} object
     */
    public static Bitmap decodeSampledBitmapFromPath(String path, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final Options options = new Options();
        options.inJustDecodeBounds = true;
        decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return decodeFileRotate(path, options);
    }

    /**
     * Calculate the "inSampleSize" of an image based on the target width and height.
     *
     * @param options The bitmap options to use when calculating.
     * @param reqWidth The target required width (in pixel).
     * @param reqHeight The target required height (in pixel).
     * @return an integer for {@link android.graphics.BitmapFactory.Options#inSampleSize}
     */
    public static int calculateInSampleSize(Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

}
