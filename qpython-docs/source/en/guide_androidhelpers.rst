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
   :param array categories(Optional): a List of categories to add to the Intent
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

   :return: List of packages running activities

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

   :return: A map of Booleans autoFocus and takePicture where True indicates success

.. py:function:: cameraInteractiveCapturePicture(targetPath)

   Starts the image capture application to take a picture and saves it to the specified path

CommonIntentsFacade
=========================

Barcode
----------
.. py:function:: scanBarcode()

   Starts the barcode scanner

   :return: A Map representation of the result Intent

View APIs
----------
.. py:function:: pick(uri)

   Display content to be picked by URI (e.g. contacts)

   :return: A map of result values

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

   :return: A map of result values

.. py:function:: pickPhone()

   Displays a list of phone numbers to pick from

   :return: The selected phone number

.. py:function:: contactsGetAttributes()

   Returns a List of all possible attributes for contacts

   :return: a List of contacts as Maps

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

   :return: result of query as Maps

.. py:function:: queryAttributes(uri)

   Content Resolver Query Attributes

   :return: a list of available columns for a given content uri

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

   :return: A List of Maps of event properties

.. py:function:: eventWaitFor(eventName, timeout)

   Blocks until an event with the supplied name occurs. The returned event is not removed from the buffer

   :return: Map of event properties

.. py:function:: eventWait(timeout)

   Blocks until an event occurs. The returned event is removed from the buffer

   :return: Map of event properties

.. py:function:: eventPost(name, data, enqueue)

   Post an event to the event queue

.. py:function:: rpcPostEvent(name, data)

   Post an event to the event queue

.. py:function:: receiveEvent()

   Returns and removes the oldest event (i.e. location or sensor update, etc.) from the event buffer

   :return: Map of event properties

.. py:function:: waitForEvent(eventName, timeout)

   Blocks until an event with the supplied name occurs. The returned event is not removed from the buffer

   :return: Map of event properties

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

   :return: A map of location information by provider

.. py:function:: stopLocating()

   Stops collecting location data

.. py:function:: getLastKnownLocation()

   Returns the last known location of the device

   :return: A map of location information by provider

*sample code*
::

    Droid = androidhelper.Android()
    location = Droid.getLastKnownLocation().result
    location = location.get('network', location.get('gps'))


GEO
-----------
.. py:function:: geocode(latitude, longitude, maxResults)

   Returns a list of addresses for the given latitude and longitude

   :return: A list of addresses

PhoneFacade
=========================

PhoneStat APIs
----------------

.. py:function:: startTrackingPhoneState()

   Starts tracking phone state

.. py:function:: readPhoneState()

   Returns the current phone state and incoming number

   :return: A Map of "state" and "incomingNumber"

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

   :return: a List of Floats [(acceleration on the) X axis, Y axis, Z axis]

.. py:function:: sensorsReadMagnetometer()

   Returns the most recently received magnetic field values

   :return: a List of Floats [(magnetic field value for) X axis, Y axis, Z axis]

.. py:function:: sensorsReadOrientation()

   Returns the most recently received orientation values

   :return: a List of Doubles [azimuth, pitch, roll]

*sample code*
::

    Droid = androidhelper.Android()
    Droid.startSensingTimed(1, 250)
    sensor = Droid.sensorsReadOrientation().result
    Droid.stopSensing()


SettingsFacade
=========================

Screen
----------

.. py:function:: setScreenTimeout(value)

   Sets the screen timeout to this number of seconds

   :return: The original screen timeout

.. py:function:: getScreenTimeout()

   Gets the screen timeout

   :return: the current screen timeout in seconds

AirplanerMode
---------------------

.. py:function:: checkAirplaneMode()

   Checks the airplane mode setting

   :return: True if airplane mode is enabled

.. py:function:: toggleAirplaneMode(enabled)

   Toggles airplane mode on and off

   :return: True if airplane mode is enabled

Ringer Silent Mode
---------------------

.. py:function:: checkRingerSilentMode()

   Checks the ringer silent mode setting

   :return: True if ringer silent mode is enabled

