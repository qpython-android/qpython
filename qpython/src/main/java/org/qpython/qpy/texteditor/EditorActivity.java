package org.qpython.qpy.texteditor;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;

import com.quseit.util.FileHelper;
import com.quseit.util.FileUtils;
import com.quseit.util.NAction;
import com.quseit.util.NStorage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.qpython.qpy.R;
import org.qpython.qpy.console.ScriptExec;
import org.qpython.qpy.databinding.DrawerEditorBinding;
import org.qpython.qpy.main.activity.BaseActivity;
import org.qpython.qpy.main.activity.GistEditActivity;
import org.qpython.qpy.main.adapter.EditorFileTreeAdapter;
import org.qpython.qpy.main.app.App;
import org.qpython.qpy.texteditor.common.RecentFiles;
import org.qpython.qpy.texteditor.common.Settings;
import org.qpython.qpy.texteditor.common.TextFileUtils;
import org.qpython.qpy.texteditor.ui.adapter.bean.PopupItemBean;
import org.qpython.qpy.texteditor.ui.view.EditorPopUp;
import org.qpython.qpy.texteditor.ui.view.EnterDialog;
import org.qpython.qpy.texteditor.widget.crouton.Crouton;
import org.qpython.qpy.texteditor.widget.crouton.Style;
import org.qpython.qpysdk.QPyConstants;
import org.qpython.qpysdk.utils.Utils;
import org.swiftp.Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.quseit.util.FileHelper.LoadDataFromAssets;
import static com.quseit.util.FileHelper.writeToFile;
import static org.qpython.qpy.texteditor.TedFragment.LOGIN_REQUEST_CODE;
import static org.qpython.qpy.texteditor.TedLocalActivity.REQUEST_OPEN;
import static org.qpython.qpy.texteditor.TedLocalActivity.REQUEST_RECENT;
import static org.qpython.qpy.texteditor.TedLocalActivity.REQUEST_SAVE_AS;
import static org.qpython.qpy.texteditor.androidlib.data.FileUtils.deleteItem;
import static org.qpython.qpy.texteditor.androidlib.data.FileUtils.getCanonizePath;
import static org.qpython.qpy.texteditor.androidlib.data.FileUtils.renameItem;
import static org.qpython.qpy.texteditor.common.Constants.ACTION_WIDGET_OPEN;
import static org.qpython.qpy.texteditor.common.Constants.EXTRA_FORCE_READ_ONLY;

public class EditorActivity extends BaseActivity implements ViewTreeObserver.OnGlobalLayoutListener {
    private DrawerEditorBinding   binding;
    private TedFragment           textFragment;
    private EditorFileTreeAdapter fileTreeAdapter;
    private final String TAG = "EditorActivity";

    private static final int OPEN_REQUEST_CODE = 5233;

    private static final String DEFAULT_ACTION = "empty";
    private static final String TEXT_ACTION    = "text";
    private static final String QR_CODE_ACTION = "qrcode";
    private static final String FILE_ACTION    = "file";
    private static final String PROJECT_ACTION = "project";

    private static final String EXTRA_PROJECT_PATH = "project_path";
    private static final String EXTRA_TEXT         = "EXTRA_TEXT";
    public static final  String EXTRA_TITLE        = "EXTRA_TITLE";

    protected String mCurrentFilePath;
    protected String mCurrentFileName;

    protected boolean mReadOnly;
    protected boolean mDirty;
    protected boolean isKeyboardVisible;

    private boolean isDrawerShowing = false;
    private String  projPath        = "";

    private EditorPopUp editorPopUp;

    private int height;

    public static void start(Context context) {
        Intent starter = new Intent(context, EditorActivity.class);
        starter.setAction(DEFAULT_ACTION);
        context.startActivity(starter);
    }

    public static void start(Context context, String text) {
        Intent starter = new Intent(context, EditorActivity.class);
        starter.putExtra(EXTRA_TEXT, text);
        starter.setAction(TEXT_ACTION);
        context.startActivity(starter);
    }

