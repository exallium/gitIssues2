package org.exallium.gitissues.adapters;

import java.util.List;

import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Label;
import org.eclipse.egit.github.core.Milestone;
import org.eclipse.egit.github.core.User;
import org.exallium.gitissues.R;
import org.exallium.gitissues.utils.Util;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TableRow;
import android.widget.TextView;

public class ExpandableIssueAdapter extends BaseExpandableListAdapter {
	
	private Context context;
	private int layout;
	private List<Issue> issues;
	
	public ExpandableIssueAdapter(Context c, int layout_id, List<Issue> issues) {
		layout = layout_id;
		context = c;
		this.issues = issues;
	}

	public Object getChild(int groupPosition, int childPosition) {
		return issues.get(groupPosition);
	}

	public long getChildId(int groupPosition, int childPosition) {
		return 1;
	}

	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		
		View v = convertView;
		
		if(v == null) {
			LayoutInflater vi = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.issue_rowitem_clicked, null);
		}
		
		Issue i = issues.get(groupPosition);
		if(i != null) {
			TextView milestone = (TextView) v.findViewById(R.id.issue_milestone);
			TextView assigned = (TextView) v.findViewById(R.id.issue_assigned);
			
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
		}
		
		return v;
		
	}

	public int getChildrenCount(int groupPosition) {
		return 1;
	}

	public Object getGroup(int groupPosition) {
		return issues.get(groupPosition);
	}

	public int getGroupCount() {
		return issues.size();
	}

	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		

		View v = convertView;
		
		if(v == null) {
			LayoutInflater vi = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.issue_rowitem_default, null);
		}
		
		Issue i = issues.get(groupPosition);
		if(i != null) {
			TextView issue_no = (TextView) v.findViewById(R.id.issue_no);
			TextView issue_date = (TextView) v.findViewById(R.id.issue_date);
			TextView issue_title = (TextView) v.findViewById(R.id.issue_title);
			issue_no.setText("#" + i.getNumber());
			issue_title.setText(i.getTitle());
			issue_date.setText(DateFormat.format("MM/dd/yyyy", i.getCreatedAt()));
			
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
		}
		
		return v;
		
	}

	public boolean hasStableIds() {
		return false;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

}