.. py:function:: toggleRingerSilentMode(enabled)

   Toggles ringer silent mode on and off

   :return: True if ringer silent mode is enabled

Vibrate Mode
---------------------

.. py:function:: toggleVibrateMode(enabled)

   Toggles vibrate mode on and off. If ringer=true then set Ringer setting, else set Notification setting

   :return: True if vibrate mode is enabled

.. py:function:: getVibrateMode(ringer)

   Checks Vibration setting. If ringer=true then query Ringer setting, else query Notification setting

   :return: True if vibrate mode is enabled

Ringer & Media Volume
---------------------

.. py:function:: getMaxRingerVolume()

   Returns the maximum ringer volume

.. py:function:: getRingerVolume()

   Returns the current ringer volume

.. py:function:: setRingerVolume(volume)

   Sets the ringer volume

.. py:function:: getMaxMediaVolume()

   Returns the maximum media volume

.. py:function:: getMediaVolume()

   Returns the current media volume

.. py:function:: setMediaVolume(volume)

   Sets the media volume

Screen Brightness
---------------------

.. py:function:: getScreenBrightness()

   Returns the screen backlight brightness

   :return: the current screen brightness between 0 and 255

.. py:function:: setScreenBrightness(value)

   Sets the the screen backlight brightness

   :return: the original screen brightness

.. py:function:: checkScreenOn()

   Checks if the screen is on or off (requires API level 7)

   :return: True if the screen is currently on


SmsFacade
=========================

.. py:function:: smsSend(destinationAddress, text)

   Sends an SMS

   :param str destinationAddress: typically a phone number
   :param str text:

.. py:function:: smsGetMessageCount(unreadOnly, folder)

   Returns the number of messages

   :param boolean unreadOnly: typically a phone number
   :param str folder(optional): default "inbox"

.. py:function:: smsGetMessageIds(unreadOnly, folder)

   Returns a List of all message IDs

   :param boolean unreadOnly: typically a phone number
   :param str folder(optional): default "inbox"

.. py:function:: smsGetMessages(unreadOnly, folder, attributes)

   Returns a List of all messages

   :param boolean unreadOnly: typically a phone number
   :param str folder: default "inbox"
   :param array attributes(optional): attributes

   :return: a List of messages as Maps

.. py:function:: smsGetMessageById(id, attributes)

   Returns message attributes

   :param int id: message ID
   :param array attributes(optional): attributes

   :return: a List of messages as Maps

.. py:function:: smsGetAttributes()

   Returns a List of all possible message attributes

.. py:function:: smsDeleteMessage(id)

   Deletes a message

   :param int id: message ID

   :return: True if the message was deleted

.. py:function:: smsMarkMessageRead(ids, read)

   Marks messages as read

   :param array ids: List of message IDs to mark as read
   :param boolean read:  true or false

   :return: number of messages marked read

SpeechRecognitionFacade
=========================

.. py:function:: recognizeSpeech(prompt, language, languageModel)

   Recognizes user's speech and returns the most likely result

   :param str prompt(optional): text prompt to show to the user when asking them to speak
   :param str language(optional): language override to inform the recognizer that it should expect speech in a language different than the one set in the java.util.Locale.getDefault()
   :param str languageModel(optional): informs the recognizer which speech model to prefer (see android.speech.RecognizeIntent)

   :return: An empty string in case the speech cannot be recongnized


ToneGeneratorFacade
=========================

.. py:function:: generateDtmfTones(phoneNumber, toneDuration)

   Generate DTMF tones for the given phone number

   :param str phoneNumber: phone number
   :param int toneDuration(optional): default 100, duration of each tone in milliseconds


WakeLockFacade
=========================

.. py:function:: wakeLockAcquireFull()

   Acquires a full wake lock (CPU on, screen bright, keyboard bright)

.. py:function:: wakeLockAcquirePartial()

   Acquires a partial wake lock (CPU on)

.. py:function:: wakeLockAcquireBright()

   Acquires a bright wake lock (CPU on, screen bright)

