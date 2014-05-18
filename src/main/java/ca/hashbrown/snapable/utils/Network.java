package ca.hashbrown.snapable.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Small class to help in determining network connectivity.
 */
public class Network {

    private Context mContext;

    public Network (Context context) {
        mContext = context;
    }

    /**
     * Test if a network interface is available.
     *
     * @return a boolean representing if a network interface is available
     */
    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null && info.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

}
