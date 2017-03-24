package org.qpython.qpysdk.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.webkit.MimeTypeMap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * 对SD卡文件的管理
 * 
 * @author ch.linghu
 * 
 */
public class FileHelper {
	@SuppressWarnings("unused")
	private static final String TAG = "FileHelper";

	public static final void createDirIfNExists(String dirname) {
		File yy = new File(dirname);
		if (!yy.exists()) {
			yy.mkdirs();
		}
	}

	public static final void createFileFromAssetsIfNExists(Context con, String filename, String dst) {
		File yy = new File(dst);
		if (!yy.exists()) {
			String content = FileHelper.LoadDataFromAssets(con, filename);
			FileHelper.writeToFile(dst, content);
		}
	}
	public static void openFile(Context context, String filePath, String fileExtension) {
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        File file = new File(filePath);
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String type = mime.getMimeTypeFromExtension(fileExtension);
        intent.setDataAndType(Uri.fromFile(file), type);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
        } catch (android.content.ActivityNotFoundException e) {
        }
}

	public static String getFileNameFromUrl(String urlFile) {
		try {
			URL url = new URL(urlFile);
			File f = new File(url.getPath());
			return f.getName();

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			return "unname.dat";
		}
	}
	public static String getTypeByMimeType(String mType) {
		if (mType.equals("application/vnd.android.package-archive")) {
			return "apk";
		} else {
			String[] xx = mType.split("/");
			if (xx.length>1) {
				return xx[0];
			}
		}
		return "other";
	}

	public static String LoadDataFromAssets(Context context, String inFile) {
		String tContents = "";

		try {
			InputStream stream = context.getAssets().open(inFile);
			int size = stream.available();
			byte[] buffer = new byte[size];
			stream.read(buffer);
			stream.close();
			tContents = new String(buffer);
		} catch (IOException e) {
		}
		return tContents;
	}

	public static void putFileContents(Context context, String filename, String content) {
		try {
			File fileCache = new File(filename);
			byte[] data = content.getBytes();
			FileOutputStream outStream;
			outStream = new FileOutputStream(fileCache);
			outStream.write(data);
			outStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();

		}
	}

	public static void writeToFile(String filePath, String data) {
		try {
			FileOutputStream fOut = new FileOutputStream(filePath);
            fOut.write(data.getBytes());
            fOut.flush();
            fOut.close();
		} catch (IOException iox) {
			iox.printStackTrace();
		}

	}


	public static String getFileContentsFromAssets(Context context, String filename) {
		String content=""; //结果字符串
		try{
			java.io.InputStream is= context.getResources().getAssets().open(filename); //打开文件
			int ch=0;  
			ByteArrayOutputStream baos = new ByteArrayOutputStream(); //实现了一个输出流
			while((ch=is.read())!=-1){  
				baos.write(ch); //将指定的字节写入此 byte 数组输出流  
			}
			byte[] buff=baos.toByteArray();//以byte 数组的形式返回此输出流的当前内容  
			baos.close(); //关闭流  
			is.close(); //关闭流  
			content=new String(buff,"UTF-8"); //设置字符串编码
		
		} catch(Exception e) {
			e.printStackTrace();
			//Log.d(TAG, "getFileContentsFromAssets:"+e.getMessage());
		}  
		return content; 
	}
	public static String getFileContents(String filename) {
		
        File scriptFile = new File( filename );
        String tContent = "";
        if (scriptFile.exists()) {
        	BufferedReader in;
			try {
				in = new BufferedReader(new FileReader(scriptFile));
            	String line;
            	
				while ((line = in.readLine())!=null) {
					tContent += line+"\n";
				}
				in.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

        }
		return tContent;
	}
	
	public static void clearDir(String dir, int level, boolean deleteS) {
		//Log.d(TAG, "clearDir:"+dir);
		File basePath = new File(dir);
		if (basePath.exists() && basePath.isDirectory()) {
			for (File item : basePath.listFiles()) {
				if (item.isFile()) {
					//Log.d(TAG, "deleteItem:"+item.getAbsolutePath());
					item.delete();
					
				} else if (item.isDirectory()){
					clearDir(item.getAbsolutePath(), level+1, deleteS);
				}
			}
			if (level>0 || deleteS) {
				basePath.delete();
			}
			
			
		}
	}
	public static File getBasePath(String parDir, String subdir) throws IOException {
		try {
		File basePath = new File(Environment.getExternalStorageDirectory(),
				parDir);

		if (!basePath.exists()) {
			if (!basePath.mkdirs()) {
				throw new IOException(String.format("%s cannot be created!",
						basePath.toString()));
			}
		}
		File subPath = null;
		if (!subdir.equals("")) {
			subPath = new File(Environment.getExternalStorageDirectory(),
					parDir+"/"+subdir);
			if (!subPath.exists()) {
				if (!subPath.mkdirs()) {
					throw new IOException(String.format("%s cannot be created!",
							subPath.toString()));
				}
			}
		}

		if (!basePath.isDirectory()) {
			throw new IOException(String.format("%s is not a directory!",
					basePath.toString()));
		}
		if (subdir.equals(""))
			return basePath;
		else
			return subPath;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/*public static File getBasePath(String subdir) throws IOException {
		File basePath = new File(Environment.getExternalStorageDirectory(),
				CONF.BASE_PATH);

		if (!basePath.exists()) {
			if (!basePath.mkdirs()) {
				throw new IOException(String.format("%s cannot be created!",
						basePath.toString()));
			}
		}
		File subPath = null;
		if (!subdir.equals("")) {
			subPath = new File(Environment.getExternalStorageDirectory(),
					CONF.BASE_PATH+"/"+subdir);
			if (!subPath.exists()) {
				if (!subPath.mkdirs()) {
					throw new IOException(String.format("%s cannot be created!",
							subPath.toString()));
				}
			}
		}

		if (!basePath.isDirectory()) {
			throw new IOException(String.format("%s is not a directory!",
					basePath.toString()));
		}
		if (subdir.equals(""))
			return basePath;
		else
			return subPath;
	}
	*/
	public static File getABSPath(String subdir) throws IOException {
		File basePath = new File(subdir);

		if (!basePath.exists()) {
			if (!basePath.mkdirs()) {
				throw new IOException(String.format("%s cannot be created!",
						basePath.toString()));
			}
		}
		File subPath = null;
		if (!subdir.equals("")) {
			subPath = new File(subdir);
			if (!subPath.exists()) {
				if (!subPath.mkdirs()) {
					throw new IOException(String.format("%s cannot be created!",
							subPath.toString()));
				}
			}
		}

		if (!basePath.isDirectory()) {
			throw new IOException(String.format("%s is not a directory!",
					basePath.toString()));
		}
		if (subdir.equals(""))
			return basePath;
		else
			return subPath;
	}
	
	public static String getFileName(String filename) {
		File f = new File(filename);
		return f.getName();
	}
	
    public static String getExt(String filename, String def) {
    	String[] yy = filename.split("\\?");
        String[] xx = yy[0].split("\\.");
        //Log.d(TAG, "filename:"+filename+"-size:"+xx.length);
    
        if (xx.length<2) {
            return def; 
        } else {
            String ext = xx[xx.length-1];
            //Log.d(TAG, "ext:"+ext);
            return ext;
        }   
    } 
    
    public static boolean getUrlAsFile(String link, String fileName) {
		try {
			// get URL content
			URL url = new URL(link);
			URLConnection conn = url.openConnection();
 
			// open the stream and put it into BufferedReader
			BufferedReader br = new BufferedReader(
                               new InputStreamReader(conn.getInputStream()));
 
			String inputLine;
 
			//save to this filename
			File file = new File(fileName);
 
			if (!file.exists()) {
				file.createNewFile();
			}
 
			//use FileWriter to write file
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
 
			while ((inputLine = br.readLine()) != null) {
				bw.write(inputLine+"\n");
			}
 
			bw.close();
			br.close();
 
			//System.out.println("Done");
			return true;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
 
	}
}
