package ca.hashbrown.snapable;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.SearchView.OnQueryTextListener;

import ca.hashbrown.snapable.fragments.EventListFragment;

import android.os.Bundle;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SearchViewCompat;
import android.util.Log;

public class EventList extends SherlockFragmentActivity implements OnQueryTextListener {

	private static final String TAG = "EventList";

	public SearchViewCompat.OnQueryTextListenerCompat searchListener = null;
	//ActionBar actionBar;

	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_event_list);
    	//actionBar = getSupportActionBar();

    	// create the fragments
    	EventListFragment eventListFragment = new EventListFragment();

    	// add the fragments
    	FragmentTransaction transaction = getCompatFragmentManager().beginTransaction();
    	transaction.add(R.id.activity_event_list__fragment_event_list, eventListFragment, "EVENT_LIST_FRAGMENT");
    	transaction.commit();
	}

	// used to pass back the search field listener back to the search view in the fragment
	//public OnQueryTextListenerCompat getOnQueryTextListenerCompat() {
	//	return searchListener;
	//}
	
	/**
	 * 
	 * @return
	 */
	private FragmentManager getCompatFragmentManager() {
			return getSupportFragmentManager();
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Create the search view
        SearchView searchView = new SearchView(getSupportActionBar().getThemedContext());
        searchView.setQueryHint("Event URL or Title");
        searchView.setOnQueryTextListener(this);

        menu.add("Search")
            .setActionView(searchView)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

        return true;
    }

	@Override
	public boolean onQueryTextSubmit(String query) {
		Log.d(TAG, "search: " + query);
		// build the search param
		Bundle args = new Bundle(1);
		args.putString("q", query);
		
		// get the fragment, and init the new search loader (using the search param)
		EventListFragment frag = (EventListFragment) getCompatFragmentManager().findFragmentByTag("EVENT_LIST_FRAGMENT");
		frag.getLoaderManager().initLoader(EventListFragment.LOADERS.EVENTS, args, frag);
		return true;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		// TODO Auto-generated method stub
		return false;
	}

}