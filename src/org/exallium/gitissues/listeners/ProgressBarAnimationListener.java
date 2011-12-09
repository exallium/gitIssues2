package org.exallium.gitissues.listeners;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

public class ProgressBarAnimationListener implements AnimationListener {
	
	View v;
	
	public ProgressBarAnimationListener(View v) {
		this.v = v;
	}

	public void onAnimationEnd(Animation animation) {
		v.setVisibility(View.GONE);
	}

	public void onAnimationRepeat(Animation animation) {
		
	}

	public void onAnimationStart(Animation animation) {
		
	}

}
