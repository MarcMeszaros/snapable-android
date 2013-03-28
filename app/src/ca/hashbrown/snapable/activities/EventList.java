package ca.hashbrown.snapable.activities;

import android.os.Bundle;
import android.util.Log;
import ca.hashbrown.snapable.R;
import ca.hashbrown.snapable.fragments.EventListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.SearchView.OnQueryTextListener;

public class EventList extends BaseFragmentActivity implements OnQueryTextListener {

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
		frag.getLoaderManager().restartLoader(EventListFragment.LOADERS.EVENTS, args, frag);
		return true;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		Log.d(TAG, "new query: " + newText.isEmpty() + " " + newText);
        if (newText.isEmpty()) {
            // get the fragment, and reset the search loader
            EventListFragment frag = (EventListFragment) getSupportFragmentManager().findFragmentById(R.id.activity_event_list__fragment_event_list);
            frag.getLoaderManager().restartLoader(EventListFragment.LOADERS.EVENTS, null, frag);
            return true;
        } else {
            return false;
        }
	}

}