package com.googlecode.android_scripting.facade;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Bundle;

import com.googlecode.android_scripting.jsonrpc.RpcReceiver;
import com.googlecode.android_scripting.jsonrpc.RpcReceiverManager;
import com.googlecode.android_scripting.rpc.Rpc;
import com.googlecode.android_scripting.rpc.RpcOptional;
import com.googlecode.android_scripting.rpc.RpcParameter;
import com.zuowuxuxi.util.NAction;
import com.zuowuxuxi.util.NUtil;

import java.util.List;

/**
 * Wifi functions.
 * 
 */
public class QPyInterfaceFacade extends RpcReceiver {
	  private final Service mService;

	public QPyInterfaceFacade(FacadeManager manager) {
		super(manager);
	    mService = manager.getService();
	    
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
	}
	
	@Rpc(description = "Execute QPython script throught SL4A", returns = "True if the operation succeeded.")
	public Boolean executeQPy(@RpcParameter(name = "QPython script path")  @RpcOptional String path) {
		
    	String extPlgPlusName = com.zuowuxuxi.config.CONF.EXT_PLG_PLUS;

		//if (NUtil.checkAppInstalledByName(mService.getApplicationContext(), extPlgPlusName)) {
		Intent intent = new Intent();
		intent.setClassName(extPlgPlusName, extPlgPlusName+".MPyApi");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(extPlgPlusName+".action.MPyApi");
		
		Bundle mBundle = new Bundle(); 
		mBundle.putString("app", NAction.getCode(mService.getApplicationContext()));
		mBundle.putString("act", "onPyApi");
		mBundle.putString("flag", "SL4A");
		mBundle.putString("param", "fileapi");
		mBundle.putString("pyfile", path);

		intent.putExtras(mBundle);
		
		mService.getApplicationContext().startActivity(intent);

		//} 

		return true;
	}

}