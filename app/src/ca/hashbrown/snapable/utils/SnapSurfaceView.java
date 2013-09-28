package ca.hashbrown.snapable.utils;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.*;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.widget.Toast;
import com.crashlytics.android.Crashlytics;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SnapSurfaceView extends android.view.SurfaceView implements SurfaceHolder.Callback, AutoFocusCallback {

	private static final String TAG = "SnapSurfaceView";

	private int cameraId;
	private Camera camera;
	private Parameters cameraParams;
	private OnCameraReadyListener cameraReadyListener;

	/**
	 * An interface for various events that are fired when the surfaceview/camera are in different
	 * states.
	 */
	public interface OnCameraReadyListener {
		public void onCameraReady(Camera.Parameters params);
	}

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
		Camera.getCameraInfo(cameraId, info);
		Log.d(TAG, "camera orientation: " + info.orientation);
		Log.d(TAG, "camera orientation fixed: " + ((getRotation(true) + info.orientation) % 360));

		// set rotation as necessary for back facing
		// http://developer.android.com/reference/android/hardware/Camera.html#setDisplayOrientation(int)
		// http://developer.android.com/reference/android/hardware/Camera.Parameters.html#setRotation(int)
		int orientation;
		int rotation;
		if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
			rotation = (info.orientation + getRotation(true)) % 360;
			orientation = (info.orientation - getRotation(false) + 360) % 360;
		}
		// assume front facing
		else {
			rotation = (info.orientation - getRotation(true) + 360) % 360;
			orientation = (info.orientation + getRotation(false)) % 360;
			orientation = (360 - orientation) % 360;
		}

		// set the camera params
		cameraParams.setRotation(rotation);
		camera.setParameters(cameraParams);
		camera.setDisplayOrientation(orientation);

		// IMPORTANT: We must call startPreview() on the camera before we take any pictures
		camera.startPreview();

        // setup the auto focus
        // we need a try/catch because some devices report being able to set the focus mode,
        // but actually fail when you try to set it to a mode it says it supports (ie. some Motorola devices -_-)
        try {
            List<String> focusModes = cameraParams.getSupportedFocusModes();
            // TODO update the SDK version so "FOCUS_MODE_CONTINUOUS_PICTURE" constant can be used
            if (focusModes.contains("continuous-picture")) {
                Log.d(TAG, "device supports continuous autofocus");
                cameraParams.setFocusMode("continuous-picture");
                camera.autoFocus(this);
            } else if(focusModes.contains(Parameters.FOCUS_MODE_AUTO)) {
                Log.d(TAG, "device supports autofocus");
                cameraParams.setFocusMode(Parameters.FOCUS_MODE_AUTO);
                camera.autoFocus(this);
            }
        } catch (RuntimeException e) {
            Log.e(TAG, "can't set focus mode", e);
            Crashlytics.logException(e);
        }
    }

	public void surfaceCreated(SurfaceHolder holder) {
		Log.d(TAG, "surfaceCreated");
		try {
			// Open the Camera in preview mode
			camera = Camera.open();

			// if there is no back facing camera, use the first camera available
			if (camera == null && Camera.getNumberOfCameras() >= 1) {
				cameraId = 0;
				camera = Camera.open(cameraId);
			} else {
				cameraId = CameraInfo.CAMERA_FACING_BACK;
			}

			// get camera info
			CameraInfo info = new CameraInfo();
			Camera.getCameraInfo(cameraId, info);

			// set some camera parameters
			cameraParams = camera.getParameters();
			List<Camera.Size> cameraSizes = cameraParams.getSupportedPictureSizes();
			List<Camera.Size> cameraPreviewSizes = cameraParams.getSupportedPreviewSizes();
			// sort the lists
			Collections.sort(cameraSizes, new CameraSizeComparator());
			Collections.sort(cameraPreviewSizes, new CameraSizeComparator());

			cameraParams.setJpegQuality(100);
			cameraParams.setPictureSize(cameraSizes.get(cameraSizes.size()-1).width, cameraSizes.get(cameraSizes.size()-1).height);
			cameraParams.setPreviewSize(cameraPreviewSizes.get(cameraPreviewSizes.size()-1).width, cameraPreviewSizes.get(cameraPreviewSizes.size()-1).height);
			camera.setParameters(cameraParams);
			for (Size size : cameraSizes) {
				Log.d(TAG, "camera size: " + size.width + "x" + size.height);
			}
			for (Size size : cameraPreviewSizes) {
				Log.d(TAG, "camera preview size: " + size.width + "x" + size.height);
			}

			// tell the camera listener the camera is ready (if the listener is set)
			if(cameraReadyListener != null) {
				cameraReadyListener.onCameraReady(cameraParams);
			}

			// set the surface preview
			camera.setPreviewDisplay(holder);
		} catch (IOException e) {
			Log.e(TAG, "problem setting up the surface view", e);
		} catch (RuntimeException e) {
            Log.e(TAG, "unable to initiate the camera", e);
            Toast.makeText(getContext(), "Unable to initiate the camera.", Toast.LENGTH_LONG).show();
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
	 * Set the listner to callback when the camera is ready.
	 *
	 * @param listener the listener to callback when the camera is ready
	 */
	public void setOnCameraReadyListener(OnCameraReadyListener listener){
		cameraReadyListener = listener;
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

	public String getFlashMode() {
		if(cameraParams != null) {
			return cameraParams.getFlashMode();
		} else {
			return null;
		}
	}

	public void setFlashMode(String mode) {
		if (cameraParams.getFlashMode() != null) {
			cameraParams.setFlashMode(mode);
			camera.setParameters(cameraParams);
		}
	}

	/**
	 * Helper class to compare Camera.Size values
	 */
	private class CameraSizeComparator implements Comparator<Camera.Size> {

		@Override
		public int compare(final Size a, final Size b) {
			return a.width * a.height - b.width * b.height;
		}

	}

}
