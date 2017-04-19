.. image:: ../_static/sl4a.jpg

AndroidFacade
===============

Clipboard APIs
----------------
.. py:function:: setClipboard(text)

   Put text in the clipboard

   :param str text: text

.. py:function:: getClipboard(text)

   Read text from the clipboard

   :return: The text in the clipboard


::

    sample code to show setClipboard/getClipboard


Intent & startActivity APIs
----------------------------------
.. py:function:: makeIntent(action, uri, type, extras, categories, packagename, classname, flags)

   Starts an activity and returns the result

   :param str action: action
   :param str uri(Optional): uri
   :param str type(Optional): MIME type/subtype of the URI
   :param jsonobject extras(Optional): a Map of extras to add to the Intent
   :param jsonarray categories(Optional): a List of categories to add to the Intent
   :param str packagename(Optional): name of package. If used, requires classname to be useful
   :param str classname(Optional): name of class. If used, requires packagename to be useful
   :param int flags(Optional): Intent flags

   :return: An object representing an Intent


::

    sample code to show makeIntent


.. py:function:: getIntent()

   Returns the intent that launched the script

::

    sample code to show getIntent


.. py:function:: startActivityForResult(action, uri, type, extras, packagename, classname)

   Starts an activity and returns the result

   :param str action: action
   :param str uri(Optional): uri
   :param str type(Optional): MIME type/subtype of the URI
   :param jsonobject extras(Optional): a Map of extras to add to the Intent
   :param str packagename(Optional): name of package. If used, requires classname to be useful
   :param str classname(Optional): name of class. If used, requires packagename to be useful

   :return: A Map representation of the result Intent


::

    sample code to show startActivityForResult


.. py:function:: startActivityForResultIntent(intent)

   Starts an activity and returns the result

   :param Intent intent: Intent in the format as returned from makeIntent


   :return: A Map representation of the result Intent


::

    sample code to show startActivityForResultIntent

.. py:function:: startActivityIntent(intent, wait)

   Starts an activity

   :param Intent intent: Intent in the format as returned from makeIntent
   :param boolean wait(Optional): block until the user exits the started activity

::

    sample code to show startActivityIntent


.. py:function:: startActivity(action, uri, type, extras, wait, packagename, classname)

   Starts an activity

   :param str action: action
   :param str uri(Optional): uri
   :param str type(Optional): MIME type/subtype of the URI
   :param jsonobject extras(Optional): a Map of extras to add to the Intent
   :param boolean wait(Optional): block until the user exits the started activity
   :param str packagename(Optional): name of package. If used, requires classname to be useful
   :param str classname(Optional): name of class. If used, requires packagename to be useful

::

    sample code to show startActivityForResultIntent


SendBroadcast APIs
-------------------
.. py:function:: sendBroadcast(action, uri, type, extras, packagename, classname)

   Send a broadcast

   :param str action: action
   :param str uri(Optional): uri
   :param str type(Optional): MIME type/subtype of the URI
   :param jsonobject extras(Optional): a Map of extras to add to the Intent
   :param str packagename(Optional): name of package. If used, requires classname to be useful
   :param str classname(Optional): name of class. If used, requires packagename to be useful


::

    sample code to show sendBroadcast

.. py:function:: sendBroadcastIntent(intent)

   Send a broadcast

   :param Intent intent: Intent in the format as returned from makeIntent

::

    sample code to show sendBroadcastIntent


Vibrate
----------
.. py:function:: vibrate(intent)

   Vibrates the phone or a specified duration in milliseconds

   :param int duration: duration in milliseconds

::

    sample code to show vibrate


NetworkStatus
---------------
.. py:function:: getNetworkStatus()

   Returns the status of network connection

::

    sample code to show getNetworkStatus

PackageVersion APIs
------------------------------
.. py:function:: requiredVersion(requiredVersion)

   Checks if version of QPython SL4A is greater than or equal to the specified version

   :param int requiredVersion: requiredVersion

   :return: true or false


