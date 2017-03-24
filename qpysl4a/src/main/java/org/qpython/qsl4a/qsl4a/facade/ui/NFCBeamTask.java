/*
 * Copyright (C) 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.qpython.qsl4a.qsl4a.facade.ui;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.Toast;

import org.qpython.qsl4a.qsl4a.activity.FutureActivity;

/**
 * Wrapper class for progress dialog running in separate thread
 * 
 * @author MeanEYE.rcf (meaneye.rcf@gmail.com)
 */
class NFCBeamTask extends DialogTask implements CreateNdefMessageCallback, OnNdefPushCompleteCallback {

	  private final String TAG = "NFCBeamTask";
	  private final String mTitle;
	  private final String mMessage;

	  private final List<String> mItems;
	  private final Set<Integer> mSelectedItems;
	  private final Map<String, Object> mResultMap;
	  private InputType mInputType;
	  private int mEditInputType = 0;

	  private String mPositiveButtonText;
	  private String mNegativeButtonText;
	  private String mNeutralButtonText;

	  private EditText mEditText;
	  private String mDefaultText;

	  private int beamStat=0;
	  private enum InputType {
	    DEFAULT, MENU, SINGLE_CHOICE, MULTI_CHOICE, PLAIN_TEXT, PASSWORD;
	  }

	  public NFCBeamTask(String title, String message) {
	    mTitle = title;
	    mMessage = message;
	    mInputType = InputType.DEFAULT;
	    mItems = new ArrayList<String>();
	    mSelectedItems = new TreeSet<Integer>();
	    mResultMap = new HashMap<String, Object>();
	  }

	  public void setPositiveButtonText(String text) {
	    mPositiveButtonText = text;
	  }

	  public void setNegativeButtonText(String text) {
	    mNegativeButtonText = text;
	  }

	  public void setNeutralButtonText(String text) {
	    mNeutralButtonText = text;
	  }

	  /**
	   * Set list items.
	   * 
	   * @param items
	   */
	  public void setItems(JSONArray items) {
	    mItems.clear();
	    for (int i = 0; i < items.length(); i++) {
	      try {
	        mItems.add(items.getString(i));
	      } catch (JSONException e) {
	        throw new RuntimeException(e);
	      }
	    }
	    mInputType = InputType.MENU;
	  }

	  /**
	   * Set single choice items.
	   * 
	   * @param items
	   *          a list of items as {@link String}s to display
	   * @param selected
	   *          the index of the item that is selected by default
	   */
	  public void setSingleChoiceItems(JSONArray items, int selected) {
	    setItems(items);
	    mSelectedItems.clear();
	    mSelectedItems.add(selected);
	    mInputType = InputType.SINGLE_CHOICE;
	  }

	  /**
	   * Set multi choice items.
	   * 
	   * @param items
	   *          a list of items as {@link String}s to display
	   * @param selected
	   *          a list of indices for items that should be selected by default
	   * @throws JSONException
	   */
	  public void setMultiChoiceItems(JSONArray items, JSONArray selected) throws JSONException {
	    setItems(items);
	    mSelectedItems.clear();
	    if (selected != null) {
	      for (int i = 0; i < selected.length(); i++) {
	        mSelectedItems.add(selected.getInt(i));
	      }
	    }
	    mInputType = InputType.MULTI_CHOICE;
	  }

	  /**
	   * Returns the list of selected items.
	   */
	  public Set<Integer> getSelectedItems() {
	    return mSelectedItems;
	  }

	  public void setTextInput(String defaultText) {
	    mDefaultText = defaultText;
	    mInputType = InputType.PLAIN_TEXT;
	    setEditInputType("text");
	  }

	  public void setEditInputType(String editInputType) {
	    String[] list = editInputType.split("\\|");
	    Map<String, Integer> types = ViewInflater.getInputTypes();
	    mEditInputType = 0;
	    for (String flag : list) {
	      Integer v = types.get(flag.trim());
	      if (v != null) {
	        mEditInputType |= v;
	      }
	    }
	    if (mEditInputType == 0) {
	      mEditInputType = android.text.InputType.TYPE_CLASS_TEXT;
	    }
	  }

	  public void setPasswordInput() {
	    mInputType = InputType.PASSWORD;
	  }

  
	NfcAdapter _nfcAdapter;
	private PendingIntent _nfcPendingIntent;
	IntentFilter[] _readTagFilters;
    //protected _WBase WBase;
    protected int dialogIndex;

    private String content = "QPython NFC";
    
