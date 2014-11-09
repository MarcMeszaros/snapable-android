package ca.hashbrown.snapable.loaders;

import android.content.Context;
import android.text.TextUtils;

import com.snapable.api.private_v1.objects.Pager;
import com.snapable.api.private_v1.objects.Event;
import com.snapable.api.private_v1.resources.EventResource;

import ca.hashbrown.snapable.api.SnapClient;

public class EventLoader extends PagedApiLoader<Event> {

    private EventResource eventResource = SnapClient.getResource(EventResource.class);
    private float mLat;
    private float mLng;
    private String mQuery;

    public EventLoader(Context context, String query) {
        super(context);
        mQuery = query;
    }

    public EventLoader(Context context, float lat, float lng) {
        super(context);
        mLat = lat;
        mLng = lng;
    }

    @Override
    Pager<Event> performApiQuery() {
        if (!TextUtils.isEmpty(getNext(true)))
            return eventResource.getEventsNext(getNext(true));
        else if (!TextUtils.isEmpty(mQuery))
            return eventResource.getEvents(mQuery);
        else
            return eventResource.getEvents(mLat, mLng);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        super.forceLoad();
    }
}
