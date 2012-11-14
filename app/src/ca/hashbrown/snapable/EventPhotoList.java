package ca.hashbrown.snapable;

import ca.hashbrown.snapable.fragments.PhotoListFragment;

import com.snapable.api.models.Event;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class EventPhotoList extends FragmentActivity {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_photo_list);

    	Bundle bundle = getIntent().getExtras();
		Event event = bundle.getParcelable("event");
		
		// Create the list fragment and add it as our sole content.
		PhotoListFragment photoListFragment = (PhotoListFragment) getSupportFragmentManager().findFragmentById(R.id.activity_photo_list__fragment_photo_list);
 		if (photoListFragment != null) {
 			photoListFragment.setEvent(event);
 		}
    }

}
