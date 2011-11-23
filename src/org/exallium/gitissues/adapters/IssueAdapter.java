package org.exallium.gitissues.adapters;

import java.util.List;

import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Label;
import org.eclipse.egit.github.core.Milestone;
import org.eclipse.egit.github.core.User;
import org.exallium.gitissues.R;
import org.exallium.gitissues.SingleIssueActivity;
import org.exallium.gitissues.utils.Util;

import com.google.gson.Gson;

import android.content.Context;
import android.content.Intent;
import android.sax.StartElementListener;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TableRow;
import android.widget.TextView;

public class IssueAdapter extends ArrayAdapter<Issue> {
	private List<Issue> issueList;
	private Context context;
	
	public IssueAdapter(Context context, int textViewResourceId,
			List<Issue> objects) {
		super(context, textViewResourceId, objects);
		
		this.context = context;
		issueList = objects;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
View v = convertView;
		
		if(v == null) {
			LayoutInflater vi = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.issue_rowitem_default, null);
		}
		
		final Issue i = issueList.get(position);
		if(i != null) {
			TextView issue_no = (TextView) v.findViewById(R.id.issue_no);
			TextView issue_date = (TextView) v.findViewById(R.id.issue_date);
			TextView issue_title = (TextView) v.findViewById(R.id.issue_title);
			TextView milestone = (TextView) v.findViewById(R.id.issue_milestone);
			TextView assigned = (TextView) v.findViewById(R.id.issue_assigned);
			issue_no.setText("#" + i.getNumber());
			issue_title.setText(i.getTitle());
			issue_date.setText(DateFormat.format("MM/dd/yyyy", i.getCreatedAt()));
			
			// Populate Milestone / Assignee
			User assignee = i.getAssignee();
			Milestone mile = i.getMilestone();
			
			if(assignee != null) {
				assigned.setText(i.getAssignee().getLogin());
			} else {
				assigned.setText("No Assignee");
			}
			
			if(mile != null) {
				milestone.setText(i.getMilestone().getTitle());
			} else {
				milestone.setText("No Milestone");
			}
			
			// Populate Labels
			TableRow tr = (TableRow) v.findViewById(R.id.issue_labels);
			List<Label> labels = i.getLabels();
			
			Log.d("label", labels.size() + "");
			
			tr.removeAllViews();
			
			for(int j = 0; j < labels.size(); j++) {
				
				TextView lv = new TextView(context);
				Label l = labels.get(j);
				
				int bg = 0xFF000000 | Integer.parseInt(l.getColor(), 16);
				
				lv.setText(l.getName());
				lv.setBackgroundColor(bg);
				lv.setPadding(10, 5, 10, 5);
				lv.setTextColor(Util.contrast(bg));
				
				tr.addView(lv);
			}
			
			v.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					Intent intent = new Intent(context, SingleIssueActivity.class);
					String data = (new Gson()).toJson(i);
					intent.putExtra("issue", data);
					context.startActivity(intent);
				}
			});
		}
		
		return v;
	}
}