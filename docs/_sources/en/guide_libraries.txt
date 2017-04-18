QPython built-in Libraries
==========================
QPython is using the Python 2.7.2 and it support most Python stardard libraries. And you could see their documentation through Python documentation.

QPython dynload libraries (python built-in .so libraries)
----------------------------------------------
Usually, you don't need to  import them manually, they were used in stardard libraries, and could be imported automatically.

* _codecs_cn.so
* _codecs_hk.so
* _codecs_iso2022.so
* _codecs_jp.so
* _codecs_kr.so
* _codecs_tw.so
* _csv.so
* _ctypes.so
* _ctypes_test.so
* _hashlib.so
* _heapq.so
* _hotshot.so
* _io.so
* _json.so
* _lsprof.so
* _multibytecodec.so
* _sqlite3.so
* _ssl.so
* _testcapi.so
* audioop.so
* future_builtins.so
* grp.so
* mmap.so
* resource.so
* syslog.so
* termios.so
* unicodedata.so

QPython stardard libraries
---------------------------
The following libraries are the stardard QPython libraries which are the same as Python:

- `BaseHTTPServer.py <https://docs.python.org/2/library/basehttpserver.html>`_
- `binhex.py <https://docs.python.org/2/library/binhex.html>`_
- `fnmatch.py <https://docs.python.org/2/library/fnmatch.html>`_
- mhlib.py
- quopri.py
- sysconfig.py
- Bastion.py
- bisect.py
- formatter.py
- mimetools.py
- random.py
- tabnanny.py
- CGIHTTPServer.py
- bsddb
- fpformat.py
- mimetypes.py
- re.py
- tarfile.py
- ConfigParser.py
- cProfile.py
- fractions.py
- mimify.py
- repr.py
- telnetlib.py
- Cookie.py
- calendar.py
- ftplib.py
- modulefinder.py
- rexec.py
- tempfile.py
- DocXMLRPCServer.py
- cgi.py
- functools.py
- multifile.py
- rfc822.py
- textwrap.py
- HTMLParser.py
- cgitb.py
- genericpath.py
- mutex.py
- rlcompleter.py
- this.py
- chunk.py
- getopt.py
- netrc.py
- robotparser.py
- threading.py
- MimeWriter.py
- cmd.py
- getpass.py
- new.py
- runpy.py
- timeit.py
- Queue.py
- code.py
- gettext.py
- nntplib.py
- sched.py
- toaiff.py
- SimpleHTTPServer.py
- codecs.py
- glob.py
- ntpath.py
- sets.py
- token.py
- SimpleXMLRPCServer.py
- codeop.py
- gzip.py
- nturl2path.py
- sgmllib.py
- tokenize.py
- SocketServer.py
- collections.py
- hashlib.py
- numbers.py
- sha.py
- trace.py
- StringIO.py
- colorsys.py
- heapq.py
- opcode.py
- shelve.py
- traceback.py
- UserDict.py
- commands.py
- hmac.py
- optparse.py
- shlex.py
- tty.py
- UserList.py
- compileall.py
- hotshot
- os.py
- shutil.py
- types.py
- UserString.py
- compiler
- htmlentitydefs.py
- os2emxpath.py
- site.py
- unittest
- _LWPCookieJar.py
- config
- htmllib.py
- smtpd.py
- urllib.py
- _MozillaCookieJar.py
- contextlib.py
- httplib.py
- pdb.py
- smtplib.py
- urllib2.py
- __future__.py
- cookielib.py
- ihooks.py
- pickle.py
- sndhdr.py
- urlparse.py
- __phello__.foo.py
- copy.py
- imaplib.py
- pickletools.py
- socket.py
- user.py
- _abcoll.py
- copy_reg.py
- imghdr.py
- pipes.py
- sqlite3
- uu.py
- _pyio.py
- csv.py
- importlib
- pkgutil.py
- sre.py
- uuid.py
- _strptime.py
- ctypes
- imputil.py
- plat-linux4
- sre_compile.py
- warnings.py
- _threading_local.py
- dbhash.py
- inspect.py
- platform.py
- sre_constants.py
- wave.py
- _weakrefset.py
- decimal.py
- io.py
- plistlib.py
- sre_parse.py
- weakref.py
- abc.py
- difflib.py
- json
- popen2.py
- ssl.py
- webbrowser.py
- aifc.py
- dircache.py
- keyword.py
- poplib.py
- stat.py
- whichdb.py
- antigravity.py
- dis.py
- lib-tk
- posixfile.py
- statvfs.py
- wsgiref
- anydbm.py
- distutils
- linecache.py
- posixpath.py
- string.py
- argparse.py
- doctest.py
- locale.py
- pprint.py
- stringold.py
- xdrlib.py
- ast.py
- dumbdbm.py
- logging
- profile.py
- stringprep.py
- xml
- asynchat.py
- dummy_thread.py
- macpath.py
- pstats.py
- struct.py
- xmllib.py
- asyncore.py
- dummy_threading.py
- macurl2path.py
- pty.py
- subprocess.py
- xmlrpclib.py
- atexit.py
- email
- mailbox.py
- py_compile.py
- sunau.py
- zipfile.py
- audiodev.py
- encodings
- mailcap.py
- pyclbr.py
- sunaudio.py
- base64.py
- filecmp.py
- markupbase.py
- pydoc.py
- symbol.py
- bdb.py
- fileinput.py
- md5.py
- pydoc_data
- symtable.py



