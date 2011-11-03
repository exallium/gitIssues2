package org.exallium.gitissues.adapters;

import java.util.List;

import org.eclipse.egit.github.core.Repository;
import org.exallium.gitissues.R;

import com.sturtz.viewpagerheader.ViewPagerHeaderProvider;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ListView;

public class MainPagerAdapter extends PagerAdapter implements ViewPagerHeaderProvider{
	
	private Context context;
	private String [] titles;
	private List<List<Repository>> repoLists;
	
	public MainPagerAdapter(Context context, List<List<Repository>> repoLists) {
		super();
		this.context = context;
		this.repoLists = repoLists;
		
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
		((ViewPager )arg0).removeView((ListView) arg2);
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
		ListView lv = new ListView(context);
		lv.setAdapter(new RepositoryAdapter(context, R.layout.repository_rowitem, repoLists.get(arg1)));
		
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
