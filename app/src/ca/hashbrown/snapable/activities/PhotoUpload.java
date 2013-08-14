package ca.hashbrown.snapable.activities;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import ca.hashbrown.snapable.R;
import ca.hashbrown.snapable.provider.SnapableContract;
import com.snapable.api.SnapApi;
import com.snapable.api.SnapClient;
import com.snapable.api.models.Event;
import com.snapable.api.resources.PhotoResource;

import java.io.*;

public class PhotoUpload extends BaseFragmentActivity implements OnClickListener {

	private static final String TAG = "PhotoUpload";

	private Event event;
	private String imagePath;

	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_photo_upload);

        findViewById(R.id.fragment_photo_upload__button_done).setOnClickListener(this);

    	// get the extra bundle data
    	Bundle bundle = getIntent().getExtras();
    	event = bundle.getParcelable("event");
		imagePath = bundle.getString("imagePath");

        // create a scaled bitmap
		ImageView photo = (ImageView) findViewById(R.id.fragment_photo_upload__image);
    	Bitmap bmScaled = PhotoUpload.decodeSampledBitmapFromPath(bundle.getString("imagePath"), 300, 300);

        try {
            // get exif data
            ExifInterface exif = new ExifInterface(imagePath);
            int exifRotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

            // rotate bitmap
            switch (exifRotation) {
                case ExifInterface.ORIENTATION_ROTATE_90: {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(90);
                    bmScaled = Bitmap.createBitmap(bmScaled, 0, 0, bmScaled.getWidth(), bmScaled.getHeight(), matrix, true);
                    break;
                }
                case ExifInterface.ORIENTATION_ROTATE_180:{
                    Matrix matrix = new Matrix();
                    matrix.postRotate(180);
                    bmScaled = Bitmap.createBitmap(bmScaled, 0, 0, bmScaled.getWidth(), bmScaled.getHeight(), matrix, true);
                    break;
                }
                case ExifInterface.ORIENTATION_ROTATE_270: {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(270);
                    bmScaled = Bitmap.createBitmap(bmScaled, 0, 0, bmScaled.getWidth(), bmScaled.getHeight(), matrix, true);
                    break;
                }
            }
        } catch (IOException e) {
            // TODO log here
        }
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

    //@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    protected int sizeOf(Bitmap data) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
            return data.getRowBytes() * data.getHeight();
        } else {
            return data.getByteCount();
        }
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
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPurgeable = true;
                Bitmap photo = BitmapFactory.decodeFile(photoPath, options);
                Log.d(TAG, "size of photo: " + sizeOf(photo));
                ExifInterface exif = new ExifInterface(photoPath);
                int exifRotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

                // turn the bitmap into a temp compressed file
                FileOutputStream tmpout = new FileOutputStream(photoPath + ".tmp");
                photo.compress(Bitmap.CompressFormat.JPEG, 50, tmpout);
                tmpout.close();
                // make sure memory is released
                photo.recycle();
                photo = null;

                // re-apply the exif rotation
                ExifInterface exitComp = new ExifInterface(photoPath + ".tmp");
                exitComp.setAttribute(ExifInterface.TAG_ORIENTATION, "");
                exitComp.saveAttributes();

                // decode temp file
                File tempFile = new File(photoPath + ".tmp");
                FileInputStream inStream = new FileInputStream(tempFile);

                // get local cached event info
                Uri queryUri = ContentUris.withAppendedId(SnapableContract.EventCredentials.CONTENT_URI, event.getId());
                Cursor c = getContentResolver().query(queryUri, null, null, null, null);

	            // upload via the API
	            PhotoResource photoRes = SnapClient.getInstance().build(PhotoResource.class);

	        	// if we have a guest id, upload the photo with the id
	        	if (c.moveToFirst()) {
	        		long guest_id = c.getLong(c.getColumnIndex(SnapableContract.EventCredentials.GUEST_ID));
	        		if(guest_id > 0) {
	        			photoRes.postPhoto(inStream, event.getResourceUri(), "/"+SnapApi.api_version +"/guest/"+guest_id+"/", caption);
	        		} else {
                        photoRes.postPhoto(inStream, event.getResourceUri(), caption);
                    }
	        	} else {
	        		photoRes.postPhoto(inStream, event.getResourceUri(), caption);
				}
	        } catch (org.codegist.crest.CRestException e) {
	        	Log.e(TAG, "problem with the response?", e);
                //Toast.makeText(getApplicationContext(), "There was a problem uploading the photo.", Toast.LENGTH_LONG).show();
	        } catch(FileNotFoundException e) {
                Log.e(TAG, "problem finding a file", e);
                Toast.makeText(getApplicationContext(), "There was a problem uploading the photo.", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Log.e(TAG, "some IO exception", e);
                Toast.makeText(getApplicationContext(), "There was a problem uploading the photo.", Toast.LENGTH_LONG).show();
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
			ProgressBar pb = (ProgressBar) findViewById(R.id.fragment_photo_upload__progressBar);
			pb.setVisibility(View.INVISIBLE);
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