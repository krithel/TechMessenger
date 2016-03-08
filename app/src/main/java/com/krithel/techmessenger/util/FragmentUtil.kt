package com.krithel.techmessenger.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.os.Build
import android.support.v4.app.Fragment
import android.util.Log
import android.view.View

/**
 * Created by Krithel on 08-Mar-16.
 */
fun Fragment.debug(msg: String): Unit {
    Log.d(this.javaClass.simpleName, msg)
}

@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
fun Fragment.showProgress(show: Boolean, pageView: View, spinnerView: View) {
    // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
    // for very easy animations. If available, use these APIs to fade-in
    // the progress spinner.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
        val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime)

        pageView.visibility = if (show) View.GONE else View.VISIBLE
        pageView.animate().setDuration(shortAnimTime.toLong()).alpha(
                (if (show) 0 else 1).toFloat()).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                pageView.visibility = if (show) View.GONE else View.VISIBLE
            }
        })

        spinnerView.visibility = if (show) View.VISIBLE else View.GONE
        spinnerView.animate().setDuration(shortAnimTime.toLong()).alpha(
                (if (show) 1 else 0).toFloat()).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                spinnerView.visibility = if (show) View.VISIBLE else View.GONE
            }
        })
    } else {
        // The ViewPropertyAnimator APIs are not available, so simply show
        // and hide the relevant UI components.
        spinnerView.visibility = if (show) View.VISIBLE else View.GONE
        pageView.visibility = if (show) View.GONE else View.VISIBLE
    }
}