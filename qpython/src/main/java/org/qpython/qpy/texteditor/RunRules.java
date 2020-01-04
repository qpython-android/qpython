package org.qpython.qpy.texteditor;

import java.util.ArrayList;
import java.util.List;


import android.support.v7.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.qpython.qpy.R;

/**
 * @author kyle kersey
 *
 */
public class RunRules extends ListActivity {
    private PackageManager packageManager = null;
    private List<ApplicationInfo> applist = null;
    private ApplicationAdapter listadaptor = null;
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.run_rules);
        
        Button addRuleButton = (Button) findViewById(R.id.add_rule_button);
        addRuleButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// uncomment this PATTERN_LINE to see the application picker
				//new LoadApplications().execute();
				startRunRuleDialog();
			}
		});
        
        packageManager = getPackageManager();        
        //new LoadApplications().execute();
    }
 
 
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
 
        ApplicationInfo app = applist.get(position);
        try {
            Intent intent = packageManager
                    .getLaunchIntentForPackage(app.packageName);
 
            if (null != intent) {
                startActivity(intent);
            }
        } catch (ActivityNotFoundException e) {
            Toast.makeText(RunRules.this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(RunRules.this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }
 
    private List<ApplicationInfo> checkForLaunchIntent(List<ApplicationInfo> list) {
        ArrayList<ApplicationInfo> applist = new ArrayList<ApplicationInfo>();
        for (ApplicationInfo info : list) {
            try {
                if (null != packageManager.getLaunchIntentForPackage(info.packageName)) {
                    applist.add(info);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
 
        return applist;
    }
 
    public void startRunRuleDialog() {
    	final Context context = this;
    	LayoutInflater li = LayoutInflater.from(context);
		View promptsView = li.inflate(R.layout.new_rule_dialog, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);

		// set prompts.xml to alertdialog builder
		alertDialogBuilder.setView(promptsView);

		final EditText shouldEndWith = (EditText) promptsView.findViewById(R.id.should_endwith);
		final Button pickAppButton = (Button) promptsView.findViewById(R.id.pick_app_button);
		pickAppButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);

				// http://developer.android.com/training/basics/intents/sending.html
				// Verify it resolves
				PackageManager packageManager = getPackageManager();
				List<ResolveInfo> activities = packageManager.queryIntentActivities(mainIntent, 0);
				boolean isIntentSafe = activities.size() > 0;
				Intent chooser = Intent.createChooser(mainIntent, "Pick a App");

				// Start an activity if it's safe
				if (isIntentSafe) {
				    startActivity(mainIntent);
				}
			}
		});


		// set dialog message
		alertDialogBuilder
			.setCancelable(false)
			.setPositiveButton("OK",
			  new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {

			    }
			  })
			.setNegativeButton("Cancel",
			  new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
				dialog.cancel();
			    }
			  });

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();

    }

    private class LoadApplications extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progress = null;

        @Override
        protected Void doInBackground(Void... params) {
            applist = checkForLaunchIntent(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));
            listadaptor = new ApplicationAdapter(RunRules.this,
                    R.layout.snippet_list_row, applist);

            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(Void result) {
            setListAdapter(listadaptor);
            progress.dismiss();
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(RunRules.this, null,
                    "Loading application info...");
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }


    }
}
