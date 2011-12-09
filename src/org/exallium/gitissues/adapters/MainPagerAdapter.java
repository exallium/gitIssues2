package org.exallium.gitissues.adapters;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.egit.github.core.Repository;
import org.exallium.gitissues.R;
import org.exallium.gitissues.listeners.ProgressBarAnimationListener;

import com.sturtz.viewpagerheader.ViewPagerHeaderProvider;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListView;

public class MainPagerAdapter extends PagerAdapter implements ViewPagerHeaderProvider{
	
	private Context context;
	private String [] titles;
	private List<List<Repository>> repoLists;
	private View[] pages;
	
	public MainPagerAdapter(Context context) {
		super();
		this.context = context;
		this.repoLists = null;
		
		pages = new View[3];
		
		// This should be done better...
		titles = new String[5];
		titles[0] = "";
		titles[1] = "Watched";
		titles[2] = "Personal";
		titles[3] = "Organization";
		titles[4] = "";
	}
	
	public void setRepoLists(List<List<Repository>> repoLists) {
		this.repoLists = repoLists;
		
		for(int i = 0; i < 3; i++) {
			Animation a = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
			View v = pages[i].findViewById(R.id.progress_head);
			a.setAnimationListener(new ProgressBarAnimationListener(v));
			v.setAnimation(a);
			v.setVisibility(View.INVISIBLE);
			
			ListView lv = (ListView) pages[i].findViewById(R.id.main_list);
			lv.setAdapter(new RepositoryAdapter(context, R.layout.repository_rowitem, repoLists.get(i)));
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
		return 3;
	}

	@Override
	public Object instantiateItem(View arg0, int arg1) {
		
		LayoutInflater vi = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = vi.inflate(R.layout.main_page, null);
		
		if (repoLists != null) {
			v.findViewById(R.id.progress_head).setVisibility(View.GONE);
			ListView lv = (ListView) v.findViewById(R.id.main_list);
			lv.setAdapter(new RepositoryAdapter(context, R.layout.repository_rowitem, repoLists.get(arg1)));
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
