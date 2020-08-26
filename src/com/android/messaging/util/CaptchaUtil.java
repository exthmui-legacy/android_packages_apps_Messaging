/*
 * Copyright 2020 exTHmUI Team
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

package com.android.messaging.util;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CaptchaUtil {

    /* 匹配度 */
    private static final int RANK_DIGITAL_6 = 4;        // 6位纯数字
    private static final int RANK_DIGITAL_4 = 3;        // 4位纯数字
    private static final int RANK_DIGITAL_OTHERS = 2;   // 纯数字
    private static final int RANK_DIGITAL_LETTERS = 1;  // 数字 + 字母
    private static final int RANK_NONE = -1;            // 不是验证码

    /* 关键字 */
    private static final String[] KEYWORDS = {"码", "碼", "口令", "コード", "PIN", "code", "Code", "CODE"};
    /* 提取验证码的正则 */
    private static final Pattern CODE_REGEX = Pattern.compile("(?<![a-zA-Z0-9])[a-zA-Z0-9]{4,8}(?![a-zA-Z0-9])");
    /* 特判正则 */
    private static final String[] SPECIAL_REGEX = {
            "【CMK】([0-9]{5})"       // Telegram
    };

    public static String getCaptcha(String content) {
        if (TextUtils.isEmpty(content)) return null;

        content = content.replaceAll("[a-zA-z]+://[^\\s]*","");  // remove URL

        // 特判
        for (String regex : SPECIAL_REGEX) {
            Pattern p = Pattern.compile(regex);
            Matcher matcher = p.matcher(content);
            if (matcher.find()) {
                return matcher.group();
            }
        }

        // 判断是否包含关键字
        String keyword = null;
        for (String word : KEYWORDS) {
            if (content.contains(word)) {
                keyword = word;
                break;
            }
        }
        if (keyword == null) {
            return null;
        }

        Matcher captchaMatcher = CODE_REGEX.matcher(content);

        ArrayList<String> matchedCodes = new ArrayList<>();
        while (captchaMatcher.find()) {
            matchedCodes.add(captchaMatcher.group());
        }
        if (matchedCodes.isEmpty()) {
            return null;
        }

        int maxRank = RANK_NONE;
        int minDistance = content.length();
        String curCode = null;

        for (String code : matchedCodes) {
            int curRank = getMatchRank(code);
            int curDistance = getDistanceToKeyword(keyword, code, content);
            if (curRank > maxRank) {
                maxRank = curRank;
                minDistance = curDistance;
                curCode = code;
            } else if (curRank == maxRank && curDistance < minDistance) {
                minDistance = curDistance;
                curCode = code;
            }
        }

        return curCode;
    }

    /**
     * 取得匹配度
     */
    private static int getMatchRank(String matchedStr) {
        if (matchedStr.matches("^[0-9]{6}$"))
            return RANK_DIGITAL_6;
        if (matchedStr.matches("^[0-9]{4}$"))
            return RANK_DIGITAL_4;
        if (matchedStr.matches("^[0-9]*$"))
            return RANK_DIGITAL_OTHERS;
        if (matchedStr.matches("^[a-zA-Z]*$"))
            return RANK_NONE;
        return RANK_DIGITAL_LETTERS;
    }

    /**
     * 计算验证码与关键字的距离
     */
    private static int getDistanceToKeyword(String keyword, String code, String content) {
        int keywordIdx = content.indexOf(keyword);
        int possibleCodeIdx = content.indexOf(code);
        return Math.abs(keywordIdx - possibleCodeIdx);
    }

}
