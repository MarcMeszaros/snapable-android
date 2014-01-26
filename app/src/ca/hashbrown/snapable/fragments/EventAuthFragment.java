package ca.hashbrown.snapable.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import ca.hashbrown.snapable.R;
import ca.hashbrown.snapable.activities.EventPhotoList;
import ca.hashbrown.snapable.provider.SnapableContract;
import com.snapable.api.models.Event;

public class EventAuthFragment extends DialogFragment implements OnEditorActionListener {

	private static final String TAG = "EventAuthFragment";

	private EditText pin;
	private EditText name;
	private EditText email;
	// data passed in from initializer
	private Event event;

	public EventAuthFragment() {
		// empty
	}

	@Override
	public void setArguments(Bundle args) {
		super.setArguments(args);
		Event event = args.getParcelable("event");
		this.event = event;
	}

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // setup the dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_event_auth, null);

        // setup the layout
        pin = (EditText) view.findViewById(R.id.fragment_event_auth__pin);
        name = (EditText) view.findViewById(R.id.fragment_event_auth__name);
        email = (EditText) view.findViewById(R.id.fragment_event_auth__email);

        if (this.event != null && this.event.is_public == true) {
            // hide the pin stuff because the event is public
            ((LinearLayout)view).removeView(view.findViewById(R.id.fragment_event_auth__pin_group));
            // Show soft keyboard automatically
            name.requestFocus();
        } else {
            // Show soft keyboard automatically
            pin.requestFocus();
        }
        email.setOnEditorActionListener(this);

        // set the positive and negative buttons
        builder.setCancelable(true);
        builder.setPositiveButton(R.string.fragment_event_auth__positive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                loginUser();
            }
        });
        builder.setNegativeButton(R.string.fragment_event_auth__negative_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // set the alert dialog view
        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return dialog;
    }

	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            return loginUser();
        }
        return false;
    }

	private boolean cachedPinMatchesEventPin(Event event) {
		Uri requestUri = ContentUris.withAppendedId(SnapableContract.EventCredentials.CONTENT_URI, event.getId());
		Cursor result = getActivity().getContentResolver().query(requestUri, null, null, null, null);

		// we have a result
		if (result.getCount() > 0 && event.is_public) {
			return true;
		}
		else if (result.getCount() > 0 && result.moveToFirst()) {
			return result.getString(result.getColumnIndex(SnapableContract.EventCredentials.PIN)).equals(event.is_public);
		}

		// there was no result
		return false;
	}

    private boolean loginUser() {
        try {
            // store the event as data to be passed
            Intent intent = new Intent(getActivity(), EventPhotoList.class);
            intent.putExtra("event", this.event);

            // if the event is public login
            if (this.event.is_public == true && cachedPinMatchesEventPin(this.event) == false) {
                // save the details in the local storage
                ContentValues values = new ContentValues(3);
                values.put(SnapableContract.EventCredentials._ID, this.event.getId());
                values.put(SnapableContract.EventCredentials.NAME, name.getText().toString());
                values.put(SnapableContract.EventCredentials.EMAIL, email.getText().toString());
                values.put(SnapableContract.EventCredentials.TYPE_ID, 6);

                // insert the event details
                getActivity().getContentResolver().insert(SnapableContract.EventCredentials.CONTENT_URI, values);

                // launch the event photo list
                this.dismiss();
                startActivity(intent);
                return true;
            }
            // if the event is private and the pins match
            else if (this.event.is_public != true && this.event.pin.equals(pin.getText().toString())) {
                // save the details in the local storage
                ContentValues values = new ContentValues(3);
                values.put(SnapableContract.EventCredentials.PIN, pin.getText().toString());
                values.put(SnapableContract.EventCredentials.NAME, name.getText().toString());
                values.put(SnapableContract.EventCredentials.EMAIL, email.getText().toString());
                values.put(SnapableContract.EventCredentials.TYPE_ID, 5);

                // check if a cached version exists
                Uri requestUri = ContentUris.withAppendedId(SnapableContract.EventCredentials.CONTENT_URI, this.event.getId());
                Cursor query = getActivity().getContentResolver().query(requestUri, null, null, null, null);

                // insert the event details
                if (query.getCount() <= 0) {
                    values.put(SnapableContract.EventCredentials._ID, this.event.getId());
                    getActivity().getContentResolver().insert(SnapableContract.EventCredentials.CONTENT_URI, values);
                } else {
                    Uri updateUri = ContentUris.withAppendedId(SnapableContract.EventCredentials.CONTENT_URI, this.event.getId());
                    getActivity().getContentResolver().update(updateUri, values, null, null);
                }
                // launch the event photo list
                this.dismiss();
                startActivity(intent);
                return true;
            }
            // the event is private and pins don't match
            else {
                pin.requestFocus();
                Toast.makeText(getActivity(), getResources().getString(R.string.strings__fragment_event_auth__pin_invalid), Toast.LENGTH_LONG).show();
                return false;
            }
        }
        catch (Exception e) {
            Log.e(TAG, "something went terribly wrong while traying to compare pins", e);
            return false;
        }
    }

}
