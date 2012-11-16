package ca.hashbrown.snapable;

import ca.hashbrown.snapable.fragments.EventListFragment;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class PhotoShare extends FragmentActivity {

	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_photo_share);
    }
}