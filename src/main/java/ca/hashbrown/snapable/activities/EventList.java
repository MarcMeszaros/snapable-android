package ca.hashbrown.snapable.activities;

import android.os.Bundle;

import ca.hashbrown.snapable.R;

public class EventList extends BaseActivity {

	private static final String TAG = "EventList";

	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_event_list);
	}

}