package ca.hashbrown.snapable.activities;

import android.content.ContentUris;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import ca.hashbrown.snapable.R;
import ca.hashbrown.snapable.provider.SnapableContract;
import com.crashlytics.android.Crashlytics;
import com.snapable.api.SnapImage;
import com.snapable.api.private_v1.Client;

import ca.hashbrown.snapable.api.SnapClient;
import ca.hashbrown.snapable.api.models.Event;
import ca.hashbrown.snapable.api.resources.PhotoResource;
import ca.hashbrown.snapable.utils.SnapBitmapFactory;
import retrofit.mime.TypedString;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PhotoUpload extends BaseActivity implements OnClickListener {

	private static final String TAG = "PhotoUpload";

	private Event event;
	private String imagePath;
    private Bitmap bmScaled;

	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_photo_upload);

        findViewById(R.id.fragment_photo_upload__button_done).setOnClickListener(this);

        // get the bundle from the saved state or try and get it from the intent
        Bundle bundle = null;
        if (savedInstanceState != null) {
            bundle = savedInstanceState.getBundle("bundle");
        } else {
            bundle = getIntent().getExtras();
        }
        event = bundle.getParcelable("event");
        imagePath = bundle.getString("imagePath");

    	// set the action bar title
    	getActionBar().setTitle(event.title);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // quick memory usage sanity check
        Log.d(TAG, String.format("Free: %,d B | Total: %,d B | Max: %,d B", Runtime.getRuntime().freeMemory(), Runtime.getRuntime().totalMemory(), Runtime.getRuntime().maxMemory()));

        // create a scaled bitmap
        Resources r = getResources();
        int dpSize = 275;
        int pxSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpSize, r.getDisplayMetrics());
        ImageView photo = (ImageView) findViewById(R.id.fragment_photo_upload__image);
        bmScaled = SnapBitmapFactory.decodeSampledBitmapFromPath(imagePath, pxSize, pxSize);

        // set the scaled image in the image view
        photo.setImageBitmap(bmScaled);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // create the save bundle
        Bundle save = new Bundle();
        save.putParcelable("event", this.event);
        save.putString("imagePath", this.imagePath);
        // and the bundle data to the saved state
        outState.putBundle("bundle", save);
    }

    @Override
    protected void onStop() {
        super.onStop();
        bmScaled.recycle();
        bmScaled = null;
        System.gc();
    }

    public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fragment_photo_upload__button_done:
			// get the image caption
			EditText caption = (EditText) findViewById(R.id.fragment_photo_upload__caption);

            if (SnapClient.getInstance().isReachable()) {
                // get the image data ready for uploading via the API
                PhotoUploadTask uploadTask = new PhotoUploadTask(event, caption.getText().toString(), imagePath);
                uploadTask.execute();
            } else {
                Toast.makeText(this, getString(R.string.api__unreachable), Toast.LENGTH_LONG).show();
            }
			break;

		default:
			break;
		}

	}

	private class PhotoUploadTask extends AsyncTask<Void, Void, Void> {

		private Event event;
		private String caption;
		private String photoPath;

        private String errorMsg;

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
			butt.setVisibility(View.GONE);
		}

		@Override
		protected Void doInBackground(Void... params) {
            try {
                SnapBitmapFactory.Options options = new SnapBitmapFactory.Options();
                options.inPurgeable = true;
                options.inTempStorage = new byte[32 * 1024]; // 32KB of temp decoding storage
                // original photo to upload
                Bitmap photo = SnapBitmapFactory.decodeFile(photoPath, options);
                Crashlytics.log(Log.DEBUG, TAG, "ByteCount of photo: " + photo.getByteCount());
                ExifInterface exif = new ExifInterface(photoPath);
                int exifRotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

                // turn the bitmap into a temp compressed file
                FileOutputStream tmpout = new FileOutputStream(photoPath + ".tmp");
                photo.compress(Bitmap.CompressFormat.JPEG, 50, tmpout);
                tmpout.close();
                // make sure memory is released
                photo.recycle();
                photo = null;
                System.gc();
                Log.d(TAG, "Created temp file");

                // re-apply the exif rotation
                ExifInterface exifComp = new ExifInterface(photoPath + ".tmp");
                exifComp.setAttribute(ExifInterface.TAG_ORIENTATION, String.valueOf(exifRotation));
                exifComp.saveAttributes();
                Log.d(TAG, "Re-applied exif data to temp file");

                // decode temp file
                File tempFile = new File(photoPath + ".tmp");
                SnapImage tempImage = new SnapImage(tempFile);

                // get local cached event info
                Uri queryUri = ContentUris.withAppendedId(SnapableContract.EventCredentials.CONTENT_URI, event.getPk());
                Cursor c = getContentResolver().query(queryUri, null, null, null, null);

	            // upload via the API
                Client client = SnapClient.getInstance();
                PhotoResource photoRes = client.getRestAdapter().create(PhotoResource.class);

	        	// if we have a guest id, upload the photo with the id
	        	if (c.moveToFirst()) {
	        		long guest_id = c.getLong(c.getColumnIndex(SnapableContract.EventCredentials.GUEST_ID));
                    if(guest_id > 0) {
                        photoRes.postPhoto(tempImage, new TypedString(event.resource_uri), new TypedString("/"+ client.VERSION +"/guest/"+guest_id+"/"), new TypedString(caption));
	        		} else {
                        photoRes.postPhoto(tempImage, new TypedString(event.resource_uri), new TypedString(caption));
                    }
	        	} else {
	        		photoRes.postPhoto(tempImage, new TypedString(event.resource_uri), new TypedString(caption));
				}
	        } catch(FileNotFoundException e) {
                Log.e(TAG, "problem finding a file", e);
                Crashlytics.logException(e);
                errorMsg = "There was a problem uploading the photo.";
            } catch (IOException e) {
                Log.e(TAG, "some IO exception", e);
                errorMsg = "There was a problem uploading the photo.";
            } catch (OutOfMemoryError e) {
                Log.e(TAG, "We ran out of memory!", e);
                Crashlytics.log(String.format("Free: %,d B | Total: %,d B | Max: %,d B", Runtime.getRuntime().freeMemory(), Runtime.getRuntime().totalMemory(), Runtime.getRuntime().maxMemory()));
                Crashlytics.logException(e);
                errorMsg = getString(R.string.api__unable_to_upload);
            } finally {
                Log.d(TAG, "delete temp file");
                File tmpFile = new File(photoPath + ".tmp");
                tmpFile.delete();
            }

            // return nothing
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			// if there is an error set, display it to the user
			if (errorMsg != null && errorMsg.length() > 0) {
				Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
			}

			// stop the progress bar
			ProgressBar pb = (ProgressBar) findViewById(R.id.fragment_photo_upload__progressBar);
			pb.setVisibility(View.GONE);
			Log.d(TAG, "upload complete");

            // Go back to the photo list when we are done uploading.
            Intent parentActivityIntent = new Intent(getApplicationContext(), EventPhotoList.class);
            parentActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            parentActivityIntent.putExtra("event", event);
            startActivity(parentActivityIntent);
            finish();
		}

	}
}