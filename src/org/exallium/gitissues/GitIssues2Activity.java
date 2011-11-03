package org.exallium.gitissues;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.service.OrganizationService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.exallium.gitissues.adapters.MainPagerAdapter;
import org.exallium.gitissues.adapters.RepositoryAdapter;
import org.exallium.gitissues.dialogs.ErrorDialog;
import org.exallium.gitissues.listeners.NewsDrawerListener;

import com.sturtz.viewpagerheader.ViewPagerHeader;
import com.sturtz.viewpagerheader.ViewPagerHeaderListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SlidingDrawer;

public class GitIssues2Activity extends Activity implements ViewPagerHeaderListener {
	
	private MainPagerAdapter mainPagerAdapter;
	private ViewPager mainPager;
	private ViewPagerHeader mainPagerHeader;
	private int mainPagerPosition;
	private NewsDrawerListener newsDrawerListener;
	private SlidingDrawer newsDrawer;
	private SharedPreferences prefs;
	
	private Thread repositoryThread;
	private Handler repositoryHandler;
	
	private List<Repository> personal;
	private List<Repository> organization;
	private List<Repository> watched;
	
	private static int SUCCESS = 0;
	private static int FAILURE = 1;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        prefs = this.getSharedPreferences("login", MODE_PRIVATE);
        if(prefs.getString("USERNAME", "").contentEquals("")) {
        	Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        	startActivity(i);
        	this.finish();
        }
         
        buildThread();
        populateRepositories();
        
        // Temporary to test clearing credentials and restarting app
        Button btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				SharedPreferences.Editor edit = prefs.edit();
				edit.putString("USERNAME", "");
				edit.putString("PASSWORD", "");
				edit.commit();
			}
		});
    }
    
    private void populateRepositories() {
		try {
			repositoryThread.start();
		} catch (Exception e) {
			repositoryThread.run();
		}
	}

	private void buildThread() {
		repositoryThread = new Thread(new Runnable() {
			
			public void run() {
				SharedPreferences prefs = GitIssues2Activity.this.getSharedPreferences("login", MODE_PRIVATE);
				String username = prefs.getString("USERNAME", "");
				String password = prefs.getString("PASSWORD", "");
				
				RepositoryService repoService = new RepositoryService();
				repoService.getClient().setCredentials(username, password);
				
				//OrganizationService orgService = new OrganizationService();
				//orgService.getClient().setCredentials(username, password);
				
				try {
					personal = repoService.getRepositories(username);
					
					if(personal == null) {
						Log.d("ASDF", "NULL");
					}
					//organization = orgService.getOrganizations(username);
					//watched = 
					repositoryHandler.sendEmptyMessage(SUCCESS);
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					repositoryHandler.sendEmptyMessage(FAILURE);
				}
			}
		});
		
		repositoryHandler = new Handler() {
			public void handleMessage(Message msg) {
				if(msg.what == SUCCESS) { 
					setupPager();
			        setupDrawer();
				} else {
					ErrorDialog.show(GitIssues2Activity.this);
				}
			};
		};
	}

	public void setupDrawer() {
    	newsDrawerListener = new NewsDrawerListener();
    	newsDrawer = (SlidingDrawer) findViewById(R.id.drawer);
    	newsDrawer.setOnDrawerOpenListener(newsDrawerListener);
    	newsDrawer.setOnDrawerCloseListener(newsDrawerListener);
    	newsDrawer.setOnDrawerScrollListener(newsDrawerListener);
    }
    
    public void setupPager() {
    	
    	List<List<Repository>> repoLists = new ArrayList<List<Repository>>();
    	repoLists.add(personal);
    	repoLists.add(personal);
    	repoLists.add(personal);
    	
    	mainPagerAdapter = new MainPagerAdapter(this, repoLists);
        mainPager = (ViewPager) findViewById(R.id.mainpager);
        mainPager.setAdapter(mainPagerAdapter);
        
        mainPagerHeader = (ViewPagerHeader) findViewById(R.id.mainpagerheader);
        mainPagerHeader.setViewPager(mainPager);
        mainPagerHeader.setViewPagerHeaderListener(this);
        
        mainPager.setCurrentItem(1);
        mainPagerHeader.setCurrentItem(1);
        mainPagerPosition = 1;
    }

	public void onPageSelected(int position) {
		mainPagerPosition = position;
	}

	public void prev() {
		mainPager.setCurrentItem(--mainPagerPosition);
	}

	public void next() {
		mainPager.setCurrentItem(++mainPagerPosition);
	}
}