package ca.hashbrown.snapable;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class EventList extends FragmentActivity {

	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_event_list);
    }
}