    public static void start(Context context, String text, String title) {
        Intent starter = new Intent(context, EditorActivity.class);
        starter.putExtra(EXTRA_TEXT, text);
        starter.putExtra(EXTRA_TITLE, title);
        starter.setAction(QR_CODE_ACTION);
        context.startActivity(starter);
    }

    public static void start(Context context, Uri path) {
        Intent starter = new Intent(context, EditorActivity.class);
        starter.setAction(FILE_ACTION);
        starter.setData(path);
        context.startActivity(starter);
    }

    public static void start(String projectPath, Context context) {
        Intent starter = new Intent(context, EditorActivity.class);
        starter.putExtra(EXTRA_PROJECT_PATH, projectPath);
        starter.setAction(PROJECT_ACTION);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        binding = DataBindingUtil.setContentView(this, R.layout.drawer_editor);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;

        initView();
        initListener();
        updateSetting();
        initFiles();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (needLog && event != null) {
            showLogDialog(event);
            needLog = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOpenFileEvent(OpenFileEvent openFileEvent) {
        binding.drawerLayout.closeDrawer(Gravity.START);
        if (openFileEvent.filePath == null) {
            Crouton.showText(this, R.string.not_support_yet, Style.ALERT);

            return;
        }
        mCurrentFilePath = openFileEvent.filePath;
        mCurrentFileName = new File(openFileEvent.filePath).getName();
        doOpenFile(new File(mCurrentFilePath), mReadOnly);
    }

    public static class OpenFileEvent {
        public String filePath;
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("LAST_OPEN_PATH", mCurrentFilePath);
        editor.apply();
    }

    private void initView() {
        setSupportActionBar(binding.lt.toolbar);
        binding.lt.toolbar.setNavigationIcon(R.drawable.ic_back);

        switch (getIntent().getAction() == null ? DEFAULT_ACTION : getIntent().getAction()) {
            case DEFAULT_ACTION:
                textFragment = TedFragment.newInstance(openLastFile());
                if (!projPath.isEmpty()) {
                    fileTreeAdapter = new EditorFileTreeAdapter(projPath);
                    binding.leftDrawer.setLayoutManager(new LinearLayoutManager(this));
                    binding.leftDrawer.setAdapter(fileTreeAdapter);
                } else {
                    binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                }
                break;
            case TEXT_ACTION:
                textFragment = TedFragment.newInstance(getIntent().getStringExtra(EXTRA_TEXT));
                binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                mDirty = true;
                break;
            case QR_CODE_ACTION:
                textFragment = TedFragment.newInstance(getIntent().getStringExtra(EXTRA_TEXT));
                binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                mCurrentFileName = getIntent().getStringExtra(EXTRA_TITLE);
                mDirty = true;
                break;
            case FILE_ACTION:
                try {
                    String uri = getIntent().getDataString();
                    if (uri != null) {
                        File file = new File(new URI(uri));
                        textFragment = TedFragment.newInstance(doOpenFile(file, false));
                        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    } else {
                        Crouton.showText(this, R.string.toast_intent_invalid_uri, Style.ALERT);

                        return;
                    }
                } catch (URISyntaxException ignore) {
                }
                break;
            case PROJECT_ACTION:
                projPath = getIntent().getStringExtra(EXTRA_PROJECT_PATH);
                fileTreeAdapter = new EditorFileTreeAdapter(projPath);
                binding.leftDrawer.setLayoutManager(new LinearLayoutManager(this));
                binding.leftDrawer.setAdapter(fileTreeAdapter);
                break;
            case Intent.ACTION_VIEW:
            case Intent.ACTION_EDIT:
                if (textFragment == null) textFragment = TedFragment.newInstance("");
                binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                Uri uri = getIntent().getData();
                if (uri == null) {
                    doDefaultAction();
                } else if ("file".equals(uri.getScheme())) {
                    doOpenFile(new File(uri.getPath()), false);
                } else if ("content".equals(uri.getScheme())) {
                    //LogUtil.d(TAG, "");
                    openFromOther(uri);
                }
                break;
            case ACTION_WIDGET_OPEN:
                if (textFragment == null) textFragment = TedFragment.newInstance("");
                try {
                    binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    File file = new File(new URI(getIntent().getData().toString()));
                    doOpenFile(file, getIntent().getBooleanExtra(EXTRA_FORCE_READ_ONLY, false));
                } catch (URISyntaxException e) {
                    Crouton.showText(this, R.string.toast_intent_invalid_uri, Style.ALERT);
                    return;
                } catch (IllegalArgumentException e) {
                    Crouton.showText(this, R.string.toast_intent_illegal, Style.ALERT);
                    return;
                }
                break;
        }

        if (textFragment == null) {
            textFragment = TedFragment.newInstance(openLastFile());
        }
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.content_frame, textFragment, "TED")
                .commit();
        updateTitle();
    }

    private void initListener() {
        binding.lt.toolbar.setNavigationOnClickListener((View v) -> {
            if (isDrawerShowing) {
                binding.drawerLayout.closeDrawer(Gravity.START);
            } else {
                finishEdit();
            }
        });

        binding.drawerLayout.addDrawerListener(new ActionBarDrawerToggle(this, binding.drawerLayout, R.string.toolbar_showing, R.string.toolbar_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                isDrawerShowing = true;
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                isDrawerShowing = false;
            }
        });
    }


