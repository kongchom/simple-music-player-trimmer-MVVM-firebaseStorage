package g3.viewmusicchoose;

public class ConvertDurationUtils {

    public static String convertDurationText(int duration) {
        if (duration > 0) {
            int minute = duration / 60;
            int second = duration % 60;
            return (minute >= 10 ? minute : "0" + minute) + ":" + (second >= 10 ? second : "0" + second);
        }
        return "00:00";
    }
}
