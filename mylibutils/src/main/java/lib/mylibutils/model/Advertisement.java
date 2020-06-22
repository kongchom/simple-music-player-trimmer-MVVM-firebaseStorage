package lib.mylibutils.model;

import java.io.Serializable;

/**
 * Created by vangioicnt on 11/6/17.
 */

public class Advertisement implements Serializable {

    private Advertisement advertisement;
    private String listCountryFacebookTarget;
    private boolean isFacebook;
    private boolean isAdMob;
    private boolean isTypeAdFirst;
    private String fbAdsFull;
    private String fbAdsBanner;
    private String fbAdsNative;
    private String fbAdsNativeBanner;
    private String fbAdsRewarded;
    private String local = "";//ex : VN, US...

    public Advertisement() {
        isFacebook = true;
        isAdMob = true;
        isTypeAdFirst = false;
        fbAdsFull = "";
        fbAdsBanner = "";
        fbAdsNative = "";
        local = "";
    }

    public Advertisement getAdvertisement() {
        return advertisement;
    }

    public String getListCountryFacebookTarget() {
        return listCountryFacebookTarget;
    }

    public void setListCountryFacebookTarget(String listCountryFacebookTarget) {
        this.listCountryFacebookTarget = listCountryFacebookTarget;
    }

    public boolean isFacebook() {
        return isFacebook;
    }

    public void setFacebook(boolean facebook) {
        isFacebook = facebook;
    }

    public boolean isAdMob() {
        return isAdMob;
    }

    public void setAdMob(boolean adMob) {
        isAdMob = adMob;
    }

    public boolean isTypeAdFirst() {
        return isTypeAdFirst;
    }

    public void setTypeAdFirst(boolean typeAdFirst) {
        isTypeAdFirst = typeAdFirst;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getFbAdsFull() {
        return fbAdsFull;
    }

    public void setFbAdsFull(String fbAdsFull) {
        this.fbAdsFull = fbAdsFull;
    }

    public String getFbAdsBanner() {
        return fbAdsBanner;
    }

    public void setFbAdsBanner(String fbAdsBanner) {
        this.fbAdsBanner = fbAdsBanner;
    }

    public String getFbAdsNative() {
        return fbAdsNative;
    }

    public void setFbAdsNative(String fbAdsNative) {
        this.fbAdsNative = fbAdsNative;
    }

    public String getFbAdsNativeBanner() {
        return fbAdsNativeBanner;
    }

    public void setFbAdsNativeBanner(String fbAdsNativeBanner) {
        this.fbAdsNativeBanner = fbAdsNativeBanner;
    }

    public String getFbAdsRewarded() {
        return fbAdsRewarded;
    }

    public void setFbAdsRewarded(String fbAdsRewarded) {
        this.fbAdsRewarded = fbAdsRewarded;
    }

    @Override
    public String toString() {
        return "Advertisement{" +
                "advertisement=" + advertisement +
                ", listCountryFacebookTarget='" + listCountryFacebookTarget + '\'' +
                ", isFacebook=" + isFacebook +
                ", isAdMob=" + isAdMob +
                ", isTypeAdFirst=" + isTypeAdFirst +
                ", fbAdsFull='" + fbAdsFull + '\'' +
                ", fbAdsBanner='" + fbAdsBanner + '\'' +
                ", fbAdsNative='" + fbAdsNative + '\'' +
                ", fbAdsNativeBanner='" + fbAdsNativeBanner + '\'' +
                ", fbAdsRewarded='" + fbAdsRewarded + '\'' +
                ", local='" + local + '\'' +
                '}';
    }
}
