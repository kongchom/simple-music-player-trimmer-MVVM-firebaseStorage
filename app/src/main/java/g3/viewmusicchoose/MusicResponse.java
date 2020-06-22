package g3.viewmusicchoose;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MusicResponse extends BaseResponse {

    private MainData data;

    public MainData getData() {
        return data;
    }

    public List<Music> getMusic() {
        if (data !=null) {
            return data.getAudioes();
        }
        return null;
    }

    public void setData(MainData data) {
        this.data = data;
    }

    private class MainData {
        @SerializedName("list_audio")
        private List<Music> audioes;


        public List<Music> getAudioes() {
            return audioes;
        }

        public void setAudioes(List<Music> audioes) {
            this.audioes = audioes;
        }
    }
}
