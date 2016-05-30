package ca.hashbrown.snapable.activities;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.snapable.api.private_v1.Client;
import com.snapable.api.private_v1.objects.Event;
import com.snapable.api.private_v1.objects.Guest;
import com.snapable.api.private_v1.resources.PhotoResource;
import com.snapable.utils.SnapImage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import ca.hashbrown.snapable.R;
import ca.hashbrown.snapable.api.SnapClient;
import ca.hashbrown.snapable.provider.SnapableContract;
import ca.hashbrown.snapable.utils.SnapBitmapFactory;
import retrofit.RetrofitError;
import retrofit.mime.TypedString;
import timber.log.Timber;

public class PhotoUpload extends BaseActivity {

    public static final String EXTRA_EVENT = "extra.event";
    public static final String EXTRA_IMAGE_PATH = "extra.image.path";

    private Event mEvent;
    private String mImagePath;
    private Bitmap bmScaled;

    public static Intent initIntent(Activity activity, Event event, String imagePath) {
        // pass all the data to the photo upload activity
        Intent intent = new Intent(activity, PhotoUpload.class);
        intent.putExtra(PhotoUpload.EXTRA_EVENT, event);
        intent.putExtra(PhotoUpload.EXTRA_IMAGE_PATH, imagePath);
        return intent;
    }

    //region == LifeCycle ==
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_upload);
        ButterKnife.inject(this);

        // get the bundle from the saved state or try and get it from the intent
        if (savedInstanceState != null) {
            mEvent = (Event) savedInstanceState.getSerializable(EXTRA_EVENT);
            mImagePath = savedInstanceState.getString(EXTRA_IMAGE_PATH);
        } else {
            mEvent = (Event) getIntent().getSerializableExtra(EXTRA_EVENT);
            mImagePath = getIntent().getStringExtra(EXTRA_IMAGE_PATH);
        }

        // set the action bar title
        if (getActionBar() != null) {
            getActionBar().setTitle(mEvent.title);
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // quick memory usage sanity check
        Timber.d("Free: %,d B | Total: %,d B | Max: %,d B", Runtime.getRuntime().freeMemory(), Runtime.getRuntime().totalMemory(), Runtime.getRuntime().maxMemory());

        // create a scaled bitmap
        int dpSize = 275;
        int pxSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpSize, getResources().getDisplayMetrics());
        ImageView photo = ButterKnife.findById(this, R.id.fragment_photo_upload__image);
        bmScaled = SnapBitmapFactory.decodeSampledBitmapFromPath(mImagePath, pxSize, pxSize);

        // set the scaled image in the image view
        photo.setImageBitmap(bmScaled);
    }

    @Override
    protected void onStop() {
        super.onStop();
        bmScaled.recycle();
        bmScaled = null;
    }
    //endregion

    //region == State ==
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(EXTRA_EVENT, mEvent);
        outState.putString(EXTRA_IMAGE_PATH, mImagePath);
    }
    //endregion

    @OnClick(R.id.fragment_photo_upload__button_done)
    public void onClickUploadButton(View v) {
        // get the image caption
        EditText caption = ButterKnife.findById(this, R.id.fragment_photo_upload__caption);

        if (SnapClient.isReachable(v.getContext())) {
            // get the image data ready for uploading via the API
            PhotoUploadTask uploadTask = new PhotoUploadTask(mEvent, caption.getText().toString(), mImagePath);
            uploadTask.execute();
        } else {
            Toast.makeText(this, getString(R.string.api__unreachable), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
                options.inTempStorage = new byte[256 * 1024]; // 256KB of temp decoding storage
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
                    options.inPurgeable = true;
                // original photo to upload
                Bitmap photo = SnapBitmapFactory.decodeFile(photoPath, options);
                Timber.i("ByteCount of photo: " + photo.getByteCount());
                ExifInterface exif = new ExifInterface(photoPath);
                int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

                // turn the bitmap into a temp compressed file
                FileOutputStream tmpout = new FileOutputStream(photoPath + ".tmp");
                photo.compress(Bitmap.CompressFormat.JPEG, 50, tmpout);
                tmpout.close();
                // make sure memory is released
                photo.recycle();
                photo = null;
                Timber.d("Created temp file");

                // re-apply the exif rotation
                ExifInterface exifComp = new ExifInterface(photoPath + ".tmp");
                exifComp.setAttribute(ExifInterface.TAG_ORIENTATION, String.valueOf(exifOrientation));
                exifComp.saveAttributes();
                Timber.d("Re-applied exif data to temp file");

                // decode temp file
                File tempFile = new File(photoPath + ".tmp");
                SnapImage tempImage = new SnapImage(tempFile);

                // get local cached mEvent info
                Uri queryUri = ContentUris.withAppendedId(SnapableContract.EventCredentials.CONTENT_URI, event.getPk());
                Cursor c = getContentResolver().query(queryUri, null, null, null, null);

                // if we have a guest id, upload the photo with the id
                PhotoResource photoRes = SnapClient.getResource(PhotoResource.class);
                if (c.moveToFirst()) {
                    long guest_id = c.getLong(c.getColumnIndex(SnapableContract.EventCredentials.GUEST_ID));
                    if (guest_id > 0) {
                        photoRes.postPhoto(tempImage, new TypedString(event.resourceUri), new TypedString(new Guest().getResourceUriFromPk(guest_id)), new TypedString(caption));
                    } else {
                        photoRes.postPhoto(tempImage, new TypedString(event.resourceUri), new TypedString(caption));
                    }
                } else {
                    photoRes.postPhoto(tempImage, new TypedString(event.resourceUri), new TypedString(caption));
                }
            } catch (FileNotFoundException e) {
                Timber.e(e, "problem finding a file");
                errorMsg = "There was a problem uploading the photo.";
            } catch (IOException e) {
                Timber.e(e, "some IO exception");
                errorMsg = "There was a problem uploading the photo.";
            } catch (OutOfMemoryError e) {
                Timber.e(e, "Free: %,d B | Total: %,d B | Max: %,d B", Runtime.getRuntime().freeMemory(), Runtime.getRuntime().totalMemory(), Runtime.getRuntime().maxMemory());
                errorMsg = getString(R.string.api__unable_to_upload);
            } catch (RetrofitError error) {
                if (error.getKind() == RetrofitError.Kind.NETWORK) {
                    errorMsg = getString(R.string.api__unreachable);
                } else {
                    errorMsg = getString(R.string.api__unable_to_upload);
                }
            } finally {
                Timber.d("delete temp file");
                File tmpFile = new File(photoPath + ".tmp");
                tmpFile.delete();
            }

            // return nothing
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            // stop the progress bar
            ProgressBar pb = (ProgressBar) findViewById(R.id.fragment_photo_upload__progressBar);
            pb.setVisibility(View.GONE);

            // if there is an error set, display it to the user
            if (errorMsg != null && errorMsg.length() > 0) {
                Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
            } else {
                // Go back to the photo list when we are done uploading.
                finish();
            }

        }

    }
}