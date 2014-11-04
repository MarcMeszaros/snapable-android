package ca.hashbrown.snapable.api.robospice;

import android.app.Application;
import android.util.Log;

import com.octo.android.robospice.SpiceService;
import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.exception.CacheCreationException;
import com.octo.android.robospice.persistence.retrofit.RetrofitObjectPersisterFactory;
import com.octo.android.robospice.request.CachedSpiceRequest;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ca.hashbrown.snapable.api.SnapClient;

import ca.hashbrown.snapable.api.resources.EventResource;
import retrofit.RestAdapter;

/**
 * A custom SpiceService implementation. The snapable-java library nicely wraps a bunch of the
 * boilerplate code and provides helper methods to setup a client.
 *
 * The robospice extension assumes that the retrofit client doesn't have the nice wrappers. Because
 * the Snapable API client wraps has these helpers, we can use the Snapable client and just pass
 * what we need to the SpiceService. The "stock" robospice retrofit extension would ask us to setup
 * some of the same stuff again.
 *
 * Loosly based on:
 * https://github.com/stephanenicolas/robospice/blob/release/extensions/robospice-retrofit-parent/robospice-retrofit/src/main/java/com/octo/android/robospice/retrofit/RetrofitSpiceService.java
 */
public class SnapSpiceService extends SpiceService {

    private static final String TAG = "SnapSpiceService";

    private SnapClient apiClient = null;
    private Map<Class<?>, Object> retrofitInterfaceToServiceMap = new HashMap<Class<?>, Object>();
    private RestAdapter restAdapter;
    protected List<Class<?>> retrofitInterfaceList = new ArrayList<Class<?>>();

    @Override
    public void onCreate() {
        super.onCreate();
        apiClient = SnapClient.getInstance();
        restAdapter = apiClient.getRestAdapter();

        // setup the interfaces
        retrofitInterfaceList.add(EventResource.class);
    }

    @Override
    public CacheManager createCacheManager(Application application) throws CacheCreationException {
        CacheManager cacheManager = new CacheManager();
        //cacheManager.addPersister(new RetrofitObjectPersisterFactory(application, apiClient.getConverter()));
        return cacheManager;
    }

    @SuppressWarnings("unchecked")
    protected <T> T getRetrofitService(Class<T> serviceClass) {
        T service = (T) retrofitInterfaceToServiceMap.get(serviceClass);
        if (service == null) {
            service = restAdapter.create(serviceClass);
            retrofitInterfaceToServiceMap.put(serviceClass, service);
        }
        return service;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void addRequest(CachedSpiceRequest<?> request, Set<RequestListener<?>> listRequestListener) {
        if (request.getSpiceRequest() instanceof RetrofitSpiceRequest) {
            RetrofitSpiceRequest retrofitSpiceRequest = (RetrofitSpiceRequest) request.getSpiceRequest();
            retrofitSpiceRequest.setService(getRetrofitService(retrofitSpiceRequest.getRetrofitedInterfaceClass()));
        }
        super.addRequest(request, listRequestListener);
    }

    public final List<Class<?>> getRetrofitInterfaceList() {
        return retrofitInterfaceList;
    }
}
