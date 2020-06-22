package lib.mylibutils

import android.view.MotionEvent
import android.view.View

interface OnCustomTouchListener {
    fun OnCustomTouchDown(v: View?, event: MotionEvent?)
    fun OnCustomTouchMoveOut(v: View?, event: MotionEvent?)
    fun OnCustomTouchUp(v: View?, event: MotionEvent?)
    fun OnCustomTouchOther(v: View?, event: MotionEvent?)
}