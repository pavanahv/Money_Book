package com.example.allakumarreddy.moneybook.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Build;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

public class AnimationUtils {

    private static final String TAG = AnimationUtils.class.getSimpleName();

    public static void revealAnimation(View progress, View mainView, View rootView) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // get the center for the clipping circle
            int cx = rootView.getWidth() / 2;
            int cy = rootView.getHeight() / 2;

            // get the final radius for the clipping circle
            float finalRadius = (float) Math.hypot(cx, cy);

            LoggerCus.d(TAG, "cx " + cx + " cy " + cy + " fradius " + finalRadius);
            // create the animator for this view (the start radius is zero)
            Animator anim = ViewAnimationUtils.createCircularReveal(mainView, cx, cy, 0f, finalRadius);
            anim.setDuration(500);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    progress.setVisibility(View.GONE);
                }
            });
            // make the view visible and start the animation
            mainView.setVisibility(View.VISIBLE);
            anim.start();
        } else {
            // set the view to invisible without a circular reveal animation below Lollipop
            mainView.setVisibility(View.VISIBLE);
        }
    }

    public static Animation translateAnimation(View animatedView,float fromX,float toX,float fromY,float toY) {
        Animation animation = new TranslateAnimation(fromX, toX, fromY, toY);
        animation.setDuration(300);
        animatedView.startAnimation(animation);
        animatedView.setVisibility(View.VISIBLE);
        return animation;
    }
}
