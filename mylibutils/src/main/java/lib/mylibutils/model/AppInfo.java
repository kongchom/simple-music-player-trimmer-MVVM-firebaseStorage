package lib.mylibutils.model;

/**
 * Created by GIOI on 4/29/2016.
 */
public class AppInfo {
    private int versionCode;
    private String urlSwitch;
    private String forceUpdate;
    private String acc;
    private String updateVi;
    private String updateEn;

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getUrlSwitch() {
        return urlSwitch;
    }

    public void setUrlSwitch(String urlSwitch) {
        this.urlSwitch = urlSwitch;
    }

    public String getForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(String forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    public String getAcc() {
        return acc;
    }

    public void setAcc(String acc) {
        this.acc = acc;
    }

    public String getUpdateVi() {
        return updateVi;
    }

    public void setUpdateVi(String updateVi) {
        this.updateVi = updateVi;
    }

    public String getUpdateEn() {
        return updateEn;
    }

    public void setUpdateEn(String updateEn) {
        this.updateEn = updateEn;
    }
}
