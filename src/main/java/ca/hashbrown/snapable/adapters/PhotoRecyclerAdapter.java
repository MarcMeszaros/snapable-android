package ca.hashbrown.snapable.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.snapable.api.private_v1.objects.Photo;

import java.util.ArrayList;
import java.util.Collection;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ca.hashbrown.snapable.R;
import ca.hashbrown.snapable.provider.SnapCache;

public class PhotoRecyclerAdapter extends RecyclerView.Adapter<PhotoRecyclerAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Photo> mItems;
    private Bitmap mPlaceholder;

    public PhotoRecyclerAdapter(Context context) {
        mContext = context;
        mPlaceholder = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.photo_blank);
        mItems = new ArrayList<>(10);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.listview_row_eventphoto, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Photo photo = mItems.get(position);

        // set the caption
        if (TextUtils.isEmpty(photo.caption)) {
            holder.caption.setVisibility(View.GONE);
        } else {
            holder.caption.setVisibility(View.VISIBLE);
            holder.caption.setText(photo.caption);
        }

        // author name
        holder.authorName.setText( TextUtils.isEmpty(photo.authorName) ? "Anonymous" : photo.authorName);

        // get the image, if there is one
        final String imageKey = photo.getPk() + "_480x480";
        Bitmap bm = new SnapCache.PhotoWorkerTask(null).getBitmapFromCache(imageKey);
        if (bm != null) {
            holder.photo.setImageBitmap(bm);
        } else if (SnapCache.PhotoWorkerTask.cancelPotentialWork(photo.getPk(), holder.photo)) {
            final SnapCache.PhotoWorkerTask task = new SnapCache.PhotoWorkerTask(holder.photo);
            final SnapCache.AsyncDrawable asyncDrawable = new SnapCache.AsyncDrawable(mContext.getResources(), mPlaceholder, task);
            holder.photo.setImageDrawable(asyncDrawable);
            task.execute(photo.getPk());
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    //region == Public Methods ==
    public void addAll(Collection<? extends Photo> collection) {
        mItems.addAll(collection);
        int position = mItems.size() - collection.size();
        if (position < 0)
            position = 0;
        notifyItemRangeInserted(position, collection.size());
    }

    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }
    //endregion

    public class ViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.listview_row_eventphoto_photo)
        ImageView photo;
        @InjectView(R.id.listview_row_eventphoto_caption)
        TextView caption;
        @InjectView(R.id.listview_row_eventphoto_author_name)
        TextView authorName;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }
}
