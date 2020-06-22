package lib.mylibutils.model;

import java.util.List;

/**
 * Created by GIOI on 9/19/2017.
 */

public class ListPhotoBackground {

    private String rootFolder;
    private String thumbFolder;
    private List<Background> listBackground;

    public String getRootFolder() {
        return rootFolder;
    }

    public void setRootFolder(String rootFolder) {
        this.rootFolder = rootFolder;
    }

    public String getThumbFolder() {
        return thumbFolder;
    }

    public void setThumbFolder(String thumbFolder) {
        this.thumbFolder = thumbFolder;
    }

    public List<Background> getListBackground() {
        return listBackground;
    }

    public void setListBackground(List<Background> listBackground) {
        this.listBackground = listBackground;
    }
}
