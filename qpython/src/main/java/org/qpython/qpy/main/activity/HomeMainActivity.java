package org.qpython.qpy.main.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.gyf.cactus.Cactus;
import com.quseit.util.FileUtils;
import com.quseit.util.NAction;
import com.quseit.util.Utils;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;
import org.qpython.qpy.R;
import org.qpython.qpy.console.TermActivity;
import org.qpython.qpy.databinding.ActivityMainBinding;
import org.qpython.qpy.main.app.App;
import org.qpython.qpy.main.app.CONF;
import org.qpython.qpy.main.server.MySubscriber;
import org.qpython.qpy.main.server.model.CourseAdModel;
import org.qpython.qpy.main.utils.Bus;
import org.qpython.qpy.texteditor.EditorActivity;
import org.qpython.qpy.texteditor.TedLocalActivity;
import org.qpython.qpy.utils.UpdateHelper;
import org.qpython.qpysdk.QPyConstants;
import org.qpython.qpysdk.QPySDK;
import org.qpython.qpysdk.utils.FileHelper;
import org.qpython.qsl4a.QPyScriptService;

import java.io.File;
import java.io.InputStream;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static org.qpython.qpysdk.QPyConstants.PYTHON_2;

public class HomeMainActivity extends BaseActivity {
    private static final String URL_COMMUNITY = "https://www.qpython.org/community.html";
    private static final String URL_COURSE    = "https://edu.qpython.org/?from=qpy2";
    private static final String USER_NAME     = "username";
    private static final String TAG = "HomeMainActivity";

    private static final int LOGIN_REQUEST_CODE = 136;

    private ActivityMainBinding binding;
    private SharedPreferences preferences;

    public static void start(Context context) {
        Intent starter = new Intent(context, HomeMainActivity.class);
        context.startActivity(starter);
    }

    public static void start(Context context, String userName) {
        Intent starter = new Intent(context, HomeMainActivity.class);
        starter.putExtra(USER_NAME, userName);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        //App.setActivity(this);
        startMain();
        handlePython3(getIntent());
        handleNotification(savedInstanceState);
    }

    private void initIcon() {
        switch (NAction.getQPyInterpreter(this)) {
            case "3.x":
                binding.icon.setImageResource(R.drawable.img_home_logo_3);
                break;
            case "2.x":
                binding.icon.setImageResource(R.drawable.img_home_logo);
                break;
            default:
                break;
        }
    }

    private void initUser() {
        if (App.getUser() == null) {
            binding.login.setVisibility(View.GONE);
        } else {
            binding.login.setText(Html.fromHtml(getString(R.string.welcome_s, App.getUser().getNick())));
        }
    }

    private void startMain() {
        initListener();
//        startPyService();
        Bus.getDefault().register(this);
        init();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            UpdateHelper.checkConfUpdate(this, QPyConstants.BASE_PATH);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initUser();
        initIcon();
        handleNotification();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handlePython3(intent);
    }

    private void initListener() {
        binding.ivScan.setOnClickListener(v -> Bus.getDefault().post(new StartQrCodeActivityEvent()));
        binding.login.setOnClickListener(v -> {
            if (App.getUser() == null) {
                sendEvent(getString(R.string.event_login));
                startActivityForResult(new Intent(this, SignInActivity.class), LOGIN_REQUEST_CODE);
            } else {
                sendEvent(getString(R.string.event_me));
                UserActivity.start(this);
            }
        });

        binding.llTerminal.setOnClickListener(v -> {
            openQpySDK(view -> {
                TermActivity.startActivity(HomeMainActivity.this);
                sendEvent(getString(R.string.event_term));
            });
        });

        binding.llTerminal.setOnLongClickListener(v -> {
            CharSequence[] chars = new CharSequence[]{ this.getString(R.string.python_interpreter), this.getString(R.string.action_notebook), this.getString(R.string.shell_terminal)};
            new AlertDialog.Builder(this, R.style.MyDialog)
                    .setTitle(R.string.choose_action)
                    .setItems(chars, (dialog, which) -> {
                        switch (which) {
                            default:
                                break;
                            case 0: // Create Shortcut
                                TermActivity.startActivity(HomeMainActivity.this);
                                break;
                            case 1:
                                NotebookActivity.start(HomeMainActivity.this, null, false);
                                break;
                            case 2:
                                TermActivity.startShell(HomeMainActivity.this);
                                break;
                        }
                    }).setNegativeButton(getString(R.string.close), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            })
                    .show();

            return true;
        });
        binding.llEditor.setOnClickListener(v -> {
            EditorActivity.start(this);
            sendEvent(getString(R.string.event_editor));
        });
        binding.llLibrary.setOnClickListener(v -> {
            LibActivity.start(this);
            sendEvent(getString(R.string.event_qpypi));
        });
//        binding.llCommunity.setOnClickListener(v -> {
//            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL_COMMUNITY)));
//            sendEvent(getString(R.string.event_commu));
//        });
//        binding.llGist.setOnClickListener(view -> GistActivity.startCommunity(HomeMainActivity.this)
//        );
        binding.llSetting.setOnClickListener(v -> {
            SettingActivity.startActivity(this);
            sendEvent(getString(R.string.event_setting));
        });
        binding.llFile.setOnClickListener(v -> {
            TedLocalActivity.start(this, TedLocalActivity.REQUEST_HOME_PAGE);
            sendEvent(getString(R.string.event_file));
        });
        binding.llQpyApp.setOnClickListener(v -> {
            openQpySDK(view -> {
                AppListActivity.start(HomeMainActivity.this, AppListActivity.TYPE_SCRIPT);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                sendEvent(getString(R.string.event_top));
            });
        });

