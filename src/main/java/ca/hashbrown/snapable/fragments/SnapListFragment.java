package ca.hashbrown.snapable.fragments;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;

import com.octo.android.robospice.SpiceManager;

import ca.hashbrown.snapable.R;
import ca.hashbrown.snapable.api.robospice.SnapSpiceService;

/**
 * The source code is almost identical to the function found in AOSP (Android Open Source Project).
 * The only difference is that the {@link #setListShown(boolean, boolean)} function uses custom ids
 * when loading.
 *
 * @see <a href="https://android.googlesource.com/platform/frameworks/base/+/master/core/java/android/app/ListFragment.java">Android Source File</a>
 * @see <a href="https://android.googlesource.com/platform/frameworks/base/+/master/core/res/res/layout/list_content.xml">Android Layout File<a/>
 */
public abstract class SnapListFragment extends ListFragment implements AbsListView.OnScrollListener {

    protected SpiceManager apiRequestManager = new SpiceManager(SnapSpiceService.class);

    private final int AUTOLOAD_THRESHOLD = 4;

    private boolean isMoreLoading = false;
    private int mScrollState = SCROLL_STATE_IDLE;
    private LoadMoreListener mLoadMoreListener;

    @Override
    public void onStart() {
        super.onStart();
        apiRequestManager.start(getActivity());
    }

    @Override
    public void onStop() {
        apiRequestManager.shouldStop();
        super.onStop();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setOnScrollListener(this);
    }

    /**
     * Control whether the list is being displayed.  You can make it not
     * displayed if you are waiting for the initial data to show in it.  During
     * this time an indeterminant progress indicator will be shown instead.
     *
     * <p>Applications do not normally need to use this themselves.  The default
     * behavior of ListFragment is to start with the list not being shown, only
     * showing it once an adapter is given with {@link #setListAdapter(ListAdapter)}.
     * If the list at that point had not been shown, when it does get shown
     * it will be do without the user ever seeing the hidden state.
     *
     * @param shown If true, the list view is shown; if false, the progress
     * indicator.  The initial value is true.
     */
    public void setListShown(boolean shown) {
        setListShown(shown, true);
    }

    /**
     * Like {@link #setListShown(boolean)}, but no animation is used when
     * transitioning from the previous state.
     */
    public void setListShownNoAnimation(boolean shown) {
        setListShown(shown, false);
    }

    /**
     * Control whether the list is being displayed.  You can make it not
     * displayed if you are waiting for the initial data to show in it.  During
     * this time an indeterminant progress indicator will be shown instead.
     *
     * @param shown If true, the list view is shown; if false, the progress
     * indicator.  The initial value is true.
     * @param animate If true, an animation will be used to transition to the
     * new state.
     */
    private void setListShown(boolean shown, boolean animate) {
        if (getView() == null)
            return;

        View mProgressContainer = getView().findViewById(R.id.progressContainer);
        View mListContainer = getView().findViewById(R.id.listContainer);
        if (mListContainer == null) {
            throw new IllegalStateException("Can't be used with a custom content view.");
        }

        boolean mListShown = (mListContainer.getVisibility() == View.VISIBLE);
        boolean mProgressShown = mProgressContainer != null && (mProgressContainer.getVisibility() == View.VISIBLE);
        if (mListShown == shown && mProgressShown != shown) {
            return;
        }

        // if the progress container is missing, just show the list container
        if (mProgressContainer == null) {
            if (animate) {
                mListContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
            } else {
                mListContainer.clearAnimation();
            }
            mListContainer.setVisibility(View.VISIBLE);
            return;
        }

        // toggle the containers if both the list container and the progress containers are available
        if (shown) {
            if (animate) {
                mProgressContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
                mListContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
            } else {
                mProgressContainer.clearAnimation();
                mListContainer.clearAnimation();
            }
            mProgressContainer.setVisibility(View.GONE);
            mListContainer.setVisibility(View.VISIBLE);
        } else {
            if (animate) {
                mProgressContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
                mListContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
            } else {
                mProgressContainer.clearAnimation();
                mListContainer.clearAnimation();
            }
            mProgressContainer.setVisibility(View.VISIBLE);
            mListContainer.setVisibility(View.GONE);
        }
    }

    // ==== AbsListView.OnScrollListener ====\\
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // if we can load more
        if (mLoadMoreListener != null && !isMoreLoading && mScrollState != SCROLL_STATE_IDLE) {
            // check if we should load
            final int lastItem = firstVisibleItem + visibleItemCount;
            if ((totalItemCount - AUTOLOAD_THRESHOLD) <= lastItem) {
                isMoreLoading = true;
                mLoadMoreListener.loadMore();
                isMoreLoading = false;
            }
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        mScrollState = scrollState;
    }

    //==== LoadMore Interface ====\\

    /**
     * Set the {@link LoadMoreListener} listener for the
     * list.
     *
     * @param listener The listener to associate.
     */
    public void setLoadMoreListener(LoadMoreListener listener) {
        mLoadMoreListener = listener;
    }

    /**
     * Callbacks to load more data based on various events such as scrolling.
     */
    public interface LoadMoreListener {
        public void loadMore();
    }

}
