package ca.hashbrown.snapable.activities;

import android.os.Bundle;

import ca.hashbrown.snapable.R;
import ca.hashbrown.snapable.fragments.EventListFragment;

public class EventList extends BaseActivity {

	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_event_list);
	}

}