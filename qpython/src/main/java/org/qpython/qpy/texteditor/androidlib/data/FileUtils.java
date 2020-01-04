package org.qpython.qpy.texteditor.androidlib.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;

@SuppressLint("DefaultLocale")
public class FileUtils {
	/** File of the external storage data */
	public static final File STORAGE = Environment
			.getExternalStorageDirectory();
	/** Path to the external storage data */
	public static final String STORAGE_PATH = STORAGE.getAbsolutePath();

	/** default android Download folder */
	public static final String DOWNLOAD_FOLDER = (STORAGE.getAbsolutePath()
			+ File.separator + "Download");

	/**
	 * Copy all files in the given asset folder to the destination folder (must
	 * be in the app's sandbox, unless the app has the write to external storage
	 * permission)
	 * 
	 * @param ctx
	 *            the current application context
	 * @param destFolder
	 *            the destination folder
	 * @param assetFolder
	 *            the source folder path (from the asset folder)
	 */
	public static void copyAssetsToAppData(Context ctx, File destFolder,
			String assetFolder) {

		String files[] = null;
		InputStream assetStream;
		File file;
		String assetFile;

		AssetManager assets = ctx.getAssets();

		try {
			files = assets.list(assetFolder);
		} catch (IOException e) {
			Log.w("AndroidLib", "Asset folder not found");
		}

		if (files != null) {
			for (String fileName : files) {
				file = new File(destFolder, fileName);

				assetFile = assetFolder + File.separatorChar + fileName;
				try {
					file.getParentFile().mkdirs();
					assetStream = assets.open(assetFile);
					FileUtils.copyFile(assetStream, file);
				} catch (IOException e) {
					Log.w("AndroidLib", "unable to copy file " + fileName);
				}
			}
		}
	}

	/**
	 * 
	 * @param file
	 *            the file to test
	 * @return if a file is a symbolic link
	 */
	public static boolean isSymLink(File file) {
		boolean result;
		result = false;

		if ((file != null) && file.exists()) {
			File canon, canonParent;
			if (file.getParent() == null) {
				canon = file;
			} else {
				try {
					canonParent = file.getParentFile().getCanonicalFile();
					canon = new File(canonParent, file.getName());
				} catch (IOException e) {
					canon = null;
				}
			}

			try {
				if (canon != null) {
					result = !canon.getCanonicalFile().equals(
							canon.getAbsoluteFile());
				}
			} catch (IOException e) {
				result = false;
			}
		}

		return result;
	}

	/**
	 * 
	 * @param file
	 *            the symbolic link file
	 * @return the target of the link
	 */
	public static File getSymLinkTarget(File file) {
		File result;

		result = file;

		if ((file != null) && file.exists()) {
			File canon, canonParent;
			if (file.getParent() == null) {
				canon = file;
			} else {
				try {
					canonParent = file.getParentFile().getCanonicalFile();
					canon = new File(canonParent, file.getName());
				} catch (IOException e) {
					canon = null;
				}
			}

			try {
				if (canon != null) {
					result = canon.getCanonicalFile();
				}
			} catch (IOException e) {
				result = file;
			}
		}

		return result;
	}

	/**
	 * 
	 * @param file
	 *            the file
	 * @return the file extension (folders return an empty extension, whatever
	 *         the name)
	 */
	public static String getFileExtension(File file) {
		String ext, name;
		int index;

		ext = "";
		if (!file.isDirectory()) {
			name = file.getName();
			index = name.lastIndexOf(".");
			if (index != -1) {
				ext = name.substring(index + 1).toLowerCase();
			}
		}
		return ext;
	}

	/**
	 * 
	 * @param file
	 *            the file
	 * @return the file extension (folders return an empty extension, whatever
	 *         the name)
	 */
	public static String getFileExtension(String filename) {
		String ext;
		int index;

		ext = "";
		if (filename != null) {
			index = filename.lastIndexOf(".");
			if (index != -1) {
				ext = filename.substring(index + 1).toLowerCase();
			}
		}
		return ext;
	}

	/**
	 * 
	 * @param file
	 *            the file
	 * @return the file prefix or the name if the file is a directory
	 */
	public static String getFilePrefix(File file) {
		String prefix, name;
		int index;

		prefix = file.getName();
		name = file.getName();
		if (!file.isDirectory()) {
			index = name.lastIndexOf(".");
			if (index != -1) {
				prefix = name.substring(0, index);
			}
		}
		return prefix;
	}

	/**
	 * @param file
	 *            the file name
	 * @return the Mime Type
	 */
	public static String getMimeType(File file) {

		String type;
		String ext;

		ext = getFileExtension(file);
		type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
				ext.toLowerCase());
		if (type == null) {
			type = MimeTypeMapEnhanced.getMimeTypeFromExtension(ext
					.toLowerCase());
		}

		if (type == null) {
			type = "?/?";
		}

