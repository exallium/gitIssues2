package org.exallium.gitissues.adapters;

import java.util.List;

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;
import org.exallium.gitissues.R;
import org.exallium.gitissues.R.id;
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
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class SingleIssuePagerAdapter extends PagerAdapter implements ViewPagerHeaderProvider {
	
	private Context context;
	private String [] titles;
	private List<Comment> comments;
	private Issue issue;
	
	private static final int MODIFY 	= 0;
	private static final int OVERVIEW 	= 1;
	private static final int COMMENTS 	= 2;
	
	private View commentView;
	private ListView commentListView;
	private boolean commentsLoaded = false;
	
	private Animation fadein;
	private Animation fadeout;
	
	public SingleIssuePagerAdapter(Context context, Issue issue) {
		super();
		this.context = context;
		this.comments = null;
		this.issue = issue;
		
		fadein = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
		fadeout = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
		
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
			LayoutInflater vi = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View first = vi.inflate(R.layout.comment_page, null);
			
			// Grab the progress layout
			View v = first.findViewById(R.id.progress_head);
			
			Log.d("inst", "comments view");
			
			if (comments != null) {
				v.setVisibility(View.GONE);
				commentListView = (ListView) first.findViewById(R.id.comments_list);
				commentListView.setAdapter(new CommentAdapter(context, R.layout.comment_rowitem, comments));
			}
			
			commentView = first;
			view = first;
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
		View progress = commentView.findViewById(R.id.progress_head);
		
		Animation a = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
		a.setAnimationListener(new ProgressBarAnimationListener(progress));
		progress.setAnimation(a);
		progress.setVisibility(View.INVISIBLE);
		
		if (comments != null) {
			// Add comments list view items here
			commentListView = (ListView) commentView.findViewById(R.id.comments_list);
			commentListView.setAdapter(new CommentAdapter(context, R.layout.comment_rowitem, comments));
		}
	}
	
	public void setLoading() {
		this.comments = null;
		Animation a = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
		commentView.findViewById(R.id.progress_head).setAnimation(a);
		commentView.findViewById(R.id.progress_head).setVisibility(View.VISIBLE);
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		try {
			return arg0 == (TextView)arg1;
		} catch (Exception e) {
			return arg0 == (View)arg1;
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