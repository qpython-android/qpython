var total = 0
function gotoApp() {
	total = total+1
	if (total>=10) {
		//setTimeout('milib.closeWait()',100);
	    $('#mainEle').html('<div id="loading">Error: Web App failed to start,<br />retry or <a href="javascript:milib.showLog()">check the log</a>.</div>');
	
	} else {
	 	if (milib.isSrvOk("")) {
	    	setTimeout('window.location=milib.getSrv()',100);
	    	
		} else {
			if (total<15) {
		    	setTimeout('gotoApp()',3000);
		    }
		}
	}
}

{
    $('#mainEle').html('<div class="loading"><div></div><div></div><div></div><div></div><div></div><div></div>&emsp;&emsp;&emsp;</div>');
    //milib.showWait();
    milib.loadConsole("");

    gotoApp();
    
    
    /*var droid = new AndroidHelper();
    droid.makeToast('HelloWorld')*/
}
