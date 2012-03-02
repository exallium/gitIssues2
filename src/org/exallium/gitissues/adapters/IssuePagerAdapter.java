package org.exallium.gitissues.adapters;

import java.util.List;

import org.eclipse.egit.github.core.Issue;
import org.exallium.gitissues.R;
import org.exallium.gitissues.listeners.ProgressBarAnimationListener;

import com.sturtz.viewpagerheader.ViewPagerHeaderProvider;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListView;

public class IssuePagerAdapter extends PagerAdapter implements ViewPagerHeaderProvider{
	
	private Context context;
	private String [] titles;
	private List<List<Issue>> issueLists;
	private View[] pages;
	
	public IssuePagerAdapter(Context context) {
		super();
		this.context = context;
		
		pages = new View[2];
		issueLists = null;
		
		// This should be done better...
		titles = new String[4];
		titles[0] = "";
		titles[1] = "Open";
		titles[2] = "Closed";
		titles[3] = "";
	}
	
	public void setIssueLists(List<List<Issue>> issueLists) {
		this.issueLists = issueLists;
		
		for(int i = 0; i < 2; i++) {
			Animation a = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
			View v = pages[i].findViewById(R.id.progress_head);
			a.setAnimationListener(new ProgressBarAnimationListener(v));
			v.setAnimation(a);
			v.setVisibility(View.INVISIBLE);
			
			ListView lv = (ListView) pages[i].findViewById(R.id.main_list);
			lv.setAdapter(new IssueAdapter(context, R.layout.issue_rowitem, issueLists.get(i)));
		}
	}

	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		((ViewPager )arg0).removeView((View) arg2);
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
		Log.d("INST", "INSTANTIATING " + arg1);
		LayoutInflater vi = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = vi.inflate(R.layout.main_page, null);
		
		if (issueLists != null) {
			Log.d("ISSUESLIST", "NOTNULL");
			v.findViewById(R.id.progress_head).setVisibility(View.GONE);
			ListView lv = (ListView) v.findViewById(R.id.main_list);
			lv.setAdapter(new IssueAdapter(context, R.layout.issue_rowitem, issueLists.get(arg1)));
		}
		
		pages[arg1] = v;
		((ViewPager) arg0).addView(v);
		
		return v;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == (View)arg1;
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