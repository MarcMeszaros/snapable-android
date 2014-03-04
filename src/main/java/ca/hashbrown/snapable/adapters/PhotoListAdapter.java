package ca.hashbrown.snapable.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import ca.hashbrown.snapable.R;
import ca.hashbrown.snapable.provider.SnapCache;
import ca.hashbrown.snapable.provider.SnapCache.AsyncDrawable;
import ca.hashbrown.snapable.provider.SnapCache.PhotoWorkerTask;
import ca.hashbrown.snapable.api.models.Photo;

public class PhotoListAdapter extends CursorAdapter {

	private static final String TAG = "PhotoListAdapter";
	private final Bitmap placeholder;

	public PhotoListAdapter(Context context, Cursor c) {
		super(context, c);
		this.placeholder = BitmapFactory.decodeResource(context.getResources(), R.drawable.photo_blank);
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
        String caption = cursor.getString(cursor.getColumnIndex(Photo.FIELD_CAPTION));
        if (caption.isEmpty()) {
            viewHolder.caption.setVisibility(View.GONE);
        } else {
            viewHolder.caption.setVisibility(View.VISIBLE);
		    viewHolder.caption.setText(caption);
        }
		String authorName = cursor.getString(cursor.getColumnIndex(Photo.FIELD_AUTHOR_NAME));
		viewHolder.authorName.setText((authorName.isEmpty()) ? "Anonymous" : authorName);

		// get the image, if there is one
		final String imageKey = cursor.getLong(cursor.getColumnIndex(Photo.FIELD_ID)) + "_480x480";
		Bitmap bm = new PhotoWorkerTask(null).getBitmapFromCacheMemory(imageKey);
		if (bm != null) {
			viewHolder.photo.setImageBitmap(bm);
		} else if (SnapCache.PhotoWorkerTask.cancelPotentialWork(cursor.getLong(cursor.getColumnIndex(Photo.FIELD_ID)), viewHolder.photo)) {
            final PhotoWorkerTask task = new PhotoWorkerTask(viewHolder.photo);
            final AsyncDrawable asyncDrawable = new AsyncDrawable(context.getResources(), this.placeholder, task);
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

}
