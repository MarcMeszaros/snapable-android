package ca.hashbrown.snapable.api;

import retrofit.RequestInterceptor;

public class SnapInterceptor implements RequestInterceptor {

    @Override
    public void intercept(RequestFacade requestFacade) {
        requestFacade.addHeader("User-Agent", "Snapable/1.0");
        //requestFacade.addHeader("Accept", "application/json");
    }
}
