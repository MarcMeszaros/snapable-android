package ca.hashbrown.snapable.loaders;

import android.content.Context;
import android.text.TextUtils;

import com.snapable.api.private_v1.objects.Pager;
import com.snapable.api.private_v1.objects.Photo;
import com.snapable.api.private_v1.resources.PhotoResource;

import ca.hashbrown.snapable.api.SnapClient;

public class PhotoLoader extends PagedApiLoader<Photo> {

    private PhotoResource photoResource = SnapClient.getResource(PhotoResource.class);
    private long mEventId;
    private boolean mIsStreamable;

    public PhotoLoader(Context context, long eventId, boolean isStreamable) {
        super(context);
        mEventId = eventId;
        mIsStreamable = isStreamable;
    }

    @Override
    Pager<Photo> performApiQuery() {
        if (!TextUtils.isEmpty(getNext(true)))
            return photoResource.getPhotos(getNext(true));
        else
            return photoResource.getPhotos(mEventId, mIsStreamable);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        super.forceLoad();
    }

}
