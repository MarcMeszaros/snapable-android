package ca.hashbrown.snapable.provider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.snapable.api.SnapClient;
import com.snapable.api.resources.EventResource;
import com.snapable.api.resources.PhotoResource;

import ca.hashbrown.snapable.R;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.util.Log;

/**
 * A class to help manage all the caching stuff.
 */
public class SnapCache {
	
	private static final String TAG = "SnapCache";

	/**
	 * The event caching class.
	 */
	public static class Event {

		// 4MB of bitmap cache
		private static LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(4 * 1024 * 1024) {
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getRowBytes() * value.getHeight();
			}
		};

		public static Bitmap getPhoto(long id) {
			return getPhoto(id, "150x150");
		}

		public static Bitmap getPhoto(long id, String size) {
			Log.i(TAG, "inside event getPhoto");
			final String imageKey = String.valueOf(id) + "_" + size;
			Bitmap bitmap = getBitmapFromCache(imageKey);
			if (bitmap != null) {
			    return bitmap;
			} else {
				bitmap = SnapClient.getInstance().build(EventResource.class).getEventPhotoBinary(id, size);
			    addBitmapToCache(imageKey, bitmap);
				return bitmap;
			}
		}
		
		public static void addBitmapToCache(String key, Bitmap bitmap) {
			if (getBitmapFromCache(key) == null) {
				mCache.put(key, bitmap);
			}
		}

		public static Bitmap getBitmapFromCache(String key) {
			return mCache.get(key);
		}

	}
	
	/**
	 * The photo caching class.
	 */
	public static class Photo {

		// 4MB of bitmap cache
		private static LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(4 * 1024 * 1024) {
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getRowBytes() * value.getHeight();
			}
		};

		public static Bitmap getPhoto(long id) {
			return getPhoto(id, "150x150");
		}

		public static Bitmap getPhoto(long id, String size) {
			Log.i(TAG, "inside photo getPhoto");
			final String imageKey = String.valueOf(id) + "_" + size;
			return getBitmapFromCache(imageKey);
			/*
			if (bitmap != null) {
			    return bitmap;
			} else {
				bitmap = SnapClient.getInstance().build(PhotoResource.class).getPhotoBinary(id, size);
			    addBitmapToCache(imageKey, bitmap);
				return bitmap;
			}
			*/
		}
		
		public static void addBitmapToCache(String key, Bitmap bitmap) {
			if (getBitmapFromCache(key) == null) {
				mCache.put(key, bitmap);
			}
		}

		public static Bitmap getBitmapFromCache(String key) {
			return mCache.get(key);
		}

	}

}
