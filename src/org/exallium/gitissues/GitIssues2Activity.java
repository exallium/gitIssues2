package org.exallium.gitissues;

import org.exallium.gitissues.adapters.MainPagerAdapter;

import com.sturtz.viewpagerheader.ViewPagerHeader;
import com.sturtz.viewpagerheader.ViewPagerHeaderListener;

import android.app.Activity;
import android.os.Bundle;

import android.support.v4.view.ViewPager;

public class GitIssues2Activity extends Activity implements ViewPagerHeaderListener {
	
	private MainPagerAdapter mainPagerAdapter;
	private ViewPager mainPager;
	private ViewPagerHeader mainPagerHeader;
	private int mainPagerPosition;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        setupPager();
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