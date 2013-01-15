package ca.hashbrown.snapable;

import ca.hashbrown.snapable.fragments.EventListFragment;
import ca.hashbrown.snapable.fragments.SearchBarFragment;
import ca.hashbrown.snapable.fragments.SearchBarFragment.OnQueryTextListener;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SearchViewCompat;
import android.support.v4.widget.SearchViewCompat.OnQueryTextListenerCompat;
import android.util.Log;

public class EventList extends FragmentActivity implements OnQueryTextListener {

	private static final String TAG = "EventList";

	public SearchViewCompat.OnQueryTextListenerCompat searchListener = null;

	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_event_list);

    	// create the fragments
    	SearchBarFragment searchFragment = new SearchBarFragment();
    	EventListFragment eventListFragment = new EventListFragment();

    	// add the fragments
    	FragmentTransaction transaction = getFragmentManager().beginTransaction();
    	transaction.add(R.id.activity_event_list__fragment_search_bar, searchFragment, "SEARCH_FRAGMENT");
    	transaction.add(R.id.activity_event_list__fragment_event_list, eventListFragment, "EVENT_LIST_FRAGMENT");
    	transaction.commit();
	}

	// used to pass back the search field listener back to the search view in the fragment
	public OnQueryTextListenerCompat getOnQueryTextListenerCompat() {
		return searchListener;
	}

	@Override
	protected void onStart() {
		EventListFragment frag = (EventListFragment) getFragmentManager().findFragmentByTag("EVENT_LIST_FRAGMENT");

		// if the event list is there, we can create the listenet that will modify it
		if (frag != null) {
			
			// create the search field listener that updates the list on submit
			this.searchListener = new OnQueryTextListenerCompat() {
	    		@Override
	    		public boolean onQueryTextSubmit(String query) {
	    			// build the search param
	    			Bundle args = new Bundle(1);
	    			args.putString("q", query);
	    			
	    			// get the fragment, and init the new search loader (using the search param)
	    			EventListFragment frag = (EventListFragment) getFragmentManager().findFragmentByTag("EVENT_LIST_FRAGMENT");
	    			frag.getLoaderManager().initLoader(EventListFragment.LOADERS.EVENTS, args, frag);
	    			return true;
	    		}
			};
		}
		super.onStart();
	}
	
	/**
	 * 
	 * @return
	 */
	private FragmentManager getFragmentManager() {
		return getSupportFragmentManager();
	}
	
}