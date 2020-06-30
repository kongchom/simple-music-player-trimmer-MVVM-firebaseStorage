package g3.viewmusicchoose;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import g3.viewmusicchoose.ui.effects.EffectAlbum;
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

    public List<EffectAlbum> getEffects() {
        if (data !=null) {
            return data.getEffects();
        }
        return null;
    }

    public void setData(MainData data) {
        this.data = data;
    }

    private class MainData {
        @SerializedName("list_audio")
        private List<Music> audios;

        @SerializedName("list_album")
        private List<Album> albums;

        @SerializedName("list_effects")
        private List<EffectAlbum> effects;

        public List<Music> getAudios() {
            return audios;
        }

        public List<Album> getAlbums() {
            return albums;
        }

        public List<EffectAlbum> getEffects() {
            return effects;
        }

        public void setAudios(List<Music> audios) {
            this.audios = audios;
        }
        public void setAlbums(List<Album> albums) {
            this.albums = albums;
        }

        public void setEffects(List<EffectAlbum> effects) {
            this.effects = effects;
        }
    }
}