.. py:function:: getPackageVersionCode(packageName)

   Returns package version code

   :param str packageName: packageName

   :return: Package version code

.. py:function:: getPackageVersion(packageName)

   Returns package version name

   :param str packageName: packageName

   :return: Package version name


::

    sample code to show getPackageVersionCode & getPackageVersion


System APIs
--------------------------------
.. py:function:: getConstants(classname)

   Get list of constants (static final fields) for a class

   :param str classname: classname

   :return: list

::

    sample code to show getConstants

.. py:function:: environment()

   A map of various useful environment details

   :return: environment map object includes id, display, offset, TZ, SDK, download, appcache, availblocks, blocksize, blockcount, sdcard

::

    sample code to show environment

.. py:function:: log(message)

   Writes message to logcat

   :param str message: message

::

    sample code to show log


SendEmail
----------
.. py:function:: sendEmail(to, subject, body, attachmentUri)

   Launches an activity that sends an e-mail message to a given recipient

   :param str to: A comma separated list of recipients
   :param str subject: subject
   :param str body: mail body
   :param str attachmentUri(Optional): message

::

    sample code to show sendEmail


Toast, getInput, getPassword, notify APIs
------------------------------------------------
.. py:function:: makeToast(message)

   Displays a short-duration Toast notification

   :param str message: message

::

    sample code to show makeToast

.. py:function:: getInput(title, message)

   Queries the user for a text input

   :param str title: title of the input box
   :param str message: message to display above the input box

::

    sample code to show getInput

.. py:function:: getPassword(title, message)

   Queries the user for a password

   :param str title: title of the input box
   :param str message: message to display above the input box

::

    sample code to show getPassword

.. py:function:: notify(title, message)

   Displays a notification that will be canceled when the user clicks on it

   :param str title: title
   :param str message: message 

::

    sample code to show notify


ApplicationManagerFacade
=========================

Manager APIs
-------------

.. py:function:: getLaunchableApplications()

   Returns a list of all launchable application class names

   :return: map object

::

    sample code to show getLaunchableApplications


.. py:function:: launch(classname)

   Start activity with the given class name

   :param str classname: classname 

::

    sample code to show launch

.. py:function:: getRunningPackages()

   Returns a list of packages running activities or services

   :return: list

::

    sample code to show getRunningPackages

.. py:function:: forceStopPackage(packageName)

   Force stops a package

   :param str packageName: packageName

::

    sample code to show forceStopPackage


CameraFacade
=========================

.. py:function:: cameraCapturePicture(targetPath)

   Take a picture and save it to the specified path

.. py:function:: cameraInteractiveCapturePicture(targetPath)

   Starts the image capture application to take a picture and saves it to the specified path

CommonIntentsFacade
=========================

Barcode
----------
.. py:function:: scanBarcode()

   Starts the barcode scanner

View APIs
----------
.. py:function:: pick(uri)

   Display content to be picked by URI (e.g. contacts)

.. py:function:: view(uri, type, extras)

   Start activity with view action by URI (i.e. browser, contacts, etc.)

.. py:function:: viewMap(query)

   Opens a map search for query (e.g. pizza, 123 My Street)

.. py:function:: viewContacts()

   Opens the list of contacts

.. py:function:: viewHtml(path)

   Opens the browser to display a local HTML file

.. py:function:: search(query)

   Starts a search for the given query

ContactsFacade
=========================

.. py:function:: pickContact()

   Displays a list of contacts to pick from

.. py:function:: pickPhone()

   Displays a list of phone numbers to pick from

.. py:function:: contactsGetAttributes()

   Returns a List of all possible attributes for contacts

.. py:function:: contactsGetIds()

   Returns a List of all contact IDs

.. py:function:: contactsGet(attributes)

   Returns a List of all contacts

.. py:function:: contactsGetById(id)

   Returns contacts by ID

.. py:function:: contactsGetCount()

   Returns the number of contacts

.. py:function:: queryContent(uri, attributes, selection, selectionArgs, order)

   Content Resolver Query

