package ca.hashbrown.snapable.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.snapable.api.private_v1.objects.Event;

import butterknife.ButterKnife;
import butterknife.OnClick;
import ca.hashbrown.snapable.R;
import ca.hashbrown.snapable.fragments.PhotoListFragment;
import ca.hashbrown.snapable.utils.SnapStorage;

import java.io.File;

public class EventPhotoList extends BaseActivity {

    public static final int PHOTO_ACTION = 0x01;

    public static final String ARG_EVENT = "arg.event";
    private static final String STATE_EVENT = "state.event";
    private static final String STATE_IMAGE_URI = "state.image.uri";

    private Event mEvent;
    private Uri mImageUri;

    public static Intent initIntent(Activity activity, Event event) {
        Intent intent = new Intent(activity, EventPhotoList.class);
        intent.putExtra(ARG_EVENT, event);
        return intent;
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_list);
        ButterKnife.inject(this);

        // try and save/resume the activity
        if (savedInstanceState != null) {
            mEvent = (Event) savedInstanceState.getSerializable(STATE_EVENT);
        } else {
            mEvent = (Event) getIntent().getSerializableExtra(ARG_EVENT);
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, PhotoListFragment.getInstance(mEvent), PhotoListFragment.class.getCanonicalName())
                    .commit();
        }

		// make the action bar button home button go back
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
	}

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE_EVENT, mEvent);
        outState.putParcelable(STATE_IMAGE_URI, mImageUri);
    }

    @OnClick(R.id.activity_photo_list__photo_button)
    public void onClickPhoto(View v) {
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
        mImageUri = SnapStorage.getOutputMediaFileUri(SnapStorage.MEDIA_TYPE_IMAGE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        startActivityForResult(takePictureIntent, PHOTO_ACTION);
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_ACTION && resultCode == RESULT_OK) {
            File filename = new File(mImageUri.getPath());

            // alert the media scanner of new file
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(mImageUri);
            this.sendBroadcast(mediaScanIntent);

            // pass all the data to the photo upload activity
            startActivity(PhotoUpload.initIntent(this, mEvent, filename.getAbsolutePath()));
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