		return type;
	}

	/**
	 * @param f
	 *            a file
	 * @return the canonical path of the file if possible, or the uncanonized
	 *         path
	 */
	public static String getCanonizePath(File f) {
		String path;
		try {
			path = f.getCanonicalPath();
		} catch (IOException e) {
			path = f.getPath();
			Log.w("AndroidLib",
					"Error while canonizing file path, using raw path instead : \""
							+ path + "\"");
		}
		return path;
	}

	/**
	 * Creates a new folder in the given parent folder
	 * 
	 * @param parent
	 *            the parent file for the folder
	 * @param name
	 *            the name of the folder to create
	 * @return if the folder was created
	 */
	public static boolean createFolder(File parent, String name) {
		File folder;
		boolean created;

		created = false;
		folder = new File(parent, name);
		if (parent.canWrite() && folder.mkdirs()) {
			created = true;
		}

		return created;
	}

	/**
	 * Creates a new file in the given parent folder
	 * 
	 * @param parent
	 *            the parent file for the folder
	 * @param name
	 *            the name of the file to create
	 * @return if the file was created
	 */
	public static boolean createFile(File parent, String name) {
		File file;
		boolean created;

		created = false;
		file = new File(parent, name);
		if (parent.canWrite()) {
			try {
				file.createNewFile();
				created = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return created;
	}

	/**
	 * Delete the given file/folder
	 * 
	 * @param file
	 *            the file/folder to delete
	 * @return if the file/folder was deleted successfully
	 */
	public static boolean deleteItem(File file) {
		boolean result;

		if (!file.exists()) {
			result = true;
		} else if (!file.canWrite()) {
			result = false;
		} else {
			result = file.delete();
		}

		return result;
	}

	/**
	 * Delete the given folder with all its content
	 * 
	 * @param folder
	 *            the folder to delete
	 * @return if the folder was deleted successfully
	 */
	public static boolean deleteRecursiveFolder(File folder) {
		boolean result;

		if (folder == null) {
			result = false;
		} else {

			File[] files = folder.listFiles();
			// usually if the folder is a file or has no children
			if (files == null) {
				result = folder.delete();
			} else {
				boolean ok = true;
				boolean fileOk;
				for (File child : files) {
					if (child.isDirectory()) {
						fileOk = deleteRecursiveFolder(child);
					} else {
						fileOk = child.delete();
					}

					if (!fileOk) {
						Log.w("", "Error deleting file " + child.getName());
					}
					ok = ok && fileOk;
				}
				if (ok) {
					result = folder.delete();
				} else {
					result = false;
				}
			}
		}

		return result;
	}

	/**
	 * Delete the file/folder at the given path
	 * 
	 * @param path
	 *            the path of the file/folder to delete
	 * @return if the file/folder was deleted successfully
	 */
	public static boolean deleteItem(String path) {
		File file;
		boolean result;

		file = new File(path);

		if (!file.exists()) {
			result = true;
		} else if (!file.canWrite()) {
			result = false;
		} else {
			result = file.delete();
		}

		return result;
	}

	/**
	 * 
	 * @param src
	 *            the source file
	 * @param dst
	 *            the destination file
	 * @return if the copy was successfull
	 */
	public static boolean copyFile(File src, File dst) {

		FileChannel inChannel = null;
		FileChannel outChannel = null;

		boolean complete = true;

		try {
			inChannel = new FileInputStream(src).getChannel();
			outChannel = new FileOutputStream(dst).getChannel();
		} catch (FileNotFoundException e) {
			Log.w("AndroidLib", "File not found or no R/W permission", e);
			complete = false;
		}

		try {
			inChannel.transferTo(0, inChannel.size(), outChannel);
		} catch (Exception e) {
			Log.w("AndroidLib", "Error during copy", e);
			complete = false;
		}

		try {
			if (inChannel != null) {
				inChannel.close();
			}
			if (outChannel != null) {
				outChannel.close();
			}
		} catch (IOException e) {
			Log.w("AndroidLib", "Error when closing files", e);
			complete = false;
		}

		return complete;
	}

	/**
	 * 
	 * @param src
	 *            the source input stream
	 * @param dst
	 *            the destination output
	 * @return if the copy was successfull
	 */
	public static boolean copyFile(InputStream inputStream, File dst) {

		FileOutputStream outputStream = null;
		byte[] buf = new byte[1024];
		int len;

		boolean complete = true;

		try {
			outputStream = new FileOutputStream(dst);
		} catch (FileNotFoundException e) {
			Log.w("AndroidLib", "File not found or no R/W permission", e);
			complete = false;
		}

		try {
			while ((len = inputStream.read(buf)) > 0) {
				outputStream.write(buf, 0, len);
			}
		} catch (Exception e) {
			Log.w("AndroidLib", "Error during copy", e);
			complete = false;
		}

		try {
			if (inputStream != null) {
				inputStream.close();
			}
			if (outputStream != null) {
				outputStream.close();
			}
		} catch (IOException e) {
			Log.w("AndroidLib", "Error when closing files", e);
			complete = false;
		}

		return complete;
	}

	/**
	 * Rename a file
	 * 
	 * @param file
	 *            the file/folder to rename
	 * @param newName
	 *            the new name to the file
	 * @return if the rename was succesfull
	 */
	public static boolean renameItem(File file, String newName) {
		File newFile;
		boolean result;

		if ((!file.exists()) || (!file.canWrite())) {
			result = false;
		} else {
			newFile = new File(file.getParentFile(), newName);
			result = file.renameTo(newFile);
		}

		return result;
	}

	/**
	 * Rename a file
	 * 
	 * @param oldPath
	 *            the path of the file/folder to rename
	 * @param newPath
	 *            the new path to the file/folder
	 * @return if the rename was succesfull
	 */
	public static boolean renameItem(String oldPath, String newPath) {
		File file, newFile;
		boolean result;

		file = new File(oldPath);

		if ((!file.exists()) || (!file.canWrite())) {
			result = false;
		} else {
			newFile = new File(newPath);
			result = file.renameTo(newFile);
		}

		return result;
	}
}
