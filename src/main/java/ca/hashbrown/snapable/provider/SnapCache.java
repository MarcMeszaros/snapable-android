package ca.hashbrown.snapable.provider;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import com.snapable.utils.SnapImage;
import com.snapable.api.private_v1.resources.EventResource;
import com.snapable.api.private_v1.resources.PhotoResource;

import ca.hashbrown.snapable.BuildConfig;
import ca.hashbrown.snapable.Snapable;
import ca.hashbrown.snapable.api.SnapClient;
import retrofit.RetrofitError;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * A class to help manage all the caching stuff.
 */
public class SnapCache {

	private static final String TAG = "SnapCache";

	/**
	 * A custom version of BitmapDrawable that wraps it with an AsyncTask to
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
        protected LruCache<String, Bitmap> mCache = null;
        protected DiskLruImageCache dCache = null;

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

        // helpers
        public Bitmap getPhoto(long id, String size) {
            final String imageKey = String.valueOf(id) + "_" + size;
            return getBitmapFromCache(imageKey);
        }

        public void addBitmapToCache(String key, Bitmap bitmap) {
            if (getBitmapFromCache(key) == null && bitmap != null) {
                if (mCache != null) { mCache.put(key, bitmap); }
                if (dCache != null) { dCache.putBitmap(key, bitmap); }
            }
        }

        @Deprecated
        public Bitmap getBitmapFromCacheMemory(String key) {
            return getBitmapFromCache(key, true);
        }

        public Bitmap getBitmapFromCache(String key) {
            return getBitmapFromCache(key, false);
        }

        public Bitmap getBitmapFromCache(String key, boolean hitMemoryOnly) {
            // make sure the caches exist
            if (mCache == null && dCache == null) {
                return null;
            }

            // try to get the bitmap from memory first
            Bitmap result = null;
            if (mCache != null) {
                result = mCache.get(key); // try and get from memory
            }

            // if we aren't only checking memory, try to get it from disk
            if (!hitMemoryOnly && result == null && dCache != null) {
                result = dCache.getBitmap(key); // try and get from disk
                if (result != null) {
                    mCache.put(key, result); // add to memory if found on disk
                }
            }

            return result;
        }
	}

	/**
	 * The event photo caching and loading class.
	 */
    public static class EventWorkerTask extends BitmapWorkerTask {

        // class specific static cached
        private static LruCache<String, Bitmap> memoryCache = null;
        private static DiskLruImageCache diskCache = null;
        private static final int MCACHE_SIZE = 2 * 1024 * 1024; // 2MB
        private static final int DCACHE_SIZE = 4 * 1024 * 1024; // 4MB

        @TargetApi(Build.VERSION_CODES.KITKAT)
        public static LruCache<String, Bitmap> getMemoryCache() {
            if (memoryCache == null) {
                memoryCache = new LruCache<String, Bitmap>(MCACHE_SIZE) {
                    @Override
                    protected int sizeOf(String key, Bitmap value) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            return value.getAllocationByteCount();
                        } else {
                            return value.getByteCount();
                        }
                    }
                };
            }
            return memoryCache;
        }

        public static DiskLruImageCache getDiskCache() {
            if (diskCache == null) {
                diskCache = new DiskLruImageCache(new File(Snapable.FILE_CACHE_DIR, "cache_event"), BuildConfig.VERSION_CODE, 1, (DCACHE_SIZE));
            }
            return diskCache;
        }

		public EventWorkerTask(ImageView imageView) {
			super(imageView);
            mCache = getMemoryCache();
            dCache = getDiskCache();
		}

		@Override
		protected Bitmap doInBackground(Long... params) {
			this.data = params[0];
			final String imageKey = params[0] + "_150x150";
			Bitmap bm = getBitmapFromCache(imageKey);
			if (bm != null) {
				return bm;
			} else{
				try {
                    SnapImage img = SnapClient.getResource(EventResource.class).getEventPhotoBinary(params[0], "150x150");
                    final byte[] bytes = img.getBytes();
                    bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    addBitmapToCache(params[0] + "_150x150", bm);
					return bm;
				} catch (RetrofitError e) {
                    Log.e(TAG, "problem getting the photo from the API", e);
                    return null;
				}
			}
		}

	}

	/**
	 * The photo caching and loading class.
	 */
	public static class PhotoWorkerTask extends BitmapWorkerTask {

        // class specific static cached
        private static LruCache<String, Bitmap> memoryCache = null;
        private static DiskLruImageCache diskCache = null;
        private static final int MCACHE_SIZE = 4 * 1024 * 1024; // 4MB
        private static final int DCACHE_SIZE = 32 * 1024 * 1024; // 32MB

        @TargetApi(Build.VERSION_CODES.KITKAT)
        public static LruCache<String, Bitmap> getMemoryCache() {
            if (memoryCache == null) {
                memoryCache = new LruCache<String, Bitmap>(MCACHE_SIZE) {
                    @Override
                    protected int sizeOf(String key, Bitmap value) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            return value.getAllocationByteCount();
                        } else {
                            return value.getByteCount();
                        }
                    }
                };
            }
            return memoryCache;
        }

        public static DiskLruImageCache getDiskCache() {
            if (diskCache == null) {
                diskCache = new DiskLruImageCache(new File(Snapable.FILE_CACHE_DIR, "cache_photo"), BuildConfig.VERSION_CODE, 1, (DCACHE_SIZE));
            }
            return diskCache;
        }

        public PhotoWorkerTask(ImageView imageView) {
			super(imageView);
            mCache = getMemoryCache();
            dCache = getDiskCache();
        }

		@Override
		protected Bitmap doInBackground(Long... params) {
			this.data = params[0];
			final String imageKey = params[0] + "_480x480";
			Bitmap bm = getBitmapFromCache(imageKey);
			if (bm != null) {
				return bm;
			} else{
				try {
                    SnapImage img = SnapClient.getResource(PhotoResource.class).getPhotoBinary(params[0], "480x480");
                    final byte[] bytes = img.getBytes();
					bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    addBitmapToCache(params[0] + "_480x480", bm);
					return bm;
				} catch (RetrofitError e) {
					Log.e(TAG, "problem getting the photo from the API", e);
                    return null;
				}
			}
		}

	}

}
