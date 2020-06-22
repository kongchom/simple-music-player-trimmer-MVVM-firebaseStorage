package lib.mylibutils.model;

import java.util.List;

/**
 * Created by dangvangioi on 12/23/16.
 */
public class Data {
    private AdsControl controlAds;
    private List<PhotoFrames> listPhotoFrames;
    private List<ListSticker> listStickers;
    private List<FontPacks> fontPacks;

    public Data() {
    }

    public List<ListSticker> getListCateSticker() {
        return listStickers;
    }

    public void setListCateSticker(List<ListSticker> listCateStatus) {
        this.listStickers = listCateStatus;
    }

    public List<PhotoFrames> getListPhotoFrames() {
        return listPhotoFrames;
    }

    public void setListPhotoFrames(List<PhotoFrames> listPhotoFrames) {
        this.listPhotoFrames = listPhotoFrames;
    }

    public List<ListSticker> getListStickers() {
        return listStickers;
    }

    public void setListStickers(List<ListSticker> listStickers) {
        this.listStickers = listStickers;
    }

    public AdsControl getControlAds() {
        return controlAds;
    }

    public void setControlAds(AdsControl controlAds) {
        this.controlAds = controlAds;
    }

    public List<FontPacks> getFontPacks() {
        return fontPacks;
    }

    public void setFontPacks(List<FontPacks> fontPacks) {
        this.fontPacks = fontPacks;
    }
}
