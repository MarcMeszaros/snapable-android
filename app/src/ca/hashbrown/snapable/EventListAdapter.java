package ca.hashbrown.snapable;

import com.snapable.api.SnapClient;
import com.snapable.api.resources.EventResource;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class EventListAdapter extends CursorAdapter {

	private static final String TAG = "EventListAdapter";
	Context context;
	
	public EventListAdapter(Context context, Cursor c) {
		super(context, c);
		this.context = context;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ImageView cover = (ImageView) view.findViewById(R.id.EventRow_event_cover);
		TextView title = (TextView) view.findViewById(R.id.EventRow_event_title);

		// set the title
		title.setText(cursor.getString(cursor.getColumnIndex("title")));
		
		// get the image, if there is one
		if (cursor.getLong(cursor.getColumnIndex("photo_count")) > 0) {
			LoadCoverTask task = new LoadCoverTask(cover);
			task.execute(cursor.getLong(cursor.getColumnIndex("_id")));
		} else {
			cover.setImageResource(R.drawable.photo_blank);
		}
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.view_event_row, parent, false);
		bindView(v, context, cursor);
		return v;
	}
	
	private class LoadCoverTask extends AsyncTask<Long, Void, Bitmap> {
		
		private ImageView cover;
		
		public LoadCoverTask(ImageView cover) {
			this.cover = cover;
		}

		@Override
		protected Bitmap doInBackground(Long... params) {
			try{
				return SnapClient.getInstance().build(EventResource.class).getEventPhotoBinary(params[0], "150x150");
			} catch (Exception e) {
				return null;
			}
		}
		
		@Override
		protected void onPostExecute(Bitmap result) {
			if (this.cover != null) {
				this.cover.setImageBitmap(result);
			}
		}
		
	}
}
