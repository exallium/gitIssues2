package org.exallium.gitissues;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.egit.github.core.IssueEvent;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.OrganizationService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.egit.github.core.service.UserService;
import org.eclipse.egit.github.core.service.WatcherService;
import org.exallium.gitissues.adapters.IssueEventAdapter;
import org.exallium.gitissues.adapters.MainPagerAdapter;
import org.exallium.gitissues.dialogs.ErrorDialog;
import org.exallium.gitissues.listeners.NewsDrawerListener;
import com.sturtz.viewpagerheader.ViewPagerHeader;
import com.sturtz.viewpagerheader.ViewPagerHeaderListener;

import android.app.Activity;
import android.app.ProgressDialog;
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
	private ProgressDialog pd;
	
	private Thread newsThread;
	private Handler newsHandler;
	
	private List<Repository> personal;
	private List<Repository> organization;
	private List<Repository> watched;
	
	private List<IssueEvent> newsFeed;
	private List<IssueEvent> personalFeed;
	private List<IssueEvent> organizationFeed;
	private List<IssueEvent> watchedFeed;
	private ListView newsFeedView;
	
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
        
        newsFeedView = (ListView) findViewById(R.id.newsfeed);
        
        buildRepoThread();
        populateRepositories();
        buildNewsThread();
        
    }
    
    // This can only be called after we have all the repositories...
    private void populateNews() {
    	
    	// Need to set progress indicator for news slider here
    	
    	try {
    		newsThread.start();
    	} catch(Exception e) {
    		newsThread.run();
    	}
    }
    
    private void populateRepositories() {
    	pd = ProgressDialog.show(this, "Please Wait", "Grabbing Repositories");
    	
		try {
			repositoryThread.start();
		} catch (Exception e) {
			repositoryThread.run();
		}
	}
    
    private void buildNewsThread() {
    	newsThread = new Thread(new Runnable() {
			public void run() {
				SharedPreferences prefs = GitIssues2Activity.this.getSharedPreferences("login", MODE_PRIVATE);
				String username = prefs.getString("USERNAME", "");
				String password = prefs.getString("PASSWORD", "");
				
				if(username == "") return;
				
				personalFeed = new ArrayList<IssueEvent>();
				organizationFeed = new ArrayList<IssueEvent>();
				watchedFeed = new ArrayList<IssueEvent>();
				
				// Get Issue related events here.
				IssueService issueService = new IssueService();
				issueService.getClient().setCredentials(username, password);
				
				try {
					genEvents(personal, personalFeed, issueService);
					genEvents(organization, organizationFeed, issueService);
					genEvents(watched, watchedFeed, issueService);
					
					
					newsHandler.sendEmptyMessage(SUCCESS);
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					newsHandler.sendEmptyMessage(FAILURE);
				}
			}
			
			private void genEvents(List<Repository> repoList, List<IssueEvent> feed, IssueService service) 
					throws IOException {
				// This should eventually be user definable
				for(int i = 0; i < repoList.size(); i++) {
					PageIterator<IssueEvent> page1 = service.pageEvents(
							repoList.get(i).getOwner().getLogin(), 
							repoList.get(i).getName(), 1);
					List<IssueEvent> issueEventList = (List<IssueEvent>) page1.next();
					feed.addAll(issueEventList);
				}
			}
		});
    	
    	newsHandler = new Handler() {
    		@Override
    		public void handleMessage(Message msg) {
    			if(msg.what == SUCCESS) {
    				
    				// Sort all into news feed by date
    				insertNewsByDate();
    				
    				newsFeedView.setAdapter(new IssueEventAdapter(getApplicationContext(), 
    						R.layout.news_rowitem, newsFeed));
    			} else {
    				
    			}
    		}
    	};
    }

	private void buildRepoThread() {
		repositoryThread = new Thread(new Runnable() {
			
			public void run() {
				SharedPreferences prefs = GitIssues2Activity.this.getSharedPreferences("login", MODE_PRIVATE);
				String username = prefs.getString("USERNAME", "");
				String password = prefs.getString("PASSWORD", "");
				
				if(username == "") return;
				
				RepositoryService repoService = new RepositoryService();
				repoService.getClient().setCredentials(username, password);
				
				OrganizationService orgService = new OrganizationService();
				orgService.getClient().setCredentials(username, password);
				
				WatcherService watcherService = new WatcherService();
				
				organization = new ArrayList<Repository>();
				watched = new ArrayList<Repository>();
				
				try {
					personal = repoService.getRepositories(username);
					List<Repository> watched_unfiltered = watcherService.getWatched(username);
					
					List<User> organizations = orgService.getOrganizations();
					
					for(int i = 0; i < organizations.size(); i++) {
						String login = organizations.get(i).getLogin();
						organization.addAll(repoService.getRepositories(login));
					}
					
					for(int i = 0; i < watched_unfiltered.size(); i++) {
						boolean exists = false;
						
						if(watched_unfiltered.get(i).getOwner().getLogin().contentEquals(username.toLowerCase())) {
							// Do Not Add
							continue;
						}
						
						for(int j = 0; j < organizations.size(); j++) {
							if(watched_unfiltered.get(i).getOwner().getLogin()
									.contentEquals(organizations.get(j).getLogin())) {
								exists = true;
								break;
							}
						}
						
						if(!exists) {
							watched.add(watched_unfiltered.get(i));
						}
					}
					
					// Do some sorting!
					
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
				pd.dismiss();
				
				if(msg.what == SUCCESS) { 
					setupPager();
			        setupDrawer();
			        populateNews();
				} else {
					ErrorDialog.show(GitIssues2Activity.this);
				}
			};
		};
	}
	
	public void insertNewsByDate() {
		newsFeed = new ArrayList<IssueEvent>();
		
		insertNewsByDate(personalFeed);
		insertNewsByDate(organizationFeed);
		insertNewsByDate(watchedFeed);
	}
	
	public void insertNewsByDate(List<IssueEvent> items) {
		for(int i = 0; i < items.size(); i++) {
			if(newsFeed.size() == 0) {
				newsFeed.add(items.get(i));
				continue;
			}
			
			for(int j = 0; j < newsFeed.size(); j++) {
				if (newsFeed.get(j).getCreatedAt().compareTo(items.get(i).getCreatedAt()) == 0) {
					newsFeed.add(j + 1, items.get(i));
					break;
				}
				
				if (newsFeed.get(j).getCreatedAt().after(items.get(i).getCreatedAt())) {
					// Should be further down in the list
					if(j + 1 != newsFeed.size()) {
						if(newsFeed.get(j + 1).getCreatedAt().before(items.get(i).getCreatedAt())) {
							newsFeed.add(j + 1, items.get(i));
							break;
						}
					} else {
						// Add to the end of the list
						newsFeed.add(items.get(i));
						break;
					}
				} else {
					// It's the first one.
					newsFeed.add(0, items.get(i));
					break;
				}
			}
		}
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
    	repoLists.add(watched);
    	repoLists.add(personal);
    	repoLists.add(organization);
    	
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