package ca.hashbrown.snapable;

import ca.hashbrown.snapable.fragments.EventListFragment;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class EventList extends FragmentActivity {

	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);

        // Create the list fragment and add it as our sole content.
 		if (getSupportFragmentManager().findFragmentById(android.R.id.content) == null) {
 			EventListFragment list = new EventListFragment();
 			getSupportFragmentManager().beginTransaction().add(android.R.id.content, list).commit();
 		}
    }
}