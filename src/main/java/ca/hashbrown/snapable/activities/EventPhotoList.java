package ca.hashbrown.snapable.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;

import ca.hashbrown.snapable.BuildConfig;
import ca.hashbrown.snapable.R;
import ca.hashbrown.snapable.fragments.PhotoListFragment;
import ca.hashbrown.snapable.utils.SnapStorage;
import ca.hashbrown.snapable.api.models.Event;

import java.io.File;

public class EventPhotoList extends BaseFragmentActivity implements OnClickListener {

	private static final String TAG = "EventPhotoList";

    public static final int PHOTO_ACTION = 0x01;

    private Event event;
    private Uri imageUri;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_list);

        // try and save/resume the activity
        if (savedInstanceState != null) {
            this.event = savedInstanceState.getParcelable("event");
            imageUri = Uri.parse(savedInstanceState.getString("imageUri"));
        } else {
            // get the extra bundle data for the fragment
            Bundle bundle = getIntent().getExtras();
            event = bundle.getParcelable("event");
        }

        // add click listener
		findViewById(R.id.activity_photo_list__photo_button).setOnClickListener(this);

		// Create the list fragment and add it as our sole content.
		PhotoListFragment photoListFragment = (PhotoListFragment) getFragmentManager().findFragmentById(R.id.activity_photo_list__fragment_photo_list);
		if (photoListFragment != null) {
			photoListFragment.setEvent(event);
		}

		// make the action bar button home button go back
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setTitle(event.title);
	}

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("event", this.event);
        if (imageUri != null) {
            outState.putString("imageUri", imageUri.getPath());
        }
    }

    public void onClick(View v) {
		switch (v.getId()) {
		case R.id.activity_photo_list__photo_button:
            // fake the camera view
            // May return null if EasyTracker has not yet been initialized with a property ID.
            Tracker easyTracker = EasyTracker.getInstance(this);

            // This screen name value will remain set on the tracker and sent with
            // hits until it is set to a new value or to null.
            easyTracker.set(Fields.SCREEN_NAME, "Camera");
            easyTracker.send(MapBuilder
                            .createAppView()
                            .build()
            );

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            this.imageUri = SnapStorage.getOutputMediaFileUri(SnapStorage.MEDIA_TYPE_IMAGE);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, this.imageUri);
            startActivityForResult(takePictureIntent, PHOTO_ACTION);
            break;

		default:
			break;
		}

	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_ACTION && resultCode == RESULT_OK) {
            File filename = new File(this.imageUri.getPath());

            // alert the media scanner of new file
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(this.imageUri);
            this.sendBroadcast(mediaScanIntent);

            // pass all the data to the photo upload activity
            Intent upload = new Intent(this, PhotoUpload.class);
            upload.putExtra("event", event);
            upload.putExtra("imagePath", filename.getAbsolutePath());
            startActivity(upload);
        } else {
            // the unhandled result calls the super (and passes it down to fragments)
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Use the new API 16+ way of handling parent activities.
     *
     * @see <a href="http://developer.android.com/guide/topics/manifest/activity-element.html#parent">Android Developers</a>
     */
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return super.onOptionsItemSelected(item);
        } else {
            switch (item.getItemId()) {
                case android.R.id.home:
                    // This is called when the Home (Up) button is pressed in the Action Bar.
                    // http://developer.android.com/training/implementing-navigation/ancestral.html
                    Intent parentActivityIntent = new Intent(this, EventList.class);
                    parentActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(parentActivityIntent);
                    finish();
                    return true;

                default:
                    return false;
            }
        }
	}

}
