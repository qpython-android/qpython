package org.qpython.qpy.texteditor.common;

import java.io.File;
import java.util.Comparator;

/**
 * Compare files Alphabetically (w/ folders listed first)
 * 
 * @author x.gouchet
 * 
 */
public class ComparatorFilesAlpha implements Comparator<File> {

	/**
	 * @see Comparator#compare(Object, Object)
	 */
	@Override
	public int compare(File file1, File file2) {
		// sort folders first
		if ((file1.isDirectory()) && (!file2.isDirectory()))
			return -1;
		if ((!file1.isDirectory()) && (file2.isDirectory()))
			return 1;

		// here both are folders or both are files : sort alpha
		return file1.getName().toLowerCase().compareTo(
				file2.getName().toLowerCase());
	}

}
