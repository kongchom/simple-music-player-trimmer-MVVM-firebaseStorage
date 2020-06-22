package g3.viewmusicchoose;

import java.io.Serializable;

public class LocalSong implements Serializable {
    private String songTitle = ""; //tên bài hát
    private String songArtist = "";//ca sĩ
    private String songData = "";//dữ liệu bài hát
    private String isChecked = "false";
    private String time;
    private boolean isSelect;

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getSongArtist() {
        return songArtist;
    }

    public void setSongArtist(String songArtist) {
        this.songArtist = songArtist;
    }

    public String getSongData() {
        return songData;
    }

    public void setSongData(String songData) {
        this.songData = songData;
    }

    public String getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(String isChecked) {
        this.isChecked = isChecked;
    }

    public String getDurationText() {
        long time = getTime(this.time);    // cấu hình duration text
        if (time > 0) {
            int minute = (int) (time / 1000 / 60);
            int second = (int) (time / 1000) % 60;
            return (minute >= 10 ? minute : "0" + minute) + ":" + (second >= 10 ? second : "0" + second);
        }
        return "00:00";
    }

    private Long getTime(String strNum) {
        Long time;
        try {
            time = Long.valueOf(strNum);
        } catch (NumberFormatException | NullPointerException nfe) {
            time = 0L;
        }
        return time;
    }

    private int convertDateToDuration(String time) {
        String[] tokens = time.split(":");
        if (tokens.length < 3) {
            int minutes = Integer.parseInt(tokens[0]);
            int seconds = Integer.parseInt(tokens[1]);
            return (60 * minutes + seconds);
        } else {
            int hours = Integer.parseInt(tokens[0]);
            int minutes = Integer.parseInt(tokens[1]);
            int seconds = Integer.parseInt(tokens[2]);
            return 3600 * hours + 60 * minutes + seconds;
        }
    }

    // Get Seconds
    public int getDuration() {
        if (!FunctionUtils.isBlank(time)) {
            if (time.contains(":")) {
                return convertDateToDuration(time);
            }
            long time = Long.valueOf(this.time);
            return (int) (time / 1000);
        }
        return 0;
    }
}
