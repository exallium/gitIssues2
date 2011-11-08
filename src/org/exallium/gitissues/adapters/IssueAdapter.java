package org.exallium.gitissues.adapters;

import java.util.List;

import org.eclipse.egit.github.core.Issue;
import org.exallium.gitissues.R;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class IssueAdapter extends ArrayAdapter<Issue> {
	
	private List<Issue> issues;

	public IssueAdapter(Context context, int textViewResourceId,
			List<Issue> objects) {
		super(context, textViewResourceId, objects);
		issues = objects;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.issue_rowitem, null);
		}
		
		Issue i = issues.get(position);
		if(i != null) {
			TextView issue_no = (TextView) v.findViewById(R.id.issue_no);
			TextView issue_date = (TextView) v.findViewById(R.id.issue_date);
			TextView issue_title = (TextView) v.findViewById(R.id.issue_title);
			issue_no.setText("#" + i.getNumber());
			issue_title.setText(i.getTitle());
			issue_date.setText(DateFormat.format("MM/dd/yyyy", i.getCreatedAt()));
		}
		
		return v;
	}

}
