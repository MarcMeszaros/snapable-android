package ca.hashbrown.snapable.utils;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

public class SnapSurfaceView extends android.view.SurfaceView implements SurfaceHolder.Callback, AutoFocusCallback {

	private static final String TAG = "SnapSurfaceView";

	private Camera camera;
	private Parameters cameraParams;

	public SnapSurfaceView(Context context) {
		super(context);

		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		SurfaceHolder holder = this.getHolder();
		holder.addCallback(this);

		// deprecated setting, but required on Android versions prior to 3.0
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,	int height) {
		Log.d(TAG, "surfaceChanged");
		// get camera info
		CameraInfo info = new CameraInfo();
		Camera.getCameraInfo(CameraInfo.CAMERA_FACING_BACK, info);
		Log.d(TAG, "camera orientation: " + info.orientation);
		Log.d(TAG, "camera orientation fixed: " + ((getRotation(true) + info.orientation) % 360));

		// set rotation as necessary
		cameraParams.setRotation((getRotation(true) + info.orientation) % 360);
		camera.setParameters(cameraParams);

		camera.setDisplayOrientation((info.orientation - getRotation(false) + 360) % 360);

		// IMPORTANT: We must call startPreview() on the camera before we take any pictures
		camera.startPreview();

		// setup he auto focus
		camera.autoFocus(this);
	}

	public void surfaceCreated(SurfaceHolder holder) {
		Log.d(TAG, "surfaceCreated");
		try {
			// get camera info
			CameraInfo info = new CameraInfo();
			Camera.getCameraInfo(CameraInfo.CAMERA_FACING_BACK, info);

			// Open the Camera in preview mode
			camera = Camera.open();

			// set some camera parameters
			cameraParams = camera.getParameters();
			List<Camera.Size> cameraSizes = cameraParams.getSupportedPictureSizes();
			List<Camera.Size> cameraPreviewSizes = cameraParams.getSupportedPreviewSizes();
			cameraParams.setJpegQuality(100);
			cameraParams.setPictureSize(cameraSizes.get(0).width, cameraSizes.get(0).height);
			cameraParams.setPreviewSize(cameraPreviewSizes.get(0).width, cameraPreviewSizes.get(0).height);
			camera.setParameters(cameraParams);
			for (Size size : cameraSizes) {	
				Log.d(TAG, "camera size: " + size.width + "x" + size.height);
			}
			for (Size size : cameraPreviewSizes) {	
				Log.d(TAG, "camera preview size: " + size.width + "x" + size.height);
			}

			// set the surface preview
			camera.setPreviewDisplay(holder);
		} catch (IOException e) {
			Log.e(TAG, "problem setting up the surface view", e);
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d(TAG, "surfaceDestroyed");
		// Surface will be destroyed when replaced with a new screen
		// Always make sure to release the Camera instance
		camera.stopPreview();
		camera.release();
		camera = null;
	}

	public void takePicture(PictureCallback pictureCallback) {
		camera.takePicture(null, null, pictureCallback);
	}

	/**
	 * 
	 * @param reversed true = clockwise vs false = counterclockwise
	 * @return
	 */
	private int getRotation(boolean reversed) {
		Activity activity = (Activity) getContext();
		int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();

		int degrees = 0;
		switch (rotation) {
		case Surface.ROTATION_0:
			degrees = 0;
			break;
		case Surface.ROTATION_90:
			degrees = (reversed) ? 270 : 90;
			break;
		case Surface.ROTATION_180:
			degrees = 180;
			break;
		case Surface.ROTATION_270:
			degrees = (reversed) ? 90 : 270;
			break;
		}

		Log.d(TAG, "getRotation: " + degrees);
		return degrees;
	}

	@Override
	public void onAutoFocus(boolean success, Camera camera) {
		// TODO Auto-generated method stub
		
	}

}
