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
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.snapable.api.private_v1.objects.Event;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ca.hashbrown.snapable.R;
import ca.hashbrown.snapable.activities.EventPhotoList;
import ca.hashbrown.snapable.provider.SnapableContract;
import timber.log.Timber;

public class EventAuthFragment extends DialogFragment {

    private static final String ARG_EVENT = "arg.event";
    private static final String ARG_UPDATE_USER = "arg.update.user";

    private Event mEvent;
    private boolean mUpdateUser = false;

    // injected views
    @InjectView(R.id.fragment_event_auth__pin)
    EditText mPinEditTextView;
    @InjectView(R.id.fragment_event_auth__name)
    EditText mNameEditTextView;
    @InjectView(R.id.fragment_event_auth__email)
    EditText mEmailEditTextView;
    @InjectView(R.id.fragment_event_auth__pin_group)
    View mPinGroupView;

    public static EventAuthFragment getInstance(Event event) {
        return getInstance(event, false);
    }

    public static EventAuthFragment getInstance(Event event, boolean updateUser) {
        EventAuthFragment fragment = new EventAuthFragment();
        Bundle args = new Bundle(2);
        args.putSerializable(ARG_EVENT, event);
        args.putBoolean(ARG_UPDATE_USER, updateUser);
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
            mUpdateUser = getArguments().getBoolean(ARG_UPDATE_USER, false);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // setup the dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_event_auth, null);
        ButterKnife.inject(this, view);

        // preload data
        Uri queryUri = ContentUris.withAppendedId(SnapableContract.EventCredentials.CONTENT_URI, mEvent.getPk());
        Cursor result = getActivity().getContentResolver().query(queryUri, null, null, null, null);
        if (result.moveToFirst()) {
            // guest details preload
            String name = result.getString(result.getColumnIndex(SnapableContract.EventCredentials.NAME));
            String email = result.getString(result.getColumnIndex(SnapableContract.EventCredentials.EMAIL));
            if (!TextUtils.isEmpty(name))
                mNameEditTextView.setText(name);
            if (!TextUtils.isEmpty(email))
                mEmailEditTextView.setText(email);
        }

        if (mUpdateUser || (mEvent != null && mEvent.is_public)) {
            mPinGroupView.setVisibility(View.GONE);
            mNameEditTextView.requestFocus();
        } else {
            // Show soft keyboard automatically
            mPinEditTextView.requestFocus();
        }
        mEmailEditTextView.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE)
                    return loginUser();
                return false;
            }
        });

        // setup the click listener
        DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    loginUser();
                }
            }
        };
        // set the positive and negative buttons
        builder.setCancelable(true);
        // TODO figure out how to display the title without having the soft keyboard hide the buttons
        //builder.setTitle(R.string.fragment_event_auth__title);
        builder.setPositiveButton(android.R.string.ok, clickListener);
        builder.setNegativeButton(android.R.string.cancel, clickListener);

        // set the alert dialog view
        builder.setView(view);
        return builder.create();
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
            mPinEditTextView.setError(null); // clear any errors

            // store the event as data to be passed
            Intent intent = EventPhotoList.initIntent(getActivity(), mEvent);

            if (mUpdateUser) {
                // save the details in the local storage
                ContentValues values = new ContentValues(3);
                values.put(SnapableContract.EventCredentials._ID, mEvent.getPk());
                values.put(SnapableContract.EventCredentials.NAME, mNameEditTextView.getText().toString());
                values.put(SnapableContract.EventCredentials.EMAIL, mEmailEditTextView.getText().toString());

                // insert the event details
                Uri updateUri = ContentUris.withAppendedId(SnapableContract.EventCredentials.CONTENT_URI, mEvent.getPk());
                getActivity().getContentResolver().update(updateUri, values, null, null);
                return true;
            }
            // if the event is public login
            else if (mEvent.is_public && !cachedPinMatchesEventPin(mEvent)) {
                // save the details in the local storage
                ContentValues values = new ContentValues(3);
                values.put(SnapableContract.EventCredentials._ID, mEvent.getPk());
                values.put(SnapableContract.EventCredentials.NAME, mNameEditTextView.getText().toString());
                values.put(SnapableContract.EventCredentials.EMAIL, mEmailEditTextView.getText().toString());
                values.put(SnapableContract.EventCredentials.TYPE_ID, 6);

                // insert the event details
                getActivity().getContentResolver().insert(SnapableContract.EventCredentials.CONTENT_URI, values);

                // launch the event photo list
                dismiss();
                startActivity(intent);
                return true;
            }
            // if the event is private and the pins match
            else if (!mEvent.is_public && mEvent.pin.equals(mPinEditTextView.getText().toString())) {
                // save the details in the local storage
                ContentValues values = new ContentValues(3);
                values.put(SnapableContract.EventCredentials.PIN, mPinEditTextView.getText().toString());
                values.put(SnapableContract.EventCredentials.NAME, mNameEditTextView.getText().toString());
                values.put(SnapableContract.EventCredentials.EMAIL, mEmailEditTextView.getText().toString());
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
                mPinEditTextView.setError(getString(R.string.fragment_event_auth__pin_invalid));
                mPinEditTextView.requestFocus();
                return false;
            }
        }
        catch (Exception e) {
            Timber.e(e, "something went terribly wrong while trying to compare pins");
            return false;
        }
    }

}
