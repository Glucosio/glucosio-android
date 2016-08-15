package org.glucosio.android.tools;

import android.animation.Animator;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AlphaAnimation;
import android.view.animation.DecelerateInterpolator;

public class AnimationTools {
    public static void startCircularReveal(View view) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            // get Fab's center
            int cx = view.getWidth() / 2;
            int cy = view.getHeight() / 2;

            // get the final radius for the clipping circle
            float finalRadius = (float) Math.max(view.getWidth(), view.getHeight());

            // create the animator for this view (the start radius is zero)
            Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);
            // make the view visible and start the animation
            view.setVisibility(View.VISIBLE);
            anim.setInterpolator(new DecelerateInterpolator());
            anim.setDuration(1000);
            anim.start();
        } else {
            view.setVisibility(View.VISIBLE);
            AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
            anim.setDuration(500);
            view.startAnimation(anim);
        }
    }
}
