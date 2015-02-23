package ca.hashbrown.snapable.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.snapable.api.private_v1.objects.Event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TimeZone;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ca.hashbrown.snapable.R;
import ca.hashbrown.snapable.provider.SnapCache.AsyncDrawable;
import ca.hashbrown.snapable.provider.SnapCache.EventWorkerTask;

public class EventRecyclerAdapter extends BaseRecyclerAdapter<EventRecyclerAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Event> mItems;
    private Bitmap mPlaceholder;
    private InteractionListener mInteractionLister;

    public EventRecyclerAdapter(Context context) {
        mContext = context;
        mPlaceholder = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.photo_blank);
        mItems = new ArrayList<>(10);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.listview_row_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // populate the holder
        final Event event = mItems.get(position);

        // set the title
        holder.title.setText(event.title);
        long start = event.startAt.getTime();
        start += TimeZone.getDefault().getRawOffset(); // add the device offset
        holder.date.setText(DateFormat.format("EEE MMMM d, h:mm a", start));

        // get the image, if there is one
        final String imageKey = event.getPk() + "_480x480";
        Bitmap bm = new EventWorkerTask(null).getBitmapFromCache(imageKey);
        if (bm != null) {
            holder.cover.setImageBitmap(bm);
        } else if (EventWorkerTask.cancelPotentialWork(event.getPk(), holder.cover)) {
            final EventWorkerTask task = new EventWorkerTask(holder.cover);
            final AsyncDrawable asyncDrawable = new AsyncDrawable(mContext.getResources(), mPlaceholder, task);
            holder.cover.setImageDrawable(asyncDrawable);
            task.execute(event.getPk());
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    //region == Public Methods ==
    public void setInteractionListener(InteractionListener listener) {
        mInteractionLister = listener;
    }

    public void addAll(Collection<? extends Event> collection) {
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

    /**
     * Gets the item at the specified position.
     *
     * @param position The item position.
     * @return The item.
     */
    public Event getItem(final int position) {
        return mItems.get(position);
    }
    //endregion

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @InjectView(R.id.partial_event_card__cover)
        ImageView cover;
        @InjectView(R.id.partial_event_card__title)
        TextView title;
        @InjectView(R.id.partial_event_card__date)
        TextView date;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            ButterKnife.inject(this, view);
        }

        @Override
        public void onClick(View view) {
            if (mInteractionLister != null)
                mInteractionLister.onClick(getPosition());
        }
    }

    public static interface InteractionListener {
        public void onClick(int position);
    }
}
