package g3.viewmusicchoose;

import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

public class ResizeView {
    private static final int widthStand= 1080;

    protected static DisplayMetrics getDisplayInfo() {
        return Resources.getSystem().getDisplayMetrics();
    }

    public static void resizeView(View view, int width, int height) {
        int pW = getDisplayInfo().widthPixels * width / widthStand;
        int pH = pW * height / width;
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = pW;
        params.height = pH;
    }
}
