package ca.hashbrown.snapable.adapters;

import java.util.ArrayList;

import ca.hashbrown.snapable.R;

import com.snapable.api.SnapClient;
import com.snapable.api.models.Photo;
import com.snapable.api.resources.PhotoResource;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
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
	
	public PhotoListAdapter(Context context, Cursor c) {
		super(context, c);
		this.imagesList = new ArrayList<Bitmap>();
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
		if (this.imagesList.size()-1 >= cursor.getPosition()) {
			Bitmap photo = (Bitmap) this.imagesList.get(cursor.getPosition());
			if (photo != null) {
				viewHolder.photo.setImageBitmap(photo);
			} else {
				viewHolder.photo.setImageResource(R.drawable.photo_blank);
			}
			
		} else {
			viewHolder.photo.setImageResource(R.drawable.photo_blank);
			LoadPhotoTask task = new LoadPhotoTask(this, this.imagesList, cursor.getPosition());
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
		
		private PhotoListAdapter adapter;
		private ArrayList<Bitmap> imagesList;
		private int position;
		
		public LoadPhotoTask(PhotoListAdapter adapter, ArrayList<Bitmap> imagesList, int position) {
			this.adapter = adapter;
			this.imagesList = imagesList;
			this.position = position;
		}

		@Override
		protected Bitmap doInBackground(Long... params) {
			try{
				return SnapClient.getInstance().build(PhotoResource.class).getPhotoBinary(params[0], "480x480");
			} catch (Exception e) {
				return null;
			}
		}
		
		@Override
		protected void onPostExecute(Bitmap result) {
			if (this.imagesList.size()-1 <= this.position) {
				this.imagesList.ensureCapacity(this.position + 1);
				this.imagesList.add(null);
			}
			this.imagesList.set(this.position, result);
			this.adapter.notifyDataSetChanged();
		}
		
	}

}
