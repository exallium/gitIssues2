package org.exallium.gitissues.adapters;

import java.util.List;

import org.eclipse.egit.github.core.Issue;
import org.exallium.gitissues.R;

import com.sturtz.viewpagerheader.ViewPagerHeaderProvider;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ListView;

public class IssuePagerAdapter extends PagerAdapter implements ViewPagerHeaderProvider{
	
	private Context context;
	private String [] titles;
	private List<List<Issue>> issueLists;
	
	public IssuePagerAdapter(Context context, List<List<Issue>> issueLists) {
		super();
		this.context = context;
		this.issueLists = issueLists;
		
		// This should be done better...
		titles = new String[4];
		titles[0] = "";
		titles[1] = "Open";
		titles[2] = "Closed";
		titles[3] = "";
	}

	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		((ViewPager )arg0).removeView((ListView) arg2);
	}

	@Override
	public void finishUpdate(View arg0) {
		
	}

	@Override
	public int getCount() {
		return 2;
	}

	@Override
	public Object instantiateItem(View arg0, int arg1) {
		final ListView lv = new ListView(context);
		final IssueAdapter ad = new IssueAdapter(
				context, R.layout.issue_rowitem, issueLists.get(arg1));
		lv.setAdapter(ad);
		
		((ViewPager) arg0).addView(lv);
		
		return lv;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == (ListView)arg1;
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