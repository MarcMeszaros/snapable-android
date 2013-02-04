package ca.hashbrown.snapable.activities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.snapable.api.models.Event;

import ca.hashbrown.snapable.R;
import ca.hashbrown.snapable.activities.PhotoUpload;
import ca.hashbrown.snapable.utils.SnapStorage;
import ca.hashbrown.snapable.utils.SnapSurfaceView;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

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

		// grab shutter button so we can reference it later
		shutterButton = (Button) findViewById(R.id.activity_camera__shutter_button);
		shutterButton.setOnClickListener(this);
		findViewById(R.id.activity_camera__image_picker).setOnClickListener(this);

		// get the display size
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);

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

}
