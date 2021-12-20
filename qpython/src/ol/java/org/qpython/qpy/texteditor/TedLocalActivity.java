package org.qpython.qpy.texteditor;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.quseit.util.FileHelper;
import com.quseit.util.NAction;

import org.qpython.qpy.R;
import org.qpython.qpy.codeshare.pojo.CloudFile;
import org.qpython.qpy.databinding.ActivityLocalBinding;
import org.qpython.qpy.main.activity.SignInActivity;
import org.qpython.qpy.main.app.App;
import org.qpython.qpy.main.app.CONF;
import org.qpython.qpy.main.fragment.ExplorerFragment;
import org.qpython.qpy.main.fragment.LocalFragment;
import org.qpython.qpy.main.fragment.MyProjectFragment;
import org.qpython.qpy.utils.NotebookUtil;
import org.qpython.qpysdk.QPyConstants;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TedLocalActivity extends AppCompatActivity {
    public static final  int REQUEST_SAVE_AS    = 107;
    public static final  int REQUEST_OPEN       = 108;
    public static final  int REQUEST_HOME_PAGE  = 109;
    public static final  int REQUEST_RECENT     = 111;
    private static final int LOGIN_REQUEST_CODE = 4806;

    private static final String EXTRA_REQUEST_CODE = "request_code";
    private static final String EXTRA_REQUEST_FN = "request_fn";

    private static final String FRAGMENT_EXPLORER  = "explorer";
    private static final String FRAGMENT_CLOUD     = "cloud";

    private ActivityLocalBinding binding;

    private Fragment firstPageFragment;
    private MyProjectFragment myProjectFragment;

    private boolean isExplorer  = true;
    private boolean isNewUpload = false;
    //private String defaultFileName = "";

    public static void start(Context context, int type) {
        Intent starter = new Intent(context, TedLocalActivity.class);
        starter.putExtra(EXTRA_REQUEST_CODE, type);
        context.startActivity(starter);
    }

    public static void start(Activity context, int type, int requestCode, String filename) {
        Intent starter = new Intent(context, TedLocalActivity.class);
        starter.putExtra(EXTRA_REQUEST_CODE, type);
        starter.putExtra(EXTRA_REQUEST_FN, filename);

        context.startActivityForResult(starter, requestCode);
    }

    public void finishForGetPath(String path) {
        Intent intent = new Intent();
        intent.putExtra("path", path);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    public void finishForOpen(String path, boolean isProj) {
        Intent intent = new Intent();
        intent.putExtra("path", path);
        intent.putExtra("isProj", isProj);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_local);
        setSupportActionBar(binding.toolbar);
        int type = getIntent().getIntExtra(EXTRA_REQUEST_CODE, REQUEST_HOME_PAGE);

        initView();
        initListener();
        switch (type) {
            case REQUEST_RECENT:
                setTitle(R.string.recent);
                binding.switchBtn.setVisibility(View.GONE);
                firstPageFragment = ExplorerFragment.newInstance(type);
                break;
            case REQUEST_OPEN:
                setTitle(R.string.open);
                binding.switchBtn.setVisibility(View.GONE);
                binding.explore.setVisibility(View.VISIBLE);
                firstPageFragment = new LocalFragment();
                break;
            case REQUEST_SAVE_AS:
                setTitle(R.string.save_as);
                binding.vsSave.getRoot().setVisibility(View.VISIBLE);
                initSaveListener();

                String fn = getIntent().getStringExtra(EXTRA_REQUEST_FN);
                if (fn!=null) {
                    binding.vsSave.etName.setText(fn);
                }
                binding.switchBtn.setVisibility(View.GONE);
                firstPageFragment = ExplorerFragment.newInstance(type);
                break;
            case REQUEST_HOME_PAGE:
                setTitle(R.string.explorer);
                firstPageFragment = ExplorerFragment.newInstance(type);
                break;
        }

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, firstPageFragment, FRAGMENT_EXPLORER)
                .commit();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void initView() {
        binding.toolbar.setNavigationIcon(R.drawable.ic_back);
        binding.toolbar.setNavigationOnClickListener(v -> finish());
        if (binding.switchBtn.getVisibility() == View.VISIBLE) {
            myProjectFragment = new MyProjectFragment();
        }
    }

    private void initListener() {
        binding.switchBtn.setOnClickListener(v -> {
            if (isExplorer) {
                if (App.getUser() == null) {
                    startActivityForResult(new Intent(this, SignInActivity.class), LOGIN_REQUEST_CODE);
                    return;
                }
                binding.switchBtn.setImageResource(R.drawable.ic_folder_open);
                binding.refresh.setVisibility(View.VISIBLE);
                if (getSupportFragmentManager().findFragmentByTag(FRAGMENT_CLOUD) == null) {
                    getSupportFragmentManager().beginTransaction().add(R.id.container, myProjectFragment, FRAGMENT_CLOUD).commit();
                    getSupportFragmentManager().beginTransaction().hide(firstPageFragment).commit();
                } else {
                    getSupportFragmentManager().beginTransaction()
                            .hide(firstPageFragment)
                            .show(myProjectFragment)
                            .commit();
                }
//                myProjectFragment.needRefresh(isNewUpload);
                isNewUpload = false;
                if (!myProjectFragment.isLoading) {
                    myProjectFragment.notifyDataSetChange();
                }
            } else {
                binding.switchBtn.setImageResource(R.drawable.ic_cloud_list);
                binding.refresh.setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction()
                        .hide(myProjectFragment)
                        .show(firstPageFragment)
                        .commit();
            }
            isExplorer = !isExplorer;
        });

        binding.refresh.setOnClickListener(v -> myProjectFragment.retry(true));

        binding.explore.setOnClickListener(v -> {
            start(this, REQUEST_HOME_PAGE);
            finish();
        });
    }

    public void doSave(String fn) {
        if (fn.length() == 0) {
            Toast.makeText(getApplicationContext(), R.string.toast_filename_empty, Toast.LENGTH_SHORT).show();
        } else {
            String filename = ((ExplorerFragment) firstPageFragment).getCurPath() + "/" + fn;
            final File f = new File(filename);
            if (f.exists()) {
                Toast.makeText(this, R.string.file_exist_hint, Toast.LENGTH_SHORT).show();
            } else {
                setSaveResult(f.getAbsolutePath());
            }
        }
    }

    protected boolean setSaveResult(String filepath) {
        File f = new File(filepath);
        if (f.getParentFile().canWrite()) {
            finishForGetPath(filepath);
        } else {
            Toast.makeText(getApplicationContext(), R.string.toast_folder_cant_write, Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    private void initSaveListener() {
        binding.vsSave.btnSave.setOnClickListener(v -> doSave(binding.vsSave.etName.getText().toString()));
        binding.vsSave.etName.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Drawable deleteText = ((EditText) v).getCompoundDrawables()[DRAWABLE_RIGHT];
                    if (deleteText != null) {
                        if (event.getRawX() >= (v.getRight() - deleteText.getBounds().width())) {
                            ((EditText) v).setText("");
                            return true;
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    v.performClick();
                    break;
                default:
                    break;
            }
            return false;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case LOGIN_REQUEST_CODE:
                    binding.switchBtn.setImageResource(R.drawable.ic_folder_open);
                    if (getSupportFragmentManager().findFragmentByTag(FRAGMENT_CLOUD) == null) {
                        getSupportFragmentManager().beginTransaction().add(R.id.container, myProjectFragment, FRAGMENT_CLOUD).commit();
                        getSupportFragmentManager().beginTransaction().hide(firstPageFragment).commit();
                    } else {
                        getSupportFragmentManager().beginTransaction()
                                .hide(firstPageFragment)
                                .show(myProjectFragment)
                                .commit();
                    }
                    isExplorer = !isExplorer;
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (firstPageFragment.isVisible() && firstPageFragment instanceof ExplorerFragment) {
            ((ExplorerFragment) firstPageFragment).backToPrev();
        } else {
            super.onBackPressed();
        }
    }

//    public void deleteCloudFile(String path) {
//        if (firstPageFragment instanceof ExplorerFragment) {
//            ((ExplorerFragment) firstPageFragment).deleteCloudedMap(path);
//        }
//    }
//
//    public void updateCloudFiles(List<CloudFile> cloudFiles) {
//        Map<String, Boolean> map = new HashMap<>();
//        boolean isQPy3 = NAction.isQPy3(getBaseContext());
//        String tag = isQPy3?"/projects3/":"/projects/";
//        for (CloudFile cloudFile : cloudFiles) {
//            if (cloudFile.getPath().contains(tag)) {
//                map.put(QPyConstants.ABSOLUTE_PATH + tag + cloudFile.getProjectName(), true);
//            }
//            map.put(QPyConstants.ABSOLUTE_PATH + cloudFile.getPath(), true);
//        }
//        ((ExplorerFragment) firstPageFragment).updateCloudedFiles(map);
//    }
//
//    public void setNewUpload() {
//        isNewUpload = true;
//    }

    /**
     * 保存云端文件目录到本地
     */
//    public void locatedCloud(List<CloudFile> cloudFiles) {
//        if (cloudFiles.size() > 0) {
//            Type type = new TypeToken<List<CloudFile>>() {
//            }.getType();
//            FileHelper.writeToFile(CONF.CLOUD_MAP_CACHE_PATH, App.getGson().toJson(cloudFiles, type));
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NotebookUtil.killServer();
    }
}
