package ca.hashbrown.snapable.api.robospice;

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import ca.hashbrown.snapable.api.models.Event;
import ca.hashbrown.snapable.api.resources.EventResource;
import retrofit.RetrofitError;

public class EventRequest {

    // for some reason, RetrofitError's getMessage() (which Robospice uses) returns nothing meaningful when an HTTP-level error happens, so in order for logs to be useful a hack like this is useful.
    public static Exception MakeReadableException(RetrofitError err) {
        if(err.getResponse() != null) {
            return new Exception(String.format("Status code: %d, reason: %s", err.getResponse().getStatus(), err.getBody()), err);
        } else {
            return err;
        }
    }

    public static class GetEvent extends RetrofitSpiceRequest<Event, EventResource> {

        long pk;

        public GetEvent(long pk) {
            super(Event.class, EventResource.class);
            this.pk = pk;
        }

        @Override
        public Event loadDataFromNetwork() throws Exception {
            try {
                return getService().getEvent(this.pk);
            } catch (RetrofitError e) {
                throw e;
                //throw MakeReadableException(e);
            }
        }
    }

}
