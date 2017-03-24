package org.qpython.qpy.main.event;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.webkit.JavascriptInterface;

import org.json.JSONException;
import org.json.JSONObject;
import org.qpython.qpy.main.utils.Utils;
import org.qpython.qpysdk.utils.FileHelper;
import org.renpy.android.PythonActivity;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;

public class Bean {
	protected String title;
    protected Context context;
    protected int dialogIndex;
    protected String srv;

    private final String TAG = "BEAN";
 
    public Bean(Context context) {
    	this.context = context;
    }

	@JavascriptInterface
	public void setSrv(String srv) {
    	this.srv = srv;
    }
	@JavascriptInterface
	public String getSrv() {
    	return this.srv;
    }
	@JavascriptInterface
    public void close() {
		Intent intent1 = new Intent(".QWebViewActivity");
		intent1.putExtra(PythonActivity.EXTRA_CONTENT_URL1, "close");
    	context.sendBroadcast(intent1);
    }
	@JavascriptInterface
    public void alert(String info) {
		Intent intent1 = new Intent(".QWebViewActivity");
		intent1.putExtra(PythonActivity.EXTRA_CONTENT_URL1, "alert");
		intent1.putExtra(PythonActivity.EXTRA_CONTENT_URL2, info);

		context.sendBroadcast(intent1);
    }

	@JavascriptInterface
    public void showWait() {
		Intent intent1 = new Intent(".QWebViewActivity");
		intent1.putExtra(PythonActivity.EXTRA_CONTENT_URL1, "showwait");
    	context.sendBroadcast(intent1);
    }
	@JavascriptInterface
    public void closeWait() {
		Intent intent1 = new Intent(".QWebViewActivity");
		intent1.putExtra(PythonActivity.EXTRA_CONTENT_URL1, "closewait");
    	context.sendBroadcast(intent1);
    }
	@JavascriptInterface
    public void pipConsole() {
		Intent intent1 = new Intent(".QWebViewActivity");
		intent1.putExtra(PythonActivity.EXTRA_CONTENT_URL1, "pipconsole");
		context.sendBroadcast(intent1);
    }
	@JavascriptInterface
    public void pipInstall(String link, String src) {
		Intent intent1 = new Intent(".QWebViewActivity");
		intent1.putExtra(PythonActivity.EXTRA_CONTENT_URL1, "pipinstall");
		intent1.putExtra(PythonActivity.EXTRA_CONTENT_URL2, link);
		intent1.putExtra(PythonActivity.EXTRA_CONTENT_URL3, src);
		context.sendBroadcast(intent1);
    }
	@JavascriptInterface
    public void libMan() {
		Intent intent1 = new Intent(".QWebViewActivity");
		intent1.putExtra(PythonActivity.EXTRA_CONTENT_URL1, "libman");
		
		context.sendBroadcast(intent1);
    }
	@JavascriptInterface
    public void showDrawerMenu(String menuPythonActivity) {
		Intent intent1 = new Intent(".QWebViewActivity");
		intent1.putExtra(PythonActivity.EXTRA_CONTENT_URL1, "showdrawermenu");
		intent1.putExtra(PythonActivity.EXTRA_CONTENT_URL2, menuPythonActivity);
		context.sendBroadcast(intent1);
    }

	@JavascriptInterface
    public boolean isSrvOk(String srv) {
    	try {
			URL u = new URL(srv.equals("")?this.srv:srv);
			int port = 80;
			if (u.getPort() != -1) {
				port = u.getPort();
			}
			String url = u.getProtocol() + "://" +u.getHost()+":"+port+"/";
	    	boolean ret =  Utils.httpPing(url, 1000);
			//Log.d("Bean", "isSrvOk:"+url+" - "+ret);
			return ret;

		} catch (MalformedURLException e) {
			//Log.d("Bean", "MalformedURLException:"+e);
			return false;
		}
    }


	@JavascriptInterface
    public void setTitle(String title){
        this.title = title;
    }

