package lib.mylibutils.model;

/**
 * Created by GIOI on 9/19/2017.
 */

public class Background {

    private String fileName;
    private int index;
    private boolean isHeader = false;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public void setHeader(boolean header) {
        isHeader = header;
    }

}
