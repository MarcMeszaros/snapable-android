package ca.hashbrown.snapable.activities;

import ca.hashbrown.snapable.PhotoShare;
import ca.hashbrown.snapable.R;
import ca.hashbrown.snapable.utils.SnapSurfaceView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;

public class CameraActivity extends Activity implements OnClickListener, PictureCallback {

	private static final String TAG = "CameraActivity";

	private SnapSurfaceView cameraSurfaceView;
	private Button shutterButton;
	private Bitmap bitmap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);

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
		
		bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

		Intent upload = new Intent(this, PhotoShare.class);
		//upload.putExtra("event", event);
		upload.putExtra("imageBitmap", bitmap);
		startActivity(upload);
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
