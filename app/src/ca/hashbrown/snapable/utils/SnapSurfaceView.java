package ca.hashbrown.snapable.utils;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.util.Log;
import android.view.SurfaceHolder;

public class SnapSurfaceView extends android.view.SurfaceView implements SurfaceHolder.Callback {

	private static final String TAG = "SnapSurfaceView";

	private Camera camera;

	public SnapSurfaceView(Context context) {
		super(context);

		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		SurfaceHolder holder = this.getHolder();
		holder.addCallback(this);

		// deprecated setting, but required on Android versions prior to 3.0
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		camera.setDisplayOrientation(90);

		// get camera info
		CameraInfo info = new CameraInfo();
		Camera.getCameraInfo(CameraInfo.CAMERA_FACING_BACK, info);
				
		// IMPORTANT: We must call startPreview() on the camera before we take
		// any pictures
		camera.startPreview();
	}

	public void surfaceCreated(SurfaceHolder holder) {
		try {
			// Open the Camera in preview mode
			camera = Camera.open();
			
			// set some camera parameters
			Parameters params = camera.getParameters();
			List<Camera.Size> sizes = params.getSupportedPictureSizes();
			params.setJpegQuality(100);
			params.setPictureSize(sizes.get(0).width, sizes.get(0).height);
			camera.setParameters(params);

			// set the surface preview
			camera.setPreviewDisplay(holder);
		} catch (IOException e) {
			Log.e(TAG, "problem setting up the surface view", e);
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// Surface will be destroyed when replaced with a new screen
		// Always make sure to release the Camera instance
		camera.stopPreview();
		camera.release();
		camera = null;
	}

	public void takePicture(PictureCallback pictureCallback) {
		camera.takePicture(null, null, pictureCallback);
	}

}
