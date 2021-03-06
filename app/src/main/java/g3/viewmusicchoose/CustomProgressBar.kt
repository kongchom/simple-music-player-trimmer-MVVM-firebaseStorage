package g3.viewmusicchoose

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import androidx.annotation.NonNull
import androidx.core.content.res.ResourcesCompat
import kotlinx.android.synthetic.main.progress_bar.view.*

class CustomProgressBar(context: Context) {

    var dialog: Dialog = Dialog(context,R.style.CustomProgressBarTheme)

    fun show(context: Context): Dialog {
        return show(context, null)
    }

    fun show(context: Context, title: CharSequence?): Dialog {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.progress_bar, null)
        if (title != null) {
            view.cp_title.text = title
        }
        view.cp_bg_view.setBackgroundColor(Color.parseColor("#60000000")) //Background Color
        view.cp_cardview.setCardBackgroundColor(Color.parseColor("#70000000")) //Box Color
        setColorFilter(view.cp_pbar.indeterminateDrawable,
                ResourcesCompat.getColor(context.resources, R.color.c_black_alpha_70, null)) //Progress Bar Color
        view.cp_title.setTextColor(Color.WHITE) //Text Color
        dialog.setContentView(view)
        dialog.show()
        return dialog
    }

    fun dismiss() {
        dialog.dismiss()
    }

    private fun setColorFilter(@NonNull drawable: Drawable, color: Int) {
        drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
    }
}