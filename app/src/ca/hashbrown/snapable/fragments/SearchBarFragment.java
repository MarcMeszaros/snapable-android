package ca.hashbrown.snapable.fragments;

import ca.hashbrown.snapable.R;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SearchViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class SearchBarFragment extends Fragment {
	
	private static final String TAG = "SearchBarFragment";
	
	private SearchViewCompat.OnQueryTextListenerCompat searchListener;

	// Container Activity must implement this interface
    public interface OnQueryTextListener {
    	public abstract SearchViewCompat.OnQueryTextListenerCompat getOnQueryTextListenerCompat();
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// get the layout
		LinearLayout view = (LinearLayout) inflater.inflate(R.layout.fragment_search_bar, container, false);

		// create the searchView
		View searchView = SearchViewCompat.newSearchView(view.getContext());
		searchView.setTag("SEARCH_VIEW");

		// append search to relative layout
		view.addView(searchView);
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.d(TAG, "start searchbar fragment: " + this.getId());
		Activity activity = getActivity();
		try {
			// try and get the search listener
			this.searchListener = ((OnQueryTextListener) activity).getOnQueryTextListenerCompat();
			View searchView = getView().findViewWithTag("SEARCH_VIEW");

			// the search listener isn't null, we can attach it to the view
			if (searchListener != null) {
				SearchViewCompat.setOnQueryTextListener(searchView, this.searchListener);
			}
		} catch (ClassCastException e) {
			Log.e(TAG, "must implement OnQueryTextListener", e);
			throw new ClassCastException(activity.toString() + " must implement OnQueryTextListener");
		}
	}

}