Python 3rd Libraries
==========================

- `BeautifulSoup.py(3) <https://www.crummy.com/software/BeautifulSoup/bs3/documentation.html>`_
- pkg_resources.py
- androidhelper
- plyer
- `bottle.py <http://bottlepy.org/docs/dev/>`_
- qpy.py
- qpythoninit.py
- setuptools
- `pip <https://pypi.python.org/pypi/pip/>`_


Androidhelper APIs
========================

.. image:: ../_static/sl4a.jpg

To simplify QPython SL4A development in IDEs with a
"hepler" class derived from the default Android class containing
SL4A facade functions & API documentation


Structure
------------


QPython NFC json result 
::

    {
    "role": <role>, # could be self/master/slave
    "stat": <stat>, # could be ok / fail / cancl
    "message": <message get> 
    }

QPy APIs
----------

.. py:function:: AndroidHelper.executeQPy(script)

    Execute a qpython script by absolute path

    :param str script: The absolute path of the qpython script
    :return: boolean


NFC APIs
------------
NFC Message Beam APIs

.. py:function:: AndroidHelper.dialogCreateNFCBeamMaster()

    Create a dialog which can send the message to NFC Beam Slave

    :return: QPython NFC json result

.. py:function:: AndroidHelper.NFCBeamMessage(message)

    Sendthe message to NFC Beam Slave without dialog

    :return: QPython NFC json result


.. py:function:: AndroidHelper.dialogCreateNFCBeamSlave()

    Create a NFC Beam Slave to wait for the master's beam message

    :return: QPython NFC json result


Location API
------------

.. py:function:: androidhelper.getLastKnownLocation


::

    Droid = androidhelper.Android()
    location = Droid.getLastKnownLocation().result
    location = location.get('network', location.get('gps'))

Sensor API
------------

.. py:function:: androidhelper.sensorsReadOrientation()

::

    Droid = androidhelper.Android()
    Droid.startSensingTimed(1, 250)
    sensor = Droid.sensorsReadOrientation().result
    Droid.stopSensing()

Other SL4A APIs
----------------

.. py:function:: AndroidHelper.dialogCreateSpinnerProgress(title,message,maximum progress)

    Create a spinner progress dialog

.. py:function:: AndroidHelper.webViewShow(url,wait)

    Display a WebView with the given URL.

    :param str url: URL
    :param boolean wait(Optional): block until the user exits the WebView
