package ca.hashbrown.snapable.loaders;

import com.snapable.utils.ToStringHelper;

import java.io.Serializable;
import java.util.ArrayList;

public class LoaderResponse<E> implements Serializable {

    /**
     * The possible loader response types.
     */
    public enum TYPE {
        FIRST,
        MORE,
        LAST,
        ERROR
    }

    public ArrayList<E> data;
    public TYPE type;

    public LoaderResponse() {
        data = new ArrayList<E>();
        type = TYPE.FIRST;
    }

    @Override
    public String toString() {
        return ToStringHelper.getInstance(this)
                .add("data", data.toString())
                .add("type", type)
                .toString();
    }

    public boolean isDataEmpty(){
        return data == null || data.size() == 0;
    }
}
