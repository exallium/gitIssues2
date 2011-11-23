package org.exallium.gitissues;

import org.eclipse.egit.github.core.Issue;
import org.exallium.gitissues.adapters.SingleIssuePagerAdapter;

import com.sturtz.viewpagerheader.ViewPagerHeader;
import com.sturtz.viewpagerheader.ViewPagerHeaderListener;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;

public class SingleIssueActivity extends Activity implements ViewPagerHeaderListener{
	
	private Thread commentsThread;
	private Handler commentsHandler;
	
	private SingleIssuePagerAdapter sipa;
	
	private ViewPager pager;
	private ViewPagerHeader pagerHeader;
	private int pagerPosition;
	
	private Issue issue;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.singleissue);
		
		setupPager();
	}
	
	private void setupPager() {
		sipa = new SingleIssuePagerAdapter(this, issue);
		
		pager = (ViewPager) this.findViewById(R.id.singleissuepager);
		pager.setAdapter(sipa);
		
		pagerHeader = (ViewPagerHeader) this.findViewById(R.id.singleissuepagerheader);
		pagerHeader.setViewPager(pager);
		pagerHeader.setViewPagerHeaderListener(this);
		
		pagerPosition = 1;
		pager.setCurrentItem(1);
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
