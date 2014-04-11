package com.android.python27.support;

import android.os.Environment;
import android.util.Log;

import com.android.python27.config.GlobalConstants;
import com.googlecode.android_scripting.FileUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Utils {
	public static String getFileExtension(String sFileName) {
	  int dotIndex = sFileName.lastIndexOf('.');
	  if (dotIndex == -1) {
	    return null;
	  }
	  return sFileName.substring(dotIndex);
	}
 
	//-------------------------------------------------------------------------------------------------

	  public static boolean unzip(InputStream inputStream, String dest, boolean replaceIfExists) {
		  
		  final int BUFFER_SIZE = 4096;
		  
		  BufferedOutputStream bufferedOutputStream = null;
		  
		  boolean succeed = true;
		  
		  try {
		      ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(inputStream));
		      ZipEntry zipEntry;
		      
		      while ((zipEntry = zipInputStream.getNextEntry()) != null){
		       
		       String zipEntryName = zipEntry.getName();
		       
		       
//		       if(!zipEntry.isDirectory()) {
//		 	       File fil = new File(dest + zipEntryName);
//		 	       fil.getParent()
//		       }
		       
		       // file exists ? delete ?
	 	       File file2 = new File(dest + zipEntryName);

	 	       if(file2.exists()) {
	 		        if (replaceIfExists) {
	 		        	
	 		 	       try {
	 		 	    	  boolean b = deleteDir(file2);
	 		 	    		  if(!b) {
	 		 						Log.e(GlobalConstants.LOG_TAG, "Unzip failed to delete " + dest + zipEntryName);
	 		 	    		  }
	 		 	    		  else {
	 		 						Log.d(GlobalConstants.LOG_TAG, "Unzip deleted " + dest + zipEntryName);
	 		 	    		  }
	 					} catch (Exception e) {
	 						Log.e(GlobalConstants.LOG_TAG, "Unzip failed to delete " + dest + zipEntryName, e);
	 					}
	 		        } 	    	   
	 	       }

		       // extract
		       File file = new File(dest + zipEntryName);
		       
		       if (file.exists()){

		       } else {
		        if(zipEntry.isDirectory()){
		         file.mkdirs(); 
		         FileUtils.chmod(file, 0755);

		        }else{
		        	
	 	         // create parent file folder if not exists yet
	 	         if(!file.getParentFile().exists()) {
			          file.getParentFile().mkdirs(); 
			          FileUtils.chmod(file.getParentFile(), 0755);
	 	         }
			 	       
		         byte buffer[] = new byte[BUFFER_SIZE];
		         bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file), BUFFER_SIZE);
		         int count;

		         while ((count = zipInputStream.read(buffer, 0, BUFFER_SIZE)) != -1) {
		          bufferedOutputStream.write(buffer, 0, count);
		         }

		         bufferedOutputStream.flush();
		         bufferedOutputStream.close(); 
		        }
		       }
		       
		       // enable standalone python
		       if(file.getName().endsWith(".so")) {
			       FileUtils.chmod(file, 0755);
		       }

		       //Log.d(GlobalConstants.LOG_TAG,"Unzip extracted " + dest + zipEntryName);
		      }
		      
		      zipInputStream.close();

		     } catch (FileNotFoundException e) {
		    	 Log.e(GlobalConstants.LOG_TAG,"Unzip error, file not found", e);
		    	 succeed = false;
		     }catch (Exception e) {
		    	 Log.e(GlobalConstants.LOG_TAG,"Unzip error: ", e);
		    	 succeed = false;
		     }
		    
		     return succeed;		     
	  }
	  
	  //-------------------------------------------------------------------------------------------------

	  public static boolean deleteDir(File dir) {
		  try {
		      if (dir.isDirectory()) {
		          String[] children = dir.list();
		          for (int i=0; i<children.length; i++) {
		              boolean success = deleteDir(new File(dir, children[i]));
		              if (!success) {
		                  return false;
		              }
		          }
		      }
		  
		      // The directory is now empty so delete it
		      return dir.delete();
		      
		} catch (Exception e) {
			Log.e(GlobalConstants.LOG_TAG,"Failed to delete " + dir + " : " + e);
			return false;
		}
	  }
	  
	public static void createDirectoryOnExternalStorage(String path) {
        try {
    		if(Environment.getExternalStorageState().equalsIgnoreCase("mounted")) {
    		    File file = new File(Environment.getExternalStorageDirectory(), path);
    		    if (!file.exists()) {
    		    	try {
    		    		file.mkdirs();

    		    		Log.d(GlobalConstants.LOG_TAG, "createDirectoryOnExternalStorage created " + Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +path);
    				} catch (Exception e) {
    		            Log.e(GlobalConstants.LOG_TAG,"createDirectoryOnExternalStorage error: ", e);
    				}
    		    }		
    		}
    		else {
                Log.e(GlobalConstants.LOG_TAG,"createDirectoryOnExternalStorage error: " + "External storage is not mounted");		
    		}
		} catch (Exception e) {
            Log.e(GlobalConstants.LOG_TAG,"createDirectoryOnExternalStorage error: " + e);		
		}

	}
	
}
