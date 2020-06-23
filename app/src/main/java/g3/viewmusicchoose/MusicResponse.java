package g3.viewmusicchoose;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import g3.viewmusicchoose.ui.featured.model.Album;

public class MusicResponse extends BaseResponse {

    private MainData data;

    public MainData getData() {
        return data;
    }

    public List<Music> getMusic() {
        if (data !=null) {
            return data.getAudios();
        }
        return null;
    }

    public List<Album> getAlbum() {
        if (data !=null) {
            return data.getAlbums();
        }
        return null;
    }

    public void setData(MainData data) {
        this.data = data;
    }

    private class MainData {
        @SerializedName("list_audio")
        private List<Music> audioes;

        @SerializedName("list_album")
        private List<Album> albums;


        public List<Music> getAudios() {
            return audioes;
        }

        public List<Album> getAlbums() {
            return albums;
        }

        public void setAudioes(List<Music> audioes) {
            this.audioes = audioes;
        }
        public void setAlbums(List<Album> albums) {
            this.albums = albums;
        }
    }
}
