package ca.hashbrown.snapable.provider;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;
import ca.hashbrown.snapable.Snapable;
import com.snapable.api.SnapClient;
import com.snapable.api.resources.EventResource;
import com.snapable.api.resources.PhotoResource;
import org.codegist.crest.CRestException;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * A class to help manage all the caching stuff.
 */
public class SnapCache {

	private static final String TAG = "SnapCache";

	/**
	 * A custom version of BitmapDrawable that wraps it with an AsuncTask to
	 * load in the data.
	 */
	public static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }

	/**
	 * Base AsyncTask used to load in images to an ImageView.
	 */
	public static abstract class BitmapWorkerTask extends AsyncTask<Long, Void, Bitmap> {

		protected long data = 0;
		protected final WeakReference<ImageView> imageView;

		public BitmapWorkerTask(ImageView imageView) {
			this.imageView = new WeakReference<ImageView>(imageView);
		}

		protected abstract Bitmap doInBackground(Long... params);

		@Override
		protected void onPostExecute(Bitmap result) {
			if (isCancelled()) {
				result = null;
			}

			if (this.imageView != null && result != null) {
	            final ImageView imageView = this.imageView.get();
	            final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
	            if (this == bitmapWorkerTask && this.imageView != null) {
                    // setup the drawables for the crossfade
                    BitmapDrawable bitmapDrawable = new BitmapDrawable(imageView.getResources(), result);
                    Drawable arrayDrawable[] = new Drawable[2];
                    arrayDrawable[0] = imageView.getDrawable();
                    arrayDrawable[1] = bitmapDrawable;
                    // setup the transition
                    TransitionDrawable transitionDrawable = new TransitionDrawable(arrayDrawable);
                    transitionDrawable.setCrossFadeEnabled(true);
                    imageView.setImageDrawable(transitionDrawable);
                    transitionDrawable.startTransition(150);
	            }
	        }
		}

		private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
	       if (imageView != null) {
	           final Drawable drawable = imageView.getDrawable();
	           if (drawable instanceof AsyncDrawable) {
	               final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
	               return asyncDrawable.getBitmapWorkerTask();
	           }
	        }
	        return null;
	    }

		public static boolean cancelPotentialWork(long data, ImageView imageView) {
	        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

	        if (bitmapWorkerTask != null) {
	            final long bitmapData = bitmapWorkerTask.data;
	            if (bitmapData != data) {
	                // Cancel previous task
	                bitmapWorkerTask.cancel(true);
	            } else {
	                // The same work is already in progress
	                return false;
	            }
	        }
	        // No task associated with the ImageView, or an existing task was cancelled
	        return true;
	    }
	}

	/**
	 * The event photo caching and loading class.
	 */
	public static class EventWorkerTask extends BitmapWorkerTask {

		// 2MB of memory bitmap cache
		private static LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(2 * 1024 * 1024) {
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getRowBytes() * value.getHeight();
			}
		};

		public EventWorkerTask(ImageView imageView) {
			super(imageView);
		}

		@Override
		protected Bitmap doInBackground(Long... params) {
			this.data = params[0];
			final String imageKey = params[0] + "_150x150";
			Bitmap bm = SnapCache.EventWorkerTask.getBitmapFromCache(imageKey);
			if (bm != null) {
				return bm;
			} else{
				try {
					bm = SnapClient.getInstance().build(EventResource.class).getEventPhotoBinary(params[0], "150x150");
					SnapCache.EventWorkerTask.addBitmapToCache(params[0] + "_150x150", bm);
					return bm;
				} catch (CRestException e) {
					Log.e(TAG, "problem getting the photo from the API", e);
					return null;
				}
			}
		}

		public static Bitmap getPhoto(long id) {
			return getPhoto(id, "150x150");
		}

		public static Bitmap getPhoto(long id, String size) {
			Log.i(TAG, "inside event getPhoto");
			final String imageKey = String.valueOf(id) + "_" + size;
			return getBitmapFromCache(imageKey);
		}

		public static void addBitmapToCache(String key, Bitmap bitmap) {
			if (getBitmapFromCache(key) == null && bitmap != null) {
				mCache.put(key, bitmap);
			}
		}

		public static Bitmap getBitmapFromCache(String key) {
            return mCache.get(key);
		}

	}

	/**
	 * The photo caching and loading class.
	 */
	public static class PhotoWorkerTask extends BitmapWorkerTask {

		// 4MB of bitmap cache
		private static LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(4 * 1024 * 1024) {
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getRowBytes() * value.getHeight();
			}
		};

        // 32MB disk cache
        private static DiskLruImageCache dCache = new DiskLruImageCache(new File(Snapable.getContext().getExternalCacheDir(), "cache_photo"), Snapable.getVersionCode(), 1, (32 * 1024 *1024));;

        public PhotoWorkerTask(ImageView imageView) {
			super(imageView);
		}

		@Override
		protected Bitmap doInBackground(Long... params) {
			this.data = params[0];
			final String imageKey = params[0] + "_480x480";
			Bitmap bm = SnapCache.PhotoWorkerTask.getBitmapFromCache(imageKey);
			if (bm != null) {
				return bm;
			} else{
				try {
					bm = SnapClient.getInstance().build(PhotoResource.class).getPhotoBinary(params[0], "480x480");
					SnapCache.PhotoWorkerTask.addBitmapToCache(params[0] + "_480x480", bm);
					return bm;
				} catch (CRestException e) {
					Log.e(TAG, "problem getting the photo from the API", e);
					return null;
				}
			}
		}

		public static Bitmap getPhoto(long id) {
			return getPhoto(id, "150x150");
		}

		public static Bitmap getPhoto(long id, String size) {
			Log.i(TAG, "inside photo getPhoto");
			final String imageKey = String.valueOf(id) + "_" + size;
			return getBitmapFromCache(imageKey);
		}

		public static void addBitmapToCache(String key, Bitmap bitmap) {
			if (getBitmapFromCache(key) == null && bitmap != null) {
				mCache.put(key, bitmap);
                dCache.putBitmap(key, bitmap);
			}
		}

        public static Bitmap getBitmapFromCacheMemory(String key) {
            return mCache.get(key); // try and get from memory
        }

		public static Bitmap getBitmapFromCache(String key) {
			Bitmap result = mCache.get(key); // try and get from memory
            if (result == null) {
                result = dCache.getBitmap(key); // try and get from disk
                if (result != null) {
                    mCache.put(key, result); // add to memory if found on disk
                }
            }
            return result;
		}

	}

}
