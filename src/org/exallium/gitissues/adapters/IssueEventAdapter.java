package org.exallium.gitissues.adapters;

import java.util.List;
import org.eclipse.egit.github.core.IssueEvent;
import org.exallium.gitissues.R;
import org.exallium.gitissues.utils.Util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class IssueEventAdapter extends ArrayAdapter<IssueEvent> {
	private List<IssueEvent> issueEventList;
	
	public IssueEventAdapter(Context context, int textViewResourceId,
			List<IssueEvent> objects) {
		super(context, textViewResourceId, objects);
		
		issueEventList = objects;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.news_rowitem, null);
		}
		
		IssueEvent i = issueEventList.get(position);
		if(i != null) {
			//ImageView avatar = (ImageView) v.findViewById(R.id.news_avatar);
			TextView actor = (TextView) v.findViewById(R.id.news_actor);
			TextView action = (TextView) v.findViewById(R.id.news_action);
			TextView issueid = (TextView) v.findViewById(R.id.news_issueid);
			TextView owner = (TextView) v.findViewById(R.id.news_owner);
			TextView repo = (TextView) v.findViewById(R.id.news_repo);
			TextView date = (TextView) v.findViewById(R.id.news_date);
			
			actor.setText(i.getActor().getLogin());
			
			if(i.getEvent().contentEquals("subscribed")) {
				action.setText("opened");
			} else {
				action.setText(i.getEvent());
			}
			
			issueid.setText("Issue " + i.getIssue().getNumber());
			
			String [] repoInfo = Util.parseRepository(i.getUrl());
			
			owner.setText(repoInfo[0]);
			repo.setText(repoInfo[1]);
			date.setText("on " + i.getCreatedAt().toLocaleString());
		}
		
		return v;
	}
}
