/*
 * Copyright 2021 The exTHmUI Open Source Project
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

package com.android.messaging.ui.appsettings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.provider.Settings;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

import java.util.Arrays;

import com.android.messaging.R;
import com.android.messaging.util.BuglePrefs;
import com.android.messaging.util.TextUtil;

import java.util.ArrayList;

public class CaptchaKeywordsPreference extends PreferenceCategory implements
        Preference.OnPreferenceClickListener {

    private Context mContext;

    private Preference mAddStringPref;

    private ArrayList<String> mKeywords = new ArrayList<>();

    private BuglePrefs mPrefs;
    private String mDefaultKeywordString;

    public CaptchaKeywordsPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mPrefs = BuglePrefs.getApplicationPrefs();

        mAddStringPref = makeAddPref();

        mDefaultKeywordString = mContext.getResources().getString(R.string.captcha_keywords_default);

        this.setOrderingAsAdded(false);
    }

    private Preference makeAddPref() {
        Preference pref = new Preference(mContext);
        pref.setTitle(R.string.add_captcha_keyword);
        pref.setIcon(R.drawable.ic_add);
        pref.setPersistent(false);
        pref.setOnPreferenceClickListener(this);
        return pref;
    }

    private void parseKeywordsList() {
        mKeywords.clear();
        String keyWordsData = mPrefs.getString(getKey(), mDefaultKeywordString);
        if (TextUtils.isEmpty(keyWordsData)) keyWordsData = mDefaultKeywordString;

        String[] keyWordsListArray = keyWordsData.split("\n");
        mKeywords.addAll(Arrays.asList(keyWordsListArray));        
    }

    private void refreshKeywordPrefs() {
        parseKeywordsList();
        removeAll();
        for (String str : mKeywords) {
            addStringToPref(str);
        }
        addPreference(mAddStringPref);
    }

    private void saveKeywordsList() {
        String keywordListData = String.join("\n", mKeywords);
        mPrefs.putString(getKey(), keywordListData);
    }

    private static String getKeyOfString(String str) {
        return "key_" + str.hashCode();
    }

    private void addStringToPref(String str) {
        Preference pref = new Preference(mContext);
        pref.setKey(getKeyOfString(str));
        pref.setTitle(str);
        pref.setPersistent(false);
        pref.setOnPreferenceClickListener(this);
        addPreference(pref);
    }

    private void addStringToList(String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        if (!mKeywords.contains(str)) {
            mKeywords.add(str);
            addStringToPref(str);
        }
    }

    private void removeStringFromList(String str) {
        mKeywords.remove(str);
        saveKeywordsList();
    }

    @Override
    public void onAttachedToActivity() {
        super.onAttachedToActivity();
        refreshKeywordPrefs();
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        if (preference == mAddStringPref) {
            final EditText editText = new EditText(mContext);
            builder.setTitle(R.string.add_captcha_keyword)
                .setView(editText)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!TextUtils.isEmpty(editText.getText())) {
                            String[] strs = editText.getText().toString().split("\n");
                            for (String str : strs) {
                                addStringToList(str);
                            }
                            saveKeywordsList();
                        }
                    }
                })
                .show();
        } else {
            builder.setTitle(R.string.dialog_delete_title)
                .setMessage(R.string.dialog_delete_message)
                .setIconAttribute(android.R.attr.alertDialogIcon)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeStringFromList(preference.getTitle().toString());
                        removePreference(preference);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
        }
        return true;
    }

}