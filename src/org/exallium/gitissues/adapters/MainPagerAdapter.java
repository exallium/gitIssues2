package org.exallium.gitissues.adapters;

import com.sturtz.viewpagerheader.ViewPagerHeaderProvider;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainPagerAdapter extends PagerAdapter implements ViewPagerHeaderProvider{
	
	private Context context;
	private String [] titles;
	
	public MainPagerAdapter(Context context) {
		super();
		this.context = context;
		
		// This should be done better...
		titles = new String[5];
		titles[0] = "";
		titles[1] = "Watched";
		titles[2] = "Personal";
		titles[3] = "Organization";
		titles[4] = "";
	}

	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		((ViewPager )arg0).removeView((TextView) arg2);
	}

	@Override
	public void finishUpdate(View arg0) {
		
	}

	@Override
	public int getCount() {
		return 3;
	}

	@Override
	public Object instantiateItem(View arg0, int arg1) {
		TextView tv = new TextView(context);
		tv.setText("No Content, page " + arg1);
		
		((ViewPager) arg0).addView(tv);
		return tv;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == (TextView)arg1;
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
		
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) {
		
	}

	public String getTitle(int position) {
		return titles[++position];
	}
	
}
