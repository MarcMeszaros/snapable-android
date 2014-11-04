package ca.hashbrown.snapable.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.text.TextUtils;

import com.snapable.api.private_v1.objects.Pager;
import com.snapable.util.ToStringHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Timer;

import retrofit.RetrofitError;
import timber.log.Timber;

/**
 * A {@link android.content.AsyncTaskLoader} that manages paging through API results.
 *
 * @param <E> The types of objects returned by the API.
 */
abstract class PagedApiLoader<E> extends AsyncTaskLoader<LoaderResponse<E>> {

    // paging stuff
    private volatile String mNextPage;
    private volatile String mPreviousPage;
    private volatile boolean hasNextPage = true;
    private volatile boolean hasPreviousPage = false;
    private volatile Object lock = new Object();

    // state
    private volatile boolean processing = false;

    public PagedApiLoader(Context context) {
        super(context);
    }

    @Override
    public LoaderResponse<E> loadInBackground() {
        synchronized (lock) {
            processing = true;
            // setup the loader response
            LoaderResponse<E> response = new LoaderResponse<E>();
            // only perform the API query if there is a next page
            if (hasNextPage && !isAbandoned()) {
                try {
                    Pager<E> pager = performApiQuery(); // perform the API query
                    mNextPage = removeApiVersion(pager.meta.next);
                    mPreviousPage = removeApiVersion(pager.meta.previous);
                    hasNextPage = !TextUtils.isEmpty(mNextPage);
                    hasPreviousPage = !TextUtils.isEmpty(mPreviousPage);
                    response.data = pager.objects;

                    if (!hasPreviousPage)
                        response.type = LoaderResponse.TYPE.FIRST;
                    else if (hasNextPage)
                        response.type = LoaderResponse.TYPE.MORE;
                    else
                        response.type = LoaderResponse.TYPE.LAST;
                } catch (RetrofitError e) {
                    Timber.e(e, e.getUrl());
                    response.type = LoaderResponse.TYPE.ERROR;
                }
            }

            // return the response
            processing = false;
            return response;
        }
    }

    @Override
    protected void onReset() {
        super.onReset();
        mNextPage = null;
        mPreviousPage = null;
        hasNextPage = true;
    }

    /**
     * Try and load the next API page.
     */
    public void loadNextPage() {
        if (isStarted()) {
            forceLoad();
        }
    }

    /**
     * Get the page to process.
     *
     * @return The current page that should be processed.
     */
    public String getNext() {
        return getNext(false);
    }

    public String getNext(boolean removeFirstChar) {
        if (removeFirstChar && !TextUtils.isEmpty(mNextPage) && mNextPage.length() > 1)
            return mNextPage.substring(1);
        else
            return mNextPage;
    }

    public String getPrevious() {
        return getPreviousPage(false);
    }

    public String getPreviousPage(boolean removeFirstChar) {
        if (removeFirstChar && !TextUtils.isEmpty(mPreviousPage) && mPreviousPage.length() > 1)
            return mPreviousPage.substring(1);
        else
            return mPreviousPage;
    }

    /**
     * A flag representing if the loader is already processing something.
     *
     * @return A boolean representing the processing state.
     */
    public boolean isProcessing() {
        return processing;
    }

    public boolean hasNextPage() {
        return hasNextPage;
    }

    /**
     * Performs the actual API query.
     *
     * @return An API pager object.
     */
    abstract Pager<E> performApiQuery();

    private static String removeApiVersion(String path) {
        if (path != null)
            return path.substring(path.indexOf('/', 1));
        else
            return null;
    }

}
