package org.exallium.gitissues.adapters;

import java.util.List;

import org.eclipse.egit.github.core.Repository;
import org.exallium.gitissues.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class RepositoryAdapter extends ArrayAdapter<Repository> {
	
	Context context;
	List<Repository> repoList;

	public RepositoryAdapter(Context context, int resource, List<Repository> objects) {
		super(context, resource, objects);
		
		this.context = context;
		repoList = objects;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.repository_rowitem, null);
		}
		
		Repository r = repoList.get(position);
		if(r != null) {
			TextView tv = (TextView) v.findViewById(R.id.repository_name);
			tv.setText(r.getName());
		}
		
		return v;
	}

}
