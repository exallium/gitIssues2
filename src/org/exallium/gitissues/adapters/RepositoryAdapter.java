package org.exallium.gitissues.adapters;

import java.util.List;

import org.eclipse.egit.github.core.Repository;
import org.exallium.gitissues.IssueListActivity;
import org.exallium.gitissues.R;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class RepositoryAdapter extends ArrayAdapter<Repository> {
	
	List<Repository> repoList;

	public RepositoryAdapter(Context context, int resource, List<Repository> objects) {
		super(context, resource, objects);
		
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
			TextView owner = (TextView) v.findViewById(R.id.repository_login);
			TextView name = (TextView) v.findViewById(R.id.repository_name);
			owner.setText(r.getOwner().getLogin() + "/");
			name.setText(r.getName());
		}
		
		v.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				TextView owner = (TextView) v.findViewById(R.id.repository_login);
				TextView repo = (TextView) v.findViewById(R.id.repository_name);
				
				String owner_string = owner.getText().toString().replace("/", "");
				String repo_string = repo.getText().toString();
				
				// This is where we start the "IssueListActivity"
				Intent i = new Intent(getContext(), IssueListActivity.class);
				i.putExtra("repo", repo_string);
				i.putExtra("owner", owner_string);
				
				getContext().startActivity(i);
			}
		});
		
		return v;
	}

}
