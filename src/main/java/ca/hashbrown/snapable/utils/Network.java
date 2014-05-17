package ca.hashbrown.snapable.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.net.URL;
import java.net.URLConnection;

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

    /**
     * Test if the client can connect to a specific url.
     *
     * @param url the url to test
     * @param timeout the timeout in milliseconds
     * @return a boolean representing if you can connect or not
     */
    public boolean canConnectToUrl(String url, int timeout) {
        try {
            URL myUrl = new URL(url);
            URLConnection connection = myUrl.openConnection();
            connection.setConnectTimeout(timeout);
            connection.connect();
            return true;
        } catch (Exception e) {
            // Handle exceptions
            return false;
        }
    }

}
