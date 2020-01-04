package org.qpython.qpy.texteditor.androidlib.comparator;

import android.annotation.SuppressLint;
import java.io.File;
import java.util.Comparator;

/**
 * Compare files by date (w/ folders listed first)
 * 
 * @author x.gouchet
 * 
 */
@SuppressLint("DefaultLocale")
public class ComparatorFilesDate implements Comparator<File> {
	/**
	 * @see Comparator#compare(Object, Object)
	 */
	public int compare(File file1, File file2) {
		int result;

		// sort folders first
		if ((file1.isDirectory()) && (!file2.isDirectory())) {
			result = -1;
		} else if ((!file1.isDirectory()) && (file2.isDirectory())) {
			result = 1;
		} else {

			// both are files, or both are folders...
			// get modif date
			long modif1 = file1.lastModified();
			long modif2 = file2.lastModified();

			// same extension, we sort alphabetically
			if (modif1 == modif2) {
				result = file1.getName().toLowerCase()
						.compareTo(file2.getName().toLowerCase());
			} else {
				result = (int) (modif1 - modif2);
			}
		}

		return result;
	}
}