    @SuppressLint("NewApi")
	public boolean initNFCBeam() {
    	Log.d(TAG, "initNFCBeam");
    	mhandler = new Handler() {
    		@Override
    		public void handleMessage(Message msg) {
    			//Toast.makeText(getActivity().getApplicationContext(), "Master has sent content", Toast.LENGTH_SHORT).show();
    			
    			setResult("master","ok");
    		}
    	};
    	
//    	WBase = new _WBase(getActivity().getApplicationContext(), getActivity());
    	dialogIndex = 1;

    	_nfcAdapter = NfcAdapter.getDefaultAdapter(getActivity().getApplicationContext());
	
    	if (_nfcAdapter == null) {
			Toast.makeText(getActivity().getApplicationContext(), "NFC is not available, please upgrade your mobile", Toast.LENGTH_SHORT).show();

    		/*WBase.setTxtDialogParam(R.drawable.alert_dialog_icon,
				"NFC is not available, please update your mobile",
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
					}
			});
    		getActivity().showDialog(_WBase.DIALOG_NOTIFY_MESSAGE + dialogIndex);
    		dialogIndex++;*/
    	} else {

			if (!_nfcAdapter.isEnabled()) {
				Toast.makeText(getActivity().getApplicationContext(), "NFC is closed, please enable it with beam", Toast.LENGTH_SHORT).show();

/*				WBase.setTxtDialogParam(R.drawable.alert_dialog_icon,
						"NFC is closed, please enable it with beam",
						new OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								getActivity().startActivity(new Intent(
										Settings.ACTION_NFC_SETTINGS));
							}
						});
				getActivity().showDialog(_WBase.DIALOG_NOTIFY_MESSAGE + dialogIndex);
				dialogIndex++;*/
			} else {
				launchNFC();
				return true;
			}
    	}
    	return false;
    }
    
