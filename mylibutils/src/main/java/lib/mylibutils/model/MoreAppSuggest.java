package lib.mylibutils.model;

import android.widget.ListView;

import java.util.List;

/**
 * Created by GIOI on 7/8/2016.
 */
public class MoreAppSuggest {

    private List<MoreAppSuggest> listMoreApp;

    private String name;
    private String storeName;
    private String packageName;
    private String urlTarget;
    private String urlImage;
    private String urlIcon;
    private String appDescription;

    public List<MoreAppSuggest> getListMoreApp() {
        return listMoreApp;
    }

    public void setListMoreApp(List<MoreAppSuggest> listMoreApp) {
        this.listMoreApp = listMoreApp;
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

    public String getUrlIcon() {
        return urlIcon;
    }

    public void setUrlIcon(String urlIcon) {
        this.urlIcon = urlIcon;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getAppDescription() {
        return appDescription;
    }

    public void setAppDescription(String appDescription) {
        this.appDescription = appDescription;
    }
}
