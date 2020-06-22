package lib.mylibutils.model;

import java.util.List;

/**
 * Created by GIOI on 4/25/2016.
 */
public class ListMoreApp {

    private AppInfo appInfo;
    private List<MoreApp> listMoreApp;
    private AdsInfo adsInfo;

    public AppInfo getAppInfo() {
        return appInfo;
    }

    public void setAppInfo(AppInfo appInfo) {
        this.appInfo = appInfo;
    }

    public List<MoreApp> getListMoreApp() {
        return listMoreApp;
    }

    public void setListMoreApp(List<MoreApp> listMoreApp) {
        this.listMoreApp = listMoreApp;
    }

    public AdsInfo getAdsInfo() {
        return adsInfo;
    }

    public void setAdsInfo(AdsInfo adsInfo) {
        this.adsInfo = adsInfo;
    }
}
