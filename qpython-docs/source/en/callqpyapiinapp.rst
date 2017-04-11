How to run QPython script in your application ?
=====================================================
You could call QPython to run some script or python code in your app

::

    String extPlgPlusName = "org.qpython.qpy";          // QPython package name
    Intent intent = new Intent();
    intent.setClassName(extPlgPlusName, "org.qpython.qpylib.MPyApi");
    intent.setAction(extPlgPlusName + ".action.MPyApi");

    Bundle mBundle = new Bundle();
    mBundle.putString("app", "myappid");
    mBundle.putString("act", "onPyApi");
    mBundle.putString("flag", "onQPyExec");             // any String flag you may use in your context
    mBundle.putString("param", "");                     // param String param you may use in your context

    /*
    * The String Python code, you can put your py file in res or raw or intenet, so that you can get it the same way, which can make it scalable
    */
    String code = "#qpy:console\n" +
                "try:\n" +
                "    import androidhelper\n" +
                "\n" +
                "    droid = androidhelper.Android()\n" +
                "    line = droid.dialogGetInput()\n" +
                "    s = 'Hello %s' % line.result\n" +
                "    droid.makeToast(s)\n" +
                "except:\n" +
                "    print(\"Hello, Please update to newest QPython version from (http://play.qpython.com/qrcode-python.html) to use this feature\")\n");


    mBundle.putString("pycode", code);
    intent.putExtras(mBundle);

    startActivityForResult(intent, SCRIPT_EXEC_PY);

    ...


    // Deal with the result callback by qpython
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SCRIPT_EXEC_PY) {
            if (data!=null) {
                Bundle bundle = data.getExtras();
                String flag = bundle.getString("flag");
                String param = bundle.getString("param");
                String result = bundle.getString("result"); // Result your Pycode generate
                Toast.makeText(this, "onQPyExec: return ("+result+")", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "onQPyExec: data is null", Toast.LENGTH_SHORT).show();

            }
        }
    }



Sample of running QPython script in other application
-------------------------------------------------------
* `You can see this sample project in github <https://github.com/qpython-android/app-call-qpython-api>`_

* `Another Application which have published in google play - QPython Plugin for Tasker <https://play.google.com/store/apps/details?id=com.qpython.tasker2>`_

.. image:: ../_static/taskerplugin-for-qpython.png
