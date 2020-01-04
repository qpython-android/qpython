package org.qpython.qpy.texteditor.androidlib.comparator;

import android.annotation.SuppressLint;
import java.io.File;
import java.util.Comparator;

/**
 * Compare files by type (w/ folders listed first)
 * 
 * @author x.gouchet
 * 
 */
@SuppressLint("DefaultLocale")
public class ComparatorFilesType implements Comparator<File> {
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
		} else if ((file1.isDirectory()) && (file2.isDirectory())) {
			// if both are folders
			result = file1.getName().toLowerCase()
					.compareTo(file2.getName().toLowerCase());
		} else {
			// both are files, we get the extension
			String ext1 = file1.getName().substring(
					file1.getName().lastIndexOf('.') + 1);
			String ext2 = file2.getName().substring(
					file2.getName().lastIndexOf('.') + 1);

			// same extension, we sort alphabetically
			if (ext1.equalsIgnoreCase(ext2)) {
				result = file1.getName().toLowerCase()
						.compareTo(file2.getName().toLowerCase());
			} else {
				result = ext1.toLowerCase().compareTo(ext2.toLowerCase());
			}
		}

		return result;
	}
}
