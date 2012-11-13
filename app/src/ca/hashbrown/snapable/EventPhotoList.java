package ca.hashbrown.snapable;

import com.snapable.api.models.Event;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class EventPhotoList extends Activity {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	Bundle bundle = getIntent().getExtras();
		Event event = bundle.getParcelable("event");
		Log.d("EventDetails", event.toString());
		
		TextView title = new TextView(this);
		title.setText(event.getTitle());
		
		setContentView(title);
    }

}
