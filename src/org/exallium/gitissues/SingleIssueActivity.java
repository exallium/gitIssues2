package org.exallium.gitissues;

import java.io.IOException;
import java.util.List;

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.UserService;
import org.exallium.gitissues.adapters.SingleIssuePagerAdapter;
import org.exallium.gitissues.dialogs.ErrorDialog;
import org.exallium.gitissues.utils.Util;

import com.google.gson.Gson;
import com.sturtz.viewpagerheader.ViewPagerHeader;
import com.sturtz.viewpagerheader.ViewPagerHeaderListener;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.format.DateFormat;
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
	
	private void getComments() {
		// set comments view to progress thingy
		
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
		pagerHeader.setCurrentItem(1);
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