    public void setBeamType(int stat) {
    	beamStat = stat;
    }
    @SuppressLint("NewApi")
	public void launchNFC() {
    	Log.d(TAG, "launchNFC:"+beamStat);
    	if (beamStat == 0) {
			_nfcAdapter.setNdefPushMessageCallback(this, getActivity());
			_nfcAdapter.setOnNdefPushCompleteCallback(this, getActivity());

			// start NFC Connection
			/*_nfcPendingIntent = PendingIntent.getActivity(getActivity().getApplicationContext(), 0,
					new Intent(getActivity().getApplicationContext(), FutureActivity.class)
							.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

			IntentFilter ndefDetected = new IntentFilter(
					NfcAdapter.ACTION_NDEF_DISCOVERED);
			try {
				ndefDetected.addDataType("application/com.hipipal.qpy.nfc");
			} catch (MalformedMimeTypeException e) {
				throw new RuntimeException("Could not add MIME type.", e);
			}

			_readTagFilters = new IntentFilter[] { ndefDetected };*/
    	} else if (beamStat == 1) {
			//_nfcAdapter.setNdefPushMessageCallback(this, getActivity());
			//_nfcAdapter.setOnNdefPushCompleteCallback(this, getActivity());

			// start NFC Connection
			_nfcPendingIntent = PendingIntent.getActivity(getActivity().getApplicationContext(), 0,
					new Intent(getActivity().getApplicationContext(), FutureActivity.class)
							.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

			IntentFilter ndefDetected = new IntentFilter(
					NfcAdapter.ACTION_NDEF_DISCOVERED);
			try {
				ndefDetected.addDataType("application/com.hipipal.qpy.nfc");
			} catch (MalformedMimeTypeException e) {
				throw new RuntimeException("Could not add MIME type.", e);
			}

			_readTagFilters = new IntentFilter[] { ndefDetected };
    	}
    }
    

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @SuppressLint("NewApi")
    @Override
    public void onCreate() {
    	super.onCreate();
    	if (initNFCBeam()) {

		    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		    if (mTitle != null) {
		      builder.setTitle(mTitle);
		    }
		    // Can't display both a message and items. We'll elect to show the items instead.
		    if (mMessage != null && mItems.isEmpty()) {
		      builder.setMessage(mMessage);
		    }
		    switch (mInputType) {
		    // Add single choice menu items to dialog.
		    case SINGLE_CHOICE:
		      builder.setSingleChoiceItems(getItemsAsCharSequenceArray(), mSelectedItems.iterator().next(),
		          new DialogInterface.OnClickListener() {
		            @Override
		            public void onClick(DialogInterface dialog, int item) {
		              mSelectedItems.clear();
		              mSelectedItems.add(item);
		            }
		          });
		      break;
		    // Add multiple choice items to the dialog.
		    case MULTI_CHOICE:
		      boolean[] selectedItems = new boolean[mItems.size()];
		      for (int i : mSelectedItems) {
		        selectedItems[i] = true;
		      }
		      builder.setMultiChoiceItems(getItemsAsCharSequenceArray(), selectedItems,
		          new DialogInterface.OnMultiChoiceClickListener() {
		            @Override
		            public void onClick(DialogInterface dialog, int item, boolean isChecked) {
		              if (isChecked) {
		                mSelectedItems.add(item);
		              } else {
		                mSelectedItems.remove(item);
		              }
		            }
		          });
		      break;
		    // Add standard, menu-like, items to dialog.
		    case MENU:
		      builder.setItems(getItemsAsCharSequenceArray(), new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int item) {
		          Map<String, Integer> result = new HashMap<String, Integer>();
		          result.put("item", item);
		          dismissDialog();
		          setResult(result);
		        }
		      });
		      break;
		    case PLAIN_TEXT:
		      mEditText = new EditText(getActivity());
		      if (mDefaultText != null) {
		        mEditText.setText(mDefaultText);
		      }
		      mEditText.setInputType(mEditInputType);
		      builder.setView(mEditText);
		      break;
		    case PASSWORD:
		      mEditText = new EditText(getActivity());
		      mEditText.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
		      mEditText.setTransformationMethod(new PasswordTransformationMethod());
		      builder.setView(mEditText);
		      break;
		    default:
		      // No input type specified.
		    }
		    configureButtons(builder, getActivity());
		    addOnCancelListener(builder, getActivity());
		    mDialog = builder.show();
		    mShowLatch.countDown();
    	} else {
    		
    	}
  	}
    
    private CharSequence[] getItemsAsCharSequenceArray() {
        return mItems.toArray(new CharSequence[mItems.size()]);
      }

      private Builder addOnCancelListener(final AlertDialog.Builder builder, final Activity activity) {
        return builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
          @Override
          public void onCancel(DialogInterface dialog) {
            mResultMap.put("canceled", true);
            setResult("self","canceled");
          }
        });
      }

      private void configureButtons(final AlertDialog.Builder builder, final Activity activity) {
        DialogInterface.OnClickListener buttonListener = new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
              mResultMap.put("which", "positive");
              break;
            case DialogInterface.BUTTON_NEGATIVE:
              mResultMap.put("which", "negative");
              break;
            case DialogInterface.BUTTON_NEUTRAL:
              mResultMap.put("which", "neutral");

              break;
            }
            setResult("self","ok");
          }
        };
        if (mNegativeButtonText != null) {
          builder.setNegativeButton(mNegativeButtonText, buttonListener);
        }
        if (mPositiveButtonText != null) {
          builder.setPositiveButton(mPositiveButtonText, buttonListener);
        }
        if (mNeutralButtonText != null) {
          builder.setNeutralButton(mNeutralButtonText, buttonListener);
        }
      }

      private void setResult(String ms, String stat) {
        dismissDialog();
        String msg = "";
        if (mInputType == InputType.PLAIN_TEXT || mInputType == InputType.PASSWORD) {
        	msg = mEditText.getText().toString();
        } else {
        	msg = content;
        }
        Log.d(TAG, "setResult:"+mResultMap);
        JSONObject ret2 = new JSONObject();
        try {
			ret2.put("role", ms);
	        ret2.put("stat", stat);
	        ret2.put("message", msg);
	        mResultMap.put("value", ret2.toString());

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        setResult(mResultMap);
      }

	@SuppressLint("NewApi")
	@Override
	public void onPause() {
		Log.d(TAG, "onPause");

		super.onPause();
		if (_nfcAdapter!=null && beamStat==1) {
			_nfcAdapter.disableForegroundDispatch(getActivity());
		}
	}

	@SuppressLint("NewApi")
	public void onResume() {
		Log.d(TAG, "onResume");
		super.onResume();
		if (_nfcAdapter!=null && beamStat==1) {
			recvMsg();

			_nfcAdapter.enableForegroundDispatch(getActivity(), _nfcPendingIntent, _readTagFilters, null);
		}
	}

	@SuppressLint("NewApi")
	public void recvMsg() {
		Log.d(TAG, "recvMsg");
		if (_nfcAdapter == null) {

			Toast.makeText(getActivity().getApplicationContext(), "NFC is not available, please upgrade your mobile", Toast.LENGTH_SHORT).show();

			/*WBase.setTxtDialogParam(R.drawable.alert_dialog_icon,
					"NFC is not available, please update your mobile",
					new OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
						}
					});
			getActivity().showDialog(_WBase.DIALOG_NOTIFY_MESSAGE + dialogIndex);
			dialogIndex++;*/
		} else {

			if (!_nfcAdapter.isEnabled()) {
				Toast.makeText(getActivity().getApplicationContext(), "NFC is closed, please enable it with beam", Toast.LENGTH_SHORT).show();

				/*WBase.setTxtDialogParam(R.drawable.alert_dialog_icon,
						"NFC is closed, please enable it with beam",
						new OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								startActivity(new Intent(
										Settings.ACTION_NFC_SETTINGS));
							}
						});
				getActivity().showDialog(_WBase.DIALOG_NOTIFY_MESSAGE + dialogIndex);
				dialogIndex++;*/
			} else {
				// OK
				if (getActivity().getIntent().getAction() != null) {

					if (getActivity().getIntent().getAction().equals(
							NfcAdapter.ACTION_NDEF_DISCOVERED)) {
						NdefMessage[] msgs = getNdefMessagesFromIntent(getActivity().getIntent());
						NdefRecord record = msgs[0].getRecords()[0];
						byte[] payload = record.getPayload();

						String payloadString = new String(payload);

						//Toast.makeText(getActivity().getApplicationContext(), payloadString, Toast.LENGTH_SHORT).show();
						
						this.content = payloadString;
		    			setResult("slave","ok");

					} else {

					}
				}

				_nfcAdapter.enableForegroundDispatch(getActivity(), _nfcPendingIntent, _readTagFilters, null);

			}
		}
	}
	
	@SuppressLint("NewApi")
	NdefMessage[] getNdefMessagesFromIntent(Intent intent) {
		// Parse the intent
		NdefMessage[] msgs = null;
		String action = intent.getAction();
		if (action.equals(NfcAdapter.ACTION_TAG_DISCOVERED)
				|| action.equals(NfcAdapter.ACTION_NDEF_DISCOVERED)) {
			Parcelable[] rawMsgs = intent
					.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			if (rawMsgs != null) {
				msgs = new NdefMessage[rawMsgs.length];
				for (int i = 0; i < rawMsgs.length; i++) {
					msgs[i] = (NdefMessage) rawMsgs[i];
				}

			} else {
				// Unknown tag type
				byte[] empty = new byte[] {};
				NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN,
						empty, empty, empty);
				NdefMessage msg = new NdefMessage(new NdefRecord[] { record });
				msgs = new NdefMessage[] { msg };
			}

		} else {
			Log.e(TAG, "Unknown intent.");
			finish();
		}
		return msgs;
	}

	@SuppressLint("NewApi")
	@Override
	public void onNewIntent(Intent intent) {
		Log.d(TAG, "onNewIntent");

		if (intent.getAction().equals(NfcAdapter.ACTION_NDEF_DISCOVERED)) {
			NdefMessage[] msgs = getNdefMessagesFromIntent(intent);
			NdefRecord record = msgs[0].getRecords()[0];
			byte[] payload = record.getPayload();

			String payloadString = new String(payload);

			//Toast.makeText(getActivity().getApplicationContext(), payloadString, Toast.LENGTH_SHORT).show();
			
			this.content = payloadString;
			setResult("slave","ok");


		} else if (intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
			Toast.makeText(getActivity().getApplicationContext(), "This NFC tag has no NDEF data.",
					Toast.LENGTH_LONG).show();
		}
	}

	/*public String getResult() {
		return content;
	}*/

	public void setContent(String content) {
		this.content = content;
	}
	Handler mhandler;

	
	@SuppressLint("NewApi")
	@Override
	public void onNdefPushComplete(NfcEvent arg0) {
		Log.d(TAG, "onNdefPushComplete");
		//Toast.makeText(getActivity().getApplicationContext(), "Master", Toast.LENGTH_SHORT).show();

		mhandler.sendEmptyMessage(0);
	}

	@SuppressLint("NewApi")
	@Override
	public NdefMessage createNdefMessage(NfcEvent arg0) {
		String data;
		if (mEditText!=null) {
			data = mEditText.getText().toString();
		} else {
			data = content;
		}
	
		String mimeType = "application/com.hipipal.qpy.nfc";
	
		byte[] mimeBytes = mimeType.getBytes(Charset.forName("UTF-8"));
		byte[] dataBytes = data.getBytes(Charset.forName("UTF-8"));
		byte[] id = new byte[0];
	
		NdefRecord record = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
				mimeBytes, id, dataBytes);
	
		NdefMessage message = new NdefMessage(new NdefRecord[] { record });
	
		return message;
	}
	
	  public boolean onKeyDown(int keyCode, KeyEvent event) {
		  getActivity().finish();
		  return true;
	  }
}
