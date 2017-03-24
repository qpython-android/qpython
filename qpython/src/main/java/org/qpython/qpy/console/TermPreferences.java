package org.qpython.qpy.console;/*
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


import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.view.MenuItem;

import org.qpython.qpy.R;

import org.qpython.qpy.console.compont.ActionBarCompat;
import org.qpython.qpy.console.compont.ActivityCompat;
import org.qpython.qpy.console.compont.AndroidCompat;


public class TermPreferences extends PreferenceActivity {
    private static final String ACTIONBAR_KEY = "actionbar";
    private static final String CATEGORY_SCREEN_KEY = "screen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the console_preferences from an XML resource
        addPreferencesFromResource(R.xml.console_preferences);

        // Remove the action bar pref on older platforms without an action bar
        if (AndroidCompat.SDK < 11) {
            Preference actionBarPref = findPreference(ACTIONBAR_KEY);
            PreferenceCategory screenCategory =
                    (PreferenceCategory) findPreference(CATEGORY_SCREEN_KEY);
            if ((actionBarPref != null) && (screenCategory != null)) {
                screenCategory.removePreference(actionBarPref);
            }
        }

        // Display up indicator on action bar home button
        if (AndroidCompat.V11ToV20) {
            ActionBarCompat bar = ActivityCompat.getActionBar(this);
            if (bar != null) {
                bar.setDisplayOptions(ActionBarCompat.DISPLAY_HOME_AS_UP, ActionBarCompat.DISPLAY_HOME_AS_UP);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case ActionBarCompat.ID_HOME:
                // Action bar home button selected
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
