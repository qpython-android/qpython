package org.qpython.qpy.texteditor;

public class FTPServerService extends org.swiftp.FTPServerService {

	@Override
	protected Class<?> getSettingClass() {
		return MFTPSettingActivity.class;
	}
}
