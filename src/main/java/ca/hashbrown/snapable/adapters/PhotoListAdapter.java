package ca.hashbrown.snapable.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.snapable.api.private_v1.objects.Photo;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ca.hashbrown.snapable.R;
import ca.hashbrown.snapable.provider.SnapCache;
import ca.hashbrown.snapable.provider.SnapCache.AsyncDrawable;
import ca.hashbrown.snapable.provider.SnapCache.PhotoWorkerTask;

public class PhotoListAdapter extends ArrayAdapter<Photo> {

	private final Bitmap placeholder;

	public PhotoListAdapter(Context context) {
        super(context, R.layout.listview_row_eventphoto);
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
            convertView = inflater.inflate(R.layout.listview_row_eventphoto, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        // populate the holder
        final com.snapable.api.private_v1.objects.Photo photo = getItem(position);

        // set the caption
        if (TextUtils.isEmpty(photo.caption)) {
            holder.caption.setVisibility(View.GONE);
        } else {
            holder.caption.setVisibility(View.VISIBLE);
            holder.caption.setText(photo.caption);
        }

        // author name
        holder.authorName.setText( TextUtils.isEmpty(photo.author_name) ? "Anonymous" : photo.author_name);

		// get the image, if there is one
		final String imageKey = photo.getPk() + "_480x480";
		Bitmap bm = new PhotoWorkerTask(null).getBitmapFromCache(imageKey);
		if (bm != null) {
			holder.photo.setImageBitmap(bm);
		} else if (SnapCache.PhotoWorkerTask.cancelPotentialWork(photo.getPk(), holder.photo)) {
            final PhotoWorkerTask task = new PhotoWorkerTask(holder.photo);
            final AsyncDrawable asyncDrawable = new AsyncDrawable(getContext().getResources(), this.placeholder, task);
            holder.photo.setImageDrawable(asyncDrawable);
            task.execute(photo.getPk());
        }

		return convertView;
	}

    static class ViewHolder {
        @InjectView(R.id.listview_row_eventphoto_photo)
        ImageView photo;
        @InjectView(R.id.listview_row_eventphoto_caption)
        TextView caption;
        @InjectView(R.id.listview_row_eventphoto_author_name)
        TextView authorName;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

}
