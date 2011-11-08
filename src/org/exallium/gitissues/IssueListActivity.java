package org.exallium.gitissues;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.service.IssueService;
import org.exallium.gitissues.adapters.IssueAdapter;
import org.exallium.gitissues.adapters.IssuePagerAdapter;
import org.exallium.gitissues.adapters.MainPagerAdapter;
import org.exallium.gitissues.dialogs.ErrorDialog;

import com.sturtz.viewpagerheader.ViewPagerHeader;
import com.sturtz.viewpagerheader.ViewPagerHeaderListener;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.widget.ListView;
import android.widget.TextView;

public class IssueListActivity extends Activity implements ViewPagerHeaderListener {
	private IssuePagerAdapter issuePagerAdapter;
	private ViewPager issuePager;
	private ViewPagerHeader issuePagerHeader;
	private int issuePagerPosition;
	
	private String owner;
	private String repo;
	
	private Thread issuesThread;
	private Handler issuesHandler;
	
	private List<Issue> openIssues;
	private List<Issue> closedIssues;
	
	private int SUCCESS = 0;
	private int FAILURE = 1;
	
	private SharedPreferences prefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.issuelist);
		
		issuePagerPosition = 0;
		
		TextView ownerView = (TextView) findViewById(R.id.issue_owner);
		TextView repoView = (TextView) findViewById(R.id.issue_repo);
		
		owner = this.getIntent().getStringExtra("owner");
		repo = this.getIntent().getStringExtra("repo");
		
		ownerView.setText(owner + "/");
		repoView.setText(repo);
		
		buildIssuesThread();
		populateIssuesThread();

	}
	
	public void setupPager() {
    	List<List<Issue>> issueLists = new ArrayList<List<Issue>>();
    	issueLists.add(openIssues);
    	issueLists.add(closedIssues);
    	
    	issuePagerAdapter = new IssuePagerAdapter(this, issueLists);
        issuePager = (ViewPager) findViewById(R.id.issuepager);
        issuePager.setAdapter(issuePagerAdapter);
        
        issuePagerHeader = (ViewPagerHeader) findViewById(R.id.issuepagerheader);
        issuePagerHeader.setViewPager(issuePager);
        issuePagerHeader.setViewPagerHeaderListener(this);
    }

	private void populateIssuesThread() {
		try {
			issuesThread.start();
		} catch (Exception e) {
			issuesThread.run();
		}
	}

	private void buildIssuesThread() {
		issuesThread = new Thread(new Runnable() {
			public void run() {
				prefs = getApplicationContext().getSharedPreferences("login", MODE_PRIVATE);
				String user = prefs.getString("USERNAME", "");
				String password = prefs.getString("PASSWORD", "");
				
				if(user.contentEquals("")) return;
				
				IssueService issueService = new IssueService();
				issueService.getClient().setCredentials(user, password);
				
				try {
					//Map<String, String> openMap = new Map<String, String>();
					
					openIssues = issueService.getIssues(owner, repo, null);
					closedIssues = issueService.getIssues(owner, repo, null);
					issuesHandler.sendEmptyMessage(SUCCESS);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					issuesHandler.sendEmptyMessage(FAILURE);
				}
			}
		});
		
		issuesHandler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if(msg.what == SUCCESS) {
					setupPager();
				} else {
					ErrorDialog.show(IssueListActivity.this);
				}
			};
		};
	}

	public void onPageSelected(int position) {
		// TODO Auto-generated method stub
		
	}

	public void prev() {
		// TODO Auto-generated method stub
		
	}

	public void next() {
		// TODO Auto-generated method stub
		
	}
}
