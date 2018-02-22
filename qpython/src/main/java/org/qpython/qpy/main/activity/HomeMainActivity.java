package org.qpython.qpy.main.activity;

import android.Manifest;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.quseit.common.updater.Updater;
import com.quseit.common.updater.callback.DialogCallback;

import org.greenrobot.eventbus.Subscribe;
import org.qpython.qpy.R;
import org.qpython.qpy.console.TermActivity;
import org.qpython.qpy.main.dialog.LogoDialog;
import org.qpython.qpy.main.dialog.RunProgramDialog;
import org.qpython.qpy.main.event.RunProgramEvent;
import org.qpython.qpy.main.utils.Bus;
import org.qpython.qpy.main.utils.Utils;
import org.qpython.qpysdk.QPySDK;
import org.qpython.qpysdk.utils.FileHelper;
import org.qpython.qsl4a.QPyScriptService;
import org.renpy.android.ResourceManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

public class HomeMainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Unbinder mUnbinder;

    public static final String URL_WIKI = "http://www.qpython.org";
    public static final String URL_GROUP = "http://groups.google.com/group/qpython";
    public static final String URL_DOCS = "http://www.qpython.org";
    public static final String URL_LIB_MANAGER = "http://qpypi.qpython.org";
    public static final int REQUEST_OPEN_FILE = 1;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.nav_view)
    NavigationView mNavigationView;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.logo_image)
    CircleImageView mLogoImage;
    @BindView(R.id.item_lin_console)
    LinearLayout mItemConsole;
    @BindView(R.id.item_lin_editor)
    LinearLayout mItemEditor;
    @BindView(R.id.item_lin_program)
    LinearLayout mItemProgram;
    @BindView(R.id.item_lin_library_manager)
    LinearLayout mItemLibraryManager;

    private long mExitTime = 0;

    @OnClick({R.id.logo_image, R.id.item_lin_console, R.id.item_lin_editor, R.id.item_lin_program, R.id.item_lin_library_manager})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.logo_image:
                showLogoDialog();
                break;
            case R.id.item_lin_console:
                TermActivity.startActivity(this);
                break;
            case R.id.item_lin_editor:
                //EditorMainActivity.startActivity(this);
                break;
            case R.id.item_lin_program:
                startProgramExplorerActivity();
                break;
            case R.id.item_lin_library_manager:
                Utils.startWebActivityWithUrl(this, getString(R.string.lib_manager), URL_LIB_MANAGER,"",false, false);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUnbinder = ButterKnife.bind(this);
        startPyService();
        Updater.checkUpdate(new DialogCallback(this, true));
        Bus.getDefault().register(this);
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Bus.getDefault().unregister(this);
        mUnbinder.unbind();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    private void startPyService() {
        Intent intent = new Intent(this, QPyScriptService.class);
        startService(intent);
    }

    private void openQpySDK() {
        QPySDK qpysdk = new QPySDK(HomeMainActivity.this, HomeMainActivity.this);
        File externalStorage = new File(Environment.getExternalStorageDirectory(), "qpython");

        qpysdk.extractRes("private", HomeMainActivity.this.getFilesDir());
        checkPermissionDo(Manifest.permission.WRITE_EXTERNAL_STORAGE , new BaseActivity.PermissionAction() {
            @Override
            public void onGrant() {
                FileHelper.createDirIfNExists(externalStorage+"/cache");
                qpysdk.extractRes("public", new File(externalStorage + "/lib"));
                extractRes();

            }

            @Override
            public void onDeny() {
                Toast.makeText(HomeMainActivity.this, "Unable to extract python files", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkExpired(final String resource, String filesDir) {
        ResourceManager resourceManager = new ResourceManager(this);

        String data_version = resourceManager.getString(resource + "_version");
        String disk_version = "0";

        // If no version, no unpacking is necessary.
        if (data_version == null) {
            return false;
        }

        // Check the current disk version, if any.
        String disk_version_fn = filesDir + "/" + resource + ".version";

        try {
            byte buf[] = new byte[64];
            InputStream is = new FileInputStream(disk_version_fn);
            int len = is.read(buf);
            disk_version = new String(buf, 0, len);
            is.close();
        } catch (Exception e) {

            disk_version = "0";
            //Mint.logException(e);

        }

        //Log.d(TAG, "data_version:"+Math.round(Double.parseDouble(data_version))+"-disk_version:"+Math.round(Double.parseDouble(disk_version))+"-RET:"+(int)(Double.parseDouble(data_version)-Double.parseDouble(disk_version)));
        if ((int)(Double.parseDouble(data_version)-Double.parseDouble(disk_version))>0 || disk_version.equals("0")) {
            try {
                FileOutputStream os = new FileOutputStream(disk_version_fn);
                try {
                    os.write(data_version.getBytes());
                    os.close();

                } catch (IOException e) {
                    e.printStackTrace();
                    //Mint.logException(e);

                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                //Mint.logException(e);

            }


            return true;
        } else {
            return false;
        }
    }

    public void extractRes() {
        File externalStorage = new File(Environment.getExternalStorageDirectory(), "qpython");

        //File res = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + this.getPackageName() + "/"+(isQpy3?CONF.DFROM_QPY3:CONF.DFROM_QPY2));
        if (checkExpired("public",new File(externalStorage+"").getAbsolutePath())) {
            String name, sFileName;
            InputStream content;

            R.raw a = new R.raw();
            java.lang.reflect.Field[] t = R.raw.class.getFields();
            Resources resources = getResources();

            boolean succeed = true;

            for (int i = 0; i < t.length; i++) {
                try {
                    name = resources.getText(t[i].getInt(a)).toString();
                    sFileName = name.substring(name.lastIndexOf('/') + 1, name.length());
                    content = getResources().openRawResource(t[i].getInt(a));
                    content.reset();

                // python project
                    if(sFileName.equals("sampleproject.zip")) {
                        Utils.createDirectoryOnExternalStorage( "qpython" );
                        Utils.createDirectoryOnExternalStorage( "qpython/projects");
                        Utils.createDirectoryOnExternalStorage( "qpython/projects/AlbumSample");

                        Utils.unzip(content, Environment.getExternalStorageDirectory().getAbsolutePath() +
                                "/qpython/projects/AlbumSample/", false);

                    } else if (sFileName.equals("showcase.zip")) {
                        Utils.createDirectoryOnExternalStorage( "qpython" );
                        Utils.createDirectoryOnExternalStorage( "qpython/projects" );
                        Utils.createDirectoryOnExternalStorage( "qpython/projects/KivyShowcase");

                        Utils.unzip(content, Environment.getExternalStorageDirectory().getAbsolutePath() + "/qpython/projects/KivyShowcase/", false);

                    } else if (sFileName.equals("samplewebapp.zip")) {
                        Utils.createDirectoryOnExternalStorage( "qpython" );
                        Utils.createDirectoryOnExternalStorage( "qpython/projects");
                        Utils.createDirectoryOnExternalStorage( "qpython/projects/WebAppSample");

                        Utils.unzip(content, Environment.getExternalStorageDirectory().getAbsolutePath() + "/qpython/projects/WebAppSample/", false);

                    } else if (sFileName.equals("scripts.zip")) {
                        Utils.createDirectoryOnExternalStorage( "qpython" );
                        Utils.createDirectoryOnExternalStorage( "qpython/scripts" );
                        Utils.unzip(content, Environment.getExternalStorageDirectory().getAbsolutePath() + "/qpython/scripts/", false);
                    }

                } catch (Exception e) {
                    Log.e("HomeMainActivity", "Failed to copyResourcesToLocal", e);
                    //Mint.logException(e);

                    succeed = false;
                }
            } // end for all files in res/raw
        }
    }

    private void init() {
        setSupportActionBar(mToolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(this);

        openQpySDK();
    }


    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return;
        }

        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Toast.makeText(this, getString(R.string.back_app), Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
            return;
        }

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_setting:
                AboutActivity.startActivity(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_document:
                Utils.startWebActivityWithUrl(this, getString(R.string.document), URL_DOCS,"", false, false);
                break;
            case R.id.nav_forum:
                Utils.startWebActivityWithUrl(this, getString(R.string.forum), URL_GROUP,"",false, false);
                break;
//            case R.id.nav_wiki:
//                Utils.startWebActivityWithUrl(this, getString(R.string.wiki), URL_WIKI,"",false, false);
//                break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK:
                //打开文件
                Log.e("------------------->>>>", data.getStringExtra("file"));
                break;
        }
    }

    private void showLogoDialog() {
        LogoDialog.newInstance().show(getSupportFragmentManager(), LogoDialog.TAG);
    }


    @Subscribe
    public void startQrCodeActivity(StartQrCodeActivityEvent event) {
        checkPermissionDo(Manifest.permission.CAMERA, new BaseActivity.PermissionAction() {
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

    @Subscribe
    public void showProgramDialog(ShowProgramDialogEvent event) {
        checkPermissionDo(Manifest.permission.READ_EXTERNAL_STORAGE, new PermissionAction() {
            @Override
            public void onGrant() {
                if (event.isProject) {
                    Fragment f = RunProgramDialog.newInstance(RunProgramDialog.TYPE_PROJECT);
                    getSupportFragmentManager().beginTransaction()
                            .add(f, RunProgramDialog.TAG)
                            .commitAllowingStateLoss();
                } else {
                    Fragment f = RunProgramDialog.newInstance(RunProgramDialog.TYPE_SCRIPT);
                    getSupportFragmentManager().beginTransaction()
                            .add(f, RunProgramDialog.TAG)
                            .commitAllowingStateLoss();
                }
            }

            @Override
            public void onDeny() {
                Toast.makeText(HomeMainActivity.this, R.string.no_storage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Subscribe
    public void runProgram(RunProgramEvent event) {
        Log.d("HomeMainActivity", "");
        if (event.isProject) {
            playProject(event.path, true);
        } else {
            playScript(event.path, null, true);
        }
    }

    private void startProgramExplorerActivity() {
        checkPermissionDo(Manifest.permission.READ_EXTERNAL_STORAGE, new PermissionAction() {
            @Override
            public void onGrant() {
                //ProgramExplorerActivity.start(HomeMainActivity.this, REQUEST_OPEN_FILE);
            }

            @Override
            public void onDeny() {
                Toast.makeText(HomeMainActivity.this, getString(R.string.no_storage), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class StartQrCodeActivityEvent {

    }

    public static class ShowProgramDialogEvent {
        public final boolean isProject;

        public ShowProgramDialogEvent(boolean isProject) {
            this.isProject = isProject;
        }
    }
}
