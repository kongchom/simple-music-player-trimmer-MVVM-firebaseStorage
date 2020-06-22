package lib.mylibutils.model;

import lib.mylibutils.MyLog;
import lib.mylibutils.TypeAdsControl;

public class AdsControl {
    private int percentInterstitialNextFrame = 30;
    private int percentInterstitialNextEditor = 30;
    private int percentInterstitialNextCollage = 30;
    private int percentInterstitialSaveCrop = 30;
    private int percentInterstitialSaveBlur = 30;
    private int percentInterstitialSaveEffects = 30;
    private int percentInterstitialHomeSavePhoto = 100;
    private int percentInterstitialSaveEditor = 100;
    private int percentInterstitialNextMyPhoto = 100;
    private int percentInterstitialDownloadFont = 100;
    private int percentInterstitialDoneCutOutActivity = 50;

    private int percentInterstitialSplash = 30;
    private int percentInterstitialFinishMenu = 50;

    private String sticker = "BANNER_ADAPTIVE";
    private String picPhoto = "BANNER_ADAPTIVE";
    private String myPhoto = "BANNER_ADAPTIVE";
    private String photoDetail = "BANNER_ADAPTIVE";

    public int getPercentInterstitialNextFrame() {
        return percentInterstitialNextFrame;
    }

    public void setPercentInterstitialNextFrame(int percentInterstitialNextFrame) {
        this.percentInterstitialNextFrame = percentInterstitialNextFrame;
    }

    public int getPercentInterstitialNextEditor() {
        return percentInterstitialNextEditor;
    }

    public void setPercentInterstitialNextEditor(int percentInterstitialNextEditor) {
        this.percentInterstitialNextEditor = percentInterstitialNextEditor;
    }

    public int getPercentInterstitialNextCollage() {
        return percentInterstitialNextCollage;
    }

    public void setPercentInterstitialNextCollage(int percentInterstitialNextCollage) {
        this.percentInterstitialNextCollage = percentInterstitialNextCollage;
    }

    public int getPercentInterstitialSaveCrop() {
        return percentInterstitialSaveCrop;
    }

    public void setPercentInterstitialSaveCrop(int percentInterstitialSaveCrop) {
        this.percentInterstitialSaveCrop = percentInterstitialSaveCrop;
    }

    public int getPercentInterstitialSaveBlur() {
        return percentInterstitialSaveBlur;
    }

    public void setPercentInterstitialSaveBlur(int percentInterstitialSaveBlur) {
        this.percentInterstitialSaveBlur = percentInterstitialSaveBlur;
    }

    public int getPercentInterstitialSaveEffects() {
        return percentInterstitialSaveEffects;
    }

    public void setPercentInterstitialSaveEffects(int percentInterstitialSaveEffects) {
        this.percentInterstitialSaveEffects = percentInterstitialSaveEffects;
    }

    public int getPercentInterstitialHomeSavePhoto() {
        return percentInterstitialHomeSavePhoto;
    }

    public void setPercentInterstitialHomeSavePhoto(int percentInterstitialHomeSavePhoto) {
        this.percentInterstitialHomeSavePhoto = percentInterstitialHomeSavePhoto;
    }

    public int getPercentInterstitialSaveEditor() {
        return percentInterstitialSaveEditor;
    }

    public void setPercentInterstitialSaveEditor(int percentInterstitialSaveEditor) {
        this.percentInterstitialSaveEditor = percentInterstitialSaveEditor;
    }

    public int getPercentInterstitialNextMyPhoto() {
        return percentInterstitialNextMyPhoto;
    }

    public void setPercentInterstitialNextMyPhoto(int percentInterstitialNextMyPhoto) {
        this.percentInterstitialNextMyPhoto = percentInterstitialNextMyPhoto;
    }

    public int getPercentInterstitialDownloadFont() {
        return percentInterstitialDownloadFont;
    }

    public void setPercentInterstitialDownloadFont(int percentInterstitialDownloadFont) {
        this.percentInterstitialDownloadFont = percentInterstitialDownloadFont;
    }

    public TypeAdsControl getSticker() {
        return getTypeAdsControl(sticker);
    }

    public void setSticker(String sticker) {
        this.sticker = sticker;
    }

    public TypeAdsControl getPicPhoto() {
        return getTypeAdsControl(picPhoto);
    }

    public void setPicPhoto(String picPhoto) {
        this.picPhoto = picPhoto;
    }

    public TypeAdsControl getMyPhoto() {
        return getTypeAdsControl(myPhoto);
    }

    public void setMyPhoto(String myPhoto) {
        this.myPhoto = myPhoto;
    }

    public TypeAdsControl getPhotoDetail() {
        return getTypeAdsControl(photoDetail);
    }

    public void setPhotoDetail(String photoDetail) {
        this.photoDetail = photoDetail;
    }

    public int getPercentInterstitialSplash() {
        return percentInterstitialSplash;
    }

    public void setPercentInterstitialSplash(int percentInterstitialSplash) {
        this.percentInterstitialSplash = percentInterstitialSplash;
    }

    public int getPercentInterstitialFinishMenu() {
        return percentInterstitialFinishMenu;
    }

    public void setPercentInterstitialFinishMenu(int percentInterstitialFinishMenu) {
        this.percentInterstitialFinishMenu = percentInterstitialFinishMenu;
    }

    public int getPercentInterstitialDoneCutOutActivity() {
        return percentInterstitialDoneCutOutActivity;
    }

    public void setPercentInterstitialDoneCutOutActivity(int percentInterstitialDoneCutOutActivity) {
        this.percentInterstitialDoneCutOutActivity = percentInterstitialDoneCutOutActivity;
    }

    private TypeAdsControl getTypeAdsControl(String str) {
        MyLog.d("AdsControl", "getTypeAdsControl str = " + str);

        if (str.equals(TypeAdsControl.BANNER_SMART.toString()))
            return TypeAdsControl.BANNER_SMART;

        if (str.equals(TypeAdsControl.NATIVE_LAGER.toString()))
            return TypeAdsControl.NATIVE_LAGER;

        if (str.equals(TypeAdsControl.NATIVE_SMALL.toString()))
            return TypeAdsControl.NATIVE_SMALL;

        return TypeAdsControl.BANNER_ADAPTIVE;
    }
}
