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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CaptchaUtil {

    private static String[] specialRegex = {
            "【CMK】([0-9]{5})"       // Telegram
    };

    public static String getCaptcha(String sms) {
        if (sms.length() == 0) {
            return null;
        }

        for (String regex : specialRegex) {
            Pattern p = Pattern.compile(regex);
            Matcher matcher = p.matcher(sms);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }

        String[] sentenceList = sms.replaceAll("[a-zA-z]+://[^\\s]*","") // remove URL
                .replaceAll("(\\[.{0,}\\]|【.{0,}】)","")                // remove sender
                .split("[.。;；!！]");

        int keyPos = 0;
        String code = "";

        for (String str : sentenceList) {
            keyPos = findKeyWord(str);
            if (keyPos != -1) {
                code = cutCaptchaCode(str, keyPos);
                if (!TextUtils.isEmpty(code)) {
                    break;
                }
            }
        }

        return code;
    }

    /* 查找关键字 */
    private static int findKeyWord(String content) {
        String[] keyWords = new String[]{"码", "碼", "コード", " code"};
        int resPos = -1;
        content = content.toLowerCase();

        for (int i = 0; i < keyWords.length; i++) {
            resPos = content.indexOf(keyWords[i]);
            if (resPos != -1) {
                break;
            }
        }

        return resPos;
    }

    private static boolean isCaptchaChar(char c) {
        return (c >= '0' && c <= '9');
    }

    private static String cutCaptchaCode(String content, int startPos) {
        int len = content.length();
        StringBuilder sb = new StringBuilder();

        // 向右查找
        for (int i = startPos; i < len; i++) {
            if (isCaptchaChar(content.charAt(i))) {
                sb.append(content.charAt(i));
            } else {
                if (sb.length() > 3) {
                    break;
                } else {
                    sb.delete(0, sb.length());
                }
            }
        }
        if (sb.length() > 3) {
            return sb.toString();
        } else {
            // 右边找不到就向左查找
            sb.delete(0, sb.length());
            for (int i = startPos; i >= 0; i--) {
                if (isCaptchaChar(content.charAt(i))) {
                    sb.append(content.charAt(i));
                } else {
                    if (sb.length() > 3) {
                        break;
                    } else {
                        sb.delete(0, sb.length());
                    }
                }
            }
        }
        if (sb.length() > 3) {
           return sb.reverse().toString();
        } else {
            return null;
        }
    }
}
