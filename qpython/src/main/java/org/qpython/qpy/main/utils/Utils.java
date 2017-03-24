package org.qpython.qpy.main.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import org.qpython.qpy.main.activity.QWebViewActivity;
import org.qpython.qpysdk.utils.FileHelper;
import org.qpython.qpysdk.utils.FileUtils;
import org.renpy.android.PythonActivity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by mathiasluo on 16-6-1.
 */

public class Utils extends org.qpython.qpysdk.utils.Utils {
    static final String TAG = "Utils";
    public static void startWebActivityWithUrl(Context context, String title, String url, String script, boolean isNoHead, boolean isDrawer)  {
        Intent intent = new Intent(context, QWebViewActivity.class);
        if (script.equals("")) {
            Uri u = Uri.parse(url);
            intent.setData(u);
        }

        intent.putExtra(PythonActivity.EXTRA_CONTENT_URL1, "main");
        intent.putExtra(PythonActivity.EXTRA_CONTENT_URL2, title);
        intent.putExtra(PythonActivity.EXTRA_CONTENT_URL3, url);
        intent.putExtra(PythonActivity.EXTRA_CONTENT_URL4, script);
        intent.putExtra(PythonActivity.EXTRA_CONTENT_URL5, isNoHead?"1":"0");
        intent.putExtra(PythonActivity.EXTRA_CONTENT_URL6, isDrawer?"drawer":"");


        context.startActivity(intent);
        /*
        new FinestWebView.Builder(context)
                .theme(R.style.WebViewTheme)
                .titleDefault(Title)
                .webViewBuiltInZoomControls(true)
                .webViewDisplayZoomControls(true)
                .dividerHeight(0)
                .gradientDivider(false)
                .swipeRefreshColorRes(R.color.colorAccent)
                .setCustomAnimations(R.anim.activity_open_enter, R.anim.activity_open_exit, R.anim.activity_close_enter, R.anim.activity_close_exit)
                .show(url);
                */
    }

    public static void createDirectoryOnExternalStorage(String path) {
        try {
            if(Environment.getExternalStorageState().equalsIgnoreCase("mounted")) {
                File file = new File(Environment.getExternalStorageDirectory(), path);
                if (!file.exists()) {
                    try {
                        file.mkdirs();

                        Log.d(TAG, "createDirectoryOnExternalStorage created " + Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +path);
                    } catch (Exception e) {
                        Log.e(TAG,"createDirectoryOnExternalStorage error: ", e);
                    }
                }
            }
            else {
                Log.e(TAG,"createDirectoryOnExternalStorage error: " + "External storage is not mounted");
            }
        } catch (Exception e) {
            Log.e(TAG,"createDirectoryOnExternalStorage error: " + e);
        }

    }

    public static boolean unzip(InputStream inputStream, String dest, boolean replaceIfExists) {
        Log.d(TAG, "unzip:"+dest);
        final int BUFFER_SIZE = 4096;

        BufferedOutputStream bufferedOutputStream = null;

        boolean succeed = true;

        if (replaceIfExists) {
            File file2 = new File(dest);
            if (file2.exists()) {
                try {
                    boolean b = FileUtils.deleteDir(file2);
                } catch (Exception e) {
                }
            }
        }

        try {
            ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(inputStream));
            ZipEntry zipEntry;

            while ((zipEntry = zipInputStream.getNextEntry()) != null){

                String zipEntryName = zipEntry.getName();
                String fs = dest + zipEntryName;

                if (!dest.endsWith("/")) {
                    fs = dest;
                }
                //Log.d(TAG, "zipEntryName:"+zipEntryName+"-file2:"+fs+"-"+fs.indexOf('/'));

//		       if(!zipEntry.isDirectory()) {
//		 	       File fil = new File(dest + zipEntryName);
//		 	       fil.getParent()
//		       }

                // file exists ? delete ?
	 	       /*File file2 = new File(fs);
	 	       if(file2.exists()) {
	 		        if (replaceIfExists) {

	 		 	       try {
	 		 	    	  boolean b = deleteDir(file2);
	 		 	    		  if(!b) {
	 		 						Log.e(TAG, "Unzip failed to delete " + dest + zipEntryName);
	 		 	    		  }
	 		 	    		  else {
	 		 						Log.d(TAG, "Unzip deleted " + dest + zipEntryName);
	 		 	    		  }
	 					} catch (Exception e) {
	 						Log.e(TAG, "Unzip failed to delete " + dest + zipEntryName, e);
	 					}
	 		        }
	 	       }*/

                // extract
                File file = new File(fs);

                if (file.exists()){
                    Log.d(TAG, "unzip exists");
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

                Log.d(TAG,"Unzip extracted " + dest + zipEntryName);
            }

            zipInputStream.close();

        } catch (FileNotFoundException e) {
            Log.e(TAG,"Unzip error, file not found", e);
            succeed = false;
        }catch (Exception e) {
            Log.e(TAG,"Unzip error: ", e);
            succeed = false;
        }

        return succeed;
    }