.. py:function:: wakeLockAcquireDim()

   Acquires a dim wake lock (CPU on, screen dim)

.. py:function:: wakeLockRelease()

   Releases the wake lock

WifiFacade
=========================

.. py:function:: wifiGetScanResults()

   Returns the list of access points found during the most recent Wifi scan

.. py:function:: wifiLockAcquireFull()

   Acquires a full Wifi lock

.. py:function:: wifiLockAcquireScanOnly()

   Acquires a scan only Wifi lock

.. py:function:: wifiLockRelease()

   Releases a previously acquired Wifi lock

.. py:function:: wifiStartScan()

   Starts a scan for Wifi access points

   :return: True if the scan was initiated successfully

.. py:function:: checkWifiState()

   Checks Wifi state

   :return: True if Wifi is enabled

.. py:function:: toggleWifiState(enabled)

   Toggle Wifi on and off

   :param boolean enabled(optional): enabled

   :return: True if Wifi is enabled

.. py:function:: wifiDisconnect()

   Disconnects from the currently active access point

   :return: True if the operation succeeded

.. py:function:: wifiGetConnectionInfo()

   Returns information about the currently active access point

.. py:function:: wifiReassociate()

   Returns information about the currently active access point

   :return: True if the operation succeeded

.. py:function:: wifiReconnect()

   Reconnects to the currently active access point

   :return: True if the operation succeeded


BatteryManagerFacade
=========================

.. py:function:: readBatteryData()

   Returns the most recently recorded battery data

.. py:function:: batteryStartMonitoring()

   Starts tracking battery state

.. py:function:: batteryStopMonitoring()

   Stops tracking battery state

.. py:function:: batteryGetStatus()

   Returns  the most recently received battery status data:
   1 - unknown;
   2 - charging;
   3 - discharging;
   4 - not charging;
   5 - full

.. py:function:: batteryGetHealth()

   Returns the most recently received battery health data:
   1 - unknown;
   2 - good;
   3 - overheat;
   4 - dead;
   5 - over voltage;
   6 - unspecified failure

.. py:function:: batteryGetPlugType()

   Returns the most recently received plug type data:
   -1 - unknown
   0 - unplugged
   1 - power source is an AC charger
   2 - power source is a USB port


.. py:function:: batteryCheckPresent()

   Returns the most recently received battery presence data

.. py:function:: batteryGetLevel()

   Returns the most recently received battery level (percentage)

.. py:function:: batteryGetVoltage()

   Returns the most recently received battery voltage

.. py:function:: batteryGetTemperature()

   Returns the most recently received battery temperature

.. py:function:: batteryGetTechnology()

   Returns the most recently received battery technology data


ActivityResultFacade
=========================

.. py:function:: setResultBoolean(resultCode, resultValue)

   Sets the result of a script execution. Whenever the script APK is called via startActivityForResult(),
   the resulting intent will contain SCRIPT_RESULT extra with the given value

   :param int resultCode:
   :param byte resultValue:


.. py:function:: setResultByte(resultCode, resultValue)

   Sets the result of a script execution. Whenever the script APK is called via startActivityForResult(),
   the resulting intent will contain SCRIPT_RESULT extra with the given value

   :param int resultCode:
   :param byte resultValue:

.. py:function:: setResultShort(resultCode, resultValue)

   Sets the result of a script execution. Whenever the script APK is called via startActivityForResult(),
   the resulting intent will contain SCRIPT_RESULT extra with the given value

   :param int resultCode:
   :param byte resultValue:

.. py:function:: setResultChar(resultCode, resultValue)

   Sets the result of a script execution. Whenever the script APK is called via startActivityForResult(),
   the resulting intent will contain SCRIPT_RESULT extra with the given value

   :param int resultCode:
   :param byte resultValue:


.. py:function:: setResultInteger(resultCode, resultValue)

   Sets the result of a script execution. Whenever the script APK is called via startActivityForResult(),
   the resulting intent will contain SCRIPT_RESULT extra with the given value

   :param int resultCode:
   :param byte resultValue:

