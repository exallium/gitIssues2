package org.exallium.gitissues.adapters;

import java.util.List;

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Label;
import org.exallium.gitissues.R;
import org.exallium.gitissues.R.id;
import org.exallium.gitissues.listeners.ProgressBarAnimationListener;
import org.exallium.gitissues.utils.Util;

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
import android.widget.TableRow;
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
		LayoutInflater vi;
		
		switch(arg1) {
		case COMMENTS:
			vi = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View first = vi.inflate(R.layout.comment_page, null);
			
			// Grab the progress layout
			View v = first.findViewById(R.id.progress_head);
			
			Log.d("inst", "comments view");
			
			if (comments != null) {
				v.setVisibility(View.GONE);
				
				if (comments.size() != 0) {
					((TextView) first.findViewById(R.id.comments_none)).setVisibility(View.GONE);
					commentListView = (ListView) first.findViewById(R.id.comments_list);
					commentListView.setAdapter(new CommentAdapter(context, R.layout.comment_rowitem, comments));
				} else {
					((TextView) first.findViewById(R.id.comments_none)).setVisibility(View.VISIBLE);
				}
			}
			
			commentView = first;
			view = first;
			break;
		case OVERVIEW:
			vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View over = vi.inflate(R.layout.overview_page, null);
			TableRow labelRow = (TableRow) over.findViewById(R.id.overview_labels);
			
			List<Label> labels = issue.getLabels();
			labelRow.removeAllViews();
			
			for(int j = 0; j < labels.size(); j++) {
				
				TextView lv = new TextView(context);
				Label l = labels.get(j);
				
				int bg = 0xFF000000 | Integer.parseInt(l.getColor(), 16);
				
				lv.setText(l.getName());
				lv.setBackgroundColor(bg);
				lv.setPadding(10, 5, 10, 5);
				lv.setTextSize(25f);
				lv.setTextColor(Util.contrast(bg));
				
				labelRow.addView(lv);
			}
			
			view = over;
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
			if (comments.size() != 0) {
				((TextView) commentView.findViewById(R.id.comments_none)).setVisibility(View.GONE);
				commentListView = (ListView) commentView.findViewById(R.id.comments_list);
				commentListView.setAdapter(new CommentAdapter(context, R.layout.comment_rowitem, comments));
			} else {
				((TextView) commentView.findViewById(R.id.comments_none)).setVisibility(View.VISIBLE);
			}
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