package ca.hashbrown.snapable.adapters;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import ca.hashbrown.snapable.R;
import ca.hashbrown.snapable.provider.SnapCache;
import ca.hashbrown.snapable.provider.SnapableContract;

import com.snapable.api.SnapClient;
import com.snapable.api.models.Event;
import com.snapable.api.resources.EventResource;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri.Builder;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class EventListAdapter extends CursorAdapter {

	private static final String TAG = "EventListAdapter";
	ArrayList<Bitmap> imagesList;
	
	public EventListAdapter(Context context, Cursor c) {
		super(context, c);
		this.imagesList = new ArrayList<Bitmap>();
	}
	
	static class ViewHolder {
        protected TextView title;
        protected ImageView cover;
    }
	
	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder viewHolder = (ViewHolder) view.getTag();

		// set the title
		viewHolder.title.setText(cursor.getString(cursor.getColumnIndex(Event.FIELD_TITLE)));

		// get the image, if there is one
		viewHolder.cover.setImageResource(R.drawable.photo_blank);
		LoadCoverTask task = new LoadCoverTask(viewHolder.cover);
		task.execute(cursor.getLong(cursor.getColumnIndex(Event.FIELD_ID)));
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.listview_row_event, parent, false);
		
		// bind the various views to the viewholder
		final ViewHolder viewHolder = new ViewHolder();
		viewHolder.title = (TextView) v.findViewById(R.id.EventRow_event_title);
		viewHolder.cover = (ImageView) v.findViewById(R.id.EventRow_event_cover);
		v.setTag(viewHolder);
		
		bindView(v, context, cursor);
		return v;
	}
	
	private class LoadCoverTask extends AsyncTask<Long, Void, Bitmap> {
		
		private ImageView coverView;
		
		public LoadCoverTask(ImageView coverView) {
			this.coverView = coverView;
		}

		@Override
		protected Bitmap doInBackground(Long... params) {
			try{
				return SnapCache.Event.getPhoto(params[0], "150x150");
			} catch (Exception e) {
				return null;
			}
		}
		
		@Override
		protected void onPostExecute(Bitmap result) {
			this.coverView.setImageBitmap(result);
		}
		
	}
}
