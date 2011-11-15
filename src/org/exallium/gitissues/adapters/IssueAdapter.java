package org.exallium.gitissues.adapters;

import java.util.List;

import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Label;
import org.exallium.gitissues.R;
import org.exallium.gitissues.utils.Util;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TableRow;
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
			
			TableRow tr = (TableRow) v.findViewById(R.id.issue_labels);
			List<Label> labels = i.getLabels();
			
			Log.d("label", labels.size() + "");
			
			tr.removeAllViews();
			
			for(int j = 0; j < labels.size(); j++) {
				
				TextView lv = new TextView(getContext());
				Label l = labels.get(j);
				
				int bg = 0xFF000000 | Integer.parseInt(l.getColor(), 16);
				
				lv.setText(l.getName());
				lv.setBackgroundColor(bg);
				lv.setTextColor(Util.contrast(bg));
				
				tr.addView(lv);
			}
		}
		
		return v;
	}

}
