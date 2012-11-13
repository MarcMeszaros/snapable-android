package ca.hashbrown.snapable;

import ca.hashbrown.snapable.fragments.PhotoListFragment;

import com.snapable.api.models.Event;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class EventPhotoList extends FragmentActivity {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	Bundle bundle = getIntent().getExtras();
		Event event = bundle.getParcelable("event");

		// Create the list fragment and add it as our sole content.
 		if (getSupportFragmentManager().findFragmentById(android.R.id.content) == null) {
 			PhotoListFragment list = new PhotoListFragment(event);
 			getSupportFragmentManager().beginTransaction().add(android.R.id.content, list).commit();
 		}
    }

}
