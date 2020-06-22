package lib.mylibutils;

import android.app.Activity;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;


/**
 * Created by dangvangioi on 4/25/16.
 */
public class CommonDialog {

    public static void showDialogConfirm(Activity activity, int message,
                                         String yesText,
                                         String noText,
                                         DialogInterface.OnClickListener onYes,
                                         DialogInterface.OnClickListener onNo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(true);
        builder.setMessage(message);
        builder.setPositiveButton(yesText, onYes);
        builder.setNegativeButton(noText, onNo);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void showInfoDialog(Activity activity, int message,
                                      String yesText,
                                      DialogInterface.OnClickListener onYes) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(true);
        builder.setMessage(message);
        builder.setPositiveButton(yesText, onYes);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void showInfoDialog(Activity activity, boolean isCancel, int message,
                                      String yesText,
                                      DialogInterface.OnClickListener onYes, int theme) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, theme);
        builder.setCancelable(isCancel);
        builder.setMessage(message);
        builder.setPositiveButton(yesText, onYes);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
