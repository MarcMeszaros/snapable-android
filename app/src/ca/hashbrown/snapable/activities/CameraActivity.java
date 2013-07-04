package ca.hashbrown.snapable.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.*;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore.Images.ImageColumns;
import android.util.FloatMath;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import ca.hashbrown.snapable.R;
import ca.hashbrown.snapable.utils.SnapStorage;
import ca.hashbrown.snapable.utils.SnapSurfaceView;
import ca.hashbrown.snapable.utils.SnapSurfaceView.OnCameraReadyListener;
import com.snapable.api.models.Event;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CameraActivity extends BaseActivity implements OnClickListener, PictureCallback, OnCameraReadyListener {

	private static final String TAG = "CameraActivity";

	private Event event;
	private SnapSurfaceView cameraSurfaceView;
	private Button shutterButton;
    private ProgressBar cameraProgress;
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
        cameraProgress = (ProgressBar) findViewById(R.id.activity_camera__progressBar);
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

    /**
     * Setup some stuff after resume.
     */
    protected void onResume() {
        super.onResume();
        // Re-enable the shutter button so that we can take another picture
        cameraProgress.setVisibility(View.GONE);
        shutterButton.setVisibility(View.VISIBLE);
        shutterButton.setEnabled(true);
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
        CropTask cropTask = new CropTask(this, data, camera);
        cropTask.execute();
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.activity_camera__shutter_button:
			shutterButton.setEnabled(false);
            shutterButton.setVisibility(View.GONE);
            cameraProgress.setVisibility(View.VISIBLE);
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
	 * Set the new flash mode for the camera and updates the UI accordingly.
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
	 * @param newMode the camera mode the background needs to be set too
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

    /**
     * Custom class that crops the image after being taken. It then initializes the
     * photo uploading activity.
     *
     * A lot of code taken from (CropImage.java):
     * https://android.googlesource.com/platform/packages/apps/Gallery2/+/5e86fd1570b943e02d8435b2ce27a1806b72ee2c
     */
    private class CropTask extends AsyncTask<Void, Void, Void> {

        private static final int MAX_PIXEL_COUNT = 5 * 1000000; // 5M pixels
        private static final int TILE_SIZE = 512;


        private Context context;
        private byte[] data;
        private Camera camera;

        public CropTask(Context context, byte[] data, Camera camera) {
            this.context = context;
            this.data = data;
            this.camera = camera;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                File filename = SnapStorage.getOutputMediaFile(SnapStorage.MEDIA_TYPE_IMAGE);
                FileOutputStream out = new FileOutputStream(filename);
                out.write(data, 0, data.length);
                out.close();

                // alert the media scanner of new file
                MediaScannerConnection.scanFile(context, new String[]{filename.getAbsolutePath()}, null, null); // tell the system to scan the image

                // pass all the data to the photo upload activity
                Intent upload = new Intent(context, PhotoUpload.class);
                upload.putExtra("event", event);
                upload.putExtra("imagePath", filename.getAbsolutePath());
                startActivity(upload);

            } catch (FileNotFoundException e) {
                Log.e(TAG, "file not found", e);
            } catch (IOException e) {
                Log.e(TAG, "there was a problem with a r/w operation", e);
            }

            // return nothing
            return null;
        }


        /*
         * Compute the sample size as a function of minSideLength
         * and maxNumOfPixels.
         * minSideLength is used to specify that minimal width or height of a
         * bitmap.
         * maxNumOfPixels is used to specify the maximal size in pixels that is
         * tolerable in terms of memory usage.
         *
         * The function returns a sample size based on the constraints.
         * Both size and minSideLength can be passed in as UNCONSTRAINED,
         * which indicates no care of the corresponding constraint.
         * The functions prefers returning a sample size that
         * generates a smaller bitmap, unless minSideLength = UNCONSTRAINED.
         *
         * Also, the function rounds up the sample size to a power of 2 or multiple
         * of 8 because BitmapFactory only honors sample size this way.
         * For example, BitmapFactory downsamples an image by 2 even though the
         * request is 3. So we round up the sample size to avoid OOM.
         */
        private int computeSampleSize(int width, int height, int minSideLength, int maxNumOfPixels) {
            int initialSize = computeInitialSampleSize(width, height, minSideLength, maxNumOfPixels);

            return (initialSize <= 8) ? nextPowerOf2(initialSize) : (initialSize + 7) / 8 * 8;
        }

        private int computeInitialSampleSize(int w, int h, int minSideLength, int maxNumOfPixels) {
            if (maxNumOfPixels == -1 && minSideLength == -1) return 1;

            int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) FloatMath.ceil(FloatMath.sqrt((float) (w * h) / maxNumOfPixels));

            if (minSideLength == -1) {
                return lowerBound;
            } else {
                int sampleSize = Math.min(w / minSideLength, h / minSideLength);
                return Math.max(sampleSize, lowerBound);
            }
        }

        // Find the min x that 1 / x >= scale
        public int computeSampleSizeLarger(float scale) {
            int initialSize = (int) FloatMath.floor(1f / scale);
            if (initialSize <= 1) return 1;

            return (initialSize <= 8) ? prevPowerOf2(initialSize) : initialSize / 8 * 8;
        }

        // Returns the next power of two.
        // Returns the input if it is already power of 2.
        // Throws IllegalArgumentException if the input is <= 0 or
        // the answer overflows.
        public int nextPowerOf2(int n) {
            if (n <= 0 || n > (1 << 30)) throw new IllegalArgumentException("n is invalid: " + n);
            n -= 1;
            n |= n >> 16;
            n |= n >> 8;
            n |= n >> 4;
            n |= n >> 2;
            n |= n >> 1;
            return n + 1;
        }

        // Returns the previous power of two.
        // Returns the input if it is already power of 2.
        // Throws IllegalArgumentException if the input is <= 0
        public int prevPowerOf2(int n) {
            if (n <= 0) throw new IllegalArgumentException();
            return Integer.highestOneBit(n);
        }

        private void drawInTiles(Canvas canvas, BitmapRegionDecoder decoder, Rect rect, Rect dest, int sample) {
            int tileSize = TILE_SIZE * sample;
            Rect tileRect = new Rect();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            options.inSampleSize = sample;
            canvas.translate(dest.left, dest.top);
            canvas.scale((float) sample * dest.width() / rect.width(), (float) sample * dest.height() / rect.height());
            Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
            for (int tx = rect.left, x = 0; tx < rect.right; tx += tileSize, x += TILE_SIZE) {
                for (int ty = rect.top, y = 0; ty < rect.bottom; ty += tileSize, y += TILE_SIZE) {
                    tileRect.set(tx, ty, tx + tileSize, ty + tileSize);
                    if (tileRect.intersect(rect)) {
                        Bitmap bitmap;

                        // To prevent concurrent access in GLThread
                        synchronized (decoder) {
                            bitmap = decoder.decodeRegion(tileRect, options);
                        }
                        canvas.drawBitmap(bitmap, x, y, paint);
                        bitmap.recycle();
                    }
                }
            }
        }
    }

}
