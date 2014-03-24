package ca.hashbrown.snapable.api.robospice;

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import retrofit.RetrofitError;

public abstract class SnapRequest<M, R> extends RetrofitSpiceRequest<M, R> {

    public SnapRequest(Class<M> clazz, Class<R> retrofitedInterfaceClass) {
        super(clazz, retrofitedInterfaceClass);
    }

    // for some reason, RetrofitError's getMessage() returns nothing meaningful when an HTTP-level error happens,
    // so in order for logs to be useful a hack like this is useful.
    public static Exception MakeReadableException(RetrofitError e) {
        if(e.getResponse() != null) {
            return new Exception(String.format("Status code: %d, reason: %s", e.getResponse().getStatus(), e.getBody()), e);
        } else {
            return e;
        }
    }

}
