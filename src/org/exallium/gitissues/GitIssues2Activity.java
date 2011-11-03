package org.exallium.gitissues;

import org.exallium.gitissues.adapters.MainPagerAdapter;
import org.exallium.gitissues.listeners.NewsDrawerListener;

import com.sturtz.viewpagerheader.ViewPagerHeader;
import com.sturtz.viewpagerheader.ViewPagerHeaderListener;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SlidingDrawer;

public class GitIssues2Activity extends Activity implements ViewPagerHeaderListener {
	
	private MainPagerAdapter mainPagerAdapter;
	private ViewPager mainPager;
	private ViewPagerHeader mainPagerHeader;
	private int mainPagerPosition;
	private NewsDrawerListener newsDrawerListener;
	private SlidingDrawer newsDrawer;
	private SharedPreferences prefs;
	
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
        
        setupPager();
        setupDrawer();
        
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
    
    public void setupDrawer() {
    	newsDrawerListener = new NewsDrawerListener();
    	newsDrawer = (SlidingDrawer) findViewById(R.id.drawer);
    	newsDrawer.setOnDrawerOpenListener(newsDrawerListener);
    	newsDrawer.setOnDrawerCloseListener(newsDrawerListener);
    	newsDrawer.setOnDrawerScrollListener(newsDrawerListener);
    }
    
    public void setupPager() {
    	mainPagerAdapter = new MainPagerAdapter(this);
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