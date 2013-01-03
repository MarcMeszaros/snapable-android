package ca.hashbrown.snapable.adapters;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import ca.hashbrown.snapable.R;
import ca.hashbrown.snapable.provider.SnapCache;

import com.snapable.api.SnapClient;
import com.snapable.api.models.Photo;
import com.snapable.api.resources.PhotoResource;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PhotoListAdapter extends CursorAdapter {

	private static final String TAG = "PhotoListAdapter";
	ArrayList<Bitmap> imagesList;
	private Context context;
	private final Bitmap placeholder;
	
	public PhotoListAdapter(Context context, Cursor c) {
		super(context, c);
		this.context = context;
		this.placeholder = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.photo_blank);
	}
	
	static class ViewHolder {
        protected ImageView photo;
        protected TextView caption;
        protected TextView authorName;
    }
	
	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder viewHolder = (ViewHolder) view.getTag();
		
		// set the text
		viewHolder.caption.setText(cursor.getString(cursor.getColumnIndex(Photo.FIELD_CAPTION)));
		String authorName = cursor.getString(cursor.getColumnIndex(Photo.FIELD_AUTHOR_NAME));
		viewHolder.authorName.setText((authorName.isEmpty()) ? "Anonymous" : authorName);
		
		// get the image, if there is one
		final String imageKey = cursor.getLong(cursor.getColumnIndex(Photo.FIELD_ID)) + "_480x480";
		Bitmap bm = SnapCache.Photo.getBitmapFromCache(imageKey);
		if (bm != null) {
			viewHolder.photo.setImageBitmap(bm);
		} else if (cancelPotentialWork(cursor.getLong(cursor.getColumnIndex(Photo.FIELD_ID)), viewHolder.photo)) {
            final LoadPhotoTask task = new LoadPhotoTask(viewHolder.photo);
            final AsyncDrawable asyncDrawable = new AsyncDrawable(this.context.getResources(), this.placeholder, task);
            viewHolder.photo.setImageDrawable(asyncDrawable);
            task.execute(cursor.getLong(cursor.getColumnIndex(Photo.FIELD_ID)));
        }
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.listview_row_eventphoto, parent, false);
		
		// bind the various views to the viewholder
		final ViewHolder viewHolder = new ViewHolder();
		viewHolder.photo = (ImageView) v.findViewById(R.id.listview_row_eventphoto_photo);
		viewHolder.caption = (TextView) v.findViewById(R.id.listview_row_eventphoto_caption);
		viewHolder.authorName = (TextView) v.findViewById(R.id.listview_row_eventphoto_author_name);
		v.setTag(viewHolder);
		
		bindView(v, context, cursor);
		return v;
	}
	
	private class LoadPhotoTask extends AsyncTask<Long, Void, Bitmap> {
		
		private final WeakReference<ImageView> photo;
		private long data = 0;
		
		public LoadPhotoTask(ImageView photo) {
			this.photo = new WeakReference<ImageView>(photo);
		}

		@Override
		protected Bitmap doInBackground(Long... params) {
			this.data = params[0];
			final String imageKey = params[0] + "_480x480";
			Bitmap bm = SnapCache.Photo.getBitmapFromCache(imageKey);
			if (bm != null) {
				return bm;
			} else{ 
				bm = SnapClient.getInstance().build(PhotoResource.class).getPhotoBinary(params[0], "480x480");
				SnapCache.Photo.addBitmapToCache(params[0] + "_480x480", bm);
				return bm;
			}
		}
		
		@Override
		protected void onPostExecute(Bitmap result) {
			if (isCancelled()) {
				result = null;
			}
			
			if (this.photo != null && result != null) {
	            final ImageView imageView = this.photo.get();
	            final LoadPhotoTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
	            if (this == bitmapWorkerTask && this.photo != null) {
	                imageView.setImageBitmap(result);
	            }
	        }
		}
		
	}
	////////
	static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<LoadPhotoTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap, LoadPhotoTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference = new WeakReference<LoadPhotoTask>(bitmapWorkerTask);
        }

        public LoadPhotoTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }
	
	public static boolean cancelPotentialWork(long data, ImageView imageView) {
        final LoadPhotoTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

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
	
	private static LoadPhotoTask getBitmapWorkerTask(ImageView imageView) {
       if (imageView != null) {
           final Drawable drawable = imageView.getDrawable();
           if (drawable instanceof AsyncDrawable) {
               final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
               return asyncDrawable.getBitmapWorkerTask();
           }
        }
        return null;
    }

}
