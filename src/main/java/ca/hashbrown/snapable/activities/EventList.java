package ca.hashbrown.snapable.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.SearchView;
import ca.hashbrown.snapable.R;
import ca.hashbrown.snapable.fragments.EventListFragment;

public class EventList extends BaseFragmentActivity {

	private static final String TAG = "EventList";

	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_event_list);
	}

}