    private void openFromOther(Uri uri) {
        Cursor returnCursor =
                getContentResolver().query(uri, null, null, null, null);
        try {
            returnCursor.moveToFirst();
            int column_index = returnCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);//Instead of "MediaStore.Images.Media.DATA" can be used "_data"
            //表中可能没有数据
            if (null == returnCursor.getString(column_index)) {
                return;
            }
            Uri filePathUri = Uri.parse(returnCursor.getString(column_index));
            String path = filePathUri.getPath();
            doOpenFile(new File(path), false);
        } catch (IllegalArgumentException e) {
            Crouton.showText(this, R.string.read_only, Style.INFO);
            InputStream inputStream = null;
            try {
                inputStream = getContentResolver().openInputStream(uri);
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder total = new StringBuilder();
            String line;
            try {
                while ((line = r.readLine()) != null) {
                    total.append(line).append('\n');
                }
                mCurrentFileName = returnCursor.getString(returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)).replace("[", "").replace("]", "");
                mReadOnly = true;
                textFragment = TedFragment.newInstance(total.toString());
                mReadOnly = true;
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void updateSetting() {
        Settings.updateFromPreferences(PreferenceManager.getDefaultSharedPreferences(this));
    }

    private void initFiles() {
        //File externalStorage = new File(Environment.getExternalStorageDirectory(), "qpython");

        //if (checkExpired("public", new File(externalStorage + "/lib").getAbsolutePath(), "programs"+NAction.getPyVer(this))) {

            //String code = NAction.getCode(this);
        boolean isQpy3 = NAction.isQPy3(getApplication());
        String baseDir = FileUtils.getAbsolutePath(App.getContext());
        File root = new File(baseDir);
        if (!(root.exists() && root.isDirectory())) {
            root.mkdir();
        }
        String path = baseDir + (isQpy3 ? "/snippets3" : "/snippets");
        File folder = new File(path);
        if (!(folder.exists() && folder.isDirectory())) {
            folder.mkdir();
        }

        // init snipples
        String[] filesList = {"Apache_License", "The_MIT_License", "QPy_WebApp", "QPy_ConsoleApp", "QPy_SL4AApp", "QPy_PygameApp"};
        for (int i=0;i<filesList.length;i++) {
            String fn = filesList[i];
            File f = new File(path + "/" + fn);
            if (!f.exists()) {
                String file1 = LoadDataFromAssets(this, fn);
                writeToFile(path + "/" + fn, file1);
            }

        }

        //}
    }

    private String openLastFile() {
        String lastFile = NStorage.getSP(this, "qedit.last_filename");
        Log.d(TAG, "openLastFile:"+lastFile);

        if (!lastFile.equals("")) {
            File f2 = new File(lastFile);
            if (f2.exists()) {
                mCurrentFileName = f2.getName();
                if (mCurrentFileName.equals("main.py")) {
                    projPath = f2.getParent();

                }

                return doOpenFile(f2, false);
            }
        }
        return "";
    }

    protected void updateTitle() {
        String title;
        String name = getString(R.string.title_untitled);
        if ((mCurrentFileName != null) && (mCurrentFileName.length() > 0))
            name = mCurrentFileName;

        if (mReadOnly) {
            title = getString(R.string.title_editor_readonly, name);
        } else if (mDirty) {
            title = getString(R.string.title_editor_dirty, name);
        } else {
            title = getString(R.string.title_editor, name);
        }

        setTitle(title);
        supportInvalidateOptionsMenu();
    }

    protected String doOpenFile(File file, boolean forceReadOnly) {
        Log.d(TAG, "doOpenFile:"+file);
        if (file == null) {
            return "";
        } else {
            String text = TextFileUtils.readTextFile(file);
            mCurrentFilePath = getCanonizePath(file);
            mCurrentFileName = file.getName();
            mReadOnly = !(file.canWrite() && (!forceReadOnly));
            if (textFragment != null) {
                if (text != null) {
                    textFragment.setEditorText(text);
                    NStorage.setSP(this, "qedit.last_filename", mCurrentFilePath);
                } else {
                    Crouton.showText(this, R.string.toast_file_cant_read, Style.ALERT);


                }
            }
            updateTitle();
            return text != null ? text : "";
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editor_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add:
                if (binding.drawerLayout.isDrawerOpen(binding.leftDrawer)) {
                    binding.drawerLayout.closeDrawer(binding.leftDrawer);
                }
                if (editorPopUp == null) {
                    List<PopupItemBean> itemBeanList = new ArrayList<>();
                    itemBeanList.add(new PopupItemBean(getString(R.string.file_new), v -> {
                        if (mDirty) {
                            promptSaveDirty(((dialog, which) -> newContent()));
                        } else {
                            newContent();
                        }
                    }));


                    itemBeanList.add(new PopupItemBean(getString(R.string.console_app_project), v -> {
                        if (mDirty) {
                            promptSaveDirty(((dialog, which) -> newProject(QPyConstants.CONSOLE_PROJECT)));
                        } else {
                            newProject(QPyConstants.CONSOLE_PROJECT);
                        }
                    }));
                    itemBeanList.add(new PopupItemBean(getString(R.string.webapp_project), v -> {
                        if (mDirty) {
                            promptSaveDirty(((dialog, which) -> newProject(QPyConstants.WEB_PROJECT)));
                        } else {
                            newProject(QPyConstants.WEB_PROJECT);
                        }
                    }));
                    itemBeanList.add(new PopupItemBean(getString(R.string.qsl4a_app_project), v -> {
                        if (mDirty) {
                            promptSaveDirty(((dialog, which) -> newProject(QPyConstants.QSL4A_PROJECT)));
                        } else {
                            newProject(QPyConstants.QSL4A_PROJECT);
                        }
                    }));

                    itemBeanList.add(new PopupItemBean(getString(R.string.pygame_app_project), v -> {
                        if (mDirty) {
                            promptSaveDirty(((dialog, which) -> newProject(QPyConstants.PYGAME_PROJECT)));
                        } else {
                            newProject(QPyConstants.PYGAME_PROJECT);
                        }
                    }));

//                    itemBeanList.add(new PopupItemBean(getString(R.string.kivy_app_project), v -> {
//                        if (mDirty) {
//                            promptSaveDirty(((dialog, which) -> newProject(QPyConstants.KIVY_PROJECT)));
//                        } else {
//                            newProject(QPyConstants.KIVY_PROJECT);
//                        }
//                    }));

                    editorPopUp = new EditorPopUp(this, itemBeanList);
                }
                editorPopUp.show(binding.lt.toolbar);
                break;
            case R.id.menu_open:
                TedLocalActivity.start(this, REQUEST_OPEN, OPEN_REQUEST_CODE,"");
                break;
            case R.id.menu_more:
                TedSettingsActivity.start(this);
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bundle extras;
        if (resultCode == RESULT_CANCELED) {
            return;
        }

        if ((resultCode != RESULT_OK) || (data == null)) {
            return;
        }

        extras = data.getExtras();
        if (extras == null) {
            return;
        }

        switch (requestCode) {
            case REQUEST_SAVE_AS:
                doSaveFile(extras.getString("path"), true);
                break;
            case REQUEST_OPEN:
                doOpenFile(new File(extras.getString("path")), false);
                break;
            case REQUEST_RECENT:
                if (extras.getString("path") == null) {
                    return;
                }
                doOpenFile(new File(extras.getString("path")), false);
                break;
            case LOGIN_REQUEST_CODE:
                GistEditActivity.start(this, mCurrentFilePath);
                break;
            case OPEN_REQUEST_CODE:
                if (extras.getBoolean("isProj")) {
                    projPath = extras.getString("path");
                    fileTreeAdapter = new EditorFileTreeAdapter(projPath);
                    binding.leftDrawer.setLayoutManager(new LinearLayoutManager(this));
                    binding.leftDrawer.setAdapter(fileTreeAdapter);
                    binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

                    doOpenFile(FileHelper.findFile(new File(projPath), "main.py"), false);
                } else {
                    binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    doOpenFile(new File(extras.getString("path")), false);
                }
                break;
        }
    }

    boolean              needLog;
    ScriptExec.LogDialog event;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ScriptExec.LogDialog event) {
        this.event = event;
        needLog = true;
    }

    public void showLogDialog(ScriptExec.LogDialog event) {
        new android.app.AlertDialog.Builder(this)
                .setTitle(R.string.last_log)
                .setMessage(com.quseit.qpyengine.R.string.open_log)
                .setNegativeButton(R.string.no, ((dialog, which) -> dialog.dismiss()))
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    dialog.dismiss();
                    Utils.checkRunTimeLog(this, event.title, event.path);
                })
                .create()
                .show();
    }

