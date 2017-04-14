Python builtin Libraries
==========================

Python 3rd Libraries
==========================

Android APIs
==========================


SL4A APIs
=============

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
