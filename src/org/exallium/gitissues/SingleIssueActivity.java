package org.exallium.gitissues;

import java.io.IOException;
import java.util.List;

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.service.IssueService;
import org.exallium.gitissues.adapters.SingleIssuePagerAdapter;
import org.exallium.gitissues.dialogs.ErrorDialog;
import org.exallium.gitissues.utils.Util;

import com.google.gson.Gson;
import com.sturtz.viewpagerheader.ViewPagerHeader;
import com.sturtz.viewpagerheader.ViewPagerHeaderListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class SingleIssueActivity extends Activity implements ViewPagerHeaderListener{
	
	private Thread commentsThread;
	private Handler commentsHandler;
	
	private SingleIssuePagerAdapter sipa;
	
	private ViewPager pager;
	private ViewPagerHeader pagerHeader;
	private int pagerPosition;
	
	private Issue issue;
	private String [] info;
	
	private static final int SUCCESS = 0;
	private static final int FAILURE = 0;
	
	private List<Comment> comments;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.singleissue);
		
		String data = getIntent().getStringExtra("issue");
		issue = (new Gson()).fromJson(data, Issue.class);
		
		TextView title = (TextView) findViewById(R.id.actionbar_title);
		TextView extra = (TextView) findViewById(R.id.actionbar_extra);
		TextView num   = (TextView) findViewById(R.id.issue_number);
		TextView date  = (TextView) findViewById(R.id.issue_date);
		
		info = Util.parseRepository(issue.getUrl());
		
		title.setText(info[0]);
		extra.setText(info[1]);
		num.setText("#" + issue.getNumber());
		date.setText(DateFormat.format("MM/dd/yyyy", issue.getCreatedAt()));
		
		setupPager();
		setupCommentsThread();
		getComments();
	}
	
	private void setupCommentsThread() {
		commentsThread = new Thread(new Runnable() {
			
			public void run() {
				SharedPreferences prefs = SingleIssueActivity.this.getSharedPreferences("login", MODE_PRIVATE);
				String username = prefs.getString("USERNAME", "");
				String password = prefs.getString("PASSWORD", "");
				
				IssueService issueService = new IssueService();
				issueService.getClient().setCredentials(username, password);
				
				try {
					comments = issueService.getComments(info[0].replace("/", ""), info[1], issue.getNumber());
				} catch (IOException e) {
					e.printStackTrace();
					commentsHandler.sendEmptyMessage(FAILURE);
				}
				
				commentsHandler.sendEmptyMessage(SUCCESS);
			}
		});
		
		commentsHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if(msg.what == SUCCESS) {
					sipa.setComments(comments);
				} else {
					ErrorDialog.show(SingleIssueActivity.this);
				}
			}
		};
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.singleissue_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()) {
		case R.id.sm_comment:
			return true;
		case R.id.sm_edit:
			return true;
		case R.id.sm_labels:
			return true;
		case R.id.sm_toggle:
			AlertDialog.Builder issueDialog = new AlertDialog.Builder(this);
			issueDialog.setTitle("Toggle Status");
			
			String message = "Are you sure you want to close this issue?";
			if (issue.getState().contentEquals(IssueService.STATE_CLOSED)) {
				message = "Are you sure you want to reopen this issue?";
			}
			
			issueDialog.setMessage(message);
			issueDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					if (issue.getState().contentEquals(IssueService.STATE_CLOSED)) {
						issue.setState(IssueService.STATE_OPEN);
						editIssue();
					} else {
						issue.setState(IssueService.STATE_CLOSED);
						if (!editIssue()) {
							ErrorDialog.show(SingleIssueActivity.this, "You can't change this.");
						}
					}
				}
			});
			issueDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// Do Nothing.
				}
			});
			issueDialog.show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
		
	}
	
	private boolean editIssue() {
		String [] str = Util.parseRepository(issue.getUrl());
		str[0] = str[0].substring(0, str[0].length() - 1);
		
		SharedPreferences prefs = SingleIssueActivity.this.getSharedPreferences("login", MODE_PRIVATE);
		String username = prefs.getString("USERNAME", "");
		String password = prefs.getString("PASSWORD", "");
		
		IssueService issueService = new IssueService();
		issueService.getClient().setCredentials(username, password);
		try {
			Issue i = issueService.editIssue(str[0], str[1], issue);
		} catch (IOException e) {
			return false;
		}
		
		return true;
	}
	
	private void getComments() {		
		try {
			commentsThread.start();
		} catch (Exception e) {
			commentsThread.run();
		}
	}
	
	private void setupPager() {
		sipa = new SingleIssuePagerAdapter(this, issue);
		
		pager = (ViewPager) this.findViewById(R.id.singleissuepager);
		pager.setAdapter(sipa);
		
		pagerHeader = (ViewPagerHeader) this.findViewById(R.id.singleissuepagerheader);
		pagerHeader.setViewPager(pager);
		pagerHeader.setViewPagerHeaderListener(this);
		
		pagerPosition = 0;
	}

	public void onPageSelected(int position) {
		pager.setCurrentItem(position);
		pagerPosition = position;
	}

	public void prev() {
		pager.setCurrentItem(--pagerPosition);
	}

	public void next() {
		pager.setCurrentItem(++pagerPosition);
	}

}
