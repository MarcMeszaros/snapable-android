package ca.hashbrown.snapable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.InputStream;

import com.snapable.api.SnapClient;
import com.snapable.api.models.Event;
import com.snapable.api.resources.PhotoResource;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class PhotoShare extends FragmentActivity implements OnClickListener {

	private static final String TAG = "PhotoShare";
	
	private Event event;
	private Bitmap imageBitmap;

	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_photo_share);
    	
    	findViewById(R.id.fragment_photo_share__button_done).setOnClickListener(this);
    	
    	// get the extra bundle data
    	Bundle bundle = getIntent().getExtras();
    	event = bundle.getParcelable("event");
		Uri imageUri = bundle.getParcelable("imageUri");
		imageBitmap = BitmapFactory.decodeFile(imageUri.getPath());
		
		// set the scaled image in the image view
    	ImageView photo = (ImageView) findViewById(R.id.fragment_photo_share__image);
    	Bitmap bmScaled = Bitmap.createScaledBitmap(imageBitmap, 100, 100, false);
    	photo.setImageBitmap(bmScaled);
    }

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fragment_photo_share__button_done:
			// get the image data ready for uploading via the API
	        PhotoUloadTask uploadTask = new PhotoUloadTask(event);
	        uploadTask.execute(imageBitmap);
			
			break;

		default:
			break;
		}
		
	}
	
	private class PhotoUloadTask extends AsyncTask<Bitmap, Void, Void> {

		private Event event;
		
		public PhotoUloadTask(Event event) {
			this.event = event;
		}
		
		@Override
		protected Void doInBackground(Bitmap... params) {
			/*
			byte[] imageData = null;
	    	ByteArrayOutputStream baos = new ByteArrayOutputStream();  
	        params[0].compress(Bitmap.CompressFormat.JPEG, 50, baos);
	        imageData = baos.toByteArray();
	        
	        // upload via the 
	        ByteArrayInputStream inStream = new ByteArrayInputStream(imageData);
			*/
	        // upload via the API
	        try {
				PhotoResource photoRes = SnapClient.getInstance().build(PhotoResource.class);
				photoRes.postPhoto(params[0], event.getResourceUri(), "/private_v1/type/6/", "awesome hardcoded android caption");
	        } catch (org.codegist.crest.CRestException e) {
	        	Log.e(TAG, "problem with the response?", e);
	        }
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			Log.d(TAG, "upload complete");
		}
	}
}