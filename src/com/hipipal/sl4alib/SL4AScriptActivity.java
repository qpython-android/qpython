
package com.hipipal.sl4alib;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.googlecode.android_scripting.Constants;
import com.googlecode.android_scripting.facade.ActivityResultFacade;
import com.googlecode.android_scripting.jsonrpc.RpcReceiverManager;
import com.hipipal.sl4alib.R;

/**
 * @author Alexey Reznichenko (alexey.reznichenko@gmail.com)
 */
public class SL4AScriptActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (Constants.ACTION_LAUNCH_SCRIPT_FOR_RESULT.equals(getIntent().getAction())) {
      setTheme(android.R.style.Theme_Dialog);
      setContentView(R.layout.dialog);
      ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
        	Log.d("SL4AScriptApp", "onServiceConnected");
          SL4AScriptService scriptService = ((SL4AScriptService.LocalBinder) service).getService();
          try {
            RpcReceiverManager manager = scriptService.getRpcReceiverManager();
            ActivityResultFacade resultFacade = manager.getReceiver(ActivityResultFacade.class);
            resultFacade.setActivity(SL4AScriptActivity.this);
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
          // Ignore.
        }
      };
  	//Log.d("SL4AScriptApp", "bindService 1");

      bindService(new Intent(this, SL4AScriptService.class), connection, Context.BIND_AUTO_CREATE);
    	//Log.d("SL4AScriptApp", "bindService 2");

      startService(new Intent(this, SL4AScriptService.class));
    } else {
    	//Log.d("SL4AScriptApp", "bindService 3");

    	MNApp application = (MNApp) getApplication();
      if (application.readyToStart()) {
        startService(new Intent(this, SL4AScriptService.class));
      }
  	//Log.d("SL4AScriptApp", "bindService 4");

      finish();
    }
  }
}
