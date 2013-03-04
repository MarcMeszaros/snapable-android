package ca.hashbrown.snapable.activities;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.InputStream;

import ca.hashbrown.snapable.R;
import ca.hashbrown.snapable.provider.SnapableContract;
import ca.hashbrown.snapable.utils.SnapBitmapFactory;
import ca.hashbrown.snapable.utils.SnapStorage;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import com.snapable.api.SnapApi;
import com.snapable.api.SnapClient;
import com.snapable.api.models.Event;
import com.snapable.api.resources.PhotoResource;

import android.content.ContentUris;
import android.database.Cursor;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class PhotoUpload extends SherlockFragmentActivity implements OnClickListener {

	private static final String TAG = "PhotoUpload";
	
	private Event event;
	//private Bitmap imageBitmap;
	private String imagePath;

	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_photo_upload);
    	
    	findViewById(R.id.fragment_photo_upload__button_done).setOnClickListener(this);
    	
    	// get the extra bundle data
    	Bundle bundle = getIntent().getExtras();
    	event = bundle.getParcelable("event");
		//imageBitmap = BitmapFactory.decodeFile(bundle.getString("imagePath"));
    	imagePath = bundle.getString("imagePath");

		// create a scaled bitmap
		ImageView photo = (ImageView) findViewById(R.id.fragment_photo_upload__image);
    	//Bitmap bmScaled = Bitmap.createScaledBitmap(imageBitmap, 150, 150, false);
    	Bitmap bmScaled = PhotoUpload.decodeSampledBitmapFromPath(bundle.getString("imagePath"), 150, 150);

    	// set the scaled image in the image view
    	photo.setImageBitmap(bmScaled);
    	
    	// set the action bar title
    	getSupportActionBar().setTitle(event.getTitle());
    }

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fragment_photo_upload__button_done:
			// get the image caption
			EditText caption = (EditText) findViewById(R.id.fragment_photo_upload__caption);

			// get the image data ready for uploading via the API
	        PhotoUploadTask uploadTask = new PhotoUploadTask(event, caption.getText().toString(), imagePath);
	        uploadTask.execute();	
			break;

		default:
			break;
		}
		
	}
	
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;
	
	    if (height > reqHeight || width > reqWidth) {
	
	        // Calculate ratios of height and width to requested height and width
	        final int heightRatio = Math.round((float) height / (float) reqHeight);
	        final int widthRatio = Math.round((float) width / (float) reqWidth);
	
	        // Choose the smallest ratio as inSampleSize value, this will guarantee
	        // a final image with both dimensions larger than or equal to the
	        // requested height and width.
	        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
	    }
	
	    return inSampleSize;
	}
	
	public static Bitmap decodeSampledBitmapFromPath(String path, int reqWidth, int reqHeight) {

	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(path, options);

	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeFile(path, options);
	}
	
	private class PhotoUploadTask extends AsyncTask<Void, Void, Void> {

		private Event event;
		private String caption;
		private String photoPath;
		
		public PhotoUploadTask(Event event, String caption, String photoPath) {
			this.event = event;
			this.caption = caption;
			this.photoPath = photoPath;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			ProgressBar pb = (ProgressBar) findViewById(R.id.fragment_photo_upload__progressBar);
			Button butt = (Button) findViewById(R.id.fragment_photo_upload__button_done);
			pb.setVisibility(View.VISIBLE);
			butt.setVisibility(View.INVISIBLE);
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			Bitmap photo = BitmapFactory.decodeFile(photoPath);
			
			// turn the bitmap into an input stream
			ByteArrayOutputStream baos = new ByteArrayOutputStream();  
	        photo.compress(Bitmap.CompressFormat.JPEG, 50, baos);
	        ByteArrayInputStream inStream = new ByteArrayInputStream(baos.toByteArray());
	        
	        // get local cached event info
	        Uri queryUri = ContentUris.withAppendedId(SnapableContract.EventCredentials.CONTENT_URI, event.getId());
	        Cursor c = getContentResolver().query(queryUri, null, null, null, null);
	        
	        // upload via the API
	        try {
	        	PhotoResource photoRes = SnapClient.getInstance().build(PhotoResource.class);
	        	
	        	// if we have a guest id, upload the photo with the id
	        	if (c.moveToFirst()) {
	        		long guest_id = c.getLong(c.getColumnIndex(SnapableContract.EventCredentials.GUEST_ID));
	        		long type_id = c.getLong(c.getColumnIndex(SnapableContract.EventCredentials.TYPE_ID));
	        		if(guest_id > 0 && type_id > 0) {
	        			photoRes.postPhoto(inStream, event.getResourceUri(), "/"+SnapApi.api_version +"/guest/"+guest_id+"/", "/"+SnapApi.api_version +"/type/"+type_id+"/", caption);
	        		} else {
	        			photoRes.postPhoto(inStream, event.getResourceUri(), "/"+SnapApi.api_version +"/type/6/", caption);
	        		}
	        	} else {
	        		photoRes.postPhoto(inStream, event.getResourceUri(), "/"+SnapApi.api_version +"/type/6/", caption);
				}
	        } catch (org.codegist.crest.CRestException e) {
	        	Log.e(TAG, "problem with the response?", e);
	        } finally {
	        	// make sure memory is released
	        	photo.recycle();
	        	photo = null;
	        }
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			ProgressBar pb = (ProgressBar) findViewById(R.id.fragment_photo_upload__progressBar);
			pb.setVisibility(View.INVISIBLE);
			Log.d(TAG, "upload complete");

			// we finished uploading the photo, close the activity
			finish();
		}

	}
}