.. py:function:: setResultLong(resultCode, resultValue)

   Sets the result of a script execution. Whenever the script APK is called via startActivityForResult(),
   the resulting intent will contain SCRIPT_RESULT extra with the given value

   :param int resultCode:
   :param byte resultValue:

.. py:function:: setResultFloat(resultCode, resultValue)

   Sets the result of a script execution. Whenever the script APK is called via startActivityForResult(),
   the resulting intent will contain SCRIPT_RESULT extra with the given value

   :param int resultCode:
   :param byte resultValue:

.. py:function:: setResultDouble(resultCode, resultValue)

   Sets the result of a script execution. Whenever the script APK is called via startActivityForResult(),
   the resulting intent will contain SCRIPT_RESULT extra with the given value

   :param int resultCode:
   :param byte resultValue:

.. py:function:: setResultString(resultCode, resultValue)

   Sets the result of a script execution. Whenever the script APK is called via startActivityForResult(),
   the resulting intent will contain SCRIPT_RESULT extra with the given value

   :param int resultCode:
   :param byte resultValue:

.. py:function:: setResultBooleanArray(resultCode, resultValue)

   Sets the result of a script execution. Whenever the script APK is called via startActivityForResult(),
   the resulting intent will contain SCRIPT_RESULT extra with the given value

   :param int resultCode:
   :param byte resultValue:

.. py:function:: setResultByteArray(resultCode, resultValue)

   Sets the result of a script execution. Whenever the script APK is called via startActivityForResult(),
   the resulting intent will contain SCRIPT_RESULT extra with the given value

   :param int resultCode:
   :param byte resultValue:

.. py:function:: setResultShortArray(resultCode, resultValue)

   Sets the result of a script execution. Whenever the script APK is called via startActivityForResult(),
   the resulting intent will contain SCRIPT_RESULT extra with the given value

   :param int resultCode:
   :param byte resultValue:

.. py:function:: setResultCharArray(resultCode, resultValue)

   Sets the result of a script execution. Whenever the script APK is called via startActivityForResult(),
   the resulting intent will contain SCRIPT_RESULT extra with the given value

   :param int resultCode:
   :param byte resultValue:

.. py:function:: setResultIntegerArray(resultCode, resultValue)

   Sets the result of a script execution. Whenever the script APK is called via startActivityForResult(),
   the resulting intent will contain SCRIPT_RESULT extra with the given value

   :param int resultCode:
   :param byte resultValue:

.. py:function:: setResultLongArray(resultCode, resultValue)

   Sets the result of a script execution. Whenever the script APK is called via startActivityForResult(),
   the resulting intent will contain SCRIPT_RESULT extra with the given value

   :param int resultCode:
   :param byte resultValue:

.. py:function:: setResultFloatArray(resultCode, resultValue)

   Sets the result of a script execution. Whenever the script APK is called via startActivityForResult(),
   the resulting intent will contain SCRIPT_RESULT extra with the given value

   :param int resultCode:
   :param byte resultValue:

.. py:function:: setResultDoubleArray(resultCode, resultValue)

   Sets the result of a script execution. Whenever the script APK is called via startActivityForResult(),
   the resulting intent will contain SCRIPT_RESULT extra with the given value

   :param int resultCode:
   :param byte resultValue:

.. py:function:: setResultStringArray(resultCode, resultValue)

   Sets the result of a script execution. Whenever the script APK is called via startActivityForResult(),
   the resulting intent will contain SCRIPT_RESULT extra with the given value

   :param int resultCode:
   :param byte resultValue:

.. py:function:: setResultSerializable(resultCode, resultValue)

   Sets the result of a script execution. Whenever the script APK is called via startActivityForResult(),
   the resulting intent will contain SCRIPT_RESULT extra with the given value

   :param int resultCode:
   :param byte resultValue:


MediaPlayerFacade
=========================

Control
-----------------
.. py:function:: mediaPlay(url, tag, play)

   Open a media file

   :param str url: url of media resource
   :param str tag(optional): string identifying resource (default=default)
   :param boolean play(optional): start playing immediately

   :return: true if play successful

