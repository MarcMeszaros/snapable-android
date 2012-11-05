package ca.hashbrown.snapable;

import com.snapable.api.SnapClient;
import com.snapable.api.model.*;
import com.snapable.api.resources.*;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.app.ListActivity;
import android.database.MatrixCursor;

public class SnapActivity extends ListActivity implements OnClickListener {

	private SnapClient snapClient;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snap);
        // TODO: remove this, it removes the runtime check for network activity on the main ui thread
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
        
        // hook up button to listener
        findViewById(R.id.get_events).setOnClickListener(this);
        
        this.snapClient = new SnapClient();
    }

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.get_events:
	        // Get a StatusService instance
	        EventResource eventsRes = this.snapClient.build(EventResource.class);
	        Pager<Event[]> events = eventsRes.getEvents();
	        
	        MatrixCursor cursor = new MatrixCursor(new String[] {"_id","title"}); 
	        
	        for (Event event : events.getObjects()) {
	        	Log.d("SnapActivity", event.toString());
	        	cursor.addRow(new Object[] {event.getId(), event.getTitle()});
			}
	        
	        Log.d("SnapActivity", "Count: "+cursor.getCount());
	        
	        EventListAdapter adapter = new EventListAdapter(this, cursor);
	        setListAdapter(adapter);
	        
	        break;
		}
	}
}