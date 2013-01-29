package ca.hashbrown.snapable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.InputStream;

import ca.hashbrown.snapable.utils.SnapBitmapFactory;
import ca.hashbrown.snapable.utils.SnapStorage;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import com.snapable.api.SnapClient;
import com.snapable.api.models.Event;
import com.snapable.api.resources.PhotoResource;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class PhotoShare extends SherlockFragmentActivity implements OnClickListener {

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
		imageBitmap = BitmapFactory.decodeFile(bundle.getString("imagePath"));

		// create a scaled bitmap
		ImageView photo = (ImageView) findViewById(R.id.fragment_photo_share__image);
    	Bitmap bmScaled = Bitmap.createScaledBitmap(imageBitmap, 100, 100, false);

    	// set the scaled image in the image view
    	photo.setImageBitmap(bmScaled);
    }

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fragment_photo_share__button_done:
			// get the image caption
			EditText caption = (EditText) findViewById(R.id.fragment_photo_share__caption);

			// get the image data ready for uploading via the API
	        PhotoUploadTask uploadTask = new PhotoUploadTask(event, caption.getText().toString(), imageBitmap);
	        uploadTask.execute();	
			break;

		default:
			break;
		}
		
	}
	
	private class PhotoUploadTask extends AsyncTask<Void, Void, Void> {

		private Event event;
		private String caption;
		private Bitmap photo;
		
		public PhotoUploadTask(Event event, String caption, Bitmap photo) {
			this.event = event;
			this.caption = caption;
			this.photo = photo;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			ProgressBar pb = (ProgressBar) findViewById(R.id.fragment_photo_share__progressBar);
			pb.setVisibility(View.VISIBLE);
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			// turn the bitmap into an input stream
			ByteArrayOutputStream baos = new ByteArrayOutputStream();  
	        photo.compress(Bitmap.CompressFormat.JPEG, 50, baos);
	        ByteArrayInputStream inStream = new ByteArrayInputStream(baos.toByteArray());
			
	        // upload via the API
	        try {
				PhotoResource photoRes = SnapClient.getInstance().build(PhotoResource.class);
				photoRes.postPhoto(inStream, event.getResourceUri(), "/private_v1/type/6/", caption);
	        } catch (org.codegist.crest.CRestException e) {
	        	Log.e(TAG, "problem with the response?", e);
	        }
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			ProgressBar pb = (ProgressBar) findViewById(R.id.fragment_photo_share__progressBar);
			pb.setVisibility(View.INVISIBLE);
			Log.d(TAG, "upload complete");

			// we finished uploading the photo, close the activity
			finish();
		}

	}
}