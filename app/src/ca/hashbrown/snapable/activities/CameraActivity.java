package ca.hashbrown.snapable.activities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.snapable.api.models.Event;

import ca.hashbrown.snapable.PhotoShare;
import ca.hashbrown.snapable.R;
import ca.hashbrown.snapable.utils.SnapStorage;
import ca.hashbrown.snapable.utils.SnapSurfaceView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;

public class CameraActivity extends Activity implements OnClickListener, PictureCallback {

	private static final String TAG = "CameraActivity";

	private Event event;
	private SnapSurfaceView cameraSurfaceView;
	private Button shutterButton;
	private Bitmap bitmap;
	
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
		preview.addView(cameraSurfaceView);

		// grab out shutter button so we can reference it later
		shutterButton = (Button) findViewById(R.id.activity_camera__shutter_button);
		shutterButton.setOnClickListener(this);
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
			Intent upload = new Intent(this, PhotoShare.class);
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

		default:
			break;
		}
	}

}
