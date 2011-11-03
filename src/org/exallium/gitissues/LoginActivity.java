package org.exallium.gitissues;

import java.io.IOException;

import org.eclipse.egit.github.core.service.RepositoryService;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.login);
		
		Button submit = (Button) findViewById(R.id.login_submit);
		submit.setOnClickListener(this);
	}
	
	public void onClick(View v) {
		// Attempt to log the user in
		EditText user = (EditText) findViewById(R.id.login_user);
		EditText pass = (EditText) findViewById(R.id.login_pass);
		
		String username = user.getText().toString();
		String password = pass.getText().toString();
		
		RepositoryService repoService = new RepositoryService();
		repoService.getClient().setCredentials(username, password);
		
		// Separate thread this stuff
		try {
			repoService.getRepositories();
			
			SharedPreferences prefs = this.getSharedPreferences("login", MODE_PRIVATE);
			SharedPreferences.Editor prefsEditor = prefs.edit();
			
			prefsEditor.putString("USERNAME", username);
			prefsEditor.putString("PASSWORD", password);
			prefsEditor.commit();
			
			Intent i = new Intent(getApplicationContext(), GitIssues2Activity.class);
			startActivity(i);
			this.finish();
			
		} catch (IOException e) {
			// NO. Blast it.
			Toast.makeText(getApplicationContext(), "Bad Username/Password", Toast.LENGTH_LONG).show();
		}
	}
}
