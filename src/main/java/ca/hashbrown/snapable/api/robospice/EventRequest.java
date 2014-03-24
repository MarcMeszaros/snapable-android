package ca.hashbrown.snapable.api.robospice;

import com.snapable.api.private_v1.objects.Event;
import com.snapable.api.private_v1.objects.Pager;
import com.snapable.api.private_v1.resources.EventResource;

import retrofit.RetrofitError;

public class EventRequest {

    public static class GetEvent extends SnapRequest<Event, EventResource> {

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
                throw MakeReadableException(e);
            }
        }
    }

    public static class GetEvents extends SnapRequest<Pager, EventResource> {

        public GetEvents() {
            super(Pager.class, EventResource.class);
        }

        @Override
        public Pager loadDataFromNetwork() throws Exception {
            try {
                return getService().getEvents();
            } catch (RetrofitError e) {
                throw MakeReadableException(e);
            }
        }
    }

    public static class GetEventsLatLng extends SnapRequest<Pager, EventResource> {

        float lat = 0;
        float lng = 0;
        public GetEventsLatLng(float lat, float lng) {
            super(Pager.class, EventResource.class);
            this.lat = lat;
            this.lng = lng;
        }

        public GetEventsLatLng(double lat, double lng) {
            super(Pager.class, EventResource.class);
            this.lat = Double.valueOf(lat).floatValue();
            this.lng = Double.valueOf(lng).floatValue();
        }

        @Override
        public Pager loadDataFromNetwork() throws Exception {
            try {
                return getService().getEvents(lat, lng);
            } catch (RetrofitError e) {
                throw MakeReadableException(e);
            }
        }
    }

    public static class GetEventsQuery extends SnapRequest<Pager, EventResource> {

        String query = "";
        public GetEventsQuery(String query) {
            super(Pager.class, EventResource.class);
            this.query = query;
        }

        @Override
        public Pager loadDataFromNetwork() throws Exception {
            try {
                return getService().getEvents(query);
            } catch (RetrofitError e) {
                throw MakeReadableException(e);
            }
        }
    }

}
