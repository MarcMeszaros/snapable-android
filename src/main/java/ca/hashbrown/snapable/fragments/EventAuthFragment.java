package ca.hashbrown.snapable.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.snapable.api.private_v1.objects.Event;

import butterknife.ButterKnife;
import ca.hashbrown.snapable.R;
import ca.hashbrown.snapable.activities.EventPhotoList;
import ca.hashbrown.snapable.provider.SnapableContract;
import timber.log.Timber;

public class EventAuthFragment extends DialogFragment implements OnEditorActionListener {

    private static final String ARG_EVENT = "arg.event";

    private AlertDialog mDialog;

	private EditText pin;
	private EditText name;
	private EditText email;
	// data passed in from initializer
	private Event mEvent;

    public static EventAuthFragment getInstance(Event event) {
        EventAuthFragment fragment = new EventAuthFragment();
        Bundle args = new Bundle(1);
        args.putSerializable(ARG_EVENT, event);
        fragment.setArguments(args);
        return fragment;
    }

	public EventAuthFragment() {
		// empty
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mEvent = (Event) getArguments().getSerializable(ARG_EVENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // setup the dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_event_auth, null);

        // setup the layout
        pin = ButterKnife.findById(view, R.id.fragment_event_auth__pin);
        name = ButterKnife.findById(view, R.id.fragment_event_auth__name);
        email = ButterKnife.findById(view, R.id.fragment_event_auth__email);

        if (mEvent != null && mEvent.is_public) {
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
        // TODO figure out how to display the title without having the soft keyboard hide the buttons
        //builder.setTitle(R.string.fragment_event_auth__title);
        builder.setPositiveButton(android.R.string.ok, null);
        builder.setNegativeButton(android.R.string.cancel, null);

        // set the alert dialog view
        builder.setView(view);
        mDialog = builder.create();
        mDialog.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        final Button positiveButton = mDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        return mDialog;
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
            pin.setError(null); // clear any errors

            // store the event as data to be passed
            Intent intent = EventPhotoList.initIntent(getActivity(), mEvent);

            // if the event is public login
            if (mEvent.is_public && !cachedPinMatchesEventPin(mEvent)) {
                // save the details in the local storage
                ContentValues values = new ContentValues(3);
                values.put(SnapableContract.EventCredentials._ID, mEvent.getPk());
                values.put(SnapableContract.EventCredentials.NAME, name.getText().toString());
                values.put(SnapableContract.EventCredentials.EMAIL, email.getText().toString());
                values.put(SnapableContract.EventCredentials.TYPE_ID, 6);

                // insert the event details
                getActivity().getContentResolver().insert(SnapableContract.EventCredentials.CONTENT_URI, values);

                // launch the event photo list
                dismiss();
                startActivity(intent);
                return true;
            }
            // if the event is private and the pins match
            else if (!mEvent.is_public && mEvent.pin.equals(pin.getText().toString())) {
                // save the details in the local storage
                ContentValues values = new ContentValues(3);
                values.put(SnapableContract.EventCredentials.PIN, pin.getText().toString());
                values.put(SnapableContract.EventCredentials.NAME, name.getText().toString());
                values.put(SnapableContract.EventCredentials.EMAIL, email.getText().toString());
                values.put(SnapableContract.EventCredentials.TYPE_ID, 5);

                // check if a cached version exists
                Uri requestUri = ContentUris.withAppendedId(SnapableContract.EventCredentials.CONTENT_URI, mEvent.getPk());
                Cursor query = getActivity().getContentResolver().query(requestUri, null, null, null, null);

                // insert the event details
                if (query.getCount() <= 0) {
                    values.put(SnapableContract.EventCredentials._ID, mEvent.getPk());
                    getActivity().getContentResolver().insert(SnapableContract.EventCredentials.CONTENT_URI, values);
                } else {
                    Uri updateUri = ContentUris.withAppendedId(SnapableContract.EventCredentials.CONTENT_URI, mEvent.getPk());
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
                pin.setError(getString(R.string.fragment_event_auth__pin_invalid));
                return false;
            }
        }
        catch (Exception e) {
            Timber.e(e, "something went terribly wrong while trying to compare pins");
            return false;
        }
    }

}
