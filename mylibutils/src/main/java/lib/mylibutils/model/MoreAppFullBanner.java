package lib.mylibutils.model;

import java.util.List;

/**
 * Created by GIOI on 7/8/2016.
 */
public class MoreAppFullBanner {

    private List<MoreAppFullBanner> listMoreApp;

    private String name;
    private String storeName;
    private String packageName;
    private String urlTarget;
    private String urlImage;

    public List<MoreAppFullBanner> getMoreAppFullBanners() {
        return listMoreApp;
    }

    public void setMoreAppFullBanners(List<MoreAppFullBanner> moreAppFullBanners) {
        this.listMoreApp = moreAppFullBanners;
    }

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

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }
}