    public static boolean netOk(Context context) {
        try {
            ConnectivityManager nInfo = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            nInfo.getActiveNetworkInfo().isConnectedOrConnecting();


            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            Log.d(TAG, "Network available:false");

            return false;
        }
    }

    public static String getLang() {
        return Locale.getDefault().getLanguage();
    }

    public static int getVersinoCode(Context context){
        int intVersioinCode=0;
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            intVersioinCode=info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return intVersioinCode;
    }

    public static Intent openRemoteLink(Context context, String link) {
        //Log.d(TAG, "openRemoteLink:"+link);
        String vlowerFileName = link.toLowerCase();
        if (vlowerFileName.startsWith("lgmarket:")) {
            String[] xx = link.split(":");
            //Log.d(TAG, "lgmarket:"+xx[1]);

            Intent intent = new Intent("com.lge.lgworld.intent.action.VIEW");
            intent.setClassName("com.lge.lgworld", "com.lge.lgworld.LGReceiver");
            intent.putExtra("lgworld.receiver","LGSW_INVOKE_DETAIL");
            intent.putExtra("APP_PID", xx[1]);

			/*Intent intent = new Intent();
			intent.setClassName("com.lg.apps.cubeapp", "com.lg.apps.cubeapp.PreIntroActivity");
			intent.putExtra("type", "APP_DETAIL ");
			intent.putExtra("codeValue", ""); // value is not needed when moving to Detail page
			intent.putExtra("content_id", xx[1]);   */

            context.sendBroadcast(intent);

            return null;

        } else {
            Uri uLink = Uri.parse(link);

            Intent intent = new Intent( Intent.ACTION_VIEW, uLink );

            return intent;
        }
    }

    public static String getCode(Context context) {
        String packageName = context.getPackageName();
        String[] xcode = packageName.split("\\.");
        String code = xcode[xcode.length-1];
        return code;
    }

    public static boolean isQPy3(Context context) {
        String code = Utils.getCode(context);
        if (code.contains("qpy3")) {
            return true;
        } else {
            return false;
        }
    }

    public static String getSrv(String script) {
        //Log.d(TAG, "getSrv:"+script);

        String content = FileHelper.getFileContents(script);

        String srv = "http://localhost";
        Pattern srvPattern = Pattern.compile("#qpy://(.+)[\\s]+", Pattern.CASE_INSENSITIVE);
        Matcher matcher1 = srvPattern.matcher(content);

        if (matcher1.find()) {
            srv = "http://"+matcher1.group(1);

            //Log.d(TAG, "URL2:"+srv);

        }
        try {
            URL n = new URL(srv);
            srv = n.getProtocol()+"://"+n.getHost()+":"+(n.getPort()>0?n.getPort():80);
            //Log.d(TAG, "getsrv:"+srv);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return srv;
    }


}
