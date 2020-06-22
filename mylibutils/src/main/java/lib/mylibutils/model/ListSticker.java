package lib.mylibutils.model;

import java.io.Serializable;

/**
 * Created by GIOI on 11/4/2016.
 */

public class ListSticker implements Serializable {

    private String name;
    private String folder;
    private String icon;
    private int totalImage;

    public ListSticker() {
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

    public int getTotalImage() {
        return totalImage;
    }

    public void setTotalImage(int totalImage) {
        this.totalImage = totalImage;
    }
}