    /*public void a8viewChanel(String url, String nav, String act, String desc, String icon) {
    	Intent intent = new Intent(context, MSearchAct.class);
		intent.putExtra(PythonActivity.EXTRA_CONTENT_URL1, url);
		intent.putExtra(PythonActivity.EXTRA_CONTENT_URL2, nav);
		intent.putExtra(PythonActivity.EXTRA_CONTENT_URL3, "0");
		intent.putExtra(PythonActivity.EXTRA_CONTENT_URL4, act);
		intent.putExtra(PythonActivity.EXTRA_CONTENT_URL5, desc);
		intent.putExtra(PythonActivity.EXTRA_CONTENT_URL6, icon);

		context.startActivity(intent);
    }*/

	@JavascriptInterface
    public void tbdownload(String title, String url, String cat) {
    	
		Intent intent1 = new Intent(".QWebViewActivity");
		intent1.putExtra(PythonActivity.EXTRA_CONTENT_URL1, "download");

		intent1.putExtra(PythonActivity.EXTRA_CONTENT_URL2, url);
		intent1.putExtra(PythonActivity.EXTRA_CONTENT_URL3, cat);
		intent1.putExtra(PythonActivity.EXTRA_CONTENT_URL4, title);

    	context.sendBroadcast(intent1);
    }
	@JavascriptInterface
    public void a8addChanel(String title, String link, String desc, String act, String theme) {
    	JSONObject obj = new JSONObject();
		try {
			obj.put("title", title);
    		obj.put("icon", "default");
    		//"grey3"
    		obj.put("theme", theme);
    		//"milib:pyplugin"
    		obj.put("act", act);
    		obj.put("link", link);
    		obj.put("desc", desc);
    		
		} catch (JSONException e) {
			e.printStackTrace();
		}

		//CatMan.setChanel(context, obj, true, "");
    }
	@JavascriptInterface
    public void openUrl(String url) {
		Intent intent = Utils.openRemoteLink(context, url);
		context.startActivity(intent);
    }
	@JavascriptInterface
    public void call(String number) {
    	Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
    	context.startActivity(phoneIntent);
    }
	@JavascriptInterface
    public String isNetworkOk(Context context) {
    	if (Utils.netOk(context)) {
    		return "1";
    	} else {
    		return "0";
    	}
    }
	@JavascriptInterface
	public void a8removeChanel(String link) {
		//CatMan.setChanel(context, null, false, link);
    }
	@JavascriptInterface
    public String returnTmpScript(String xcode, String flag, String param) {
		try {
			//
			File root = new File(Environment.getExternalStorageDirectory()+"/qpython/cache");
			if (root!=null) {
				FileHelper.clearDir( root.toString(), 0, false);
			}

			//String py = FileHelper.getBasePath(MyApp.getInstance().getRoot(), PythonActivity.DFROM_QPY)+"/"+PythonActivity.API_SCRIPT;
			String py;
			String code;
			if (flag.equals("qedit")) {
				if (param!=null && !param.equals("")) {
					File f = new File(param);
					py = new File(f.getParentFile(), ".last_tmp.py").toString();
				} else {
					py = new File(root, ".last_tmp.py").toString();
				}
				if (xcode.contains("#{HEADER}")) {
					code = xcode.replace("#{HEADER}", "");
				} else {
					code = xcode;
				}

			} else {
				py = Environment.getExternalStorageDirectory()+"/qpython/cache/main.py";
				if (xcode.contains("#{HEADER}")) {
					code = xcode.replace("#{HEADER}", "PARAM = '"+param+"'");

				} else {
					code = "PARAM = '"+param+"'\n"+ xcode;
				}
			}
			//Log.d(TAG, "py:"+py);
	    	File pyCache = new File(py);
	    	if (!pyCache.exists()) {
	    		pyCache.createNewFile();
	    	}
	    	byte[] xcontent = code.getBytes();
			RandomAccessFile accessFile = new RandomAccessFile(pyCache.getAbsoluteFile(), "rwd");
			accessFile.setLength(xcontent.length);
			accessFile.seek(0);
			accessFile.write(xcontent, 0, xcontent.length);
			accessFile.close();

			return py;
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
    }


	@JavascriptInterface
    public void qeditor(String content) {
    	
    	String py = returnTmpScript(content, "qedit", null);

		Intent intent = new Intent();
		intent.setClassName(context, "com.hipipal.texteditor.TedActivity");
		intent.setAction(Intent.ACTION_VIEW);

		intent.putExtra(PythonActivity.EXTRA_CONTENT_URL0, "web");
        Uri uri = Uri.fromFile(new File(py));
        intent.setDataAndType(uri , "text/x-python");

		context.startActivity(intent);
    }

	@JavascriptInterface
	public String qpyChecklibinstall(String cat, String smodule) {
    	final File libFile = getLibFile(smodule, cat);

    	if (libFile.exists()) {
        	return "1";
        } else {
        	return "0";
        }
    }

	@JavascriptInterface
	public File getLibFile(String smodule, String cat) {
		//String code = NAction.getCode(this);
		boolean isQpy3 = Utils.isQPy3(context);
    	String base = isQpy3?"python3.2/site-packages":"python2.7/site-packages";
    	String sbase = isQpy3?"python3.2":"python2.7";
    	String ubase = isQpy3?"scripts3":"scripts";
    	String pbase = isQpy3?"projects3":"projects";

		File libFile;
		if (cat.equals("script")) {
            libFile = new File(Environment.getExternalStorageDirectory(), "qpython/"+ubase+"/"+smodule);

		} else if (cat.equals("user")) {
            libFile = new File(Environment.getExternalStorageDirectory(),"qpython/"+pbase+"/"+smodule);

		} else if (cat.equals("component")) {
            libFile = new File(Environment.getExternalStorageDirectory(), "qpython/lib/"+smodule);

    	} else if (cat.equals("dev")) {
            libFile = new File(context.getFilesDir(), "/lib/"+sbase+"/site-packages/"+smodule);
            

    	} else {
            libFile = new File(Environment.getExternalStorageDirectory(), "qpython/"+PythonActivity.LIB_DIR+"/"+base+"/"+smodule);

    	} 
    	return libFile;
	}

	@JavascriptInterface
	public void qpylibinstall(String cat, String link, String target) {
    	if (target.equals("commerce")) {
    		context.startActivity(Utils.openRemoteLink(context, link));
    		
    	} else {
	    	Intent intent = new Intent();
	    	if (cat.equals("user") || cat.equals("script")) {
	    		intent.setClassName(context.getPackageName(), context.getPackageName()+".UProfileAct");
	    		intent.putExtra("from", cat);
	
	    	} else {
	    		intent.setClassName(context.getPackageName(), context.getPackageName()+".MPyLibAct");
	    	}
	    	intent.putExtra(PythonActivity.EXTRA_CONTENT_URL0, "install");
	    	intent.putExtra(PythonActivity.EXTRA_CONTENT_URL1, cat);
	    	intent.putExtra(PythonActivity.EXTRA_CONTENT_URL2, link);
	    	intent.putExtra(PythonActivity.EXTRA_CONTENT_URL3, target);
	
	    	context.startActivity(intent);
    	}
    	
    }

	@JavascriptInterface
	public void setQBTitle(String title) {
		Intent intent1 = new Intent(".QWebViewActivity");
		intent1.putExtra(PythonActivity.EXTRA_CONTENT_URL1, "settitle");
		intent1.putExtra(PythonActivity.EXTRA_CONTENT_URL2, title);
    	context.sendBroadcast(intent1);

    }

	@JavascriptInterface
	public void showLog() {
		Intent intent2 = new Intent(".QWebViewActivity");
		intent2.putExtra(PythonActivity.EXTRA_CONTENT_URL1, "notifylog");
		intent2.putExtra(PythonActivity.EXTRA_CONTENT_URL2, "");

		context.sendBroadcast(intent2);
    }

	@JavascriptInterface
	public void feedback(String logfile) {
		Intent intent2 = new Intent(".QWebViewActivity");
		intent2.putExtra(PythonActivity.EXTRA_CONTENT_URL1, "feedback");
		intent2.putExtra(PythonActivity.EXTRA_CONTENT_URL2, logfile);

		context.sendBroadcast(intent2);
    }

	@JavascriptInterface
	public void loadConsole(String script) {
    	//Log.d(TAG, "loadConsole:"+script);
		Intent intent1 = new Intent(".QWebViewActivity");
		intent1.putExtra(PythonActivity.EXTRA_CONTENT_URL1, "launchsrv");
		intent1.putExtra(PythonActivity.EXTRA_CONTENT_URL2, script);
    	context.sendBroadcast(intent1);

    }

	@JavascriptInterface
	public void setActivityTitle(String title) {
    }


	@JavascriptInterface
	public String getTitle(){
        return this.title;
    }
}
