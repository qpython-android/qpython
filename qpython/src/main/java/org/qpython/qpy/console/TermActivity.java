/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.qpython.qpy.console;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.quseit.util.KeyboardUtils;
import com.quseit.util.NAction;

import org.qpython.qpy.R;
import org.qpython.qpy.console.compont.ActivityCompat;
import org.qpython.qpysdk.utils.AndroidCompat;
import org.qpython.qpy.console.compont.FileCompat;
import org.qpython.qpy.console.compont.MenuItemCompat;
import org.qpython.qpy.console.util.SessionList;
import org.qpython.qpy.console.util.TermSettings;
import org.qpython.qpy.main.activity.NotebookActivity;
import org.qpython.qpy.main.app.App;
import org.qpython.qpy.texteditor.ui.adapter.bean.PopupItemBean;
import org.qpython.qpy.texteditor.ui.view.EditorPopUp;
import org.qpython.qsl4a.qsl4a.StringUtils;

import java.io.File;
import java.io.IOException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import jackpal.androidterm.emulatorview.EmulatorView;
import jackpal.androidterm.emulatorview.TermSession;
import jackpal.androidterm.emulatorview.TermShortcutLayout;
import jackpal.androidterm.emulatorview.UpdateCallback;
import jackpal.androidterm.emulatorview.compat.ClipboardManagerCompat;
import jackpal.androidterm.emulatorview.compat.ClipboardManagerCompatFactory;
import jackpal.androidterm.emulatorview.compat.KeycodeConstants;


/**
 * A terminal emulator activity.
 */

public class TermActivity extends AppCompatActivity implements UpdateCallback, SharedPreferences.OnSharedPreferenceChangeListener {


    public static final  int    REQUEST_CHOOSE_WINDOW             = 1;
    public static final  String EXTRA_WINDOW_ID                   = "jackpal.androidterm.window_id";
    public static final  String ARGS                              = "PYTHONARGS";
    /**
     * The name of the ViewFlipper in the resources.
     */
    private static final int    VIEW_FLIPPER                      = R.id.view_flipper;
    private final static int    SELECT_TEXT_ID                    = 0;
    private final static int    COPY_ALL_ID                       = 1;
    private final static int    PASTE_ID                          = 2;
    private final static int    SEND_CONTROL_KEY_ID               = 3;
    private final static int    SEND_FN_KEY_ID                    = 4;
    // Available on API 12 and later
    private static final int    WIFI_MODE_FULL_HIGH_PERF          = 3;
    private static final String ACTION_PATH_BROADCAST             = "jackpal.androidterm.broadcast.APPEND_TO_PATH";
    private static final String ACTION_PATH_PREPEND_BROADCAST     = "jackpal.androidterm.broadcast.PREPEND_TO_PATH";
    private static final String PERMISSION_PATH_BROADCAST         = "jackpal.androidterm.permission.APPEND_TO_PATH";
    private static final String PERMISSION_PATH_PREPEND_BROADCAST = "jackpal.androidterm.permission.PREPEND_TO_PATH";
    // Available on API 12 and later
    private static final int    FLAG_INCLUDE_STOPPED_PACKAGES     = 0x20;
    /**
     * The ViewFlipper which holds the collection of EmulatorView widgets.
     */
    private TermViewFlipper mViewFlipper;
    private SessionList     mTermSessions;
    private TermSettings    mSettings;
    private boolean mStopServiceOnFinish = false;
    private Intent TSIntent;
    private int onResumeSelectWindow = -1;
    private ComponentName         mPrivateAlias;
    private PowerManager.WakeLock mWakeLock;
    private WifiManager.WifiLock  mWifiLock;
    private boolean               mBackKeyPressed;
    private int mPendingPathBroadcasts = 0;
    private TermService        mTermService;
    //    private ActionBarCompat mActionBar;
    private Toolbar            toolbar;
    private AppBarLayout       toolbarLayout;
    private Spinner            spinner;
    private WindowTitleAdapter adapter;
    private ArrayList<String> windowTitle    = new ArrayList<>();
    private int               mActionBarMode = TermSettings.ACTION_BAR_MODE_ALWAYS_VISIBLE;
    private AlertDialog dialog;
    private EditorPopUp popUp;
    private boolean mHaveFullHwKeyboard = false;
    /**
     * Should we use keyboard shortcuts?
     */
    private boolean mUseKeyboardShortcuts;
    /**
     * Intercepts keys before the view/terminal gets it.
     */
    private View.OnKeyListener mKeyListener = new View.OnKeyListener() {
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            return backkeyInterceptor(keyCode, event) || keyboardShortcuts(keyCode, event);
        }

