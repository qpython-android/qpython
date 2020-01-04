package org.qpython.qpy.texteditor.androidlib.data;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

public class RootFileUtils {

	public static List<File> listFiles(File folder) {

		Process suProcess;
		DataOutputStream os;
		DataInputStream is;
		StringBuffer text = new StringBuffer();
		int c;

		try {
			suProcess = Runtime.getRuntime().exec("su");

			os = new DataOutputStream(suProcess.getOutputStream());
			is = new DataInputStream(suProcess.getInputStream());

			os.writeBytes("ls " + FileUtils.getCanonizePath(folder) + "\n");
			os.flush();

			os.writeBytes("exit \n");
			os.flush();

			int suProcessRetVal = suProcess.waitFor();
			byte buffer[] = new byte[1024];
			Reader reader = new BufferedReader(new InputStreamReader(is));
			do {
				c = reader.read();
				if (c != -1) {
					text.append((char) c);
				}
			} while (c != -1);
			reader.close();
		} catch (Exception e) {

		}

		return null;

	}
}
