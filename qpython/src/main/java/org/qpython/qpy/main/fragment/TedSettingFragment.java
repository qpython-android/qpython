package org.qpython.qpy.main.fragment;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import org.qpython.qpy.R;
import org.qpython.qpy.texteditor.TedFontActivity;
import org.qpython.qpy.texteditor.TedSettingsActivity;
import org.qpython.qpy.texteditor.common.Settings;
import org.qpython.qpy.texteditor.widget.crouton.Crouton;
import org.qpython.qpy.texteditor.widget.crouton.Style;

import static org.qpython.qpy.texteditor.common.Constants.PREFERENCE_COLOR_THEME;
import static org.qpython.qpy.texteditor.common.Constants.PREFERENCE_MAX_LINES_NUM_WITH_SYNTAX;
import static org.qpython.qpy.texteditor.common.Constants.PREFERENCE_SELECT_FONT;
import static org.qpython.qpy.texteditor.common.Constants.PREFERENCE_TEXT_SIZE;
import static org.qpython.qpy.texteditor.common.Constants.REQUEST_FONT;

/**
 * Created by Hmei
 * 1/15/18.
 */

public class TedSettingFragment extends PreferenceFragment {
    private TedSettingsActivity activity;
    private String TAG = "TedSettingFragment";

    @Override
    public void onAttach(Activity activity) {
        Log.d(TAG, "onAttach activity");
        super.onAttach(activity);
        this.activity = (TedSettingsActivity) activity;

    }
        @Override
    public void onAttach(Context context) {
        Log.d(TAG, "onAttach context");
        super.onAttach(context);
        activity = (TedSettingsActivity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.ted_prefs);

        getPreferenceManager().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(activity);

        Settings.updateFromPreferences(getPreferenceManager()
                .getSharedPreferences());

        findPreference(PREFERENCE_SELECT_FONT).setOnPreferenceClickListener(
                preference -> {
                    try {
                        Intent selectFont = new Intent();
                        selectFont.setClass(this.getActivity().getApplicationContext(),
                                TedFontActivity.class);
                        try {
                            startActivityForResult(selectFont, REQUEST_FONT);
                        } catch (ActivityNotFoundException e) {
                            Crouton.showText(this.getActivity(),
                                    R.string.toast_activity_open, Style.ALERT);
                        }
                    } catch (Exception e) {
                        Toast.makeText(this.getActivity(),"Exception occured when setting fonts, please retry or report to dev",Toast.LENGTH_SHORT).show();
                    }
                    return true;
                });
        updateSummaries();
    }

    public void updateSummaries() {
        Log.d("TedSettingFragment", "updateSummaries");

        ListPreference listPref = (ListPreference) findPreference(PREFERENCE_COLOR_THEME);
        listPref.setSummary(listPref.getEntry());

        ListPreference textSize = (ListPreference) findPreference(PREFERENCE_TEXT_SIZE);
        textSize.setSummary(textSize.getEntry());

        ListPreference maxLine = (ListPreference) findPreference(PREFERENCE_MAX_LINES_NUM_WITH_SYNTAX);

        try {
            // DON'T CHANGE THIS LINE
            if (maxLine!=null) {
                maxLine.setSummary(activity.getString(R.string.max_lines_num_with_syntax_summary, maxLine.getEntry()));
            }
        } catch (Exception ignore) {
        }
    }

}
