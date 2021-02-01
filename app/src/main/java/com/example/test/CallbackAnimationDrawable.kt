package com.example.test

import android.graphics.drawable.AnimationDrawable
import android.os.Handler
import android.support.v4.os.HandlerCompat.postDelayed
import android.os.Looper



class CallbackAnimationDrawable : AnimationDrawable() {
    interface OnAnimDrawableCallback {
        fun onAnimationOneShotFinished()
    }

    private var adListener: OnAnimDrawableCallback? = null
    private var timingHandler: Handler? = null

    fun initListener(callback: OnAnimDrawableCallback){
        this.adListener = callback

        for (i in 0 until this.numberOfFrames) {
            this.addFrame(this.getFrame(i), this.getDuration(i))
        }
        timingHandler = Handler(Looper.getMainLooper())
    }

    override fun selectDrawable(index: Int): Boolean {
        if (index != 0 && index == numberOfFrames - 1) {
            timingHandler!!.postDelayed(resultListenerRunnable, this.getDuration(index) as Long)
        }
        return super.selectDrawable(index)
    }

    private val resultListenerRunnable = Runnable { if (adListener != null) adListener!!.onAnimationOneShotFinished() }
}