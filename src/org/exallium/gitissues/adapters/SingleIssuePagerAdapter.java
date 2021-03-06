package org.exallium.gitissues.adapters;

import java.util.List;

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Label;
import org.eclipse.egit.github.core.Milestone;
import org.eclipse.egit.github.core.User;
import org.exallium.gitissues.R;
import org.exallium.gitissues.listeners.ProgressBarAnimationListener;
import org.exallium.gitissues.utils.Util;

import com.sturtz.viewpagerheader.ViewPagerHeaderProvider;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;

public class SingleIssuePagerAdapter extends PagerAdapter implements ViewPagerHeaderProvider {
	
	private Context context;
	private String [] titles;
	private List<Comment> comments;
	private Issue issue;
	
	private static final int OVERVIEW 	= 0;
	private static final int COMMENTS 	= 1;
	
	private View commentView;
	private ListView commentListView;

	
	public SingleIssuePagerAdapter(Context context, Issue issue) {
		super();
		this.context = context;
		this.comments = null;
		this.issue = issue;
		
		// This should be done better...
		titles = new String[4];
		titles[0] = "";
		titles[1] = "Overview";
		titles[2] = "Comments";
		titles[3] = "";
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
		return 2;
	}

	@Override
	public Object instantiateItem(View arg0, int arg1) {
		View view = null;
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
			
			TextView login = (TextView) over.findViewById(R.id.overview_login);
			login.setText(issue.getUser().getLogin());
			
			TextView created = (TextView) over.findViewById(R.id.overview_created);
			created.setText("created on " + DateFormat.format("MM/dd/yyyy", issue.getCreatedAt()));
			
			TextView milestone = (TextView) over.findViewById(R.id.overview_milestone);
			Milestone m = issue.getMilestone();
			String m_text = (m != null) ? m.getTitle() : "No Milestone";
			milestone.setText(m_text);
			
			TextView assigned = (TextView) over.findViewById(R.id.overview_assigned);
			User a = issue.getAssignee();
			String a_text = (a != null) ? a.getLogin() : "No Assignee";
			assigned.setText(a_text);
			
			TextView description = (TextView) over.findViewById(R.id.overview_description);
			description.setText(issue.getBody());
			
			view = over;
			break;
			default:
				TextView tv = new TextView(context);
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