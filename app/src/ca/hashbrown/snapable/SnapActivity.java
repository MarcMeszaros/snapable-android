package ca.hashbrown.snapable;

import com.snapable.api.SnapableContract;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.app.ListActivity;
import android.database.Cursor;

public class SnapActivity extends ListActivity implements OnClickListener {

	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snap);
        // TODO: remove this, it removes the runtime check for network activity on the main ui thread
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
        
        // hook up button to listener
        findViewById(R.id.get_events).setOnClickListener(this);    
    }

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.get_events:
	        // the events
	        Cursor cursor = getContentResolver().query(SnapableContract.Event.CONTENT_URI, null, null, null, null);
	        EventListAdapter adapter = new EventListAdapter(this, cursor);
	        setListAdapter(adapter);
	        break;
		}
	}
}