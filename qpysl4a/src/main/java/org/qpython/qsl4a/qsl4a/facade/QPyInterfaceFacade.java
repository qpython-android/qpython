package org.qpython.qsl4a.qsl4a.facade;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;

import org.qpython.qsl4a.qsl4a.jsonrpc.RpcReceiver;
import org.qpython.qsl4a.qsl4a.rpc.Rpc;
import org.qpython.qsl4a.qsl4a.rpc.RpcOptional;
import org.qpython.qsl4a.qsl4a.rpc.RpcParameter;


/**
 * Wifi functions.
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
    public Boolean executeQPy(@RpcParameter(name = "QPython script path") @RpcOptional String path) {

        String extPlgPlusName = "org.qpython.qpy";

        //if (NUtil.checkAppInstalledByName(mService.getApplicationContext(), extPlgPlusName)) {
        Intent intent = new Intent();
        intent.setClassName(extPlgPlusName, extPlgPlusName + ".MPyApi");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(extPlgPlusName + ".action.MPyApi");

        Bundle mBundle = new Bundle();
        mBundle.putString("app", "app");
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