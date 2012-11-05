package ca.hashbrown.snapable;

import java.io.IOException;
import java.io.InputStream;

import com.snapable.api.SnapClient;
import com.snapable.api.resources.EventResource;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class EventListAdapter extends CursorAdapter {

	private static final String TAG = "EventListAdapter";

	public EventListAdapter(Context context, Cursor c) {
		super(context, c);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ImageView cover = (ImageView) view.findViewById(R.id.EventRow_event_cover);
		TextView title = (TextView) view.findViewById(R.id.EventRow_event_title);
		
		// set the title
		title.setText(cursor.getString(cursor.getColumnIndex("title")));
		try {
			InputStream photo = new SnapClient().build(EventResource.class).getEventPhotoBinary(cursor.getLong(cursor.getColumnIndex("_id")), "150x150");
	 		if (photo != null) {
					Log.i("SnapActivity", Integer.toString(photo.available()));
					Bitmap bm = BitmapFactory.decodeStream(photo);
					cover.setImageBitmap(bm);	
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//cover.setImageURI(Uri.parse(TNManager.getThumbnail(isbn10).getAbsolutePath()));
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.view_event_row, parent, false);
		bindView(v, context, cursor);
		return v;
	}
}
