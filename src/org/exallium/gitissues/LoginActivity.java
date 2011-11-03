package org.exallium.gitissues;

import java.io.IOException;

import org.eclipse.egit.github.core.service.RepositoryService;
import org.exallium.gitissues.dialogs.ErrorDialog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends Activity implements OnClickListener {
	
	private Thread loginCheckThread;
	private Handler loginCheckHandler;
	private ProgressDialog pd;
	
	private String username = "";
	private String password = "";
	
	private static int SUCCESS = 0;
	private static int FAILURE = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.login);
		
		Button submit = (Button) findViewById(R.id.login_submit);
		submit.setOnClickListener(this);
		
		loginCheckThread = new Thread(new Runnable() {
			public void run() {
				try {
					RepositoryService repoService = new RepositoryService();
					repoService.getClient().setCredentials(username, password);
					repoService.getRepositories();
					
					SharedPreferences prefs = LoginActivity.this.getSharedPreferences("login", MODE_PRIVATE);
					SharedPreferences.Editor prefsEditor = prefs.edit();
					
					prefsEditor.putString("USERNAME", username);
					prefsEditor.putString("PASSWORD", password);
					prefsEditor.commit();
					
					loginCheckHandler.sendEmptyMessage(SUCCESS);
					
				} catch (IOException e) {
					loginCheckHandler.sendEmptyMessage(FAILURE);
				}
			}
		});
		
		loginCheckHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if(msg.what == SUCCESS) {
					Intent i = new Intent(getApplicationContext(), GitIssues2Activity.class);
					startActivity(i);
					LoginActivity.this.finish();
				} else {
					ErrorDialog.show(LoginActivity.this);
				}
				
				pd.dismiss();
			}
		};
	}
	
	public void onClick(View v) {
		// Attempt to log the user in
		EditText user = (EditText) findViewById(R.id.login_user);
		EditText pass = (EditText) findViewById(R.id.login_pass);
		
		username = user.getText().toString();
		password = pass.getText().toString();
		
		// Show the dialog
		pd = ProgressDialog.show(this, "Authenticating...", "Logging in to Github");
		
		// Try to Login
		try {
			loginCheckThread.start();
		} catch(Exception e) {
			loginCheckThread.run();
		}
	}
}
