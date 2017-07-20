#-------------------------------------------------------------------------------
# Name:         androidhelper.py
#
# Purpose:      To simplify Python-for-Android SL4A development in IDEs with a
#               "hepler" class derived from the default Android class containing
#               SL4A facade functions & API documentation
#
# Usage:        copy androidhelper.py into either the folder containing your
#               SL4A python script or to some location on the python import path
#               that your IDE can see and in your script, instead of:
#
#                   import android
#
#               use the following import code:
#
#                   try:
#                       import androidhelper as android
#                   except:
#                       import android
#
# Sources:      Derived from API documentation in HTML files contained in
#               /android-scripting/android/ScriptingLayerForAndroid/assets/sl4adoc.zip
#
# Version:      for SL4A Release R5, created on 7-Apr-2012
#
# Author(s):    Hariharan Srinath (srinathdevelopment@gmail.com) with inputs
#               from Robbie Matthews (rjmatthews62@gmail.com)
#
# Copyright:    Copyright (C) 2012, Hariharan Srinath, Robbie Matthews
#
# Licensed under the Apache License, Version 2.0 (the "License"); you may not
# use this file except in compliance with the License. You may obtain a copy of
# the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
# WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
# License for the specific language governing permissions and limitations under
# the License.

import sl4a

class Android(sl4a.Android):
	def setResultBoolean(self,resultCode,resultValue):
		'''
		setResultBoolean(resultCode,resultValue)
		Sets the result of a script execution. Whenever the script APK is called via startActivityForResult(), the resulting intent will contain SCRIPT_RESULT extra with the given value.
			resultCode (Integer) The result code to propagate back to the originating activity, often RESULT_CANCELED (0) or RESULT_OK (-1)
			resultValue (Boolean)
		'''
		return self._rpc("setResultBoolean",resultCode,resultValue)

	def setResultBooleanArray(self,resultCode,resultValue):
		'''
		setResultBooleanArray(resultCode,resultValue)
		Sets the result of a script execution. Whenever the script APK is called via startActivityForResult(), the resulting intent will contain SCRIPT_RESULT extra with the given value.
			resultCode (Integer) The result code to propagate back to the originating activity, often RESULT_CANCELED (0) or RESULT_OK (-1)
			resultValue (Boolean)
		'''
		return self._rpc("setResultBooleanArray",resultCode,resultValue)

	def setResultByte(self,resultCode,resultValue):
		'''
		setResultByte(resultCode,resultValue)
		Sets the result of a script execution. Whenever the script APK is called via startActivityForResult(), the resulting intent will contain SCRIPT_RESULT extra with the given value.
			resultCode (Integer) The result code to propagate back to the originating activity, often RESULT_CANCELED (0) or RESULT_OK (-1)
			resultValue (Byte)
		'''
		return self._rpc("setResultByte",resultCode,resultValue)

	def setResultByteArray(self,resultCode,resultValue):
		'''
		setResultByteArray(resultCode,resultValue)
		Sets the result of a script execution. Whenever the script APK is called via startActivityForResult(), the resulting intent will contain SCRIPT_RESULT extra with the given value.
			resultCode (Integer) The result code to propagate back to the originating activity, often RESULT_CANCELED (0) or RESULT_OK (-1)
			resultValue (Byte)
		'''
		return self._rpc("setResultByteArray",resultCode,resultValue)

	def setResultChar(self,resultCode,resultValue):
		'''
		setResultChar(resultCode,resultValue)
		Sets the result of a script execution. Whenever the script APK is called via startActivityForResult(), the resulting intent will contain SCRIPT_RESULT extra with the given value.
			resultCode (Integer) The result code to propagate back to the originating activity, often RESULT_CANCELED (0) or RESULT_OK (-1)
			resultValue (Character)
		'''
		return self._rpc("setResultChar",resultCode,resultValue)

	def setResultCharArray(self,resultCode,resultValue):
		'''
		setResultCharArray(resultCode,resultValue)
		Sets the result of a script execution. Whenever the script APK is called via startActivityForResult(), the resulting intent will contain SCRIPT_RESULT extra with the given value.
			resultCode (Integer) The result code to propagate back to the originating activity, often RESULT_CANCELED (0) or RESULT_OK (-1)
			resultValue (Character)
		'''
		return self._rpc("setResultCharArray",resultCode,resultValue)

	def setResultDouble(self,resultCode,resultValue):
		'''
		setResultDouble(resultCode,resultValue)
		Sets the result of a script execution. Whenever the script APK is called via startActivityForResult(), the resulting intent will contain SCRIPT_RESULT extra with the given value.
			resultCode (Integer) The result code to propagate back to the originating activity, often RESULT_CANCELED (0) or RESULT_OK (-1)
			resultValue (Double)
		'''
		return self._rpc("setResultDouble",resultCode,resultValue)

	def setResultDoubleArray(self,resultCode,resultValue):
		'''
		setResultDoubleArray(resultCode,resultValue)
		Sets the result of a script execution. Whenever the script APK is called via startActivityForResult(), the resulting intent will contain SCRIPT_RESULT extra with the given value.
			resultCode (Integer) The result code to propagate back to the originating activity, often RESULT_CANCELED (0) or RESULT_OK (-1)
			resultValue (Double)
		'''
		return self._rpc("setResultDoubleArray",resultCode,resultValue)

	def setResultFloat(self,resultCode,resultValue):
		'''
		setResultFloat(resultCode,resultValue)
		Sets the result of a script execution. Whenever the script APK is called via startActivityForResult(), the resulting intent will contain SCRIPT_RESULT extra with the given value.
			resultCode (Integer) The result code to propagate back to the originating activity, often RESULT_CANCELED (0) or RESULT_OK (-1)
			resultValue (Float)
		'''
		return self._rpc("setResultFloat",resultCode,resultValue)

	def setResultFloatArray(self,resultCode,resultValue):
		'''
		setResultFloatArray(resultCode,resultValue)
		Sets the result of a script execution. Whenever the script APK is called via startActivityForResult(), the resulting intent will contain SCRIPT_RESULT extra with the given value.
			resultCode (Integer) The result code to propagate back to the originating activity, often RESULT_CANCELED (0) or RESULT_OK (-1)
			resultValue (Float)
		'''
		return self._rpc("setResultFloatArray",resultCode,resultValue)

	def setResultInteger(self,resultCode,resultValue):
		'''
		setResultInteger(resultCode,resultValue)
		Sets the result of a script execution. Whenever the script APK is called via startActivityForResult(), the resulting intent will contain SCRIPT_RESULT extra with the given value.
			resultCode (Integer) The result code to propagate back to the originating activity, often RESULT_CANCELED (0) or RESULT_OK (-1)
			resultValue (Integer)
		'''
		return self._rpc("setResultInteger",resultCode,resultValue)

	def setResultIntegerArray(self,resultCode,resultValue):
		'''
		setResultIntegerArray(resultCode,resultValue)
		Sets the result of a script execution. Whenever the script APK is called via startActivityForResult(), the resulting intent will contain SCRIPT_RESULT extra with the given value.
			resultCode (Integer) The result code to propagate back to the originating activity, often RESULT_CANCELED (0) or RESULT_OK (-1)
			resultValue (Integer)
		'''
		return self._rpc("setResultIntegerArray",resultCode,resultValue)

	def setResultLong(self,resultCode,resultValue):
		'''
		setResultLong(resultCode,resultValue)
		Sets the result of a script execution. Whenever the script APK is called via startActivityForResult(), the resulting intent will contain SCRIPT_RESULT extra with the given value.
			resultCode (Integer) The result code to propagate back to the originating activity, often RESULT_CANCELED (0) or RESULT_OK (-1)
			resultValue (Long)
		'''
		return self._rpc("setResultLong",resultCode,resultValue)

	def setResultLongArray(self,resultCode,resultValue):
		'''
		setResultLongArray(resultCode,resultValue)
		Sets the result of a script execution. Whenever the script APK is called via startActivityForResult(), the resulting intent will contain SCRIPT_RESULT extra with the given value.
			resultCode (Integer) The result code to propagate back to the originating activity, often RESULT_CANCELED (0) or RESULT_OK (-1)
			resultValue (Long)
		'''
		return self._rpc("setResultLongArray",resultCode,resultValue)

	def setResultSerializable(self,resultCode,resultValue):
		'''
		setResultSerializable(resultCode,resultValue)
		Sets the result of a script execution. Whenever the script APK is called via startActivityForResult(), the resulting intent will contain SCRIPT_RESULT extra with the given value.
			resultCode (Integer) The result code to propagate back to the originating activity, often RESULT_CANCELED (0) or RESULT_OK (-1)
			resultValue (Serializable)
		'''
		return self._rpc("setResultSerializable",resultCode,resultValue)

	def setResultShort(self,resultCode,resultValue):
		'''
		setResultShort(resultCode,resultValue)
		Sets the result of a script execution. Whenever the script APK is called via startActivityForResult(), the resulting intent will contain SCRIPT_RESULT extra with the given value.
			resultCode (Integer) The result code to propagate back to the originating activity, often RESULT_CANCELED (0) or RESULT_OK (-1)
			resultValue (Short)
		'''
		return self._rpc("setResultShort",resultCode,resultValue)

	def setResultShortArray(self,resultCode,resultValue):
		'''
		setResultShortArray(resultCode,resultValue)
		Sets the result of a script execution. Whenever the script APK is called via startActivityForResult(), the resulting intent will contain SCRIPT_RESULT extra with the given value.
			resultCode (Integer) The result code to propagate back to the originating activity, often RESULT_CANCELED (0) or RESULT_OK (-1)
			resultValue (Short)
		'''
		return self._rpc("setResultShortArray",resultCode,resultValue)

	def setResultString(self,resultCode,resultValue):
		'''
		setResultString(resultCode,resultValue)
		Sets the result of a script execution. Whenever the script APK is called via startActivityForResult(), the resulting intent will contain SCRIPT_RESULT extra with the given value.
			resultCode (Integer) The result code to propagate back to the originating activity, often RESULT_CANCELED (0) or RESULT_OK (-1)
			resultValue (String)
		'''
		return self._rpc("setResultString",resultCode,resultValue)

	def setResultStringArray(self,resultCode,resultValue):
		'''
		setResultStringArray(resultCode,resultValue)
		Sets the result of a script execution. Whenever the script APK is called via startActivityForResult(), the resulting intent will contain SCRIPT_RESULT extra with the given value.
			resultCode (Integer) The result code to propagate back to the originating activity, often RESULT_CANCELED (0) or RESULT_OK (-1)
			resultValue (String)
		'''
		return self._rpc("setResultStringArray",resultCode,resultValue)

	def environment(self):
		'''
		environment(self)
		A map of various useful environment details
		Map returned:
		TZ = Timezone
		id = Timezone ID
		display = Timezone display name
		offset = Offset from UTC (in ms)
		SDK = SDK Version
		download = default download path
		appcache = Location of application cache
		sdcard = Space on sdcard
		availblocks = Available blocks
		blockcount = Total Blocks
		blocksize = size of block.
		'''
		return self._rpc("environment")

	def getClipboard(self):
		'''
		getClipboard(self)
		Read text from the clipboard.
		returns: (String) The text in the clipboard.
		'''
		return self._rpc("getClipboard")

	def getConstants(self,classname):
		'''
		getConstants(classname)
		Get list of constants (static final fields) for a class
			classname (String) Class to get constants from
		'''
		return self._rpc("getConstants",classname)

	def getInput(self,title="SL4A Input",message="Please enter value:"):
		'''
		getInput(title="SL4A Input",message="Please enter value:")
		Queries the user for a text input.
			title (String) title of the input box (default=SL4A Input)
			message (String) message to display above the input box (default=Please enter value:)
		Deprecated in r3. Use dialogGetInput instead.
		'''
		return self._rpc("getInput",title,message)

	def getIntent(self):
		'''
		getIntent(self)
		Returns the intent that launched the script.
		'''
		return self._rpc("getIntent")

	def getPackageVersion(self,packageName):
		'''
		getPackageVersion(packageName)
		Returns package version name.
			packageName (String)
		'''
		return self._rpc("getPackageVersion",packageName)

	def getPackageVersionCode(self,packageName):
		'''
		getPackageVersionCode(packageName)
		Returns package version code.
			packageName (String)
		'''
		return self._rpc("getPackageVersionCode",packageName)

	def getPassword(self,title="SL4A Password Input",message="Please enter password:"):
		'''
		getPassword(title="SL4A Password Input",message="Please enter password:")
		Queries the user for a password.
			title (String) title of the input box (default=SL4A Password Input)
			message (String) message to display above the input box (default=Please enter password:)
		Deprecated in r3. Use dialogGetPassword instead.
		'''
		return self._rpc("getPassword",title,message)

	def log(self,message):
		'''
		log(message)
		Writes message to logcat.
			message (String)
		'''
		return self._rpc("log",message)

	def makeIntent(self,action,uri=None,type=None,extras=None,categories=None,packagename=None,classname=None,flags=None):
		'''
		makeIntent(action,uri=None,type=None,extras=None,categories=None,packagename=None,classname=None,flags=None)
		Create an Intent.
			action (String)
			uri (String)  (optional)
			type (String) MIME type/subtype of the URI (optional)
			extras (JSONObject) a Map of extras to add to the Intent (optional)
			categories (JSONArray) a List of categories to add to the Intent (optional)
			packagename (String) name of package. If used, requires classname to be useful (optional)
			classname (String) name of class. If used, requires packagename to be useful (optional)
			flags (Integer) Intent flags (optional)
		returns: (Intent) An object representing an Intent
		'''
		return self._rpc("makeIntent",action,uri,type,extras,categories,packagename,classname,flags)

	def makeToast(self,message):
		'''
		makeToast(message)
		Displays a short-duration Toast notification.
			message (String)
		'''
		return self._rpc("makeToast",message)

	def notify(self,title,message):
		'''
		notify(title,message)
		Displays a notification that will be canceled when the user clicks on it.
			title (String) title
			message (String)
		'''
		return self._rpc("notify",title,message)

	def requiredVersion(self,requiredVersion):
		'''
		requiredVersion(requiredVersion)
		Checks if version of SL4A is greater than or equal to the specified version.
			requiredVersion (Integer)
		'''
		return self._rpc("requiredVersion",requiredVersion)

	def sendBroadcast(self,action,uri=None,type=None,extras=None,packagename=None,classname=None):
		'''
		sendBroadcast(action,uri=None,type=None,extras=None,packagename=None,classname=None)
		Send a broadcast.
			action (String)
			uri (String)  (optional)
			type (String) MIME type/subtype of the URI (optional)
			extras (JSONObject) a Map of extras to add to the Intent (optional)
			packagename (String) name of package. If used, requires classname to be useful (optional)
			classname (String) name of class. If used, requires packagename to be useful (optional)
		'''
		return self._rpc("sendBroadcast",action,uri,type,extras,packagename,classname)

	def sendBroadcastIntent(self,intent):
		'''
		sendBroadcastIntent(intent)
		Send Broadcast Intent
			intent (Intent) Intent in the format as returned from makeIntent
		'''
		return self._rpc("sendBroadcastIntent",intent)

	def sendEmail(self,to,subject,body,attachmentUri=None):
		'''
		sendEmail(to,subject,body,attachmentUri=None)
		Launches an activity that sends an e-mail message to a given recipient.
			to (String) A comma separated list of recipients.
			subject (String)
			body (String)
			attachmentUri (String)  (optional)
		'''
		return self._rpc("sendEmail",to,subject,body,attachmentUri)

	def setClipboard(self,text):
		'''
		setClipboard(text)
		Put text in the clipboard.
			text (String)
		Creates a new AndroidFacade that simplifies the interface to various Android APIs.
		'''
		return self._rpc("setClipboard",text)

	def startActivity(self,action,uri=None,type=None,extras=None,wait=None,packagename=None,classname=None):
		'''
		startActivity(action,uri=None,type=None,extras=None,wait=None,packagename=None,classname=None)
		Starts an activity.
			action (String)
			uri (String)  (optional)
			type (String) MIME type/subtype of the URI (optional)
			extras (JSONObject) a Map of extras to add to the Intent (optional)
			wait (Boolean) block until the user exits the started activity (optional)
			packagename (String) name of package. If used, requires classname to be useful (optional)
			classname (String) name of class. If used, requires packagename to be useful (optional)
		packagename and classname, if provided, are used in a 'setComponent' call.
		'''
		return self._rpc("startActivity",action,uri,type,extras,wait,packagename,classname)

	def startActivityForResult(self,action,uri=None,type=None,extras=None,packagename=None,classname=None):
		'''
		startActivityForResult(action,uri=None,type=None,extras=None,packagename=None,classname=None)
		Starts an activity and returns the result.
			action (String)
			uri (String)  (optional)
			type (String) MIME type/subtype of the URI (optional)
			extras (JSONObject) a Map of extras to add to the Intent (optional)
			packagename (String) name of package. If used, requires classname to be useful (optional)
			classname (String) name of class. If used, requires packagename to be useful (optional)
		returns: (Intent) A Map representation of the result Intent.
		'''
		return self._rpc("startActivityForResult",action,uri,type,extras,packagename,classname)

	def startActivityForResultIntent(self,intent):
		'''
		startActivityForResultIntent(intent)
		Starts an activity and returns the result.
			intent (Intent) Intent in the format as returned from makeIntent
		returns: (Intent) A Map representation of the result Intent.
		'''
		return self._rpc("startActivityForResultIntent",intent)

	def startActivityIntent(self,intent,wait=None):
		'''
		startActivityIntent(intent,wait=None)
		Start Activity using Intent
			intent (Intent) Intent in the format as returned from makeIntent
			wait (Boolean) block until the user exits the started activity (optional)
		'''
		return self._rpc("startActivityIntent",intent,wait)

	def vibrate(self,duration=300):
		'''
		vibrate(duration=300)
		Vibrates the phone or a specified duration in milliseconds.
			duration (Integer) duration in milliseconds (default=300)
		'''
		return self._rpc("vibrate",duration)

	def forceStopPackage(self,packageName):
		'''
		forceStopPackage(packageName)
		Force stops a package.
			packageName (String) name of package
		'''
		return self._rpc("forceStopPackage",packageName)

	def getLaunchableApplications(self):
		'''
		getLaunchableApplications(self)
		Returns a list of all launchable application class names.
		'''
		return self._rpc("getLaunchableApplications")

	def getRunningPackages(self):
		'''
		getRunningPackages(self)
		Returns a list of packages running activities or services.
		returns: (List) List of packages running activities.
		'''
		return self._rpc("getRunningPackages")

	def launch(self,className):
		'''
		launch(className)
		Start activity with the given class name.
			className (String)
		'''
		return self._rpc("launch",className)

	def batteryCheckPresent(self):
		'''
		batteryCheckPresent(self)
		Returns the most recently received battery presence data.
		Min SDK level=5
		'''
		return self._rpc("batteryCheckPresent")

	def batteryGetHealth(self):
		'''
		batteryGetHealth(self)
		Returns the most recently received battery health data:1 - unknown;2 - good;3 - overheat;4 - dead;5 - over voltage;6 - unspecified failure;
		'''
		return self._rpc("batteryGetHealth")

	def batteryGetLevel(self):
		'''
		batteryGetLevel(self)
		Returns the most recently received battery level (percentage).
		Min SDK level=5
		'''
		return self._rpc("batteryGetLevel")

	def batteryGetPlugType(self):
		'''
		batteryGetPlugType(self)
		Returns the most recently received plug type data:-1 - unknown0 - unplugged;1 - power source is an AC charger2 - power source is a USB port
		'''
		return self._rpc("batteryGetPlugType")

	def batteryGetStatus(self):
		'''
		batteryGetStatus(self)
		Returns  the most recently received battery status data:1 - unknown;2 - charging;3 - discharging;4 - not charging;5 - full;
		'''
		return self._rpc("batteryGetStatus")

	def batteryGetTechnology(self):
		'''
		batteryGetTechnology(self)
		Returns the most recently received battery technology data.
		Min SDK level=5
		'''
		return self._rpc("batteryGetTechnology")

	def batteryGetTemperature(self):
		'''
		batteryGetTemperature(self)
		Returns the most recently received battery temperature.
		Min SDK level=5
		'''
		return self._rpc("batteryGetTemperature")

	def batteryGetVoltage(self):
		'''
		batteryGetVoltage(self)
		Returns the most recently received battery voltage.
		Min SDK level=5
		'''
		return self._rpc("batteryGetVoltage")

	def batteryStartMonitoring(self):
		'''
		batteryStartMonitoring(self)
		Starts tracking battery state.
		throws "battery" events
		'''
		return self._rpc("batteryStartMonitoring")

	def batteryStopMonitoring(self):
		'''
		batteryStopMonitoring(self)
		Stops tracking battery state.
		'''
		return self._rpc("batteryStopMonitoring")

	def readBatteryData(self):
		'''
		readBatteryData(self)
		Returns the most recently recorded battery data.
		'''
		return self._rpc("readBatteryData")

	def bluetoothAccept(self,uuid="457807c0-4897-11df-9879-0800200c9a66",timeout=0):
		'''
		bluetoothAccept(uuid="457807c0-4897-11df-9879-0800200c9a66",timeout=0)
		Listens for and accepts a Bluetooth connection. Blocks until the connection is established or fails.
			uuid (String)  (default=457807c0-4897-11df-9879-0800200c9a66)
			timeout (Integer) How long to wait for a new connection, 0 is wait for ever (default=0)
		'''
		return self._rpc("bluetoothAccept",uuid,timeout)

	def bluetoothActiveConnections(self):
		'''
		bluetoothActiveConnections(self)
		Returns true when there\'s an active Bluetooth connection.
		'''
		return self._rpc("bluetoothActiveConnections")

	def bluetoothConnect(self,uuid="457807c0-4897-11df-9879-0800200c9a66",address=None):
		'''
		bluetoothConnect(uuid="457807c0-4897-11df-9879-0800200c9a66",address=None)
		Connect to a device over Bluetooth. Blocks until the connection is established or fails.
			uuid (String) The UUID passed here must match the UUID used by the server device. (default=457807c0-4897-11df-9879-0800200c9a66)
			address (String) The user will be presented with a list of discovered devices to choose from if an address is not provided. (optional)
		returns: (String) True if the connection was established successfully.
		'''
		return self._rpc("bluetoothConnect",uuid,address)

	def bluetoothGetConnectedDeviceName(self,connID=None):
		'''
		bluetoothGetConnectedDeviceName(connID=None)
		Returns the name of the connected device.
			connID (String) Connection id (optional) (default=)
		'''
		return self._rpc("bluetoothGetConnectedDeviceName",connID)

	def bluetoothGetLocalName(self):
		'''
		bluetoothGetLocalName(self)
		Gets the Bluetooth Visible device name
		'''
		return self._rpc("bluetoothGetLocalName")

	def bluetoothGetRemoteDeviceName(self,address):
		'''
		bluetoothGetRemoteDeviceName(address)
		Queries a remote device for it\'s name or null if it can\'t be resolved
			address (String) Bluetooth Address For Target Device
		'''
		return self._rpc("bluetoothGetRemoteDeviceName",address)

	def bluetoothGetScanMode(self):
		'''
		bluetoothGetScanMode(self)
		Gets the scan mode for the local dongle.\rReturn values:\r\t-1 when Bluetooth is disabled.\r\t0 if non discoverable and non connectable.\r\r1 connectable non discoverable.\r3 connectable and discoverable.
		'''
		return self._rpc("bluetoothGetScanMode")

	def bluetoothMakeDiscoverable(self,duration=300):
		'''
		bluetoothMakeDiscoverable(duration=300)
		Requests that the device be discoverable for Bluetooth connections.
			duration (Integer) period of time, in seconds, during which the device should be discoverable (default=300)
		'''
		return self._rpc("bluetoothMakeDiscoverable",duration)

	def bluetoothRead(self,bufferSize=4096,connID=None):
		'''
		bluetoothRead(bufferSize=4096,connID=None)
		Read up to bufferSize ASCII characters.
			bufferSize (Integer)  (default=4096)
			connID (String) Connection id (optional) (default=)
		'''
		return self._rpc("bluetoothRead",bufferSize,connID)

	def bluetoothReadBinary(self,bufferSize=4096,connID=None):
		'''
		bluetoothReadBinary(bufferSize=4096,connID=None)
		Read up to bufferSize bytes and return a chunked, base64 encoded string.
			bufferSize (Integer)  (default=4096)
			connID (String) Connection id (optional) (default=)
		'''
		return self._rpc("bluetoothReadBinary",bufferSize,connID)

	def bluetoothReadLine(self,connID=None):
		'''
		bluetoothReadLine(connID=None)
		Read the next line.
			connID (String) Connection id (optional) (default=)
		'''
		return self._rpc("bluetoothReadLine",connID)

	def bluetoothReadReady(self,connID=None):
		'''
		bluetoothReadReady(connID=None)
		Returns True if the next read is guaranteed not to block.
			connID (String) Connection id (optional) (default=)
		'''
		return self._rpc("bluetoothReadReady",connID)

	def bluetoothSetLocalName(self,name):
		'''
		bluetoothSetLocalName(name)
		Sets the Bluetooth Visible device name, returns True on success
			name (String) New local name
		'''
		return self._rpc("bluetoothSetLocalName",name)

	def bluetoothStop(self,connID=None):
		'''
		bluetoothStop(connID=None)
		Stops Bluetooth connection.
			connID (String) Connection id (optional) (default=)
		'''
		return self._rpc("bluetoothStop",connID)

	def bluetoothWrite(self,ascii,connID=""):
		'''
		bluetoothWrite(ascii,connID="")
		Sends ASCII characters over the currently open Bluetooth connection.
			ascii (String)
			connID (String) Connection id (default=)
		'''
		return self._rpc("bluetoothWrite",ascii,connID)

	def bluetoothWriteBinary(self,base64,connID=None):
		'''
		bluetoothWriteBinary(base64,connID=None)
		Send bytes over the currently open Bluetooth connection.
			base64 (String) A base64 encoded String of the bytes to be sent.
			connID (String) Connection id (optional) (default=)
		'''
		return self._rpc("bluetoothWriteBinary",base64,connID)

	def checkBluetoothState(self):
		'''
		checkBluetoothState(self)
		Checks Bluetooth state.
		returns: (Boolean) True if Bluetooth is enabled.
		'''
		return self._rpc("checkBluetoothState")

	def toggleBluetoothState(self,enabled=None,prompt=True):
		'''
		toggleBluetoothState(enabled=None,prompt=True)
		Toggle Bluetooth on and off.
			enabled (Boolean)  (optional)
			prompt (Boolean) Prompt the user to confirm changing the Bluetooth state. (default=true)
		returns: (Boolean) True if Bluetooth is enabled.
		'''
		return self._rpc("toggleBluetoothState",enabled,prompt)

	def cameraCapturePicture(self,targetPath,useAutoFocus=True):
		'''
		cameraCapturePicture(targetPath,useAutoFocus=True)
		Take a picture and save it to the specified path.
			targetPath (String)
			useAutoFocus (Boolean)  (default=true)
		returns: (Bundle) A map of Booleans autoFocus and takePicture where True indicates success.
		'''
		return self._rpc("cameraCapturePicture",targetPath,useAutoFocus)

	def cameraInteractiveCapturePicture(self,targetPath):
		'''
		cameraInteractiveCapturePicture(targetPath)
		Starts the image capture application to take a picture and saves it to the specified path.
			targetPath (String)
		'''
		return self._rpc("cameraInteractiveCapturePicture",targetPath)

	def pick(self,uri):
		'''
		pick(uri)
		Display content to be picked by URI (e.g. contacts)
			uri (String)
		returns: (Intent) A map of result values.
		'''
		return self._rpc("pick",uri)

	def scanBarcode(self):
		'''
		scanBarcode(self)
		Starts the barcode scanner.
		returns: (Intent) A Map representation of the result Intent.
		'''
		return self._rpc("scanBarcode")

	def search(self,query):
		'''
		search(query)
		Starts a search for the given query.
			query (String)
		'''
		return self._rpc("search",query)

	def view(self,uri,type=None,extras=None):
		'''
		view(uri,type=None,extras=None)
		Start activity with view action by URI (i.e. browser, contacts, etc.).
			uri (String)
			type (String) MIME type/subtype of the URI (optional)
			extras (JSONObject) a Map of extras to add to the Intent (optional)
		'''
		return self._rpc("view",uri,type,extras)

	def viewContacts(self):
		'''
		viewContacts(self)
		Opens the list of contacts.
		'''
		return self._rpc("viewContacts")

	def viewHtml(self,path):
		'''
		viewHtml(path)
		Opens the browser to display a local HTML file.
			path (String) the path to the HTML file
		'''
		return self._rpc("viewHtml",path)

	def viewMap(self,query):
		'''
		viewMap(query,_e.g._pizza,_123_My_Street)
		Opens a map search for query (e.g. pizza, 123 My Street).
			query, e.g. pizza, 123 My Street (String)
		'''
		return self._rpc("viewMap",query)

	def contactsGet(self,attributes=None):
		'''
		contactsGet(attributes=None)
		Returns a List of all contacts.
			attributes (JSONArray)  (optional)
		returns: (List) a List of contacts as Maps
		'''
		return self._rpc("contactsGet",attributes)

	def contactsGetAttributes(self):
		'''
		contactsGetAttributes(self)
		Returns a List of all possible attributes for contacts.
		'''
		return self._rpc("contactsGetAttributes")

	def contactsGetById(self,id,attributes=None):
		'''
		contactsGetById(id,attributes=None)
		Returns contacts by ID.
			id (Integer)
			attributes (JSONArray)  (optional)
		'''
		return self._rpc("contactsGetById",id,attributes)

	def contactsGetCount(self):
		'''
		contactsGetCount(self)
		Returns the number of contacts.
		'''
		return self._rpc("contactsGetCount")

	def contactsGetIds(self):
		'''
		contactsGetIds(self)
		Returns a List of all contact IDs.
		'''
		return self._rpc("contactsGetIds")

	def pickContact(self):
		'''
		pickContact(self)
		Displays a list of contacts to pick from.
		returns: (Intent) A map of result values.
		'''
		return self._rpc("pickContact")

	def pickPhone(self):
		'''
		pickPhone(self)
		Displays a list of phone numbers to pick from.
		returns: (String) The selected phone number.
		'''
		return self._rpc("pickPhone")

	def queryAttributes(self,uri):
		'''
		queryAttributes(uri)
		Content Resolver Query Attributes
			uri (String) The URI, using the content:// scheme, for the content to retrieve.
		returns: (JSONArray) a list of available columns for a given content uri
		'''
		return self._rpc("queryAttributes",uri)

	def queryContent(self,uri,attributes=None,selection=None,selectionArgs=None,order=None):
		'''
		queryContent(uri,attributes=None,selection=None,selectionArgs=None,order=None)
		Content Resolver Query
			uri (String) The URI, using the content:// scheme, for the content to retrieve.
			attributes (JSONArray) A list of which columns to return. Passing null will return all columns (optional)
			selection (String) A filter declaring which rows to return (optional)
			selectionArgs (JSONArray) You may include ?s in selection, which will be replaced by the values from selectionArgs (optional)
			order (String) How to order the rows (optional)
		returns: (List) result of query as Maps
		Exactly as per ContentResolver.query
		'''
		return self._rpc("queryContent",uri,attributes,selection,selectionArgs,order)

	def eventClearBuffer(self):
		'''
		eventClearBuffer(self)
		Clears all events from the event buffer.
		Example (python): droid.eventClearBuffer()
		'''
		return self._rpc("eventClearBuffer")

	def eventGetBrodcastCategories(self):
		'''
		eventGetBrodcastCategories(self)
		Lists all the broadcast signals we are listening for
		'''
		return self._rpc("eventGetBrodcastCategories")

	def eventPoll(self,number_of_events=1):
		'''
		eventPoll(number_of_events=1)
		Returns and removes the oldest n events (i.e. location or sensor update, etc.) from the event buffer.
			number_of_events (Integer)  (default=1)
		returns: (List) A List of Maps of event properties.
		Actual data returned in the map will depend on the type of event.
		Example (python):
		import android, time
		droid = android.Android()
		droid.startSensing()
		time.sleep(1)
		droid.eventClearBuffer()
		time.sleep(1)
		e = eventPoll(1).result
		event_entry_number = 0
		x = e[event_entry_ number]['data']['xforce']
		e has the format:
		[{u'data': {u'accuracy': 0, u'pitch': -0.48766891956329345, u'xmag': -5.6875, u'azimuth':
		0.3312483489513397, u'zforce': 8.3492730000000002, u'yforce': 4.5628165999999997, u'time':
		1297072704.813, u'ymag': -11.125, u'zmag': -42.375, u'roll': -0.059393649548292161, u'xforce':
		0.42223078000000003}, u'name': u'sensors', u'time': 1297072704813000L}]
		x has the string value of the x force data (0.42223078000000003) at the time of the event
		entry.
		'''
		return self._rpc("eventPoll",number_of_events)

	def eventPost(self,name,data,enqueue=None):
		'''
		eventPost(name,data,enqueue=None)
		Post an event to the event queue.
			name (String) Name of event
			data (String) Data contained in event.
			enqueue (Boolean) Set to False if you don\'t want your events to be added to the event queue, just dispatched. (optional) (default=false)
		Example:
		import android
		from datetime import datetime
		droid = android.Android()
		t = datetime.now()
		droid.eventPost('Some Event', t)
		'''
		return self._rpc("eventPost",name,data,enqueue)

	def eventRegisterForBroadcast(self,category,enqueue=True):
		'''
		eventRegisterForBroadcast(category,enqueue=True)
		Registers a listener for a new broadcast signal
			category (String)
			enqueue (Boolean) Should this events be added to the event queue or only dispatched (default=true)
		Registers a listener for a new broadcast signal
		'''
		return self._rpc("eventRegisterForBroadcast",category,enqueue)

	def eventUnregisterForBroadcast(self,category):
		'''
		eventUnregisterForBroadcast(category)
		Stop listening for a broadcast signal
			category (String)
		'''
		return self._rpc("eventUnregisterForBroadcast",category)

	def eventWait(self,timeout=None):
		'''
		eventWait(timeout=None)
		Blocks until an event occurs. The returned event is removed from the buffer.
			timeout (Integer) the maximum time to wait (optional)
		returns: (Event) Map of event properties.
		'''
		return self._rpc("eventWait",timeout)

	def eventWaitFor(self,eventName,timeout=None):
		'''
		eventWaitFor(eventName,timeout=None)
		Blocks until an event with the supplied name occurs. The returned event is not removed from the buffer.
			eventName (String)
			timeout (Integer) the maximum time to wait (in ms) (optional)
		returns: (Event) Map of event properties.
		'''
		return self._rpc("eventWaitFor",eventName,timeout)

	def receiveEvent(self):
		'''
		receiveEvent(self)
		Returns and removes the oldest event (i.e. location or sensor update, etc.) from the event buffer.
		returns: (Event) Map of event properties.
		Deprecated in r4. Use eventPoll instead.
		'''
		return self._rpc("receiveEvent")

	def rpcPostEvent(self,name,data):
		'''
		rpcPostEvent(name,data)
		Post an event to the event queue.
			name (String)
			data (String)
		Deprecated in r4. Use eventPost instead.
		'''
		return self._rpc("rpcPostEvent",name,data)

	def startEventDispatcher(self,port=None):
		'''
		startEventDispatcher(port=None)
		Opens up a socket where you can read for events posted
			port (Integer) Port to use (optional) (default=0)
		'''
		return self._rpc("startEventDispatcher",port)

	def stopEventDispatcher(self):
		'''
		stopEventDispatcher(self)
		Stops the event server, you can\'t read in the port anymore
		'''
		return self._rpc("stopEventDispatcher")

	def waitForEvent(self,eventName,timeout=None):
		'''
		waitForEvent(eventName,timeout=None)
		Blocks until an event with the supplied name occurs. The returned event is not removed from the buffer.
			eventName (String)
			timeout (Integer) the maximum time to wait (optional)
		returns: (Event) Map of event properties.
		Deprecated in r4. Use eventWaitFor instead.
		'''
		return self._rpc("waitForEvent",eventName,timeout)

	def ttsSpeak(self,message):
		'''
		ttsSpeak(message)
		Speaks the provided message via TTS.
			message (String)
		'''
		return self._rpc("ttsSpeak",message)

	def geocode(self,latitude,longitude,maxResults=1):
		'''
		geocode(latitude,longitude,maxResults=1)
		Returns a list of addresses for the given latitude and longitude.
			latitude (Double)
			longitude (Double)
			maxResults (Integer) maximum number of results (default=1)
		returns: (List) A list of addresses.
		'''
		return self._rpc("geocode",latitude,longitude,maxResults)

	def getLastKnownLocation(self):
		'''
		getLastKnownLocation(self)
		Returns the last known location of the device.
		returns: (Map) A map of location information by provider.
		'''
		return self._rpc("getLastKnownLocation")

	def readLocation(self):
		'''
		readLocation(self)
		Returns the current location as indicated by all available providers.
		returns: (Map) A map of location information by provider.
		'''
		return self._rpc("readLocation")

	def startLocating(self,minDistance=60000,minUpdateDistance=30):
		'''
		startLocating(minDistance=60000,minUpdateDistance=30)
		Starts collecting location data.
			minDistance (Integer) minimum time between updates in milliseconds (default=60000)
			minUpdateDistance (Integer) minimum distance between updates in meters (default=30)
		'''
		return self._rpc("startLocating",minDistance,minUpdateDistance)

	def stopLocating(self):
		'''
		stopLocating(self)
		Stops collecting location data.
		'''
		return self._rpc("stopLocating")

	def mediaIsPlaying(self,tag="default"):
		'''
		mediaIsPlaying(tag="default")
		Checks if media file is playing.
			tag (String) string identifying resource (default=default)
		returns: (boolean) true if playing
		'''
		return self._rpc("mediaIsPlaying",tag)

	def mediaPlay(self,url,tag="default",play=True):
		'''
		mediaPlay(url,tag="default",play=True)
		Open a media file
			url (String) url of media resource
			tag (String) string identifying resource (default=default)
			play (Boolean) start playing immediately (default=true)
		returns: (boolean) true if play successful
		'''
		return self._rpc("mediaPlay",url,tag,play)

	def mediaPlayClose(self,tag="default"):
		'''
		mediaPlayClose(tag="default")
		Close media file
			tag (String) string identifying resource (default=default)
		returns: (boolean) true if successful
		'''
		return self._rpc("mediaPlayClose",tag)

	def mediaPlayInfo(self,tag="default"):
		'''
		mediaPlayInfo(tag="default")
		Information on current media
			tag (String) string identifying resource (default=default)
		returns: (Map) Media Information
		'''
		return self._rpc("mediaPlayInfo",tag)

	def mediaPlayList(self):
		'''
		mediaPlayList(self)
		Lists currently loaded media
		returns: (Set) List of Media Tags
		'''
		return self._rpc("mediaPlayList")

	def mediaPlayPause(self,tag="default"):
		'''
		mediaPlayPause(tag="default")
		pause playing media file
			tag (String) string identifying resource (default=default)
		returns: (boolean) true if successful
		'''
		return self._rpc("mediaPlayPause",tag)

	def mediaPlaySeek(self,msec,tag="default"):
		'''
		mediaPlaySeek(msec,tag="default")
		Seek To Position
			msec (Integer) Position in millseconds
			tag (String) string identifying resource (default=default)
		returns: (int) New Position (in ms)
		'''
		return self._rpc("mediaPlaySeek",msec,tag)

	def mediaPlaySetLooping(self,enabled=True,tag="default"):
		'''
		mediaPlaySetLooping(enabled=True,tag="default")
		Set Looping
			enabled (Boolean)  (default=true)
			tag (String) string identifying resource (default=default)
		returns: (boolean) True if successful
		'''
		return self._rpc("mediaPlaySetLooping",enabled,tag)

	def mediaPlayStart(self,tag="default"):
		'''
		mediaPlayStart(tag="default")
		start playing media file
			tag (String) string identifying resource (default=default)
		returns: (boolean) true if successful
		'''
		return self._rpc("mediaPlayStart",tag)

	def recorderCaptureVideo(self,targetPath,duration=None,recordAudio=True):
		'''
		recorderCaptureVideo(targetPath,duration=None,recordAudio=True)
		Records video (and optionally audio) from the camera and saves it to the given location. Duration specifies the maximum duration of the recording session. If duration is not provided this method will return immediately and the recording will only be stopped when recorderStop is called or when a scripts exits. Otherwise it will block for the time period equal to the duration argument.
			targetPath (String)
			duration (Integer)  (optional)
			recordAudio (Boolean)  (default=true)
		'''
		return self._rpc("recorderCaptureVideo",targetPath,duration,recordAudio)

	def recorderStartMicrophone(self,targetPath):
		'''
		recorderStartMicrophone(targetPath)
		Records audio from the microphone and saves it to the given location.
			targetPath (String)
		'''
		return self._rpc("recorderStartMicrophone",targetPath)

	def recorderStartVideo(self,targetPath,duration=0,videoSize=1):
		'''
		recorderStartVideo(targetPath,duration=0,videoSize=1)
		Records video from the camera and saves it to the given location. Duration specifies the maximum duration of the recording session. If duration is 0 this method will return and the recording will only be stopped when recorderStop is called or when a scripts exits. Otherwise it will block for the time period equal to the duration argument.videoSize: 0=160x120, 1=320x240, 2=352x288, 3=640x480, 4=800x480.
			targetPath (String)
			duration (Integer)  (default=0)
			videoSize (Integer)  (default=1)
		'''
		return self._rpc("recorderStartVideo",targetPath,duration,videoSize)

	def recorderStop(self):
		'''
		recorderStop(self)
		Stops a previously started recording.
		'''
		return self._rpc("recorderStop")

	def startInteractiveVideoRecording(self,path):
		'''
		startInteractiveVideoRecording(path)
		Starts the video capture application to record a video and saves it to the specified path.
			path (String)
		'''
		return self._rpc("startInteractiveVideoRecording",path)

	def checkNetworkRoaming(self):
		'''
		checkNetworkRoaming(self)
		Returns true if the device is considered roaming on the current network, for GSM purposes.
		'''
		return self._rpc("checkNetworkRoaming")

	def getCellLocation(self):
		'''
		getCellLocation(self)
		Returns the current cell location.
		'''
		return self._rpc("getCellLocation")

	def getDeviceId(self):
		'''
		getDeviceId(self)
		Returns the unique device ID, for example, the IMEI for GSM and the MEID for CDMA phones. Return null if device ID is not available.
		'''
		return self._rpc("getDeviceId")

	def getDeviceSoftwareVersion(self):
		'''
		getDeviceSoftwareVersion(self)
		Returns the software version number for the device, for example, the IMEI/SV for GSM phones. Return null if the software version is not available.
		'''
		return self._rpc("getDeviceSoftwareVersion")

	def getLine1Number(self):
		'''
		getLine1Number(self)
		Returns the phone number string for line 1, for example, the MSISDN for a GSM phone. Return null if it is unavailable.
		'''
		return self._rpc("getLine1Number")

	def getNeighboringCellInfo(self):
		'''
		getNeighboringCellInfo(self)
		Returns the neighboring cell information of the device.
		'''
		return self._rpc("getNeighboringCellInfo")

	def getNetworkOperator(self):
		'''
		getNetworkOperator(self)
		Returns the numeric name (MCC+MNC) of current registered operator.
		'''
		return self._rpc("getNetworkOperator")

	def getNetworkOperatorName(self):
		'''
		getNetworkOperatorName(self)
		Returns the alphabetic name of current registered operator.
		'''
		return self._rpc("getNetworkOperatorName")

	def getNetworkType(self):
		'''
		getNetworkType(self)
		Returns a the radio technology (network type) currently in use on the device.
		'''
		return self._rpc("getNetworkType")

	def getPhoneType(self):
		'''
		getPhoneType(self)
		Returns the device phone type.
		'''
		return self._rpc("getPhoneType")

	def getSimCountryIso(self):
		'''
		getSimCountryIso(self)
		Returns the ISO country code equivalent for the SIM provider\'s country code.
		'''
		return self._rpc("getSimCountryIso")

	def getSimOperator(self):
		'''
		getSimOperator(self)
		Returns the MCC+MNC (mobile country code + mobile network code) of the provider of the SIM. 5 or 6 decimal digits.
		'''
		return self._rpc("getSimOperator")

	def getSimOperatorName(self):
		'''
		getSimOperatorName(self)
		Returns the Service Provider Name (SPN).
		'''
		return self._rpc("getSimOperatorName")

	def getSimSerialNumber(self):
		'''
		getSimSerialNumber(self)
		Returns the serial number of the SIM, if applicable. Return null if it is unavailable.
		'''
		return self._rpc("getSimSerialNumber")

	def getSimState(self):
		'''
		getSimState(self)
		Returns the state of the device SIM card.
		'''
		return self._rpc("getSimState")

	def getSubscriberId(self):
		'''
		getSubscriberId(self)
		Returns the unique subscriber ID, for example, the IMSI for a GSM phone. Return null if it is unavailable.
		'''
		return self._rpc("getSubscriberId")

	def getVoiceMailAlphaTag(self):
		'''
		getVoiceMailAlphaTag(self)
		Retrieves the alphabetic identifier associated with the voice mail number.
		'''
		return self._rpc("getVoiceMailAlphaTag")

	def getVoiceMailNumber(self):
		'''
		getVoiceMailNumber(self)
		Returns the voice mail number. Return null if it is unavailable.
		'''
		return self._rpc("getVoiceMailNumber")

	def phoneCall(self,uri):
		'''
		phoneCall(uri)
		Calls a contact/phone number by URI.
			uri (String)
		'''
		return self._rpc("phoneCall",uri)

	def phoneCallNumber(self,phone_number):
		'''
		phoneCallNumber(phone_number)
		Calls a phone number.
			phone number (String)
		'''
		return self._rpc("phoneCallNumber",phone_number)

	def phoneDial(self,uri):
		'''
		phoneDial(uri)
		Dials a contact/phone number by URI.
			uri (String)
		'''
		return self._rpc("phoneDial",uri)

	def phoneDialNumber(self,phone_number):
		'''
		phoneDialNumber(phone_number)
		Dials a phone number.
			phone number (String)
		'''
		return self._rpc("phoneDialNumber",phone_number)

	def readPhoneState(self):
		'''
		readPhoneState(self)
		Returns the current phone state and incoming number.
		returns: (Bundle) A Map of \"state\" and \"incomingNumber\"
		'''
		return self._rpc("readPhoneState")

	def startTrackingPhoneState(self):
		'''
		startTrackingPhoneState(self)
		Starts tracking phone state.
		'''
		return self._rpc("startTrackingPhoneState")

	def stopTrackingPhoneState(self):
		'''
		stopTrackingPhoneState(self)
		Stops tracking phone state.
		'''
		return self._rpc("stopTrackingPhoneState")

	def prefGetAll(self,filename=None):
		'''
		prefGetAll(filename=None)
		Get list of Shared Preference Values
			filename (String) Desired preferences file. If not defined, uses the default Shared Preferences. (optional)
		returns: (Map) Map of key,value
		'''
		return self._rpc("prefGetAll",filename)

	def prefGetValue(self,key,filename=None):
		'''
		prefGetValue(key,filename=None)
		Read a value from shared preferences
			key (String)
			filename (String) Desired preferences file. If not defined, uses the default Shared Preferences. (optional)
		'''
		return self._rpc("prefGetValue",key,filename)

	def prefPutValue(self,key,value,filename=None):
		'''
		prefPutValue(key,value,filename=None)
		Write a value to shared preferences
			key (String)
			value (Object)
			filename (String) Desired preferences file. If not defined, uses the default Shared Preferences. (optional)
		'''
		return self._rpc("prefPutValue",key,value,filename)

	def readSensors(self):
		'''
		readSensors(self)
		Returns the most recently recorded sensor data.
		'''
		return self._rpc("readSensors")

	def sensorsGetAccuracy(self):
		'''
		sensorsGetAccuracy(self)
		Returns the most recently received accuracy value.
		'''
		return self._rpc("sensorsGetAccuracy")

	def sensorsGetLight(self):
		'''
		sensorsGetLight(self)
		Returns the most recently received light value.
		'''
		return self._rpc("sensorsGetLight")

	def sensorsReadAccelerometer(self):
		'''
		sensorsReadAccelerometer(self)
		Returns the most recently received accelerometer values.
		returns: (List) a List of Floats [(acceleration on the) X axis, Y axis, Z axis].
		'''
		return self._rpc("sensorsReadAccelerometer")

	def sensorsReadMagnetometer(self):
		'''
		sensorsReadMagnetometer(self)
		Returns the most recently received magnetic field values.
		returns: (List) a List of Floats [(magnetic field value for) X axis, Y axis, Z axis].
		'''
		return self._rpc("sensorsReadMagnetometer")

	def sensorsReadOrientation(self):
		'''
		sensorsReadOrientation(self)
		Returns the most recently received orientation values.
		returns: (List) a List of Doubles [azimuth, pitch, roll].
		'''
		return self._rpc("sensorsReadOrientation")

	def startSensing(self,sampleSize=5):
		'''
		startSensing(sampleSize=5)
		Starts recording sensor data to be available for polling.
			sampleSize (Integer) number of samples for calculating average readings (default=5)
		Deprecated in 4. Use startSensingTimed or startSensingThreshhold instead.
		'''
		return self._rpc("startSensing",sampleSize)

	def startSensingThreshold(self,sensorNumber,threshold,axis):
		'''
		startSensingThreshold(sensorNumber,threshold,axis)
		Records to the Event Queue sensor data exceeding a chosen threshold.
			sensorNumber (Integer) 1 = Orientation, 2 = Accelerometer, 3 = Magnetometer and 4 = Light
			threshold (Integer) Threshold level for chosen sensor (integer)
			axis (Integer) 0 = No axis, 1 = X, 2 = Y, 3 = X+Y, 4 = Z, 5= X+Z, 6 = Y+Z, 7 = X+Y+Z
		'''
		return self._rpc("startSensingThreshold",sensorNumber,threshold,axis)

	def startSensingTimed(self,sensorNumber,delayTime):
		'''
		startSensingTimed(sensorNumber,delayTime)
		Starts recording sensor data to be available for polling.
			sensorNumber (Integer) 1 = All, 2 = Accelerometer, 3 = Magnetometer and 4 = Light
			delayTime (Integer) Minimum time between readings in milliseconds
		'''
		return self._rpc("startSensingTimed",sensorNumber,delayTime)

	def stopSensing(self):
		'''
		stopSensing(self)
		Stops collecting sensor data.
		'''
		return self._rpc("stopSensing")

	def checkAirplaneMode(self):
		'''
		checkAirplaneMode(self)
		Checks the airplane mode setting.
		returns: (Boolean) True if airplane mode is enabled.
		'''
		return self._rpc("checkAirplaneMode")

	def checkRingerSilentMode(self):
		'''
		checkRingerSilentMode(self)
		Checks the ringer silent mode setting.
		returns: (Boolean) True if ringer silent mode is enabled.
		'''
		return self._rpc("checkRingerSilentMode")

	def checkScreenOn(self):
		'''
		checkScreenOn(self)
		Checks if the screen is on or off (requires API level 7).
		returns: (Boolean) True if the screen is currently on.
		'''
		return self._rpc("checkScreenOn")

	def getMaxMediaVolume(self):
		'''
		getMaxMediaVolume(self)
		Returns the maximum media volume.
		'''
		return self._rpc("getMaxMediaVolume")

	def getMaxRingerVolume(self):
		'''
		getMaxRingerVolume(self)
		Returns the maximum ringer volume.
		'''
		return self._rpc("getMaxRingerVolume")

	def getMediaVolume(self):
		'''
		getMediaVolume(self)
		Returns the current media volume.
		'''
		return self._rpc("getMediaVolume")

	def getRingerVolume(self):
		'''
		getRingerVolume(self)
		Returns the current ringer volume.
		'''
		return self._rpc("getRingerVolume")

	def getScreenBrightness(self):
		'''
		getScreenBrightness(self)
		Returns the screen backlight brightness.
		returns: (Integer) the current screen brightness between 0 and 255
		'''
		return self._rpc("getScreenBrightness")

	def getScreenTimeout(self):
		'''
		getScreenTimeout(self)
		Returns the current screen timeout in seconds.
		returns: (Integer) the current screen timeout in seconds.
		'''
		return self._rpc("getScreenTimeout")

	def getVibrateMode(self,ringer=None):
		'''
		getVibrateMode(ringer=None)
		Checks Vibration setting. If ringer=true then query Ringer setting, else query Notification setting
			ringer (Boolean)  (optional)
		returns: (Boolean) True if vibrate mode is enabled.
		'''
		return self._rpc("getVibrateMode",ringer)

	def setMediaVolume(self,volume):
		'''
		setMediaVolume(volume)
		Sets the media volume.
			volume (Integer)
		'''
		return self._rpc("setMediaVolume",volume)

	def setRingerVolume(self,volume):
		'''
		setRingerVolume(volume)
		Sets the ringer volume.
			volume (Integer)
		'''
		return self._rpc("setRingerVolume",volume)

	def setScreenBrightness(self,value):
		'''
		setScreenBrightness(value)
		Sets the the screen backlight brightness.
			value (Integer) brightness value between 0 and 255
		returns: (Integer) the original screen brightness.
		'''
		return self._rpc("setScreenBrightness",value)

	def setScreenTimeout(self,value):
		'''
		setScreenTimeout(value)
		Sets the screen timeout to this number of seconds.
			value (Integer)
		returns: (Integer) The original screen timeout.
		'''
		return self._rpc("setScreenTimeout",value)

	def toggleAirplaneMode(self,enabled=None):
		'''
		toggleAirplaneMode(enabled=None)
		Toggles airplane mode on and off.
			enabled (Boolean)  (optional)
		returns: (Boolean) True if airplane mode is enabled.
		'''
		return self._rpc("toggleAirplaneMode",enabled)

	def toggleRingerSilentMode(self,enabled=None):
		'''
		toggleRingerSilentMode(enabled=None)
		Toggles ringer silent mode on and off.
			enabled (Boolean)  (optional)
		returns: (Boolean) True if ringer silent mode is enabled.
		'''
		return self._rpc("toggleRingerSilentMode",enabled)

	def toggleVibrateMode(self,enabled=None,ringer=None):
		'''
		toggleVibrateMode(enabled=None,ringer=None)
		Toggles vibrate mode on and off. If ringer=true then set Ringer setting, else set Notification setting
			enabled (Boolean)  (optional)
			ringer (Boolean)  (optional)
		returns: (Boolean) True if vibrate mode is enabled.
		'''
		return self._rpc("toggleVibrateMode",enabled,ringer)

	def readSignalStrengths(self):
		'''
		readSignalStrengths(self)
		Returns the current signal strengths.
		returns: (Bundle) A map of \"gsm_signal_strength\"
		'''
		return self._rpc("readSignalStrengths")

	def startTrackingSignalStrengths(self):
		'''
		startTrackingSignalStrengths(self)
		Starts tracking signal strengths.
		'''
		return self._rpc("startTrackingSignalStrengths")

	def stopTrackingSignalStrengths(self):
		'''
		stopTrackingSignalStrengths(self)
		Stops tracking signal strength.
		'''
		return self._rpc("stopTrackingSignalStrengths")

	def smsDeleteMessage(self,id):
		'''
		smsDeleteMessage(id)
		Deletes a message.
			id (Integer)
		returns: (Boolean) True if the message was deleted
		'''
		return self._rpc("smsDeleteMessage",id)

	def smsGetAttributes(self):
		'''
		smsGetAttributes(self)
		Returns a List of all possible message attributes.
		'''
		return self._rpc("smsGetAttributes")

	def smsGetMessageById(self,id,attributes=None):
		'''
		smsGetMessageById(id,attributes=None)
		Returns message attributes.
			id (Integer) message ID
			attributes (JSONArray)  (optional)
		'''
		return self._rpc("smsGetMessageById",id,attributes)

	def smsGetMessageCount(self,unreadOnly,folder="inbox"):
		'''
		smsGetMessageCount(unreadOnly,folder="inbox")
		Returns the number of messages.
			unreadOnly (Boolean)
			folder (String)  (default=inbox)
		'''
		return self._rpc("smsGetMessageCount",unreadOnly,folder)

	def smsGetMessageIds(self,unreadOnly,folder="inbox"):
		'''
		smsGetMessageIds(unreadOnly,folder="inbox")
		Returns a List of all message IDs.
			unreadOnly (Boolean)
			folder (String)  (default=inbox)
		'''
		return self._rpc("smsGetMessageIds",unreadOnly,folder)

	def smsGetMessages(self,unreadOnly,folder="inbox",attributes=None):
		'''
		smsGetMessages(unreadOnly,folder="inbox",attributes=None)
		Returns a List of all messages.
			unreadOnly (Boolean)
			folder (String)  (default=inbox)
			attributes (JSONArray)  (optional)
		returns: (List) a List of messages as Maps
		'''
		return self._rpc("smsGetMessages",unreadOnly,folder,attributes)

	def smsMarkMessageRead(self,ids,read):
		'''
		smsMarkMessageRead(ids,read)
		Marks messages as read.
			ids (JSONArray) List of message IDs to mark as read.
			read (Boolean)
		returns: (Integer) number of messages marked read
		'''
		return self._rpc("smsMarkMessageRead",ids,read)

	def smsSend(self,destinationAddress,text):
		'''
		smsSend(destinationAddress,text)
		Sends an SMS.
			destinationAddress (String) typically a phone number
			text (String)
		'''
		return self._rpc("smsSend",destinationAddress,text)

	def recognizeSpeech(self,prompt=None,language=None,languageModel=None):
		'''
		recognizeSpeech(prompt=None,language=None,languageModel=None)
		Recognizes user\'s speech and returns the most likely result.
			prompt (String) text prompt to show to the user when asking them to speak (optional)
			language (String) language override to inform the recognizer that it should expect speech in a language different than the one set in the java.util.Locale.getDefault() (optional)
			languageModel (String) informs the recognizer which speech model to prefer (see android.speech.RecognizeIntent) (optional)
		returns: (String) An empty string in case the speech cannot be recongnized.
		'''
		return self._rpc("recognizeSpeech",prompt,language,languageModel)

	def ttsIsSpeaking(self):
		'''
		ttsIsSpeaking(self)
		Returns True if speech is currently in progress.
		'''
		return self._rpc("ttsIsSpeaking")

	def ttsSpeak(self,message):
		'''
		ttsSpeak(message)
		Speaks the provided message via TTS.
			message (String)
		'''
		return self._rpc("ttsSpeak",message)

	def generateDtmfTones(self,phoneNumber,toneDuration=100):
		'''
		generateDtmfTones(phoneNumber,toneDuration=100)
		Generate DTMF tones for the given phone number.
			phoneNumber (String)
			toneDuration (Integer) duration of each tone in milliseconds (default=100)
		'''
		return self._rpc("generateDtmfTones",phoneNumber,toneDuration)

	def addContextMenuItem(self,label,event,eventData=None):
		'''
		addContextMenuItem(label,event,eventData=None)
		Adds a new item to context menu.
			label (String) label for this menu item
			event (String) event that will be generated on menu item click
			eventData (Object)  (optional)
		Context menus are used primarily with webViewShow
		'''
		return self._rpc("addContextMenuItem",label,event,eventData)

	def addOptionsMenuItem(self,label,event,eventData=None,iconName=None):
		'''
		addOptionsMenuItem(label,event,eventData=None,iconName=None)
		Adds a new item to options menu.
			label (String) label for this menu item
			event (String) event that will be generated on menu item click
			eventData (Object)  (optional)
			iconName (String) Android system menu icon, see http://developer.android.com/reference/android/R.drawable.html (optional)
		Example (python)
		import android
		droid=android.Android()
		droid.addOptionsMenuItem("Silly","silly",None,"star_on")
		droid.addOptionsMenuItem("Sensible","sensible","I bet.","star_off")
		droid.addOptionsMenuItem("Off","off",None,"ic_menu_revert")
		print "Hit menu to see extra options."
		print "Will timeout in 10 seconds if you hit nothing."
		while True: # Wait for events from the menu.
		response=droid.eventWait(10000).result
		if response==None:
		break
		print response
		if response["name"]=="off":
		break
		print "And done."
		'''
		return self._rpc("addOptionsMenuItem",label,event,eventData,iconName)

	def clearContextMenu(self):
		'''
		clearContextMenu(self)
		Removes all items previously added to context menu.
		'''
		return self._rpc("clearContextMenu")

	def clearOptionsMenu(self):
		'''
		clearOptionsMenu(self)
		Removes all items previously added to options menu.
		'''
		return self._rpc("clearOptionsMenu")

	def dialogCreateAlert(self,title=None,message=None):
		'''
		dialogCreateAlert(title=None,message=None)
		Create alert dialog.
			title (String)  (optional)
			message (String)  (optional)
		Example (python)
		import android
		droid=android.Android()
		droid.dialogCreateAlert("I like swords.","Do you like swords?")
		droid.dialogSetPositiveButtonText("Yes")
		droid.dialogSetNegativeButtonText("No")
		droid.dialogShow()
		response=droid.dialogGetResponse().result
		droid.dialogDismiss()
		if response.has_key("which"):
		result=response["which"]
		if result=="positive":
		print "Yay! I like swords too!"
		elif result=="negative":
		print "Oh. How sad."
		elif response.has_key("canceled"): # Yes, I know it's mispelled.
		print "You can't even make up your mind?"
		else:
		print "Unknown response=",response
		print "Done"
		'''
		return self._rpc("dialogCreateAlert",title,message)

	def dialogCreateDatePicker(self,year=1970,month=1,day=1):
		'''
		dialogCreateDatePicker(year=1970,month=1,day=1)
		Create date picker dialog.
			year (Integer)  (default=1970)
			month (Integer)  (default=1)
			day (Integer)  (default=1)
		'''
		return self._rpc("dialogCreateDatePicker",year,month,day)

	def dialogCreateHorizontalProgress(self,title=None,message=None,maximum_progress=100):
		'''
		dialogCreateHorizontalProgress(title=None,message=None,maximum_progress=100)
		Create a horizontal progress dialog.
			title (String)  (optional)
			message (String)  (optional)
			maximum progress (Integer)  (default=100)
		'''
		return self._rpc("dialogCreateHorizontalProgress",title,message,maximum_progress)

	def dialogCreateInput(self,title="Value",message="Please enter value:",defaultText=None,inputType=None):
		'''
		dialogCreateInput(title="Value",message="Please enter value:",defaultText=None,inputType=None)
		Create a text input dialog.
			title (String) title of the input box (default=Value)
			message (String) message to display above the input box (default=Please enter value:)
			defaultText (String) text to insert into the input box (optional)
			inputType (String) type of input data, ie number or text (optional)
		For inputType, see InputTypes. Some useful ones are text, number, and textUri. Multiple flags can be
		supplied, seperated by "|", ie: "textUri|textAutoComplete"
		'''
		return self._rpc("dialogCreateInput",title,message,defaultText,inputType)

	def dialogCreatePassword(self,title="Password",message="Please enter password:"):
		'''
		dialogCreatePassword(title="Password",message="Please enter password:")
		Create a password input dialog.
			title (String) title of the input box (default=Password)
			message (String) message to display above the input box (default=Please enter password:)
		'''
		return self._rpc("dialogCreatePassword",title,message)

	def dialogCreateSeekBar(self,starting_value=50,maximum_value=100,title="",message=""):
		'''
		dialogCreateSeekBar(starting_value=50,maximum_value=100,title,message)
		Create seek bar dialog.
			starting value (Integer)  (default=50)
			maximum value (Integer)  (default=100)
			title (String)
			message (String)
		Will produce "dialog" events on change, containing:
		"progress" - Position chosen, between 0 and max
		"which" = "seekbar"
		"fromuser" = true/false change is from user input
		Response will contain a "progress" element.
		'''
		return self._rpc("dialogCreateSeekBar",starting_value,maximum_value,title,message)

	def dialogCreateSpinnerProgress(self,title=None,message=None,maximum_progress=100):
		'''
		dialogCreateSpinnerProgress(title=None,message=None,maximum_progress=100)
		Create a spinner progress dialog.
			title (String)  (optional)
			message (String)  (optional)
			maximum progress (Integer)  (default=100)
		'''
		return self._rpc("dialogCreateSpinnerProgress",title,message,maximum_progress)

	def dialogCreateTimePicker(self,hour=0,minute=0,is24hour=False):
		'''
		dialogCreateTimePicker(hour=0,minute=0,is24hour=False)
		Create time picker dialog.
			hour (Integer)  (default=0)
			minute (Integer)  (default=0)
			is24hour (Boolean) Use 24 hour clock (default=false)
		'''
		return self._rpc("dialogCreateTimePicker",hour,minute,is24hour)

	def dialogDismiss(self):
		'''
		dialogDismiss(self)
		Dismiss dialog.
		'''
		return self._rpc("dialogDismiss")

	def dialogGetInput(self,title="Value",message="Please enter value:",defaultText=None):
		'''
		dialogGetInput(title="Value",message="Please enter value:",defaultText=None)
		Queries the user for a text input.
			title (String) title of the input box (default=Value)
			message (String) message to display above the input box (default=Please enter value:)
			defaultText (String) text to insert into the input box (optional)
		The result is the user's input, or None (null) if cancel was hit.
		Example (python)
		import android
		droid=android.Android()
		print droid.dialogGetInput("Title","Message","Default").result
		'''
		return self._rpc("dialogGetInput",title,message,defaultText)

	def dialogGetPassword(self,title="Password",message="Please enter password:"):
		'''
		dialogGetPassword(title="Password",message="Please enter password:")
		Queries the user for a password.
			title (String) title of the password box (default=Password)
			message (String) message to display above the input box (default=Please enter password:)
		'''
		return self._rpc("dialogGetPassword",title,message)

	def dialogGetResponse(self):
		'''
		dialogGetResponse(self)
		Returns dialog response.
		'''
		return self._rpc("dialogGetResponse")

	def dialogGetSelectedItems(self):
		'''
		dialogGetSelectedItems(self)
		This method provides list of items user selected.
		returns: (Set) Selected items
		'''
		return self._rpc("dialogGetSelectedItems")

	def dialogSetCurrentProgress(self,current):
		'''
		dialogSetCurrentProgress(current)
		Set progress dialog current value.
			current (Integer)
		'''
		return self._rpc("dialogSetCurrentProgress",current)

	def dialogSetItems(self,items):
		'''
		dialogSetItems(items)
		Set alert dialog list items.
			items (JSONArray)
		This effectively creates list of options. Clicking on an item will immediately return an "item"
		element, which is the index of the selected item.
		'''
		return self._rpc("dialogSetItems",items)

	def dialogSetMaxProgress(self,max):
		'''
		dialogSetMaxProgress(max)
		Set progress dialog maximum value.
			max (Integer)
		'''
		return self._rpc("dialogSetMaxProgress",max)

	def dialogSetMultiChoiceItems(self,items,selected=None):
		'''
		dialogSetMultiChoiceItems(items,selected=None)
		Set dialog multiple choice items and selection.
			items (JSONArray)
			selected (JSONArray) list of selected items (optional)
		This creates a list of check boxes. You can select multiple items out of the list. A response
		will not be returned until the dialog is closed, either with the Cancel key or a button
		(positive/negative/neutral). Use dialogGetSelectedItems() to find out what was
		selected.
		'''
		return self._rpc("dialogSetMultiChoiceItems",items,selected)

	def dialogSetNegativeButtonText(self,text):
		'''
		dialogSetNegativeButtonText(text)
		Set alert dialog button text.
			text (String)
		'''
		return self._rpc("dialogSetNegativeButtonText",text)

	def dialogSetNeutralButtonText(self,text):
		'''
		dialogSetNeutralButtonText(text)
		Set alert dialog button text.
			text (String)
		'''
		return self._rpc("dialogSetNeutralButtonText",text)

	def dialogSetPositiveButtonText(self,text):
		'''
		dialogSetPositiveButtonText(text)
		Set alert dialog positive button text.
			text (String)
		'''
		return self._rpc("dialogSetPositiveButtonText",text)

	def dialogSetSingleChoiceItems(self,items,selected=0):
		'''
		dialogSetSingleChoiceItems(items,selected=0)
		Set dialog single choice items and selected item.
			items (JSONArray)
			selected (Integer) selected item index (default=0)
		This creates a list of radio buttons. You can select one item out of the list. A response will
		not be returned until the dialog is closed, either with the Cancel key or a button
		(positive/negative/neutral). Use dialogGetSelectedItems() to find out what was
		selected.
		'''
		return self._rpc("dialogSetSingleChoiceItems",items,selected)

	def dialogShow(self):
		'''
		dialogShow(self)
		Show dialog.
		'''
		return self._rpc("dialogShow")

	def fullDismiss(self):
		'''
		fullDismiss(self)
		Dismiss Full Screen.
		'''
		return self._rpc("fullDismiss")

	def fullQuery(self):
		'''
		fullQuery(self)
		Get Fullscreen Properties
		'''
		return self._rpc("fullQuery")

	def fullQueryDetail(self,id):
		'''
		fullQueryDetail(id)
		Get fullscreen properties for a specific widget
			id (String) id of layout widget
		'''
		return self._rpc("fullQueryDetail",id)

	def fullSetList(self,id,list):
		'''
		fullSetList(id,list)
		Attach a list to a fullscreen widget
			id (String) id of layout widget
			list (JSONArray) List to set
		'''
		return self._rpc("fullSetList",id,list)

	def fullSetProperty(self,id,property,value):
		'''
		fullSetProperty(id,property,value)
		Set fullscreen widget property
			id (String) id of layout widget
			property (String) name of property to set
			value (String) value to set property to
		'''
		return self._rpc("fullSetProperty",id,property,value)

	def fullShow(self,layout):
		'''
		fullShow(layout)
		Show Full Screen.
			layout (String) String containing View layout
		See wiki page for more
		detail.
		'''
		return self._rpc("fullShow",layout)

	def webViewShow(self,url,wait=None):
		'''
		webViewShow(url,wait=None)
		Display a WebView with the given URL.
			url (String)
			wait (Boolean) block until the user exits the WebView (optional)
		See wiki page for more
		detail.
		'''
		return self._rpc("webViewShow",url,wait)

	def wakeLockAcquireBright(self):
		'''
		wakeLockAcquireBright(self)
		Acquires a bright wake lock (CPU on, screen bright).
		'''
		return self._rpc("wakeLockAcquireBright")

	def wakeLockAcquireDim(self):
		'''
		wakeLockAcquireDim(self)
		Acquires a dim wake lock (CPU on, screen dim).
		'''
		return self._rpc("wakeLockAcquireDim")

	def wakeLockAcquireFull(self):
		'''
		wakeLockAcquireFull(self)
		Acquires a full wake lock (CPU on, screen bright, keyboard bright).
		'''
		return self._rpc("wakeLockAcquireFull")

	def wakeLockAcquirePartial(self):
		'''
		wakeLockAcquirePartial(self)
		Acquires a partial wake lock (CPU on).
		'''
		return self._rpc("wakeLockAcquirePartial")

	def wakeLockRelease(self):
		'''
		wakeLockRelease(self)
		Releases the wake lock.
		'''
		return self._rpc("wakeLockRelease")

	def cameraStartPreview(self,resolutionLevel=0,jpegQuality=20,filepath=None):
		'''
		cameraStartPreview(resolutionLevel=0,jpegQuality=20,filepath=None)
		Start Preview Mode. Throws \'preview\' events.
			resolutionLevel (Integer) increasing this number provides higher resolution (default=0)
			jpegQuality (Integer) a number from 0-100 (default=20)
			filepath (String) Path to store jpeg files. (optional)
		returns: (boolean) True if successful
		'''
		return self._rpc("cameraStartPreview",resolutionLevel,jpegQuality,filepath)

	def cameraStopPreview(self):
		'''
		cameraStopPreview(self)
		Stop the preview mode.
		'''
		return self._rpc("cameraStopPreview")

	def webcamAdjustQuality(self,resolutionLevel=0,jpegQuality=20):
		'''
		webcamAdjustQuality(resolutionLevel=0,jpegQuality=20)
		Adjusts the quality of the webcam stream while it is running.
			resolutionLevel (Integer) increasing this number provides higher resolution (default=0)
			jpegQuality (Integer) a number from 0-100 (default=20)
		'''
		return self._rpc("webcamAdjustQuality",resolutionLevel,jpegQuality)

	def webcamStart(self,resolutionLevel=0,jpegQuality=20,port=0):
		'''
		webcamStart(resolutionLevel=0,jpegQuality=20,port=0)
		Starts an MJPEG stream and returns a Tuple of address and port for the stream.
			resolutionLevel (Integer) increasing this number provides higher resolution (default=0)
			jpegQuality (Integer) a number from 0-100 (default=20)
			port (Integer) If port is specified, the webcam service will bind to port, otherwise it will pick any available port. (default=0)
		'''
		return self._rpc("webcamStart",resolutionLevel,jpegQuality,port)

	def webcamStop(self):
		'''
		webcamStop(self)
		Stops the webcam stream.
		'''
		return self._rpc("webcamStop")

	def checkWifiState(self):
		'''
		checkWifiState(self)
		Checks Wifi state.
		returns: (Boolean) True if Wifi is enabled.
		'''
		return self._rpc("checkWifiState")

	def toggleWifiState(self,enabled=None):
		'''
		toggleWifiState(enabled=None)
		Toggle Wifi on and off.
			enabled (Boolean)  (optional)
		returns: (Boolean) True if Wifi is enabled.
		'''
		return self._rpc("toggleWifiState",enabled)

	def wifiDisconnect(self):
		'''
		wifiDisconnect(self)
		Disconnects from the currently active access point.
		returns: (Boolean) True if the operation succeeded.
		'''
		return self._rpc("wifiDisconnect")

	def wifiGetConnectionInfo(self):
		'''
		wifiGetConnectionInfo(self)
		Returns information about the currently active access point.
		'''
		return self._rpc("wifiGetConnectionInfo")

	def wifiGetScanResults(self):
		'''
		wifiGetScanResults(self)
		Returns the list of access points found during the most recent Wifi scan.
		'''
		return self._rpc("wifiGetScanResults")

	def wifiLockAcquireFull(self):
		'''
		wifiLockAcquireFull(self)
		Acquires a full Wifi lock.
		'''
		return self._rpc("wifiLockAcquireFull")

	def wifiLockAcquireScanOnly(self):
		'''
		wifiLockAcquireScanOnly(self)
		Acquires a scan only Wifi lock.
		'''
		return self._rpc("wifiLockAcquireScanOnly")

	def wifiLockRelease(self):
		'''
		wifiLockRelease(self)
		Releases a previously acquired Wifi lock.
		'''
		return self._rpc("wifiLockRelease")

	def wifiReassociate(self):
		'''
		wifiReassociate(self)
		Reassociates with the currently active access point.
		returns: (Boolean) True if the operation succeeded.
		'''
		return self._rpc("wifiReassociate")

	def wifiReconnect(self):
		'''
		wifiReconnect(self)
		Reconnects to the currently active access point.
		returns: (Boolean) True if the operation succeeded.
		'''
		return self._rpc("wifiReconnect")

	def wifiStartScan(self):
		'''
		wifiStartScan(self)
		Starts a scan for Wifi access points.
		returns: (Boolean) True if the scan was initiated successfully.
		'''
		return self._rpc("wifiStartScan")

