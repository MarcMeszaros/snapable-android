package ca.hashbrown.snapable;


import com.snapable.api.SnapApi;

import android.os.Bundle;
import android.util.Log;
import android.app.Activity;

public class SnapActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snap);

        String[] resp = SnapApi.send("GET", "event");
        Log.i("SnapApi", resp[1]);
    }
}