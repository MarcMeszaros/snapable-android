package ca.hashbrown.snapable;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import ca.hashbrown.snapable.fragments.PhotoListFragment;

import com.snapable.api.SnapClient;
import com.snapable.api.models.Event;
import com.snapable.api.resources.EventResource;
import com.snapable.api.resources.PhotoResource;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class EventPhotoList extends FragmentActivity implements OnClickListener {
	
	private static final String TAG = "EventPhotoList";
	
	private Uri imageUri;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_photo_list);

    	// add click listener
    	findViewById(R.id.activity_photo_list__photo_button).setOnClickListener(this);
    	
    	// get the extra bundle data for the fragment
    	Bundle bundle = getIntent().getExtras();
		Event event = bundle.getParcelable("event");
		
		// Create the list fragment and add it as our sole content.
		PhotoListFragment photoListFragment = (PhotoListFragment) getSupportFragmentManager().findFragmentById(R.id.activity_photo_list__fragment_photo_list);
 		if (photoListFragment != null) {
 			photoListFragment.setEvent(event);
 		}
    }

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.activity_photo_list__photo_button:
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			
			imageUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			
			startActivityForResult(intent, 0);
			break;

		default:
			break;
		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 0:
			if (resultCode == RESULT_OK) {
				//PhotoResource photoRes = SnapClient.getInstance().build(PhotoResource.class);
				
				//photoRes.uploadPhoto(photo, event, guest, type, caption)
				Intent upload = new Intent(this, PhotoShare.class);
				startActivity(upload);
			}
			break;

		default:
			break;
		}
	}
	
	public static final int MEDIA_TYPE_IMAGE = 1;
	
	/** Create a file Uri for saving an image or video */
	private static Uri getOutputMediaFileUri(int type) {
	      return Uri.fromFile(getOutputMediaFile(type));
	}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type) {
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.

	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Snapable");
	    // This location works best if you want the created images to be shared
	    // between applications and persist after your app has been uninstalled.

	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d(TAG, "failed to create directory");
	            return null;
	        }
	        Log.d(TAG, "created storage directory");
	    }

	    // Create a media file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    File mediaFile;
	    if (type == MEDIA_TYPE_IMAGE){
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg");
	    } else {
	        return null;
	    }

	    return mediaFile;
	}

}
