package org.exallium.gitissues.adapters;

import java.util.List;

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;
import com.sturtz.viewpagerheader.ViewPagerHeaderProvider;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class SingleIssuePagerAdapter extends PagerAdapter implements ViewPagerHeaderProvider{
	
	private Context context;
	private String [] titles;
	private List<Comment> comments;
	private Issue issue;
	
	private static final int MODIFY 	= 0;
	private static final int OVERVIEW 	= 1;
	private static final int COMMENTS 	= 2;
	
	private ViewSwitcher commentView;
	private ListView commentListView;
	private boolean commentsLoaded = false;
	
	public SingleIssuePagerAdapter(Context context, Issue issue) {
		super();
		this.context = context;
		this.comments = null;
		this.issue = issue;
		
		// This should be done better...
		titles = new String[5];
		titles[0] = "";
		titles[1] = "Modify";
		titles[2] = "Overview";
		titles[3] = "Comments";
		titles[4] = "";
	}

	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		((ViewPager )arg0).removeViewAt(arg1);
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
		View view;
		
		switch(arg1) {
		case COMMENTS:
			ViewSwitcher v = new ViewSwitcher(context);
			
			TextView first = new TextView(context);
			ListView second = new ListView(context);
			
			TextView second2 = new TextView(context);
			
			first.setText("first");
			second2.setText("second");
			
			v.addView(first, 0);
			v.addView(second2, 1);
			
			Log.d("inst", "comments view");
			
			if (commentsLoaded) {
				second2.setText("" + comments.size() + " comments");
				v.setDisplayedChild(1);
			}
			
			commentView = v;
			view = v;
			break;
		default:
			TextView tv = new TextView(context);
			tv.setText("ASDF");
			view = tv;
			break;	
		}	
		
		((ViewPager)arg0).addView(view, arg1);
		return view;
	}
	
	public void setComments(List<Comment> comments) {
		this.comments = comments;
		
		// Build comments list view
		
		commentView.setDisplayedChild(1);
	}
	
	public void setLoading() {
		commentView.setDisplayedChild(0);
		this.comments = null;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		try {
			return arg0 == (TextView)arg1;
		} catch (Exception e) {
			return arg0 == (ViewSwitcher)arg1;
		}
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