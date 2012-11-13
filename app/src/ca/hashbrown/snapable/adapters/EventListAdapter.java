package ca.hashbrown.snapable.adapters;

import java.util.ArrayList;

import ca.hashbrown.snapable.R;

import com.snapable.api.SnapClient;
import com.snapable.api.models.Event;
import com.snapable.api.resources.EventResource;

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
		if (this.imagesList.size()-1 >= cursor.getPosition()) {
			Bitmap cover = (Bitmap) this.imagesList.get(cursor.getPosition());
			if (cover != null) {
				viewHolder.cover.setImageBitmap(cover);
			} else {
				viewHolder.cover.setImageResource(R.drawable.photo_blank);
			}
			
		} else {
			viewHolder.cover.setImageResource(R.drawable.photo_blank);
			LoadCoverTask task = new LoadCoverTask(this, this.imagesList, cursor.getPosition());
			task.execute(cursor.getLong(cursor.getColumnIndex(Event.FIELD_ID)));
		}
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.view_event_row, parent, false);
		
		// bind the various views to the viewholder
		final ViewHolder viewHolder = new ViewHolder();
		viewHolder.title = (TextView) v.findViewById(R.id.EventRow_event_title);
		viewHolder.cover = (ImageView) v.findViewById(R.id.EventRow_event_cover);
		v.setTag(viewHolder);
		
		bindView(v, context, cursor);
		return v;
	}
	
	private class LoadCoverTask extends AsyncTask<Long, Void, Bitmap> {
		
		private EventListAdapter adapter;
		private ArrayList<Bitmap> imagesList;
		private int position;
		
		public LoadCoverTask(EventListAdapter adapter, ArrayList<Bitmap> imagesList, int position) {
			this.adapter = adapter;
			this.imagesList = imagesList;
			this.position = position;
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
			if (this.imagesList.size()-1 <= this.position) {
				this.imagesList.ensureCapacity(this.position + 1);
				this.imagesList.add(null);
			}
			this.imagesList.set(this.position, result);
			this.adapter.notifyDataSetChanged();
		}
		
	}
}
