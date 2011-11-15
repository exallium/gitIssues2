package org.exallium.gitissues.utils;

import android.graphics.Color;

public class Util {
	public static int contrast(int argb) {
		int d;
		
		double a = 1 - (0.299 * Color.red(argb) + 0.587 * Color.green(argb) + 0.144 * Color.blue(argb))/255;
		
		if(a < 0.5) {
			d = 0;
		} else {
			d = 255;
		}
		
		return Color.argb(255, d, d, d);
	}
}