    /**
     * 一个work around，点击返回时软键盘的事件会先触发，所以延迟0.2秒更新状态
     */
    @Override
    public void onGlobalLayout() {
        Rect bottom = new Rect();
        binding.drawerLayout.getWindowVisibleDisplayFrame(bottom);
        Observable.just(null)
                .delay(200, TimeUnit.MICROSECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(o -> isKeyboardVisible = bottom.bottom < height);
    }

    protected void hideKeyboard() {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public boolean onKeyDown(int keyCoder, KeyEvent event) {
        boolean isCtr = event.isCtrlPressed();
        if (keyCoder == KeyEvent.KEYCODE_BACK) {
            if (textFragment.isSearchShowing()) {
                textFragment.setSearchState();
            } else if (isKeyboardVisible) {
                hideKeyboard();
            } else if (binding.drawerLayout.isDrawerOpen(binding.leftDrawer)) {
                binding.drawerLayout.closeDrawer(binding.leftDrawer);
            } else {
                finishEdit();
            }
            return true;
        } else if (isCtr) {
            switch (keyCoder) {
                case KeyEvent.KEYCODE_F:
                    textFragment.setSearch();
                    break;
                case KeyEvent.KEYCODE_S:
                    saveContent(true, false);
                    break;
                case KeyEvent.KEYCODE_O:
//                    openFile();
                    break;
                case KeyEvent.KEYCODE_Z:
                    textFragment.undo();
                    break;
                case KeyEvent.KEYCODE_L:
                    textFragment.goToLine();
                    break;
                case KeyEvent.KEYCODE_LEFT_BRACKET:
                    textFragment.leftIndent();
                    break;
                case KeyEvent.KEYCODE_RIGHT_BRACKET:
                    textFragment.rightIndent();
                    break;
                case KeyEvent.KEYCODE_R:
//                    runScript();
                    break;
                case KeyEvent.KEYCODE_SOFT_LEFT:

                default:
                    break;
            }
        } else {
            switch (keyCoder) {
                // F5 ,run the code
                case KeyEvent.KEYCODE_F5:
                    // Toast.makeText(this,"f5 f5 f5", Toast.LENGTH_SHORT).show();
//                    runScript();
                    break;
                case KeyEvent.KEYCODE_TAB:
                    textFragment.rightIndent();
                    break;
                case KeyEvent.KEYCODE_SEARCH:
                    textFragment.setSearchState();
                    break;
            }
        }

        return super.onKeyDown(keyCoder, event);
    }

    private void finishEdit() {
        if (NAction.getCode(this).contains("qedit")) {
            if (mCurrentFilePath == null) {
                if (mDirty) {
                    promptSaveDirty();
                } else {
                    finish();
                }
            } else {
                Intent intent = getIntent();
                String action = intent.getAction();
                if (action == null) {
                    newContent();
                } else {
                    if (mDirty) {
                        promptSaveDirty();
                    } else {
                        finish();
                    }
                }
            }
        } else {
            if (mDirty) {
                promptSaveDirty();
            } else {
                finish();
            }
        }
    }

    protected boolean doOpenBackup() {
        try {
            String text = TextFileUtils.readInternal(this);
            if (!TextUtils.isEmpty(text)) {
                textFragment = TedFragment.newInstance(text);
                mCurrentFilePath = getPreferences(MODE_PRIVATE).getString("LAST_OPEN_PATH", "");
                String[] findName = mCurrentFilePath.split("/");
                mCurrentFileName = findName.length == 0 ? "" : findName[findName.length - 1];
                mDirty = false;
                mReadOnly = false;
                return true;
            } else {
                return false;
            }
        } catch (OutOfMemoryError e) {
            Crouton.showText(this, R.string.toast_memory_open, Style.ALERT);

        }

        return true;
    }

    protected void doDefaultAction() {
        File file;
        boolean loaded;
        loaded = doOpenBackup();

        if ((!loaded) && Settings.USE_HOME_PAGE) {
            file = new File(Settings.HOME_PAGE_PATH);
            if (!file.exists()) {
                Crouton.showText(this, R.string.toast_open_home_page_error, Style.ALERT);

            } else if (!file.canRead()) {
                Crouton.showText(this, R.string.toast_home_page_cant_read, Style.ALERT);

            } else {
                doOpenFile(file, false);
            }
        }

//        if (!loaded) doClearContents(); todo
    }

    protected void promptSaveDirty() {
        promptSaveDirty(((dialog, which) -> finish()));
    }

    protected void promptSaveDirty(DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder;

        builder = new AlertDialog.Builder(this, R.style.MyDialog);
        builder.setTitle(R.string.warning);
        builder.setMessage(R.string.ui_save_text);

        builder.setPositiveButton(R.string.ui_save, (dialog, which) -> {
            saveContent(true, false);
            listener.onClick(dialog, which);
        });
        builder.setNegativeButton(R.string.ui_cancel, null);
        builder.setNeutralButton(R.string.ui_no_save, listener);

        builder.create().show();
    }

    protected void doSaveFile(String path, boolean showToast) {
        if (path == null) {
            Crouton.showText(this, R.string.toast_save_null, Style.ALERT);

            return;
        }
        String content = textFragment.getEditorString();
        if (!TextFileUtils.writeTextFile(path + ".tmp", content)) {
            Crouton.showText(this, R.string.toast_save_temp, Style.ALERT);

            return;
        }

        if (!deleteItem(path)) {
            Crouton.showText(this, R.string.toast_save_delete, Style.ALERT);

            return;
        }

        if (!renameItem(path + ".tmp", path)) {
            Crouton.showText(this, R.string.toast_save_rename, Style.ALERT);

            return;
        }

        mCurrentFilePath = getCanonizePath(new File(path));
        mCurrentFileName = (new File(path)).getName();
        RecentFiles.updateRecentList(path);
        RecentFiles.saveRecentList(PreferenceManager.getDefaultSharedPreferences(this));
        mReadOnly = false;
        mDirty = false;
        updateTitle();

        NStorage.setSP(this, "qedit.last_filename", mCurrentFilePath);
        if (showToast) {
            Crouton.showText(this, R.string.toast_save_success, Style.INFO);
        }
    }

    protected void saveAs() {
        TedLocalActivity.start(this, TedLocalActivity.REQUEST_SAVE_AS, REQUEST_SAVE_AS,mCurrentFileName!=null?mCurrentFileName:".py");
    }

    protected void saveContent(boolean showToast, boolean auto) {
        if (!mDirty) {
            if (showToast)  {
                Crouton.showText(this, R.string.no_change, Style.INFO);
            }
            return;
        }
        if ((mCurrentFilePath == null) || (mCurrentFilePath.length() == 0)) {
            if (auto) {
                //when edit, if didn't save before, it wont save still
            } else {
                TedLocalActivity.start(this, TedLocalActivity.REQUEST_SAVE_AS, REQUEST_SAVE_AS, mCurrentFileName!=null?mCurrentFileName:".py");
            }
        } else {
            doSaveFile(mCurrentFilePath, showToast);
        }
    }

    protected void newContentInProject() {
        if (projPath != null && !projPath.equals("")) {
            new EnterDialog(this)
                    .setTitle(getString(R.string.new_file))
                    .setExt(".py")
                    .setConfirmListener(name -> {
                        String path = new File(mCurrentFilePath).getParent() + "/" + name;
                        File file = new File(path);
                        if (file.exists()) {
                            Crouton.showText(this, R.string.file_exists, Style.ALERT);

                            return false;
                        }
                        updateTitle();
                        try {
                            file.createNewFile();
                            mCurrentFilePath = file.getAbsolutePath();
                            doOpenFile(file, false);
                            fileTreeAdapter.addNewFile(path);
                            return true;
                        } catch (IOException e) {
                            e.printStackTrace();
                            return false;
                        }
                    })
                    .show();
        }
    }
    protected void newContent() {

        mCurrentFileName = null;
        mCurrentFilePath = null;
        textFragment.setEditorText("");
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        updateTitle();
    }

    protected void newScript() {
        final boolean isQpy3 = NAction.isQPy3(getApplicationContext());

        new EnterDialog(this)
                .setTitle(getString(R.string.new_script))
                .setExt(".py")
                .setConfirmListener(name -> {
                    File file = new File(FileUtils.getAbsolutePath(App.getContext()) + "/" + (isQpy3? QPyConstants.DFROM_QPY3:QPyConstants.DFROM_QPY2) + "/" + name);
                    if (file.exists()) {
                        Crouton.showText(this, R.string.file_exists, Style.ALERT);

                        return false;
                    }
                    mCurrentFileName = null;
                    mCurrentFilePath = null;
                    textFragment.setEditorText("");
                    binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    try {
                        file.createNewFile();
                        mCurrentFilePath = file.getAbsolutePath();
                        doOpenFile(file, false);
                        return true;
                    } catch (IOException e) {
                        e.printStackTrace();
                        return false;
                    }
                })
                .show();
    }

    /**
     * @param type WEB_PROJECT/CONSOLE_PROJECT/QSL4A_PROJECT
     */
    protected void newProject(final String type) {
        NStorage.setSP(getApplicationContext(), "qedit.last_filename", "");
        new EnterDialog(this)
            .setTitle(getString(R.string.new_project))
            .setHint(getString(R.string.project_name))
            .setConfirmListener(name -> {
                Stack<String> curArtistDir = new Stack<>();
                final boolean isQpy3 = NAction.isQPy3(getApplicationContext());

                curArtistDir.push(FileUtils.getAbsolutePath(App.getContext())
                        + "/" + (isQpy3 ? QPyConstants.DFROM_PRJ3 : QPyConstants.DFROM_PRJ2) + "/" + name);

                File fileN = new File(curArtistDir.peek());
                if (fileN.exists()) {
                    Crouton.showText(this, R.string.file_exists, Style.ALERT);

                    return false;
                } else {
                    try {
                        if (fileN.mkdirs()) {
                            File mainPy = new File(fileN.getAbsolutePath(), "main.py");
                            if (mainPy.createNewFile()) {
                                mCurrentFilePath = mainPy.getAbsolutePath();
                                doOpenFile(mainPy, false);
                                textFragment.insertSnippet(type);
                                saveContent(true, false);
                            }
                        }
                    } catch (IOException e) {
                        Crouton.showText(this, R.string.error_dont_kn, Style.ALERT);

                        e.printStackTrace();
                        return false;
                    }
                    Crouton.showText(this, R.string.success, Style.INFO);

                    return true;
                }
            })
            .show();
    }
}