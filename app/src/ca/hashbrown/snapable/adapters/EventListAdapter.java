package ca.hashbrown.snapable.adapters;

import java.util.ArrayList;

import ca.hashbrown.snapable.R;
import ca.hashbrown.snapable.provider.SnapCache;
import ca.hashbrown.snapable.provider.SnapCache.AsyncDrawable;
import ca.hashbrown.snapable.provider.SnapCache.EventWorkerTask;

import com.snapable.api.models.Event;

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

public class EventListAdapter extends CursorAdapter {

	private static final String TAG = "EventListAdapter";
	private final Bitmap placeholder;
	
	public EventListAdapter(Context context, Cursor c) {
		super(context, c);
		this.placeholder = BitmapFactory.decodeResource(context.getResources(), R.drawable.photo_blank);
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
		final String imageKey = cursor.getLong(cursor.getColumnIndex(Event.FIELD_ID)) + "_480x480";
		Bitmap bm = SnapCache.PhotoWorkerTask.getBitmapFromCache(imageKey);
		if (bm != null) {
			viewHolder.cover.setImageBitmap(bm);
		} else if (SnapCache.EventWorkerTask.cancelPotentialWork(cursor.getLong(cursor.getColumnIndex(Event.FIELD_ID)), viewHolder.cover)) {
            final EventWorkerTask task = new EventWorkerTask(viewHolder.cover);
            final AsyncDrawable asyncDrawable = new AsyncDrawable(context.getResources(), this.placeholder, task);
            viewHolder.cover.setImageDrawable(asyncDrawable);
            task.execute(cursor.getLong(cursor.getColumnIndex(Event.FIELD_ID)));
        }
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.listview_row_event, parent, false);
		
		// bind the various views to the viewholder
		final ViewHolder viewHolder = new ViewHolder();
		viewHolder.title = (TextView) v.findViewById(R.id.listview_row_event__title);
		viewHolder.cover = (ImageView) v.findViewById(R.id.listview_row_event__cover);
		v.setTag(viewHolder);
		
		bindView(v, context, cursor);
		return v;
	}
}
