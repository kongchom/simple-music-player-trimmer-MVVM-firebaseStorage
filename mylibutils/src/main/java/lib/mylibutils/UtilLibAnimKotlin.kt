package lib.mylibutils

import android.content.Context
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.animation.Animation
import android.view.animation.AnimationUtils


class UtilLibAnimKotlin {
    companion object {
        @JvmStatic
        fun scaleView(context: Context, v: View, id: Int) {
            val scaleDown: Animation = AnimationUtils.loadAnimation(context, id)
            v.startAnimation(scaleDown)
        }

        @JvmStatic
        fun setOnCustomTouchView(
                view: View,
                onCustomTouchListener: OnCustomTouchListener?
        ) {
            view.setOnTouchListener(object : OnTouchListener {
                private var rect: Rect? = null
                override fun onTouch(v: View, event: MotionEvent): Boolean {
                    if (onCustomTouchListener == null) return false
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        rect = Rect(
                                v.left,
                                v.top,
                                v.right,
                                v.bottom
                        )
                        onCustomTouchListener.OnCustomTouchDown(v, event)
                    } else if (rect != null && !rect!!.contains(
                                    v.left + event.x.toInt(),
                                    v.top + event.y.toInt()
                            )
                    ) {
                        onCustomTouchListener.OnCustomTouchMoveOut(v, event)
                        return false
                    } else if (event.action == MotionEvent.ACTION_UP) {
                        onCustomTouchListener.OnCustomTouchUp(v, event)
                    } else {
                        onCustomTouchListener.OnCustomTouchOther(v, event)
                    }
                    return true
                }
            })
        }


        //----------------------------------------------------------------------------------------------
        @JvmStatic
        fun setOnCustomTouchViewScale(
                view: View,
                customClickListener: OnCustomClickListener?
        ) {
            setOnCustomTouchView(view, object : OnCustomTouchListener {
                private fun setScale(scale: Float) {
                    view.scaleX = scale
                    view.scaleY = scale
                }

                override fun OnCustomTouchDown(
                        v: View?,
                        event: MotionEvent?
                ) {
                    setScale(0.9f)
                }

                override fun OnCustomTouchMoveOut(
                        v: View?,
                        event: MotionEvent?
                ) {
                    setScale(1f)
                }

                override fun OnCustomTouchUp(v: View?, event: MotionEvent?) {
                    setScale(1f)
                    customClickListener?.OnCustomClick(v, event)
                }

                override fun OnCustomTouchOther(
                        v: View?,
                        event: MotionEvent?
                ) {
                    setScale(1f)
                }
            })
        }

        //----------------------------------------------------------------------------------------------
        @JvmStatic
        fun setOnCustomTouchViewScaleNotOther(
                view: View,
                customClickListener: OnCustomClickListener?
        ) {
            setOnCustomTouchView(view, object : OnCustomTouchListener {
                private fun setScale(scale: Float) {
                    view.scaleX = scale
                    view.scaleY = scale
                }

                override fun OnCustomTouchDown(
                        v: View?,
                        event: MotionEvent?
                ) {
                    setScale(0.9f)
                }

                override fun OnCustomTouchMoveOut(
                        v: View?,
                        event: MotionEvent?
                ) {
                    setScale(1f)
                }

                override fun OnCustomTouchUp(v: View?, event: MotionEvent?) {
                    setScale(1f)
                    customClickListener?.OnCustomClick(v, event)
                }

                override fun OnCustomTouchOther(
                        v: View?,
                        event: MotionEvent?
                ) {
                }
            })
        }

        //----------------------------------------------------------------------------------------------

        @JvmStatic
        fun setOnCustomTouchViewAlphaNotOther(
                view: View,
                customClickListener: OnCustomClickListener?
        ) {
            setOnCustomTouchView(view, object : OnCustomTouchListener {
                var isTouchDown = false
                var isTouchMoveOutAndUp = false
                private fun setAlpha(alpha: Float) {
                    view.alpha = alpha
                }

                override fun OnCustomTouchDown(
                        v: View?,
                        event: MotionEvent?
                ) {
                    isTouchDown = true
                    setAlpha(0.7f)
                }

                override fun OnCustomTouchMoveOut(
                        v: View?,
                        event: MotionEvent?
                ) {
                    isTouchMoveOutAndUp = true
                    setAlpha(1f)
                }

                override fun OnCustomTouchUp(v: View?, event: MotionEvent?) {
                    isTouchMoveOutAndUp = true
                    setAlpha(1f)
                    customClickListener?.OnCustomClick(v, event)
                }

                override fun OnCustomTouchOther(
                        v: View?,
                        event: MotionEvent?
                ) {
                    if (!isTouchMoveOutAndUp && isTouchDown) setAlpha(1f)
                    isTouchDown = false
                    isTouchMoveOutAndUp = false
                }
            })
        }
    }


}

