package org.qpython.qpy.texteditor;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

import org.qpython.qpy.R;
import org.qpython.qpy.databinding.LayoutPrefsBinding;
import org.qpython.qpy.main.fragment.TedSettingFragment;
import org.qpython.qpy.texteditor.common.Constants;
import org.qpython.qpy.texteditor.common.Settings;
import org.qpython.qpy.texteditor.widget.crouton.Crouton;
import org.qpython.qpy.texteditor.widget.crouton.Style;

public class TedSettingsActivity extends AppCompatActivity implements
		Constants, OnSharedPreferenceChangeListener {

    private TedSettingFragment fragment;
    protected boolean mPreviousHP;
    private LayoutPrefsBinding binding;
    private final String TAG = "TedSettingsActivity";

    public static void start(Context context) {
        Intent starter = new Intent(context, TedSettingsActivity.class);
        context.startActivity(starter);
    }

    /**
     * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
     */

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
        binding = DataBindingUtil.setContentView(this, R.layout.layout_prefs);
        binding.topBar.toolbar.setTitle(R.string.settings_label);
        binding.topBar.toolbar.setNavigationIcon(R.drawable.ic_back);
        binding.topBar.toolbar.setNavigationOnClickListener(v -> finish());

        binding.sampleEditor.updateFromSettings("");
        binding.sampleEditor.setEnabled(false);

        fragment = new TedSettingFragment();

        getFragmentManager().beginTransaction()
                .replace(R.id.setting_fragment, fragment)
                .commit();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        Log.d(TAG, "onSharedPreferenceChanged");
        Settings.updateFromPreferences(sharedPreferences);
        binding.sampleEditor.updateFromSettings("");
        fragment.updateSummaries();

        //CheckBoxPreference checkBox = (CheckBoxPreference) findPreference(PREFERENCE_USE_HOME_PAGE);
        //checkBox.setSummaryOn(Settings.HOME_PAGE_PATH);

        //CheckBoxPreference shareCheck = (CheckBoxPreference) fragment.findPreference(PREFERENCES_GIST);
//        if (Settings.USE_HOME_PAGE && !mPreviousHP) {
//            Intent setHomePage = new Intent();
//            setHomePage.setClass(this, TedOpenActivity.class);
//            setHomePage.putExtra(EXTRA_REQUEST_CODE, REQUEST_HOME_PAGE);
//            try {
//                startActivityForResult(setHomePage, REQUEST_HOME_PAGE);
//            } catch (ActivityNotFoundException e) {
//                Crouton.showText(this, R.string.toast_activity_open,
//                        Style.ALERT);
//            }
//        }

        mPreviousHP = Settings.USE_HOME_PAGE;
    }
}