.. py:function:: mediaPlayPause(tag)

   pause playing media file

   :param str tag: string identifying resource (default=default)

   :return: true if successful

.. py:function:: mediaPlayStart(tag)

   start playing media file

   :param str tag: string identifying resource (default=default)

   :return: true if successful

.. py:function:: mediaPlayClose(tag)

   Close media file

   :param str tag: string identifying resource (default=default)

   :return: true if successful

.. py:function:: mediaIsPlaying(tag)

   Checks if media file is playing

   :param str tag: string identifying resource (default=default)

   :return: true if successful


.. py:function:: mediaPlaySetLooping(enabled, tag)

   Set Looping

   :param boolean enabled: default true
   :param str tag: string identifying resource (default=default)

   :return: True if successful

.. py:function:: mediaPlaySeek(msec, tag)

   Seek To Position

   :param int msec: default true
   :param str tag: string identifying resource (default=default)

   :return: New Position (in ms)

Get Information
-----------------
.. py:function:: mediaPlayInfo(tag)

   Information on current media

   :param str tag: string identifying resource (default=default)

   :return: Media Information

.. py:function:: mediaPlayList()

   Lists currently loaded media

   :return: List of Media Tags


PreferencesFacade
=========================

.. py:function:: prefGetValue(key, filename)

   Read a value from shared preferences

   :param str key: key
   :param str filename(optional): Desired preferences file. If not defined, uses the default Shared Preferences.


.. py:function:: prefPutValue(key, value, filename)

   Write a value to shared preferences

   :param str key: key
   :param str value: value
   :param str filename(optional): Desired preferences file. If not defined, uses the default Shared Preferences.

.. py:function:: prefGetAll(filename)

   Get list of Shared Preference Values

   :param str filename(optional): Desired preferences file. If not defined, uses the default Shared Preferences.


QPyInterfaceFacade
=========================

.. py:function:: executeQPy(script)

   Execute a qpython script by absolute path

   :param str script: The absolute path of the qpython script

   :return: boolean


TextToSpeechFacade
=========================

.. py:function:: ttsSpeak(message)

   Speaks the provided message via TTS

   :param str message: message

.. py:function:: ttsIsSpeaking()

   Returns True if speech is currently in progress

EyesFreeFacade
=========================

.. py:function:: ttsSpeak(message)

   Speaks the provided message via TTS

   :param str message: message


BluetoothFacade
=========================

.. py:function:: bluetoothActiveConnections()

   Returns active Bluetooth connections


.. py:function:: bluetoothWriteBinary(base64, connID)

   Send bytes over the currently open Bluetooth connection

   :param str base64: A base64 encoded String of the bytes to be sent
   :param str connID(optional): Connection id

.. py:function:: bluetoothReadBinary(bufferSize, connID)

   Read up to bufferSize bytes and return a chunked, base64 encoded string

   :param int bufferSize: default 4096
   :param str connID(optional): Connection id

.. py:function:: bluetoothConnect(uuid, address)

   Connect to a device over Bluetooth. Blocks until the connection is established or fails

   :param str uuid: The UUID passed here must match the UUID used by the server device
   :param str address(optional): The user will be presented with a list of discovered devices to choose from if an address is not provided

   :return: True if the connection was established successfully

.. py:function:: bluetoothAccept(uuid, timeout)

   Listens for and accepts a Bluetooth connection. Blocks until the connection is established or fails

   :param str uuid: The UUID passed here must match the UUID used by the server device
   :param int timeout: How long to wait for a new connection, 0 is wait for ever (default=0)

.. py:function:: bluetoothMakeDiscoverable(duration)

   Requests that the device be discoverable for Bluetooth connections

   :param int duration: period of time, in seconds, during which the device should be discoverable (default=300)

.. py:function:: bluetoothWrite(ascii, connID)

   Sends ASCII characters over the currently open Bluetooth connection

   :param str ascii: text
   :param str connID: Connection id

