package lib.mylibutils

import android.view.MotionEvent
import android.view.View

interface OnCustomClickListener {
    fun OnCustomClick(v: View?, event: MotionEvent?)
}