        /**
         * Keyboard shortcuts (tab management, paste)
         */
        private boolean keyboardShortcuts(int keyCode, KeyEvent event) {
            if (event.getAction() != KeyEvent.ACTION_DOWN) {
                return false;
            }
            if (!mUseKeyboardShortcuts) {
                return false;
            }
            boolean isCtrlPressed = (event.getMetaState() & KeycodeConstants.META_CTRL_ON) != 0;
            boolean isShiftPressed = (event.getMetaState() & KeycodeConstants.META_SHIFT_ON) != 0;

            if (keyCode == KeycodeConstants.KEYCODE_TAB && isCtrlPressed) {
                if (isShiftPressed) {
                    mViewFlipper.showPrevious();
                } else {
                    mViewFlipper.showNext();
                }

                return true;
            } else if (keyCode == KeycodeConstants.KEYCODE_N && isCtrlPressed && isShiftPressed) {
                doCreateNewWindow();

                return true;
            } else if (keyCode == KeycodeConstants.KEYCODE_V && isCtrlPressed && isShiftPressed) {
                doPaste();
                return true;
            } else {
                return false;
            }
        }

        /**
         * Make sure the back button always leaves the application.
         */
        private boolean backkeyInterceptor(int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK /*&& mActionBarMode == TermSettings.ACTION_BAR_MODE_HIDES && mActionBar != null && mActionBar.isShowing()*/) {
                /* We need to intercept the key event before the view sees it,
                   otherwise the view will handle it before we get it */
                onKeyUp(keyCode, event);
                return true;
            } else {
                return false;
            }
        }
    };

    private BroadcastReceiver mPathReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String path = makePathFromBundle(getResultExtras(false));
            if (intent.getAction().equals(ACTION_PATH_PREPEND_BROADCAST)) {
                mSettings.setPrependPath(path);
            } else {
                mSettings.setAppendPath(path);
            }
            mPendingPathBroadcasts--;

            if (mPendingPathBroadcasts <= 0 && mTermService != null) {
                try {
                    populateViewFlipper();
                    populateWindowList();
                } catch (IOException e) {
                    Toast.makeText(context, R.string.fail_resume_console, Toast.LENGTH_SHORT).show();
                }

            }
        }
    };
    private ServiceConnection mTSConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.i(TermDebug.LOG_TAG, "Bound to TermService");
            TermService.TSBinder binder = (TermService.TSBinder) service;
            mTermService = binder.getService();
            if (mPendingPathBroadcasts <= 0) {
                try {
                    populateViewFlipper();
                    populateWindowList();
                } catch (IOException e) {
                    Toast.makeText(binder.getService().getApplicationContext(), R.string.fail_resume_console, Toast.LENGTH_SHORT).show();
                }
            }
        }

        public void onServiceDisconnected(ComponentName arg0) {
            mTermService = null;
        }
    };

    private Handler mHandler = new Handler();

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, TermActivity.class);
        context.startActivity(intent);
    }

    public static void startShell(Context context) {
        Intent intent = new Intent(context, TermActivity.class);
        intent.putExtra("shell_type","shell");
        context.startActivity(intent);
    }

    protected static TermSession createTermSession(Context context, TermSettings settings, String initialCommand, String path) throws IOException {
        GenericTermSession session = new ShellTermSession(context, settings, initialCommand, path);
        session.setProcessExitMessage(context.getString(R.string.process_exit_message));
        return session;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        mSettings.readPrefs(sharedPreferences);
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        // Check if need to hide status bar
        final SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mPrefs.registerOnSharedPreferenceChangeListener(this);
        mSettings = new TermSettings(getResources(), mPrefs);
        if (Build.VERSION.SDK_INT < 16 && mSettings.showStatusBar()) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.term_activity);
        initView();
        mPrivateAlias = new ComponentName(this, RemoteInterface.PRIVACT_ACTIVITY_ALIAS);
        if (icicle == null)
            onNewIntent(getIntent());


        Intent broadcast = new Intent(ACTION_PATH_BROADCAST);
        if (AndroidCompat.SDK >= 12) {
            broadcast.addFlags(FLAG_INCLUDE_STOPPED_PACKAGES);
        }
        mPendingPathBroadcasts++;
        sendOrderedBroadcast(broadcast, PERMISSION_PATH_BROADCAST, mPathReceiver, null, RESULT_OK, null, null);

        broadcast = new Intent(broadcast);
        broadcast.setAction(ACTION_PATH_PREPEND_BROADCAST);
        mPendingPathBroadcasts++;
        sendOrderedBroadcast(broadcast, PERMISSION_PATH_PREPEND_BROADCAST, mPathReceiver, null, RESULT_OK, null, null);

        TSIntent = new Intent(this, TermService.class);
        startService(TSIntent);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, TermDebug.LOG_TAG);
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        int wifiLockMode = WifiManager.WIFI_MODE_FULL;
        if (AndroidCompat.SDK >= 12) {
            wifiLockMode = WIFI_MODE_FULL_HIGH_PERF;
        }
        mWifiLock = wm.createWifiLock(wifiLockMode, TermDebug.LOG_TAG);
        mHaveFullHwKeyboard = checkHaveFullHwKeyboard(getResources().getConfiguration());
        updatePrefs();
        if (!bindService(TSIntent, mTSConnection, BIND_AUTO_CREATE)) {
            throw new IllegalStateException(getString(R.string.failed_bind_term));
        }
        KeyboardUtils.addKeyboardToggleListener(this, isVisible -> setStatusBarVisible(mSettings.showStatusBar()));
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarLayout = (AppBarLayout) findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(v -> closeActivity());

        spinner = (Spinner) findViewById(R.id.spinner_toolbar);
        mViewFlipper = (TermViewFlipper) findViewById(VIEW_FLIPPER);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int oldPosition = mViewFlipper.getDisplayedChild();
                if (position != oldPosition) {
                    if (position >= mViewFlipper.getChildCount()) {
                        mViewFlipper.addView(createEmulatorView(mTermSessions.get(position)));
                    }
                    mViewFlipper.setDisplayedChild(position);
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spinner.setSelection(0);
            }
        });

        TermShortcutLayout shortcutLayout = (TermShortcutLayout) findViewById(R.id.term_shortcut);
        shortcutLayout.setCallback(this::setTextToEmulator);

        findViewById(R.id.notebook).setOnClickListener(v -> {
            NotebookActivity.start(this, null, false);
        });

        //findViewById(R.id.history).setOnClickListener(v -> showHistory());
        findViewById(R.id.switch_notebook_img).setOnClickListener(v -> openNotebook());
    }

    private void openNotebook() {
        startActivity(new Intent(this, NotebookActivity.class));
    }

    @Override
    public void onPause() {
        Log.d("TermActivity", "onPause");
        if (dialog != null) {
            dialog.dismiss();
        }
        super.onPause();

        if (AndroidCompat.SDK < 5) {
            /* If we lose focus between a back key down and a back key up,
               we shouldn't respond to the next back key up event unless
               we get another key down first */
            mBackKeyPressed = false;
        }

        /* Explicitly close the input method
           Otherwise, the soft keyboard could cover up whatever activity takes
           our place */
        final IBinder token = mViewFlipper.getWindowToken();
        new Thread() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(token, 0);
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        Log.d("TermActivity", "onDestroy");
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);

        if (mTermSessions != null) {
            mTermSessions.removeCallback(this);

            if (adapter != null) {
                mTermSessions.removeCallback(adapter);
                mTermSessions.removeTitleChangedListener(adapter);
                mViewFlipper.removeCallback(adapter);
            }
        }

        mViewFlipper.removeAllViews();

        unbindService(mTSConnection);
        //LogUtil.d("TermActivity", "mStopServiceOnFinish:"+mStopServiceOnFinish);

        if (mStopServiceOnFinish) {
            stopService(TSIntent);
        }
        //LogUtil.d("TermActivity", "HERE");
        mTermService = null;
        mTSConnection = null;
        if (mWakeLock.isHeld()) {
            mWakeLock.release();
        }
        if (mWifiLock.isHeld()) {
            mWifiLock.release();
        }

        mTermSessions = null;

        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.cancel();
            } else {
            }
            dialog.dismiss();

        }
        if (popUp!=null) {
            popUp.dismiss();
        }

        KeyboardUtils.removeAllKeyboardToggleListeners();

        super.onDestroy();
    }


    private String makePathFromBundle(Bundle extras) {
        if (extras == null || extras.size() == 0) {
            return "";
        }

        String[] keys = new String[extras.size()];
        keys = extras.keySet().toArray(keys);
        Collator collator = Collator.getInstance(Locale.US);
        Arrays.sort(keys, collator);

        StringBuilder path = new StringBuilder();
        for (String key : keys) {
            String dir = extras.getString(key);
            if (dir != null && !dir.equals("")) {
                path.append(dir);
                path.append(":");
            }
        }

        return path.substring(0, path.length() - 1);
    }

    private void populateViewFlipper() throws IOException {
        Log.d("TermActivity", "populateViewFlipper");
        if (mTermService != null) {
            mTermSessions = mTermService.getSessions();
            String[] mArgs = this.getIntent().getStringArrayExtra(ARGS);
            if (mArgs != null) {
                mTermSessions.add(createPyTermSession(mArgs));
            } else {
                mArgs = this.getIntent().getStringArrayExtra("ARGS");
                if (mArgs != null) {
                    mTermSessions.add(createPyTermSession(mArgs));

                } else if (mTermSessions.size() == 0) {
                    mTermSessions.add(createPyTermSession(null));
                }
            }
            //存在内存泄漏
            mTermSessions.addCallback(this);
            for (TermSession session : mTermSessions) {
                Log.d("TermActivity", "createEmulatorView:" + session);
                EmulatorView view = createEmulatorView(session);
                mViewFlipper.addView(view);
            }
            mViewFlipper.setDisplayedChild(mTermSessions.size() - 1);
            updatePrefs();

            if (onResumeSelectWindow >= 0) {
                mViewFlipper.setDisplayedChild(onResumeSelectWindow);
                onResumeSelectWindow = -1;
            }
            mViewFlipper.onResume();
        }
    }

    private void populateWindowList() {
        if (mTermSessions != null) {
            int position = mViewFlipper.getDisplayedChild();
            for (int i = 0; i < mTermSessions.size(); i++) {
                windowTitle.add(getString(R.string.window_title, i));
            }
            adapter = new WindowTitleAdapter(this, mTermSessions);
            spinner.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            mViewFlipper.addCallback(adapter);
            spinner.setSelection(position);
        }
    }

    private TermView createEmulatorView(TermSession session) {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        TermView emulatorView = new TermView(this, session, metrics);

        emulatorView.setExtGestureListener(new EmulatorViewGestureListener(emulatorView));
        emulatorView.setOnKeyListener(mKeyListener);
        registerForContextMenu(emulatorView);

        return emulatorView;
    }

    private TermSession getCurrentTermSession() {
        SessionList sessions = mTermSessions;
        if (sessions == null) {
            return null;
        } else {
            return sessions.get(mViewFlipper.getDisplayedChild());
        }
    }

    private EmulatorView getCurrentEmulatorView() {
        return (EmulatorView) mViewFlipper.getCurrentView();
    }

    private void updatePrefs() {
        mUseKeyboardShortcuts = mSettings.getUseKeyboardShortcutsFlag();

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        mViewFlipper.updatePrefs(mSettings);

        for (View v : mViewFlipper) {
            ((EmulatorView) v).setDensity(metrics);
            ((TermView) v).updatePrefs(mSettings);
        }

        if (mTermSessions != null) {
            for (TermSession session : mTermSessions) {
                ((GenericTermSession) session).updatePrefs(mSettings);
            }
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            setStatusBarVisible(mSettings.showStatusBar());
        } else {
            Toast.makeText(App.getContext(), R.string.under_16_status_bar_hint, Toast.LENGTH_SHORT).show();
        }
        mActionBarMode = mSettings.actionBarMode();
        switch (mActionBarMode) {
            case TermSettings.ACTION_BAR_MODE_ALWAYS_VISIBLE:
                toolbarLayout.setVisibility(View.VISIBLE);
                break;
            case TermSettings.ACTION_BAR_MODE_HIDES:
                toolbarLayout.setVisibility(View.GONE);
                break;
        }
        int orientation = mSettings.getScreenOrientation();
        int o = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        if (orientation == 0) {
            o = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
        } else if (orientation == 1) {
            o = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        } else if (orientation == 2) {
            o = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        } else {
            /* Shouldn't be happened. */
        }
        setRequestedOrientation(o);
    }

    private void setStatusBarVisible(boolean visible) {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(visible ? View.SYSTEM_UI_FLAG_VISIBLE : View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    private boolean checkHaveFullHwKeyboard(Configuration c) {
        return (c.keyboard == Configuration.KEYBOARD_QWERTY) &&
                (c.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        mHaveFullHwKeyboard = checkHaveFullHwKeyboard(newConfig);

        EmulatorView v = (EmulatorView) mViewFlipper.getCurrentView();
        if (v != null) {
            v.updateSize(false);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_console_menu, menu);
        MenuItemCompat.setShowAsAction(menu.findItem(R.id.menu_new_window), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_new_window:
                doCreateNewWindow();
                break;
            case R.id.menu_new_more:
                if (popUp == null) {
                    List<PopupItemBean> popupItemBeans = new ArrayList<>();
                    popupItemBeans.add(new PopupItemBean(getString(R.string.window_list), v ->
                            startActivityForResult(new Intent(this, WindowList.class), REQUEST_CHOOSE_WINDOW)));
                    popupItemBeans.add(new PopupItemBean(getString(R.string.preferences), v -> doPreferences()));
                    popupItemBeans.add(new PopupItemBean(getString(R.string.send_email), v -> doEmailTranscript()));
                    popupItemBeans.add(new PopupItemBean(getString(R.string.enable_wakelock), v -> doToggleWakeLock())
                            .setTitle2(getString(R.string.disable_wakelock)));
                    popupItemBeans.add(new PopupItemBean(getString(R.string.enable_wifilock), v -> doToggleWifiLock())
                            .setTitle2(getString(R.string.disable_wifilock)));
                    popUp = new EditorPopUp(this, popupItemBeans);
                }
                popUp.show(findViewById(R.id.toolbar));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void doCreateNewWindow() {
        Log.d("TermActivity", "doCreateNewWindow");

        if (mTermSessions == null) {
            Log.w(TermDebug.LOG_TAG, "Couldn't create new window because mTermSessions == null");
            return;
        }

        try {
            TermSession session = createPyTermSession(null);

            mTermSessions.add(session);

            TermView view = createEmulatorView(session);
            view.updatePrefs(mSettings);

            mViewFlipper.addView(view);
            int position = mViewFlipper.getChildCount() - 1;
            mViewFlipper.setDisplayedChild(position);
            spinner.setSelection(position);
        } catch (IOException e) {
            Toast.makeText(this, "Failed to create a session", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmCloseWindow() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setIcon(android.R.drawable.ic_dialog_alert);
        b.setMessage(R.string.confirm_window_close_message);
        final Runnable closeWindow = this::doCloseWindow;
        b.setPositiveButton(android.R.string.yes, (dialog1, id) -> {
            dialog1.dismiss();
            mHandler.post(closeWindow);
        });
        b.setNegativeButton(android.R.string.no, (dialog1, id) -> {
            dialog1.dismiss();
        });
        dialog = b.create();
        dialog.show();
    }

    private void doCloseWindow() {
        if (mTermSessions == null) {
            return;
        }

        EmulatorView view = getCurrentEmulatorView();
        if (view == null) {
            return;
        }
        TermSession session = mTermSessions.remove(mViewFlipper.getDisplayedChild());
        view.onPause();
        session.finish();
        mViewFlipper.removeView(view);
        if (mTermSessions.size() != 0) {
            mViewFlipper.showNext();
        }
    }

    @Override
    protected void onActivityResult(int request, int result, Intent data) {
        switch (request) {
            case REQUEST_CHOOSE_WINDOW:
                if (result == RESULT_OK && data != null) {
                    int position = data.getIntExtra(EXTRA_WINDOW_ID, -2);
                    if (position >= 0) {
                        // Switch windows after session list is in sync, not here
                        onResumeSelectWindow = position;
                    } else if (position == -1) {
                        doCreateNewWindow();
                        if (mTermSessions == null) {
                            Log.w(TermDebug.LOG_TAG, "Couldn't create new window because mTermSessions == null");
                            return;
                        }
                        onResumeSelectWindow = mTermSessions.size() - 1;
                    }
                } else {
                    // Close the activity if user closed all sessions
                    // TODO the left path will be invoked when nothing happened, but this Activity was destroyed!
                    if (mTermSessions == null || mTermSessions.size() == 0) {
                        mStopServiceOnFinish = true;
                        finish();
                    }
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePrefs();
    }

    private void setTextToEmulator(String text) {
        if (mViewFlipper.getCurrentView() != null) {
            if (text!=null && text.equals("[tab]")) {
                Log.d("TermAcity", "setTextToEmulator:"+text);

                //((EmulatorView) mViewFlipper.getCurrentView()).sendTabKey();

                ((EmulatorView) mViewFlipper.getCurrentView()).setTextBuff("\t");
                ((EmulatorView) mViewFlipper.getCurrentView()).getTermSession().write("\t");
                ((EmulatorView) mViewFlipper.getCurrentView()).getTermSession().notifyUpdate();


            } else {
                ((EmulatorView) mViewFlipper.getCurrentView()).setTextBuff(text);
                ((EmulatorView) mViewFlipper.getCurrentView()).getTermSession().write(text);
                ((EmulatorView) mViewFlipper.getCurrentView()).getTermSession().notifyUpdate();
            }
        }
    }
/*
    private void showHistory() {
        if (mViewFlipper.getCurrentView() != null) {
            //LinkedList<String> history = ((EmulatorView) mViewFlipper.getCurrentView()).getEmulator().getHistory();
            LinkedList<String> history = ((EmulatorView) mViewFlipper.getCurrentView()).getHistoryCmd();
            if (history == null || history.size() == 0) {
                Toast.makeText(mTermService, R.string.history_emp, Toast.LENGTH_SHORT).show();
            } else {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.enter_history)
                        .setNegativeButton(R.string.close, (dialog1, which) -> dialog1.dismiss())
                        .setAdapter(new ArrayAdapter<>(this, R.layout.dialog_history, history), (dialog1, which) -> {
                            dialog1.dismiss();
                            ((EmulatorView) mViewFlipper.getCurrentView()).setTextBuff(history.get(which));
                            ((EmulatorView) mViewFlipper.getCurrentView()).getTermSession().write(history.get(which));
                            ((EmulatorView) mViewFlipper.getCurrentView()).getTermSession().notifyUpdate();
                        })
                        .show();
            }
        }
    }
*/
    @Override
    protected void onNewIntent(Intent intent) {
        if ((intent.getFlags() & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) != 0) {
            // Don't repeat action if intent comes from history
            return;
        }

        String action = intent.getAction();
        if (TextUtils.isEmpty(action) || !mPrivateAlias.equals(intent.getComponent())) {
            return;
        }

        // huge number simply opens new window
        // TODO: add a way to restrict max number of windows per caller (possibly via reusing BoundSession)
        switch (action) {
            case RemoteInterface.PRIVACT_OPEN_NEW_WINDOW:
                onResumeSelectWindow = Integer.MAX_VALUE;
                break;
            case RemoteInterface.PRIVACT_SWITCH_WINDOW:
                int target = intent.getIntExtra(RemoteInterface.PRIVEXTRA_TARGET_WINDOW, -1);
                if (target >= 0) {
                    onResumeSelectWindow = target;
                }
                break;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle(R.string.edit_text);
        menu.add(0, SELECT_TEXT_ID, 0, R.string.select_text);
        menu.add(0, COPY_ALL_ID, 0, R.string.copy_all);
        menu.add(0, PASTE_ID, 0, R.string.paste);
        menu.add(0, SEND_CONTROL_KEY_ID, 0, R.string.send_control_key);
        menu.add(0, SEND_FN_KEY_ID, 0, R.string.send_fn_key);
        if (!canPaste()) {
            menu.getItem(PASTE_ID).setEnabled(false);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Log.d("TermActivity", "onContextItemSelected:"+item.getItemId());
        switch (item.getItemId()) {
            case SELECT_TEXT_ID:
                getCurrentEmulatorView().toggleSelectingText();
                return true;
            case COPY_ALL_ID:
                doCopyAll();
                return true;
            case PASTE_ID:
                doPaste();
                return true;
            case SEND_CONTROL_KEY_ID:
                doSendControlKey();
                return true;
            case SEND_FN_KEY_ID:
                doSendFnKey();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("TermActivity", "onKeyDown");
        /* The pre-Eclair default implementation of onKeyDown() would prevent
           our handling of the Back key in onKeyUp() from taking effect, so
           ignore it here */
        if (AndroidCompat.SDK < 5 && keyCode == KeyEvent.KEYCODE_BACK) {
            /* Android pre-Eclair has no key event tracking, and a back key
               down event delivered to an activity above us in the back stack
               could be succeeded by a back key up event to us, so we need to
               keep track of our own back key presses */
            mBackKeyPressed = true;
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.d("TermActivity", "onKeyUp:"+keyCode);

        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (AndroidCompat.SDK < 5) {
                    if (!mBackKeyPressed) {
                    /* This key up event might correspond to a key down
                       delivered to another activity -- ignore */
                        return false;
                    }
                    mBackKeyPressed = false;
                }
//                if (mActionBarMode == TermSettings.ACTION_BAR_MODE_HIDES && mActionBar != null && mActionBar.isShowing()) {
//                    mActionBar.hide();
//                    return true;
//                }
                switch (mSettings.getBackKeyAction()) {
                    case TermSettings.BACK_KEY_STOPS_SERVICE:
                        mStopServiceOnFinish = true;
                        break;
                    case TermSettings.BACK_KEY_CLOSES_ACTIVITY:
                        closeActivity();
                        return true;
                    case TermSettings.BACK_KEY_CLOSES_WINDOW:
                        doCloseWindow();
                        return true;
                    default:
                        return false;
                }
                break;
            case KeyEvent.KEYCODE_MENU:
                return super.onKeyUp(keyCode, event);
            default:
                return super.onKeyUp(keyCode, event);
        }
        return super.onKeyUp(keyCode, event);

    }

    private void closeActivity() {
        //LogUtil.d("TermActivity", "closeActivity");
        try {
            if (dialog!=null) {
                dialog.dismiss();
            }
            dialog = new AlertDialog.Builder(this, R.style.MyDialog)
                    .setTitle(R.string.run_bg_title)
                    .setMessage(R.string.terminal_bg_hint)
                    .setPositiveButton(R.string.yes, ((dialog1, which) -> {
                        dialog1.dismiss();
                        finish();
                    }))
                    .setNegativeButton(R.string.no, ((dialog1, which) -> {
                        dialog1.dismiss();
//                        //在关闭当前窗口时mTermSessions可能还未初始化完成
                        if (mTermSessions != null) {
                            for (int i = mTermSessions.size(); i > 0; i--) {

                                mTermSessions.get(i - 1).finish();

                            }
                        }
                        finish();
                    }))
                    .create();
            dialog.show();
        } catch (Exception e) {

        }
    }

    // Called when the list of sessions changes
    public void onUpdate() {
        SessionList sessions = mTermSessions;
        if (sessions == null) {
            return;
        }

        if (sessions.size() == 0) {
            mStopServiceOnFinish = true;
            finish();
        } else if (sessions.size() < mViewFlipper.getChildCount()) {
            for (int i = 0; i < mViewFlipper.getChildCount(); ++i) {
                EmulatorView v = (EmulatorView) mViewFlipper.getChildAt(i);
                if (!sessions.contains(v.getTermSession())) {
                    v.onPause();
                    mViewFlipper.removeView(v);
                    --i;
                }
            }
        }
    }

    private boolean canPaste() {
        ClipboardManagerCompat clip = ClipboardManagerCompatFactory
                .getManager(getApplicationContext());
        if (clip.hasText()) {
            return true;
        }
        return false;
    }

    private void doPreferences() {
        startActivity(new Intent(this, TermPreferences.class));
    }

    private void doResetTerminal() {
        TermSession session = getCurrentTermSession();
        if (session != null) {
            session.reset();
        }
    }

    private void doEmailTranscript() {
        TermSession session = getCurrentTermSession();
        if (session != null) {
            // Don't really want to supply an address, but
            // currently it's required, otherwise nobody
            // wants to handle the intent.
            String addr = "support@qpython.org";
            Intent intent =
                    new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"
                            + addr));

            String subject = getString(R.string.email_transcript_subject);
            String title = session.getTitle();
            if (title != null) {
                subject = subject + " - " + title;
            }
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            intent.putExtra(Intent.EXTRA_TEXT,
                    session.getTranscriptText().trim());
            try {
                startActivity(Intent.createChooser(intent,
                        getString(R.string.email_transcript_chooser_title)));
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this,
                        R.string.email_transcript_no_email_activity_found,
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void doCopyAll() {
        ClipboardManagerCompat clip = ClipboardManagerCompatFactory
                .getManager(getApplicationContext());
        clip.setText(getCurrentTermSession().getTranscriptText().trim());
    }

    private void doPaste() {
        if (!canPaste()) {
            return;
        }
        ClipboardManagerCompat clip = ClipboardManagerCompatFactory
                .getManager(getApplicationContext());
        CharSequence paste = clip.getText();
        getCurrentTermSession().write(paste.toString());
    }

    private void doSendTabKey() {
        getCurrentEmulatorView().sendTabKey();
    }

    private void doSendControlKey() {
        getCurrentEmulatorView().sendControlKey();
    }

    private void doSendFnKey() {
        getCurrentEmulatorView().sendFnKey();
    }

    private void doDocumentKeys() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Resources r = getResources();
        builder.setTitle(r.getString(R.string.control_key_dialog_title));
        builder.setMessage(
                formatMessage(mSettings.getControlKeyId(), TermSettings.CONTROL_KEY_ID_NONE,
                        r, R.array.control_keys_short_names,
                        R.string.control_key_dialog_control_text,
                        R.string.control_key_dialog_control_disabled_text, "CTRLKEY")
                        + "\n\n" +
                        formatMessage(mSettings.getFnKeyId(), TermSettings.FN_KEY_ID_NONE,
                                r, R.array.fn_keys_short_names,
                                R.string.control_key_dialog_fn_text,
                                R.string.control_key_dialog_fn_disabled_text, "FNKEY"));
        dialog = builder.create();
        dialog.show();
    }

    private String formatMessage(int keyId, int disabledKeyId,
                                 Resources r, int arrayId,
                                 int enabledId,
                                 int disabledId, String regex) {
        if (keyId == disabledKeyId) {
            return r.getString(disabledId);
        }
        String[] keyNames = r.getStringArray(arrayId);
        String keyName = keyNames[keyId];
        String template = r.getString(enabledId);
        String result = template.replaceAll(regex, keyName);
        return result;
    }

    private void doToggleSoftKeyboard() {
        if (!mViewFlipper.getCurrentView().hasFocus()) {
            mViewFlipper.getCurrentView().requestFocus();
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(mViewFlipper.getCurrentView(), 0);
        } else {
            mViewFlipper.getCurrentView().clearFocus();
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }

    }

    private void doToggleWakeLock() {
        if (mWakeLock.isHeld()) {
            mWakeLock.release();
        } else {
            mWakeLock.acquire();
        }
        ActivityCompat.invalidateOptionsMenu(this);
    }

    private void doToggleWifiLock() {
        if (mWifiLock.isHeld()) {
            mWifiLock.release();
        } else {
            mWifiLock.acquire();
        }
        ActivityCompat.invalidateOptionsMenu(this);
    }

    private void doToggleActionBar() {
        if (toolbarLayout.getVisibility() == View.VISIBLE) {
            toolbarLayout.setVisibility(View.GONE);
        } else {
            toolbarLayout.setVisibility(View.VISIBLE);
        }
    }

    private void doUIToggle(int x, int y, int width, int height) {
        switch (mActionBarMode) {
            case TermSettings.ACTION_BAR_MODE_NONE:
                if (AndroidCompat.SDK >= 11 && (mHaveFullHwKeyboard || y < height / 2)) {
                    openOptionsMenu();
                    return;
                } else {
                    doToggleSoftKeyboard();
                }
                break;
            case TermSettings.ACTION_BAR_MODE_ALWAYS_VISIBLE:
                if (!mHaveFullHwKeyboard) {
                    doToggleSoftKeyboard();
                }
                break;
            case TermSettings.ACTION_BAR_MODE_HIDES:
                if (mHaveFullHwKeyboard || y < height / 2) {
                    doToggleActionBar();
                    return;
                } else {
                    doToggleSoftKeyboard();
                }
                break;
        }
        getCurrentEmulatorView().requestFocus();
    }

    /**
     * Send a URL up to Android to be handled by a browser.
     *
     * @param link The URL to be opened.
     */
    private void execURL(String link) {
        Uri webLink = Uri.parse(link);
        Intent openLink = new Intent(Intent.ACTION_VIEW, webLink);
        PackageManager pm = getPackageManager();
        List<ResolveInfo> handlers = pm.queryIntentActivities(openLink, 0);
        if (handlers.size() > 0)
            startActivity(openLink);
    }

    private TermSession createPyTermSession(String[] mArgs) throws IOException {
        Log.d("TermActivity", "createPyTermSession:" + mArgs);
        TermSettings settings = mSettings;
        TermSession session;
        Intent intent = this.getIntent();
        String shell_type = intent.getStringExtra("shell_type");
        if (shell_type!=null && shell_type.equals("shell")) {

            session = createTermSession(this, settings, "cd $HOME && cat SHELL", this.getFilesDir().getAbsolutePath());

        } else {

            if (mArgs == null) {
                String scmd = getScmd() + " && exit";
                session = createTermSession(this, settings, scmd, "");
            } else {
                //String content = FileHelper.getFileContents(mArgs[0]);
                //String cmd = settings.getInitialCommand().equals("")?scmd:settings.getInitialCommand();
                String scmd = getScmd();
                if (mArgs.length < 3) {
                    session = createTermSession(this, settings, scmd + " \"" + StringUtils.addSlashes(mArgs[0]) + "\" && exit", mArgs[1]);
                } else {
                    StringBuilder cm = new StringBuilder();
                    for (int i = 0; i < mArgs.length; i++) {
                        if (i == 1 && mArgs[0].contains(mArgs[i])) continue;
                        cm.append(" ").append(mArgs[i]).append(" ");
                    }
                    session = createTermSession(this, settings, scmd + " " + cm + " && exit", mArgs[1]);
                }
            }
        }
        // TODO: INIT.SH
        //initPyInit();

        session.setFinishCallback(mTermService);
        return session;
    }

    private String getScmd() {
        boolean isRootEnable = NAction.isRootEnable(this);
        String scmd;
        if (Build.VERSION.SDK_INT >= 20) {
            if (isRootEnable) {
                scmd = getApplicationContext().getFilesDir() + "/bin/qpython" + (NAction.isQPy3(this) ? "3" : "") + "-android5-root.sh";
            } else {
                scmd = getApplicationContext().getFilesDir() + "/bin/qpython" + (NAction.isQPy3(this) ? "3" : "") + "-android5.sh";
            }
        } else {
            if (isRootEnable) {
                scmd = getApplicationContext().getFilesDir() + "/bin/qpython" + (NAction.isQPy3(this) ? "3" : "") + "-root.sh";
            } else {
                scmd = getApplicationContext().getFilesDir() + "/bin/qpython" + (NAction.isQPy3(this) ? "3" : "") + ".sh";

            }
        }
        return scmd;
    }

    private String checkPath(String path) {
        String[] dirs = path.split(":");
        StringBuilder checkedPath = new StringBuilder(path.length());
        for (String dirname : dirs) {
            File dir = new File(dirname);
            if (dir.isDirectory() && FileCompat.canExecute(dir)) {
                checkedPath.append(dirname);
                checkedPath.append(":");
            }
        }
        return checkedPath.substring(0, checkedPath.length() - 1);
    }

    private class EmulatorViewGestureListener extends SimpleOnGestureListener {
        private EmulatorView view;

        EmulatorViewGestureListener(EmulatorView view) {
            this.view = view;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            // Let the EmulatorView handle taps if mouse tracking is active
            if (view.isMouseTrackingActive()) return false;

            //Check for link at tap location
            String link = view.getURLat(e.getX(), e.getY());
            if (link != null)
                execURL(link);
            else
                doUIToggle((int) e.getX(), (int) e.getY(), view.getVisibleWidth(), view.getVisibleHeight());
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float absVelocityX = Math.abs(velocityX);
            float absVelocityY = Math.abs(velocityY);
            if (absVelocityX > Math.max(1000.0f, 2.0 * absVelocityY)) {
                // Assume user wanted side to side movement
                int windowCount = spinner.getCount();
                int index = spinner.getSelectedItemPosition();
                if (velocityX > 0) {
                    // Left to right swipe -- previous window
                    mViewFlipper.showPrevious();
                    spinner.setSelection(index - 1 >= 0 ? spinner.getSelectedItemPosition() - 1 : windowCount);
                } else {
                    // Right to left swipe -- next window
                    mViewFlipper.showNext();
                    spinner.setSelection(index + 1 < windowCount ? index + 1 : 0);
                }
                return true;
            } else {
                return false;
            }
        }
    }

    private class WindowTitleAdapter extends ArrayAdapter<TermSession> implements UpdateCallback {

        WindowTitleAdapter(@NonNull Context context, List<TermSession> dataList) {
            super(context, android.R.layout.simple_spinner_item, dataList);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            TextView tv = new TextView(parent.getContext());
            tv.setText(getString(R.string.window_title, position + 1));
            tv.setTextAppearance(parent.getContext(), R.style.toolbar_text);
            return tv;
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.window_list_item, null);
            TextView tv = (TextView) convertView.findViewById(R.id.window_list_label);
            tv.setText(getString(R.string.window_title, position + 1));
            convertView.findViewById(R.id.window_list_close).setOnClickListener(v -> {
                TermSession session = mTermSessions.remove(position);
                if (session != null) {
                    session.finish();
                    notifyDataSetChanged();
                }
            });
            return convertView;
        }

        @Override
        public void onUpdate() {
            notifyDataSetChanged();
        }
    }
}