.. py:function:: bluetoothReadReady(connID)

   Sends ASCII characters over the currently open Bluetooth connection

   :param str ascii: text
   :param str connID: Connection id

.. py:function:: bluetoothRead(bufferSize, connID)

   Read up to bufferSize ASCII characters

   :param int bufferSize: default=4096
   :param str connID(optional): Connection id

.. py:function:: bluetoothReadLine(connID)

   Read the next line

   :param str connID(optional): Connection id

.. py:function:: bluetoothGetRemoteDeviceName(address)

   Queries a remote device for it's name or null if it can't be resolved

   :param str address: Bluetooth Address For Target Device

.. py:function:: bluetoothGetLocalName()

   Gets the Bluetooth Visible device name

.. py:function:: bluetoothSetLocalName(name)

   Sets the Bluetooth Visible device name, returns True on success

   :param str name: New local name

.. py:function:: bluetoothGetScanMode()

   Gets the scan mode for the local dongle.
   Return values:
   -1 when Bluetooth is disabled.
   0 if non discoverable and non connectable.
   1 connectable non discoverable.
   3 connectable and discoverable.

.. py:function:: bluetoothGetConnectedDeviceName(connID)

   Returns the name of the connected device

   :param str connID: Connection id

.. py:function:: checkBluetoothState()

   Checks Bluetooth state

   :return: True if Bluetooth is enabled

.. py:function:: toggleBluetoothState(enabled, prompt)

   Toggle Bluetooth on and off

   :param boolean enabled:
   :param str prompt: Prompt the user to confirm changing the Bluetooth state, default=true

   :return: True if Bluetooth is enabled

.. py:function:: bluetoothStop(connID)

   Stops Bluetooth connection

   :param str connID: Connection id

.. py:function:: bluetoothGetLocalAddress()

   Returns the hardware address of the local Bluetooth adapter

.. py:function:: bluetoothDiscoveryStart()

   Start the remote device discovery process

   :return: true on success, false on error

.. py:function:: bluetoothDiscoveryCancel()

   Cancel the current device discovery process

   :return: true on success, false on error

.. py:function:: bluetoothIsDiscovering()

   Return true if the local Bluetooth adapter is currently in the device discovery process


SignalStrengthFacade
=========================
.. py:function:: startTrackingSignalStrengths()

   Starts tracking signal strengths

.. py:function:: readSignalStrengths()

   Returns the current signal strengths

   :return: A map of gsm_signal_strength

.. py:function:: stopTrackingSignalStrengths()

   Stops tracking signal strength


WebCamFacade
=========================

.. py:function:: webcamStart(resolutionLevel, jpegQuality, port)

   Starts an MJPEG stream and returns a Tuple of address and port for the stream

   :param int resolutionLevel: increasing this number provides higher resolution (default=0)
   :param int jpegQuality: a number from 0-10 (default=20)
   :param int port: If port is specified, the webcam service will bind to port, otherwise it will pick any available port (default=0)

.. py:function:: webcamAdjustQuality(resolutionLevel, jpegQuality)

   Adjusts the quality of the webcam stream while it is running

   :param int resolutionLevel: increasing this number provides higher resolution (default=0)
   :param int jpegQuality: a number from 0-10 (default=20)

.. py:function:: cameraStartPreview(resolutionLevel, jpegQuality, filepath)

   Start Preview Mode. Throws 'preview' events

   :param int resolutionLevel: increasing this number provides higher resolution (default=0)
   :param int jpegQuality: a number from 0-10 (default=20)
   :param str filepath: Path to store jpeg files

   :return: True if successful

.. py:function:: cameraStopPreview()

   Stop the preview mode


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



Other SL4A APIs
----------------

.. py:function:: AndroidHelper.dialogCreateSpinnerProgress(title,message,maximum progress)

    Create a spinner progress dialog

.. py:function:: AndroidHelper.webViewShow(url,wait)

    Display a WebView with the given URL.

    :param str url: URL
    :param boolean wait(Optional): block until the user exits the WebView
