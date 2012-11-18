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
			EditText caption = (EditText) findViewById(R.id.fragment_photo_share__caption);
	        PhotoUloadTask uploadTask = new PhotoUloadTask(event, caption.getText().toString(), imageBitmap);
	        uploadTask.execute();	
			break;

		default:
			break;
		}
		
	}
	
	private class PhotoUloadTask extends AsyncTask<Void, Void, Void> {

		private Event event;
		private String caption;
		private Bitmap photo;
		
		public PhotoUloadTask(Event event, String caption, Bitmap photo) {
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
			//pb.setVisibility(View.VISIBLE);
			
			this.photo = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
			
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
		}

	}
}