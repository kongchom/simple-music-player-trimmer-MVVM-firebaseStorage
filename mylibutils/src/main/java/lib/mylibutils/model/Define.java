package lib.mylibutils.model;

import java.io.Serializable;

/**
 * Created by GIOI on 11/13/2017.
 * Photo frame define
 */

public class Define implements Serializable {

    private int start;
    private int end;
    private int totalCollageItemContainer;
    private int indexDefineCollage;

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getTotalCollageItemContainer() {
        return totalCollageItemContainer;
    }

    public void setTotalCollageItemContainer(int totalCollageItemContainer) {
        this.totalCollageItemContainer = totalCollageItemContainer;
    }

    public int getIndexDefineCollage() {
        return indexDefineCollage;
    }

    public void setIndexDefineCollage(int indexDefineCollage) {
        this.indexDefineCollage = indexDefineCollage;
    }

    @Override
    public String toString() {
        return "Define {" +
                "start = " + start +
                ", end = " + end +
                ", totalCollageItemContainer = " + totalCollageItemContainer +
                ", indexDefineCollage = " + indexDefineCollage +
                '}';
    }
}
