package ca.hashbrown.snapable;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.SearchView.OnQueryTextListener;

import ca.hashbrown.snapable.R;
import ca.hashbrown.snapable.fragments.EventListFragment;

import android.os.Bundle;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

public class EventList extends SherlockFragmentActivity implements OnQueryTextListener {

	private static final String TAG = "EventList";

	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_event_list);
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Create the search view
		getSupportMenuInflater().inflate(R.menu.fragment_event_list, menu);
		SearchView searchView = (SearchView) menu.findItem(R.id.menu__fragment_event_list__search).getActionView();
		searchView.setQueryHint("Event Title or URL");
		searchView.setOnQueryTextListener(this);

        return true;
    }

	@Override
	public boolean onQueryTextSubmit(String query) {
		Log.d(TAG, "search: " + query);
		// build the search param
		Bundle args = new Bundle(1);
		args.putString("q", query);
		
		// get the fragment, and init the new search loader (using the search param)
		EventListFragment frag = (EventListFragment) getSupportFragmentManager().findFragmentById(R.id.activity_event_list__fragment_event_list);
		frag.getLoaderManager().initLoader(EventListFragment.LOADERS.EVENTS, args, frag);
		return true;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		// TODO Auto-generated method stub
		return false;
	}

}