package ca.hashbrown.snapable.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;


/**
 * This Timber tree is a Crashlytics optimized Timber tree. It only sends logs to crashlytics on
 * error or warning level logs. It gets the current class as the TAG.
 */
public class CrashlyticsTree extends Timber.Tree {

    /**
     * Create the new Crashlytics tree with an {@link android.content.Context}.
     *
     * @param context The context to use.
     */
    public CrashlyticsTree(Context context) {
        this(context, null, null, null);
    }

    /**
     * Create the new Crashlytics tree with an {@link android.content.Context} and various user details.
     *
     * @param context The context to use.
     * @param username The username for the current user or null.
     * @param email The email of the current user or null.
     * @param userIdentifier The user identifier or null (ex. user internal ID).
     */
    public CrashlyticsTree(Context context, String username, String email, String userIdentifier) {
        Fabric.with(context, new Crashlytics());

        if (!TextUtils.isEmpty(username))
            Crashlytics.setUserName(username);
        if (!TextUtils.isEmpty(email))
            Crashlytics.setUserEmail(email);
        if (!TextUtils.isEmpty(userIdentifier))
            Crashlytics.setUserIdentifier(userIdentifier);
    }

    // https://github.com/JakeWharton/timber/blob/master/timber/src/main/java/timber/log/Timber.java#L229
    static String formatString(String message, Object... args) {
        // If no varargs are supplied, treat it as a request to log the string without formatting.
        return args.length == 0 ? message : String.format(message, args);
    }

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        // We don't want to deal with anything less than INFO.
        if (priority < Log.INFO)
            return;

        StringBuilder s = new StringBuilder();

        // add the error type to the Crashlytics string
        s.append("[");
        switch (priority) {
            case Log.INFO:
                s.append("INFO");
                break;
            case Log.WARN:
                s.append("WARN");
                break;
            case Log.ERROR:
                s.append("ERROR");
                break;
        }
        s.append("]");

        // add the tag if it's not null, add it to the Crashlytics string
        if (tag != null)
            s.append(" ").append(tag);

        // Prepare for adding message.
        s.append(" - ");
        boolean shouldLogException = t != null && priority > Log.INFO;

        // Timber#prepareLog method appends the StackTrace to the message.
        // We don't need to append the StackTrace
        // if we're eventually logging the exception to Crashlytics.
        if (message != null)
            if (!shouldLogException)
                s.append(message);
            else
                // Timber adds `\n` between message and the StackTrace.
                // We'll detect that and strip the message. The down side is, that it'
                s.append(message.substring(0, message.indexOf("\n")));

        // actually send the log to crashlytics
        Crashlytics.log(s.toString());

        // send the throwable to crashlytics
        if (shouldLogException)
            Crashlytics.logException(t);
    }

}
