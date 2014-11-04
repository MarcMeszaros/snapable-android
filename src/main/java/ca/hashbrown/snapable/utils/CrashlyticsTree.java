package ca.hashbrown.snapable.utils;

import android.content.Context;
import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import timber.log.Timber;

/**
 * This Timber tree is a Crashlytics optimized Timber tree. It only sends logs to crashlytics on
 * error or warning level logs. It gets the current class as the TAG.
 */
public class CrashlyticsTree extends Timber.HollowTree implements Timber.TaggedTree {
    private static final Pattern ANONYMOUS_CLASS = Pattern.compile("\\$\\d+$");
    private static final ThreadLocal<String> NEXT_TAG = new ThreadLocal<String>();

    /**
     * Create the new Crashlytics tree with an {@link android.content.Context}.
     *
     * @param context The context for {@link com.crashlytics.android.Crashlytics#start(android.content.Context)}.
     */
    public CrashlyticsTree(Context context) {
        this(context, null, null, null);
    }

    /**
     * Create the new Crashlytics tree with an {@link android.content.Context} and various user details.
     *
     * @param context The context for {@link com.crashlytics.android.Crashlytics#start(android.content.Context)}.
     * @param username The username for the current user or null.
     * @param email The email of the current user or null.
     * @param userIdentifier The user identifier or null (ex. user internal ID).
     */
    public CrashlyticsTree(Context context, String username, String email, String userIdentifier) {
        Crashlytics.start(context);

        if (!TextUtils.isEmpty(username))
            Crashlytics.setUserName(username);
        if (!TextUtils.isEmpty(email))
            Crashlytics.setUserEmail(email);
        if (!TextUtils.isEmpty(userIdentifier))
            Crashlytics.setUserIdentifier(userIdentifier);
    }

    // https://github.com/JakeWharton/timber/blob/master/timber/src/main/java/timber/log/Timber.java#L209
    private static String createTag() {
        String tag = NEXT_TAG.get();
        if (tag != null) {
            NEXT_TAG.remove();
            return tag;
        }

        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        if (stackTrace.length < 6) {
            throw new IllegalStateException("Synthetic stacktrace didn't have enough elements: are you using proguard?");
        }
        tag = stackTrace[5].getClassName();
        Matcher m = ANONYMOUS_CLASS.matcher(tag);
        if (m.find()) {
            tag = m.replaceAll("");
        }
        return tag.substring(tag.lastIndexOf('.') + 1) + ".class";
    }

    // https://github.com/JakeWharton/timber/blob/master/timber/src/main/java/timber/log/Timber.java#L229
    static String formatString(String message, Object... args) {
        // If no varargs are supplied, treat it as a request to log the string without formatting.
        return args.length == 0 ? message : String.format(message, args);
    }

    // https://github.com/SimonVT/cathode/blob/master/cathode/src/main/java/net/simonvt/cathode/CrashlyticsTree.java#L63
    private void log(String logType, String message, Object... args) {
        if (message == null)
            message = "";
        StringBuilder s = new StringBuilder();
        String tag = createTag();
        if (logType != null) {
            s.append("[").append(logType).append("] ");
        }
        s.append(tag).append(" - ").append(formatString(message, args));
        Crashlytics.log(s.toString());
    }

    @Override
    public void tag(String tag) {
        NEXT_TAG.set(tag);
    }

    @Override
    public void i(String message, Object... args) {
        log("INFO", message, args);
    }

    @Override
    public void w(String message, Object... args) {
        log("WARN", message, args);
    }

    @Override
    public void w(Throwable t, String message, Object... args) {
        if (!TextUtils.isEmpty(message))
            Crashlytics.log(formatString("EXTRA DETAILS: " + message, args));
        Crashlytics.logException(t);
    }

    @Override
    public void e(String message, Object... args) {
        log("ERROR", message, args);
    }

    @Override
    public void e(Throwable t, String message, Object... args) {
        if (!TextUtils.isEmpty(message))
            Crashlytics.log(formatString("EXTRA DETAILS: " + message, args));
        Crashlytics.logException(t);
    }

}
