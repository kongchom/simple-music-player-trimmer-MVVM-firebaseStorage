package lib.mylibutils.model;

import java.util.List;

/**
 * Created by GIOI on 4/8/2016.
 */
public class CommonData<T> {

    private List<T> data;

    public List<T> getArray() {
        return data;
    }

    public void setArray(List<T> data) {
        this.data = data;
    }
}
