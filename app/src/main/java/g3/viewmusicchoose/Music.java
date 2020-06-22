package g3.viewmusicchoose;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class Music extends RealmObject {

    @PrimaryKey
    @SerializedName("key_id")
    private String keyId;

    @SerializedName("audio_name")
    private String name;
    private int duration;
    @Ignore
    private boolean isSelect;
    @SerializedName("audio_url")
    private String url;
    private boolean isDownload;
    @Ignore
    private boolean isLoading;
    @Ignore
    private boolean isAssetAudioFile = false;

    public Music() {
        //no-op
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public boolean isDownload() {
        return isDownload;
    }

    public void setDownload(boolean download) {
        isDownload = download;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getDuration() {
        return duration;
    }

    public String getDurationText() {
        if (duration > 0) {
            int minute = duration / 60;
            int second = duration % 60;
            return (minute > 10 ? minute : "0" + minute) + ":" + (second > 10 ? second : "0" + second);
        }
        return "00:00";
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public String getAudioFileName() {
        return isAssetAudioFile ? name : keyId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public boolean isAssetAudioFile() {
        return isAssetAudioFile;
    }

    public void setAssetAudioFile(boolean assetAudioFile) {
        isAssetAudioFile = assetAudioFile;
    }
}
