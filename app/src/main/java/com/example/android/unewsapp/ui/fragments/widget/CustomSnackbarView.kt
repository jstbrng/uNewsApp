package com.example.android.unewsapp.ui.fragments.widget

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.android.unewsapp.R
import com.google.android.material.snackbar.ContentViewCallback
import kotlinx.android.synthetic.main.snackbar_with_button.view.*

class CustomSnackbarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), ContentViewCallback {

    init {
        View.inflate(context, R.layout.snackbar, this)
        clipToPadding = false
    }

    override fun animateContentOut(delay: Int, duration: Int) {

    }

    override fun animateContentIn(delay: Int, duration: Int) {

    }
}