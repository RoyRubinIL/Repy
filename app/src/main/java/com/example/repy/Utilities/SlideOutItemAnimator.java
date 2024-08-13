package com.example.repy.Utilities;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

public class SlideOutItemAnimator extends DefaultItemAnimator {
    private Context context;

    public SlideOutItemAnimator(Context context) {
        this.context = context;
    }

    @Override
    public boolean animateRemove(RecyclerView.ViewHolder holder) {
        final RecyclerView.ViewHolder viewHolder = holder;
        final View view = viewHolder.itemView;

        TranslateAnimation translateAnimation = new TranslateAnimation(0, -((View) view).getWidth(), 0, 0);
        translateAnimation.setDuration(300);
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                dispatchRemoveFinished(viewHolder);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        view.startAnimation(translateAnimation);
        return true;
    }
}
