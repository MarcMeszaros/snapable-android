package ca.hashbrown.snapable.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.snapable.api.private_v1.objects.Event;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ca.hashbrown.snapable.R;
import ca.hashbrown.snapable.provider.SnapCache;
import ca.hashbrown.snapable.provider.SnapCache.AsyncDrawable;
import ca.hashbrown.snapable.provider.SnapCache.EventWorkerTask;


import java.util.TimeZone;

public class EventListAdapter extends ArrayAdapter<Event> {

	private final Bitmap placeholder;

	public EventListAdapter(Context context) {
		super(context, R.layout.listview_row_event);
		this.placeholder = BitmapFactory.decodeResource(context.getResources(), R.drawable.photo_blank);
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_row_event, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        // populate the holder
        final com.snapable.api.private_v1.objects.Event event = getItem(position);

		// set the title
        holder.title.setText(event.title);
		long start = event.start_at.getTime();
        start += TimeZone.getDefault().getRawOffset(); // add the device offset
        holder.date.setText(DateFormat.format("EEE MMMM d, h:mm a", start));

		// get the image, if there is one
		final String imageKey = event.getPk() + "_480x480";
		Bitmap bm = new EventWorkerTask(null).getBitmapFromCache(imageKey);
		if (bm != null) {
			holder.cover.setImageBitmap(bm);
		} else if (SnapCache.EventWorkerTask.cancelPotentialWork(event.getPk(), holder.cover)) {
            final EventWorkerTask task = new EventWorkerTask(holder.cover);
            final AsyncDrawable asyncDrawable = new AsyncDrawable(getContext().getResources(), this.placeholder, task);
            holder.cover.setImageDrawable(asyncDrawable);
            task.execute(event.getPk());
        }

		return convertView;
	}

    static class ViewHolder {
        @InjectView(R.id.listview_row_event__cover)
        ImageView cover;
        @InjectView(R.id.listview_row_event__title)
        TextView title;
        @InjectView(R.id.listview_row_event__date)
        TextView date;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
