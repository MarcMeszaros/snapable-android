package ca.hashbrown.snapable.fragments;

import com.snapable.api.models.Event;

import ca.hashbrown.snapable.EventPhotoList;
import ca.hashbrown.snapable.R;
import android.content.Intent;
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
            	if (this.event.getIsPublic() == true) {
                	this.dismiss();
            		startActivity(intent);
            		return true;
            	} 
            	// if the event is private and the pins match
            	else if (this.event.getIsPublic() != true && this.event.getPin().contentEquals(pin.getText().toString())) {
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

}
