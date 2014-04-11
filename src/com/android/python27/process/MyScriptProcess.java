package com.android.python27.process;

import com.googlecode.android_scripting.AndroidProxy;
import com.googlecode.android_scripting.interpreter.InterpreterConfiguration;
import com.hipipal.sl4alib.PyScriptService;

import java.io.File;
import java.util.List;
import java.util.Map;

public class MyScriptProcess extends PythonScriptProcess {

	  private String workingDirectory;
	  private String sdcardPackageDirectory;

	  private MyScriptProcess(PyScriptService context,File paramFile, InterpreterConfiguration paramInterpreterConfiguration, AndroidProxy paramAndroidProxy, String workingDir, String sdcardPackageDirectory) {
	    super(context, paramFile, paramInterpreterConfiguration, paramAndroidProxy);
	    this.workingDirectory = workingDir;
	    this.sdcardPackageDirectory = sdcardPackageDirectory;
	  }

	  public static MyScriptProcess launchScript(PyScriptService context,File script, InterpreterConfiguration configuration, final AndroidProxy proxy, Runnable shutdownHook, String workingDir, String sdcardPackageDirectory, List<String> args, Map<String, String> envVars, File binary) {
		  /*if (!script.exists()) {
	        throw new RuntimeException("No such script to launch.");
	      }*/

		  MyScriptProcess localScriptProcess = new MyScriptProcess(context,script, configuration, proxy, workingDir, sdcardPackageDirectory);
		  localScriptProcess.putAllEnvironmentVariables(envVars); // set our python env var
		  /*localScriptProcess.setBinary(binary);
	    
		  if (shutdownHook == null) {
			  localScriptProcess.start(new Runnable() {
		          @Override
		          public void run() {
		            proxy.shutdown();
		          }
			  }, args);
		  } else {
			  localScriptProcess.start(shutdownHook, args);
	      }*/
	      return localScriptProcess;
	  }
	    
	  @Override
	  public String getWorkingDirectory() {
	    return workingDirectory;
	  }

	  @Override
	  public String getSdcardPackageDirectory() {
	    return sdcardPackageDirectory;
	  }
}