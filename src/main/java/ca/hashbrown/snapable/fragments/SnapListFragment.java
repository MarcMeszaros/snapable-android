package ca.hashbrown.snapable.fragments;

import android.app.ListFragment;
import android.view.View;
import android.view.animation.AnimationUtils;

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
public abstract class SnapListFragment extends ListFragment {

    protected SpiceManager apiRequestManager = new SpiceManager(SnapSpiceService.class);

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
        View mProgressContainer = getView().findViewById(R.id.progressContainer);
        View mListContainer = getView().findViewById(R.id.listContainer);
        boolean mListShown = (mListContainer.getVisibility() == View.VISIBLE);
        if (mProgressContainer == null) {
            throw new IllegalStateException("Can't be used with a custom content view");
        }
        if (mListShown == shown) {
            return;
        }
        mListShown = shown;
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

}
