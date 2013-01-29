package ca.hashbrown.snapable;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import ca.hashbrown.snapable.activities.CameraActivity;
import ca.hashbrown.snapable.fragments.PhotoListFragment;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

import com.snapable.api.SnapClient;
import com.snapable.api.models.Event;
import com.snapable.api.resources.EventResource;
import com.snapable.api.resources.PhotoResource;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class EventPhotoList extends SherlockFragmentActivity implements OnClickListener {

	private static final String TAG = "EventPhotoList";

	private Event event;
	private Uri imageUri;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_list);

		// add click listener
		findViewById(R.id.activity_photo_list__photo_button).setOnClickListener(this);

		// get the extra bundle data for the fragment
		Bundle bundle = getIntent().getExtras();
		event = bundle.getParcelable("event");

		// Create the list fragment and add it as our sole content.
		PhotoListFragment photoListFragment = (PhotoListFragment) getSupportFragmentManager().findFragmentById(R.id.activity_photo_list__fragment_photo_list);
		if (photoListFragment != null) {
			photoListFragment.setEvent(event);
		}

		// make the action bar button home button go back
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.activity_photo_list__photo_button:
			Intent intent = new Intent(this, CameraActivity.class);
			intent.putExtra("event", this.event);
			startActivity(intent);
			break;

		default:
			break;
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;

		default:
			return false;
		}
	}

}
