package org.exallium.gitissues.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class ErrorDialog {
	
	public static void show(Activity activity) {
		AlertDialog.Builder errorDialog = new AlertDialog.Builder(activity);
		errorDialog.setTitle("Error");
		errorDialog.setMessage("Bad Username/Password Combination, or no connection to Internet.");
		errorDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// Do Nothing.
				
			}
		});
		errorDialog.show();
	}
}