.. py:function:: queryAttributes(uri)

   Content Resolver Query Attributes

EventFacade
=========================

.. py:function:: eventClearBuffer()

   Clears all events from the event buffer

.. py:function:: eventRegisterForBroadcast(category, enqueue)

   Registers a listener for a new broadcast signal

.. py:function:: eventUnregisterForBroadcast(category)

   Stop listening for a broadcast signal

.. py:function:: eventGetBrodcastCategories()

   Lists all the broadcast signals we are listening for

.. py:function:: eventPoll(number_of_events)

   Returns and removes the oldest n events (i.e. location or sensor update, etc.) from the event buffer

.. py:function:: eventWaitFor(eventName, timeout)

   Blocks until an event with the supplied name occurs. The returned event is not removed from the buffer

.. py:function:: eventWait(timeout)

   Blocks until an event occurs. The returned event is removed from the buffer

.. py:function:: eventPost(name, data, enqueue)

   Post an event to the event queue

.. py:function:: rpcPostEvent(name, data)

   Post an event to the event queue

.. py:function:: receiveEvent()

   Returns and removes the oldest event (i.e. location or sensor update, etc.) from the event buffer

.. py:function:: waitForEvent(eventName, timeout)

   Blocks until an event with the supplied name occurs. The returned event is not removed from the buffer

.. py:function:: startEventDispatcher(port)

   Opens up a socket where you can read for events posted

.. py:function:: stopEventDispatcher()

   Stops the event server, you can't read in the port anymore

LocationFacade
=========================

Providers APIs
-----------------

.. py:function:: locationProviders()

   Returns availables providers on the phone

.. py:function:: locationProviderEnabled(provider)

   Ask if provider is enabled

Location APIs
-----------------
.. py:function:: startLocating(minDistance, minUpdateDistance)

   Starts collecting location data

.. py:function:: readLocation()

   Returns the current location as indicated by all available providers

.. py:function:: stopLocating()

   Stops collecting location data

.. py:function:: getLastKnownLocation()

   Returns the last known location of the device

GEO
-----------
.. py:function:: geocode(latitude, longitude, maxResults)

   Returns a list of addresses for the given latitude and longitude

PhoneFacade
=========================

PhoneStat APIs
----------------

.. py:function:: startTrackingPhoneState()

   Starts tracking phone state

.. py:function:: readPhoneState()

   Returns the current phone state and incoming number

.. py:function:: stopTrackingPhoneState()

   Stops tracking phone state


Call & Dia APIs
----------------

.. py:function:: phoneCall(uri)

   Calls a contact/phone number by URI

.. py:function:: phoneCallNumber(number)

   Calls a phone number

.. py:function:: phoneDial(uri)

   Dials a contact/phone number by URI

.. py:function:: phoneDialNumber(number)

   Dials a phone number



Get information APIs
------------------------
.. py:function:: getCellLocation()

   Returns the current cell location

.. py:function:: getNetworkOperator()

   Returns the numeric name (MCC+MNC) of current registered operator

.. py:function:: getNetworkOperatorName()

   Returns the alphabetic name of current registered operator

.. py:function:: getNetworkType()

   Returns a the radio technology (network type) currently in use on the device

.. py:function:: getPhoneType()

   Returns the device phone type

.. py:function:: getSimCountryIso()

   Returns the ISO country code equivalent for the SIM provider's country code

.. py:function:: getSimOperator()

   Returns the MCC+MNC (mobile country code + mobile network code) of the provider of the SIM. 5 or 6 decimal digits

.. py:function:: getSimOperatorName()

   Returns the Service Provider Name (SPN)

.. py:function:: getSimSerialNumber()

   Returns the serial number of the SIM, if applicable. Return null if it is unavailable

.. py:function:: getSimState()

   Returns the state of the device SIM card

.. py:function:: getSubscriberId()

   Returns the unique subscriber ID, for example, the IMSI for a GSM phone. Return null if it is unavailable

.. py:function:: getVoiceMailAlphaTag()

   Retrieves the alphabetic identifier associated with the voice mail number

