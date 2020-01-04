package org.qpython.qpy.texteditor;

import android.content.res.Configuration;
import android.view.View;
import com.quseit.base.QBaseActivity;
import com.quseit.util.NAction;
import com.quseit.util.NUtil;

import org.qpython.qpysdk.QPyConstants;

public class BaseActivity extends QBaseActivity {
    protected static final int SCRIPT_EXEC_PY = 2235;  
    protected static final int SCRIPT_EXEC_CODE = 1235;
    private static final int SCRIPT_CONSOLE_CODE = 1237;

    @Override
    protected void onDestroy() {
    	super.onDestroy();
    }

    @Override
	public void onConfigurationChanged(Configuration newConfig) {
    	super.onConfigurationChanged(newConfig);
    }

	@Override
	public Class<?> getUpdateSrv() {
		return null;
	}
	

	@Override
	public String confGetUpdateURL(int flag) {
		if (flag == 2) {
			return QPyConstants.LOG_URL+this.getPackageName()+"/"+NUtil.getVersinoCode(this);
		} else if (flag == 3) {
			return QPyConstants.AD_URL+this.getPackageName()+"/"+NUtil.getVersinoCode(this)+"?"
					+ NAction.getUserUrl(getApplicationContext());

		} else {
			return QPyConstants.UPDATE_URL+this.getPackageName()+"/"+NUtil.getVersinoCode(this);

		}
	}
}
