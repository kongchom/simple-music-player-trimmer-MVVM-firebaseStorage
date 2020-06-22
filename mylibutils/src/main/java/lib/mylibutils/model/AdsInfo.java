package lib.mylibutils.model;

import java.io.Serializable;

/**
 * Created by GIOI on 5/4/2016.
 */
public class AdsInfo implements Serializable {

    private String name;
    private String packageName;
    private String urlTarget;
    private String urlImage;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getUrlTarget() {
        return urlTarget;
    }

    public void setUrlTarget(String urlTarget) {
        this.urlTarget = urlTarget;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }
}
