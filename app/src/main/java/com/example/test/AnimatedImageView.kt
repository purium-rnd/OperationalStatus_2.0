package com.example.test

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.AnimationDrawable
import android.util.AttributeSet
import android.widget.ImageView


class AnimatedImageView : ImageView {
    private var mAnim: AnimationDrawable? = null
    private var mAttached: Boolean = false

    /**
     * 기본 생성자
     *
     * @param context 컨텍스트
     */
    constructor(context: Context) : super(context) {}

    /**
     * 기본 생성자
     *
     * @param context 생성자
     * @param attrs 속성들
     */
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    /**
     * 애니메이션을 변경하고 시작합니다.
     */
    private fun updateAnim() {
        val drawable = getDrawable()
        if (mAttached && mAnim != null) {
            mAnim!!.stop()
        }
        if (drawable is AnimationDrawable) {
            mAnim = drawable
            if (mAttached) {
                mAnim!!.start()
            }
        } else {
            mAnim = null
        }
    }

    override fun setImageDrawable(drawable: Drawable) {
        super.setImageDrawable(drawable)
        updateAnim()
    }

    override fun setImageResource(resid: Int) {
        super.setImageDrawable(null)
        super.setImageResource(resid)
        updateAnim()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (mAnim != null) {
            mAnim!!.start()
        }
        mAttached = true
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (mAnim != null) {
            mAnim!!.stop()
        }
        mAttached = false
    }

}