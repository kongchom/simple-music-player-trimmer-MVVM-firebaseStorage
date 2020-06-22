package lib.mylibutils.model;

import java.io.Serializable;
import java.util.List;

public class PhotoFrames implements Serializable {

    private String name;
    private String folder;
    private String icon;
    private String cover;
    private int totalImage;
    private int photoFramePosition;
    private boolean lock;
    private String openPackageName;
    private List<Define> defines;

    public PhotoFrames() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public int getTotalImage() {
        return totalImage;
    }

    public void setTotalImage(int totalImage) {
        this.totalImage = totalImage;
    }

    public List<Define> getDefines() {
        return defines;
    }

    public void setDefines(List<Define> defines) {
        this.defines = defines;
    }

    public int getPhotoFramePosition() {
        return photoFramePosition;
    }

    public void setPhotoFramePosition(int photoFramePosition) {
        this.photoFramePosition = photoFramePosition;
    }

    public boolean isLock() {
        return lock;
    }

    public void setLock(boolean lock) {
        this.lock = lock;
    }

    public String getOpenPackageName() {
        return openPackageName;
    }

    public void setOpenPackageName(String openPackageName) {
        this.openPackageName = openPackageName;
    }
}
