package org.exallium.gitissues.adapters;

import java.util.List;

import org.eclipse.egit.github.core.Comment;
import org.exallium.gitissues.R;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CommentAdapter extends ArrayAdapter<Comment> {
	private Context context;
	private List<Comment> objects;

	public CommentAdapter(Context context, int textViewResourceId,
			List<Comment> objects) {
		super(context, textViewResourceId, objects);

		this.context = context;
		this.objects = objects;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		
		if(v == null) {
			LayoutInflater vi = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.comment_rowitem, null);
		}
		
		Comment c = objects.get(position);
		if (c != null) {
			TextView author = (TextView) v.findViewById(R.id.comment_author);
			TextView created = (TextView) v.findViewById(R.id.comment_created);
			TextView body = (TextView) v.findViewById(R.id.comment_body);
			
			author.setText(c.getUser().getLogin());
			created.setText(DateFormat.format("MM/dd/yyyy", c.getCreatedAt()));
			body.setText(c.getBody());
		}
		
		return v;
	}
	
}
