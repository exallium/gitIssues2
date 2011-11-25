package org.exallium.gitissues.utils;

import android.graphics.Color;

public class Util {
	
	private static String prefix = "https://api.github.com/repos/";
	
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
	
	public static String [] parseRepository(String url) {
		String [] info = new String[2];
		url = url.replace(prefix, "");
		info[0] = url.substring(0, url.indexOf("/") + 1);
		url = url.substring(info[0].length());
		info[1] = url.substring(0, url.indexOf("/"));
		return info;
	}
}
