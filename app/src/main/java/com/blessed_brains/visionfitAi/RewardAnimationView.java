package com.blessed_brains.visionfitAi;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

public class RewardAnimationView {

    private Context context;
    private ViewGroup parentLayout;
    int screenWidth;
    int screenHeight;

    public RewardAnimationView(Context context, ViewGroup parentLayout) {
        this.context = context;
        this.parentLayout = parentLayout;
         screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        screenHeight = context.getResources().getDisplayMetrics().heightPixels;



    }

    public void showReward(int x, int y, int points) {
        // Create TextView for the reward
        TextView rewardText = new TextView(context);
        rewardText.setText("+" + points);
        rewardText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
        rewardText.setTextColor(Color.parseColor("#4CAF50")); // Green
        rewardText.setShadowLayer(8, 0, 0, Color.BLACK);
        rewardText.setTypeface(null, android.graphics.Typeface.BOLD);

        // Set position
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.leftMargin = x;
        params.topMargin = y;
        params.gravity = Gravity.TOP | Gravity.START;

        rewardText.setLayoutParams(params);
        parentLayout.addView(rewardText);

        // Create animations
        ObjectAnimator moveUp = ObjectAnimator.ofFloat(rewardText, "translationY", 0f, -300f);
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(rewardText, "alpha", 1f, 0f);
        fadeOut.setStartDelay(500);

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(rewardText, "scaleX", 1f, 1.3f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(rewardText, "scaleY", 1f, 1.3f);
        scaleX.setDuration(500);
        scaleY.setDuration(500);

        // Combine animations
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(moveUp, fadeOut, scaleX, scaleY);
        animatorSet.setDuration(1500);
        animatorSet.setInterpolator(new AccelerateInterpolator());

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                parentLayout.removeView(rewardText);
            }
        });

        animatorSet.start();
    }

    // Show at center of screen
    public void showReward(int points) {

        showReward(screenWidth / 2 - 50, screenHeight / 2 - 50, points);
    }

    // Show at specific view location
    public void showRewardAtView(android.view.View view, int points) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        showReward(location[0] + view.getWidth() / 2, location[1], points);
    }
}