package ca.hashbrown.snapable.activities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.google.analytics.tracking.android.EasyTracker;
import com.snapable.api.models.Event;

import ca.hashbrown.snapable.R;
import ca.hashbrown.snapable.activities.PhotoUpload;
import ca.hashbrown.snapable.utils.SnapStorage;
import ca.hashbrown.snapable.utils.SnapSurfaceView;
import ca.hashbrown.snapable.utils.SnapSurfaceView.OnCameraReadyListener;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.provider.MediaStore.Images.ImageColumns;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class CameraActivity extends Activity implements OnClickListener, PictureCallback, OnCameraReadyListener {

	private static final String TAG = "CameraActivity";

	private Event event;
	private SnapSurfaceView cameraSurfaceView;
	private Button shutterButton;
	private Bitmap bitmap;
	private String lastFlashMode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);

		// get the extra bundle data for the fragment
    	Bundle bundle = getIntent().getExtras();
		event = bundle.getParcelable("event");

		// set up our preview surface
		FrameLayout preview = (FrameLayout) findViewById(R.id.activity_camera__preview);
		cameraSurfaceView = new SnapSurfaceView(this);
		cameraSurfaceView.setOnCameraReadyListener(this);
		preview.addView(cameraSurfaceView);

		// grab shutter button so we can reference it later
		shutterButton = (Button) findViewById(R.id.activity_camera__shutter_button);
		shutterButton.setOnClickListener(this);
		findViewById(R.id.activity_camera__image_picker).setOnClickListener(this);
		findViewById(R.id.activity_camera__flash_mode).setOnClickListener(this);

		// get the display size
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		try {
	        display.getSize(size); // newer devices
	    } catch (java.lang.NoSuchMethodError e) { // Older device
	        size.x = display.getWidth();
	        size.y = display.getHeight();
	    }

		// if width < height
		Log.d(TAG, "x,y: " + size.x + "," + size.y);
		LinearLayout overlay = (LinearLayout) findViewById(R.id.activity_camera__overlay);
		RelativeLayout first = (RelativeLayout) findViewById(R.id.activity_camera__first_blackbar);
		RelativeLayout second = (RelativeLayout) findViewById(R.id.activity_camera__second_blackbar);
		RelativeLayout transparent = (RelativeLayout) findViewById(R.id.activity_camera__transparent_bar);

		// calculate the weights
		float transparentWeight = 0.0f;
		float blackBarWeight = 0.0f;
		if (size.x < size.y) {
			transparentWeight = (float)size.x / (float)size.y;
			blackBarWeight = (1.0f - transparentWeight) / 2;
		} else {
			transparentWeight = (float)size.y / (float)size.x;
			blackBarWeight = (1.0f - transparentWeight) / 2;
		}

		// set the layout weight
		((LinearLayout.LayoutParams)first.getLayoutParams()).weight = blackBarWeight;
		((LinearLayout.LayoutParams)second.getLayoutParams()).weight = blackBarWeight;
		((LinearLayout.LayoutParams)transparent.getLayoutParams()).weight = transparentWeight;
		overlay.requestLayout();
	}

	@Override
	protected void onStart() {
		super.onStart();
		EasyTracker.getInstance().activityStart(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		EasyTracker.getInstance().activityStop(this);
	}

	/**
	 * Save the "lastFlashMode" so it persists when the user returns from the upload activity.
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString("lastFlashMode", lastFlashMode);
		super.onSaveInstanceState(outState);
	}

	/**
	 * When restoring the activity state, check if there is a "lastFlashMode" set in the bundle.
	 */
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		lastFlashMode = savedInstanceState.getString("lastFlashMode");
		super.onRestoreInstanceState(savedInstanceState);
	}

	public void onPictureTaken(byte[] data, Camera camera) {
		Log.d(TAG, "picture taken");
		// Restart the preview and re-enable the shutter button so that we can take another picture
		camera.startPreview();
		shutterButton.setEnabled(true);

		try {
			// save the image
			bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
			// tweak the bitmap so it's square before saving
			if (bitmap.getWidth() > bitmap.getHeight()) {
				int x = (bitmap.getWidth() - bitmap.getHeight()) / 2;
				int y = 0;
				bitmap = Bitmap.createBitmap(bitmap, x, y, bitmap.getHeight(), bitmap.getHeight());
			} else {
				int x = 0;
				int y = (bitmap.getHeight() - bitmap.getWidth()) / 2;
				bitmap = Bitmap.createBitmap(bitmap, x, y, bitmap.getWidth(), bitmap.getWidth());
			}

			// save the file to storage
			File filename = SnapStorage.getOutputMediaFile(SnapStorage.MEDIA_TYPE_IMAGE);
			FileOutputStream out = new FileOutputStream(filename);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			MediaScannerConnection.scanFile(this, new String[]{filename.getAbsolutePath()}, null, null); // tell the system to scan the image

			// pass all the data to the photo upload activity
			Intent upload = new Intent(this, PhotoUpload.class);
			upload.putExtra("event", event);
			upload.putExtra("imagePath", filename.getAbsolutePath());
			startActivity(upload);
			
		} catch (FileNotFoundException e) {
			Log.e(TAG, "file not found", e);
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.activity_camera__shutter_button:
			shutterButton.setEnabled(false);
			cameraSurfaceView.takePicture(this);
			break;

		case R.id.activity_camera__image_picker:
			Intent intent = new Intent(Intent.ACTION_PICK);
			intent.setType("image/jpeg");
			
			startActivityForResult(intent, 0); // TODO the code shouldn't be hardcoded
			break;
		
		case R.id.activity_camera__flash_mode:
			toggleFlashMode();
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 0) { // TODO code shouldn't be hardcoded
			  if (data != null) {
				  Cursor cursor = getContentResolver().query(data.getData(), null, null, null, null);
				  cursor.moveToFirst();  //if not doing this, 01-22 19:17:04.564: ERROR/AndroidRuntime(26264): Caused by: android.database.CursorIndexOutOfBoundsException: Index -1 requested, with a size of 1
				  int idx = cursor.getColumnIndex(ImageColumns.DATA);
				  String fileSrc = cursor.getString(idx);
				  
				  // pass all the data to the photo upload activity
				  Intent upload = new Intent(this, PhotoUpload.class);
				  upload.putExtra("event", event);
				  upload.putExtra("imagePath", fileSrc);
				  startActivity(upload);
			  }
		}

	}

	/**
	 * Set the new flash mode for the camera and updates the UI accordinggly.
	 * 
	 * @param newMode the new flash mode for the camera
	 */
	private void setFlashMode(String newMode) {
		// get current flash mode
		String mode = cameraSurfaceView.getFlashMode();
		cameraSurfaceView.setFlashMode(newMode);
		setFlashModeButton(newMode);
		lastFlashMode = newMode;
	}

	/**
	 * Toggle the flash mode (if possible);
	 */
	private void toggleFlashMode() {
		// get current flash mode
		String mode = cameraSurfaceView.getFlashMode();

		if (mode != null) {
			Log.d(TAG, "toggle flash(current): " + mode);
			if (mode.equals(Camera.Parameters.FLASH_MODE_AUTO)) {
				setFlashMode(Camera.Parameters.FLASH_MODE_ON);
			} else if (mode.equals(Camera.Parameters.FLASH_MODE_ON)) {
				setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
			} else {
				setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
			}
		}
	}

	/**
	 * Set the flash mode button background.
	 * 
	 * @param newMode the camera mode the backgroud need to be set too
	 */
	private void setFlashModeButton(String newMode) {
		ImageButton flashButton = (ImageButton) findViewById(R.id.activity_camera__flash_mode);
		
		// if the flash mode isn't null, set it, otherwise hide the button
		if (newMode != null) {
			cameraSurfaceView.setFlashMode(newMode);
			if (newMode.equals(Camera.Parameters.FLASH_MODE_AUTO)) {
				flashButton.setImageResource(R.drawable.button__flash_mode__auto);
			} else if (newMode.equals(Camera.Parameters.FLASH_MODE_ON)) {
				flashButton.setImageResource(R.drawable.button__flash_mode__on);
			} else {
				flashButton.setImageResource(R.drawable.button__flash_mode__off);
			}
		} else {
			flashButton.setVisibility(View.GONE);
		}
	}

	/**
	 * This is called when the camera is ready. Any last minute configurations should be made here.
	 */
	@Override
	public void onCameraReady(Parameters params) {
		if (lastFlashMode == null) {
			setFlashMode(params.getFlashMode());
		} else {
			setFlashMode(lastFlashMode);
		}
	}

}
