package ca.hashbrown.snapable.fragments;

import com.snapable.api.models.Event;

import ca.hashbrown.snapable.R;
import ca.hashbrown.snapable.activities.EventPhotoList;
import ca.hashbrown.snapable.provider.SnapableContract;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_auth, container);
        pin = (EditText) view.findViewById(R.id.fragment_event_auth__pin);
        name = (EditText) view.findViewById(R.id.fragment_event_auth__name);
        email = (EditText) view.findViewById(R.id.fragment_event_auth__email);
        
        // set the title
        getDialog().setTitle(R.string.strings__fragment_event_auth__title);
        
        if (this.event != null && this.event.getIsPublic() == true) {
        	// hide the pin stuff because the event is public
        	((LinearLayout)view).removeView(view.findViewById(R.id.fragment_event_auth__pin_group));
        	// Show soft keyboard automatically
        	name.requestFocus();
        } else {
        	// Show soft keyboard automatically
        	pin.requestFocus();
        }
        getDialog().getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        email.setOnEditorActionListener(this);

        return view;
    }
	
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            try {
            	// store the event as data to be passed
	    		Intent intent = new Intent(getActivity(), EventPhotoList.class);
	    		intent.putExtra("event", this.event);
	    		
	    		// if the event is public login
            	if (this.event.getIsPublic() == true && cachedPinMatchesEventPin(this.event) == false) {
            		// save the details in the local storage
            		ContentValues values = new ContentValues(3);
            		values.put(SnapableContract.EventCredentials._ID, this.event.getId());
            		values.put(SnapableContract.EventCredentials.NAME, name.getText().toString());
            		values.put(SnapableContract.EventCredentials.EMAIL, email.getText().toString());

            		// insert the event details
            		getActivity().getContentResolver().insert(SnapableContract.EventCredentials.CONTENT_URI, values);

            		// launch the event photo list
                	this.dismiss();
            		startActivity(intent);
            		return true;
            	} 
            	// if the event is private and the pins match
            	else if (this.event.getIsPublic() != true && this.event.getPin().equals(pin.getText().toString())) {
            		// save the details in the local storage
            		ContentValues values = new ContentValues(3);
            		values.put(SnapableContract.EventCredentials.PIN, pin.getText().toString());
            		values.put(SnapableContract.EventCredentials.NAME, name.getText().toString());
            		values.put(SnapableContract.EventCredentials.EMAIL, email.getText().toString());

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
        return false;
    }
	
	private boolean cachedPinMatchesEventPin(Event event) {
		Uri requestUri = ContentUris.withAppendedId(SnapableContract.EventCredentials.CONTENT_URI, event.getId());
		Cursor result = getActivity().getContentResolver().query(requestUri, null, null, null, null);

		// we have a result
		if (result.getCount() > 0 && event.getIsPublic()) {
			return true;
		}
		else if (result.getCount() > 0 && result.moveToFirst()) {
			return result.getString(result.getColumnIndex(SnapableContract.EventCredentials.PIN)).equals(event.getPin());
		}
		
		// there was no result
		return false;
	}

}