.. py:function:: getVoiceMailNumber()

   Returns the voice mail number. Return null if it is unavailable

.. py:function:: checkNetworkRoaming()

   Returns true if the device is considered roaming on the current network, for GSM purposes

.. py:function:: getDeviceId()

   Returns the unique device ID, for example, the IMEI for GSM and the MEID for CDMA phones. Return null if device ID is not available

.. py:function:: getDeviceSoftwareVersion()

   Returns the software version number for the device, for example, the IMEI/SV for GSM phones. Return null if the software version is not available

.. py:function:: getLine1Number()

   Returns the phone number string for line 1, for example, the MSISDN for a GSM phone. Return null if it is unavailable

.. py:function:: getNeighboringCellInfo()

   Returns the neighboring cell information of the device

MediaRecorderFacade
=========================


Audio
--------

.. py:function:: recorderStartMicrophone(targetPath)

   Records audio from the microphone and saves it to the given location

Video APIs
-----------

.. py:function:: recorderStartVideo(targetPath, duration, videoSize)

   Records video from the camera and saves it to the given location.
   Duration specifies the maximum duration of the recording session.
   If duration is 0 this method will return and the recording will only be stopped
   when recorderStop is called or when a scripts exits.
   Otherwise it will block for the time period equal to the duration argument.
   videoSize: 0=160x120, 1=320x240, 2=352x288, 3=640x480, 4=800x480.


.. py:function:: recorderCaptureVideo(targetPath, duration, recordAudio)

   Records video (and optionally audio) from the camera and saves it to the given location.
   Duration specifies the maximum duration of the recording session.
   If duration is not provided this method will return immediately and the recording will only be stopped
   when recorderStop is called or when a scripts exits.
   Otherwise it will block for the time period equal to the duration argument.

.. py:function:: startInteractiveVideoRecording(path)

   Starts the video capture application to record a video and saves it to the specified path


Stop
--------
.. py:function:: recorderStop()

   Stops a previously started recording


SensorManagerFacade
=========================

Start & Stop
-------------
.. py:function:: startSensingTimed(sensorNumber, delayTime)

   Starts recording sensor data to be available for polling

.. py:function:: startSensingThreshold(ensorNumber, threshold, axis)

   Records to the Event Queue sensor data exceeding a chosen threshold

.. py:function:: startSensing(sampleSize)

   Starts recording sensor data to be available for polling

.. py:function:: stopSensing()

   Stops collecting sensor data

Read data APIs
---------------
.. py:function:: readSensors()

   Returns the most recently recorded sensor data

.. py:function:: sensorsGetAccuracy()

   Returns the most recently received accuracy value

.. py:function:: sensorsGetLight()

   Returns the most recently received light value

.. py:function:: sensorsReadAccelerometer()

   Returns the most recently received accelerometer values

.. py:function:: sensorsReadMagnetometer()

   Returns the most recently received magnetic field values

.. py:function:: sensorsReadOrientation()

   Returns the most recently received orientation values





SettingsFacade
=========================

SmsFacade
=========================

SpeechRecognitionFacade
=========================

ToneGeneratorFacade
=========================

WakeLockFacade
=========================

WifiFacade
=========================


BatteryManagerFacade
=========================

ActivityResultFacade
=========================

MediaPlayerFacade
=========================

PreferencesFacade
=========================

QPyInterfaceFacade
=========================

Execute a qpython script
------------------------------

.. py:function:: AndroidHelper.executeQPy(script)

   Execute a qpython script by absolute path

   :param str script: The absolute path of the qpython script
   :return: boolean


TextToSpeechFacade
=========================

EyesFreeFacade
=========================

BluetoothFacade
=========================

SignalStrengthFacade
=========================

WebCamFacade
=========================

UiFacade
=========================


NFC APIs
------------

**QPython NFC json result**
::

    {
    "role": <role>, # could be self/master/slave
    "stat": <stat>, # could be ok / fail / cancl
    "message": <message get> 
    }


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
