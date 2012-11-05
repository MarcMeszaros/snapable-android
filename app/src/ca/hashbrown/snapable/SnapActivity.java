package ca.hashbrown.snapable;

import com.snapable.api.SnapClient;
import com.snapable.api.model.*;
import com.snapable.api.resources.EventResource;
import com.snapable.api.resources.PhotoResource;

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
		PhotoResource photoRes = this.snapClient.build(PhotoResource.class);

		switch (v.getId()) {
		case R.id.get_events:
	        // Get a StatusService instance
	        EventResource eventsRes = this.snapClient.build(EventResource.class);
	        Pager<Event[]> events = eventsRes.getEvents();
	        
	        MatrixCursor cursor = new MatrixCursor(new String[] {"_id","title"}); 
	        
	        //ArrayList<String> titles = new ArrayList<String>(5);
	        for (Event event : events.getObjects()) {
	        	Log.d("SnapActivity", event.toString());
	        	cursor.addRow(new Object[] {event.getId(), event.getTitle()});
			}
	        
	        Log.d("SnapActivity", "Count: "+cursor.getCount());
	        
	        EventListAdapter adapter = new EventListAdapter(this, cursor);
	        setListAdapter(adapter);
	        
	        break;
		/*
		case R.id.get_photo:
			InputStream photo = photoRes.getPhotoBinary(301);
			if (photo != null) {
				try {
					Log.i("SnapActivity", Integer.toString(photo.available()));
					Bitmap bm = BitmapFactory.decodeStream(photo);
					ImageView imview = (ImageView)findViewById(R.id.image);
					imview.setImageBitmap(bm);
					Log.i("SnapActivity", "bitmap: "+bm.toString());
					Log.i("SnapActivity", "bitmap: "+bm.getWidth()+"x"+bm.getHeight());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			break;
			*/
		}
	}
}