package org.qpython.qsl4a.qsl4a.facade;

import android.app.Service;
import android.content.Intent;


import org.qpython.qsl4a.qsl4a.jsonrpc.RpcReceiver;
import org.qpython.qsl4a.qsl4a.jsonrpc.RpcReceiverManager;
import org.qpython.qsl4a.qsl4a.jsonrpc.RpcReceiverManagerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FacadeManagerFactory implements RpcReceiverManagerFactory {

  private final int mSdkLevel;
  private final Service mService;
  private final Intent mIntent;
  private final Collection<Class<? extends RpcReceiver>> mClassList;
  private final List<RpcReceiverManager> mFacadeManagers;

  public FacadeManagerFactory(int sdkLevel, Service service, Intent intent,
      Collection<Class<? extends RpcReceiver>> classList) {
    mSdkLevel = sdkLevel;
    mService = service;
    mIntent = intent;
    mClassList = classList;
    mFacadeManagers = new ArrayList<RpcReceiverManager>();
  }

  public FacadeManager create() {
    FacadeManager facadeManager = new FacadeManager(mSdkLevel, mService, mIntent, mClassList);
    mFacadeManagers.add(facadeManager);
    return facadeManager;
  }

  @Override
  public List<RpcReceiverManager> getRpcReceiverManagers() {
    return mFacadeManagers;
  }
}