        binding.llCourse.setOnClickListener(v -> {
            CourseActivity.start(HomeMainActivity.this);
            sendEvent(getString(R.string.event_course));
        });
/*        binding.llCourse.setOnClickListener(v ->
                QWebViewActivity.start(HomeMainActivity.this, getString(R.string.course), URL_COURSE));

        initCourseListener();*/
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Bus.getDefault().unregister(this);
        boolean isKeepAlive = preferences.getBoolean(getString(R.string.key_alive), false);
        if (!isKeepAlive){
            return;
        }
        Cactus.getInstance().unregister(this);
    }

    private void handlePython3(Intent intent) {
        String action = intent.getAction();
        if (action != null && action.equals(getString(R.string.action_from_python_three))
                && NAction.getQPyInterpreter(this).equals(PYTHON_2)) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.py2_now)
                    .setMessage(R.string.switch_py3_hint)
                    .setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss())
                    .setPositiveButton(R.string.goto_setting, (dialog, which) -> SettingActivity.startActivity(this))
                    .create()
                    .show();
        }
    }

    private void handleNotification(Bundle bundle) {
        if (bundle == null) {return;}
        if (!bundle.getBoolean("force") && !PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.key_hide_push), true)) {
            return;
        }
        String type = bundle.getString("type", "");
        if (!type.equals("")) {
            String link = bundle.getString("link", "");
            String title = bundle.getString("title", "");

            switch (type) {
                case "in":
                    QWebViewActivity.start(this, title, link);
                    break;
                case "ext":
                    Intent starter = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                    startActivity(starter);
                    break;
                default:break;
            }
        }
    }

    private void handleNotification() {
        if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.key_hide_push), true)) {
            return;
        }
        SharedPreferences sharedPreferences = getSharedPreferences(CONF.NOTIFICATION_SP_NAME, MODE_PRIVATE);
        try {
            String notifString = sharedPreferences.getString(CONF.NOTIFICATION_SP_OBJ, "");
            if ("".equals(notifString)) {
                return;
            }
            JSONObject extra = new JSONObject(notifString);
            String type = extra.getString("type");
            String link = extra.getString("link");
            String title = extra.getString("title");
            switch (type) {
                case "in":
                    QWebViewActivity.start(this, title, link);
                    break;
                case "ext":
                    Intent starter = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                    startActivity(starter);
                    break;
                default:break;
            }
            sharedPreferences.edit().clear().apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

//    private void initCourseListener() {
//        App.getService().getCourseAd().subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new MySubscriber<CourseAdModel>() {
//                    @Override
//                    public void onNext(CourseAdModel o) {
//                        super.onNext(o);
//                        if ("0".equals(o.getQpy().getCourse_open())) {
//                            // open web
//                            binding.llCourse.setOnClickListener(v ->
//                                    QWebViewActivity.start(HomeMainActivity.this, getString(R.string.course), URL_COURSE));
//                        } else {
//                            // open with native
//                            binding.llCourse.setOnClickListener(v -> {
//                                CourseActivity.start(HomeMainActivity.this);
//                                sendEvent(getString(R.string.event_course));
//                            });
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        binding.llCourse.setOnClickListener(v ->
//                                QWebViewActivity.start(HomeMainActivity.this, getString(R.string.course), URL_COURSE));
//                    }
//                });
//    }

    @Override
    protected void onPause() {
        super.onPause();
    }

//    private void startPyService() {
//        Log.d(TAG, "startPyService");
//        Intent intent = new Intent(this, QPyScriptService.class);
//        startService(intent);
//    }

    private void openQpySDK(View.OnClickListener clickListener) {
        Log.d("HomeMainActivity", "openQpySDK");
        String[] permssions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        checkPermissionDo(permssions, new BaseActivity.PermissionAction() {
            @Override
            public void onGrant() {
                //这里只执行一次做为初始化

                if (!NAction.isQPyInterpreterSet(HomeMainActivity.this)) {
                    new AlertDialog.Builder(HomeMainActivity.this, R.style.MyDialog)
                            .setTitle(R.string.notice)
                            .setMessage(R.string.py2_or_3)
                            .setPositiveButton(R.string.use_py3, (dialog1, which)
                                    -> {
                                initQpySDK3();
                                clickListener.onClick(null);
                            })
                            .setNegativeButton(R.string.use_py2, (dialog1, which)
                                    -> {
                                initQpySDK();
                                clickListener.onClick(null);
                            })
                            .create()
                            .show();
                } else {
                    clickListener.onClick(null);
                }
            }

            @Override
            public void onDeny() {
                Toast.makeText(HomeMainActivity.this,  getString(R.string.grant_storage_hint), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 在工作线程中作初始化
     */
    private void initQpySDK3() {
        Log.d(TAG, "initQpySDK3");
        NAction.setQPyInterpreter(HomeMainActivity.this, "3.x");
        initQPy(true);
        initIcon();
    }
    private void initQpySDK() {
        Log.d(TAG, "initQpySDK");
        initQPy(false);
        NAction.setQPyInterpreter(HomeMainActivity.this, "2.x");
        initIcon();
    }

    private void initQPy(boolean py3) {
        new Thread(() -> {
            QPySDK qpysdk = new QPySDK(HomeMainActivity.this, HomeMainActivity.this);
            //这里会在切换qpy3的时候再次释放相关资源
            qpysdk.extractRes(py3?"private31":"private1", HomeMainActivity.this.getFilesDir());
            qpysdk.extractRes(py3?"private32":"private2", HomeMainActivity.this.getFilesDir());
            qpysdk.extractRes(py3?"private33":"private3", HomeMainActivity.this.getFilesDir());
            if (py3) {
                qpysdk.extractRes("notebook3", HomeMainActivity.this.getFilesDir());
            }
            File externalStorage = new File(FileUtils.getPath(App.getContext()), "qpython");
            FileHelper.createDirIfNExists(externalStorage + "/cache");
            FileHelper.createDirIfNExists(externalStorage + "/log");
            FileHelper.createDirIfNExists(externalStorage + "/notebooks");

            qpysdk.extractRes(py3?"public3":"public", new File(externalStorage + "/lib"));

            qpysdk.extractRes("ipynb", new File(externalStorage + "/notebooks"));

            extractRes();
        }).start();
    }


    /**
     * 初始化内置python项目
     */
    public void extractRes() {
        File externalStorage = new File(FileUtils.getAbsolutePath(getApplicationContext()));
        if (checkExpired("public", new File(externalStorage + "/lib").getAbsolutePath(), "programs"+NAction.getPyVer(this))) {
            String name, sFileName;
            InputStream content;

            R.raw a = new R.raw();
            java.lang.reflect.Field[] t = R.raw.class.getFields();
            Resources resources = getResources();

            for (int i = 0; i < t.length; i++) {
                try {
                    name = resources.getText(t[i].getInt(a)).toString();
                    sFileName = name.substring(name.lastIndexOf('/') + 1, name.length());
                    content = getResources().openRawResource(t[i].getInt(a));
                    content.reset();

                    if (sFileName.equals("projects2.zip")) {
                        Utils.createDirectoryOnExternalStorage(App.getContext(),"qpython/projects/");
                        Utils.unzip(content, FileUtils.getQyPath(App.getContext()) + "/qpython/projects/", false);

                    } else if (sFileName.equals("scripts2.zip")) {
                        Utils.unzip(content, FileUtils.getQyPath(App.getContext()) + "/qpython/scripts/", false);

                    } else if (sFileName.equals("projects3.zip")) {
                        Utils.createDirectoryOnExternalStorage(App.getContext(),"qpython/projects3/");
                        Utils.unzip(content, FileUtils.getQyPath(App.getContext()) + "/qpython/projects3/", false);

                    } else if (sFileName.equals("scripts3.zip")) {
                        Utils.createDirectoryOnExternalStorage(App.getContext(),"qpython/scripts3/");
                        Utils.unzip(content, FileUtils.getQyPath(App.getContext()) + "/qpython/scripts3/", false);

                    } if (sFileName.equals("ipynb.zip")) {
                        Utils.createDirectoryOnExternalStorage(App.getContext(),"qpython/notebooks/");
                        Utils.unzip(content, FileUtils.getQyPath(App.getContext()) + "/qpython/notebooks/", false);
                    }

                } catch (Exception e) {
                    Log.e("HomeMainActivity", "Failed to copyResourcesToLocal", e);

                }
            }
        }
    }

    private void init() {
        //点击时再申请权限，加载资源方法
//        openQpySDK(null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case LOGIN_REQUEST_CODE:
                    binding.login.setText(Html.fromHtml(getString(R.string.welcome_s, App.getUser().getNick())));
                    break;
            }
        }
    }

    @Subscribe
    public void startQrCodeActivity(StartQrCodeActivityEvent event) {
        String[] permissions = {Manifest.permission.CAMERA};

        checkPermissionDo(permissions, new BaseActivity.PermissionAction() {
            @Override
            public void onGrant() {
                QrCodeActivity.start(HomeMainActivity.this);
            }

            @Override
            public void onDeny() {
                Toast.makeText(HomeMainActivity.this, getString(R.string.no_camera), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class StartQrCodeActivityEvent {

    }

    private void sendEvent(String evenName) {

    }

}
