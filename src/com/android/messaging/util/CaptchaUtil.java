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

public class CaptchaUtil {

    public static String getCaptcha(String sms) {
        if (sms.length() == 0) {
            return null;
        }

        sms = sms.replaceAll("[a-zA-z]+://[^\\s]*",""); // remove URL
        sms = sms.replaceAll("(\\[.{0,}\\]|【.{0,}】)",""); // remove Sender

        ArrayList<String> sentenceList = cutSentence(sms);

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

    /* 切割句子 */
    private static ArrayList<String> cutSentence(String content) {

        ArrayList<String> sentenceList = new ArrayList<>();
        int left = 0;
        int len = content.length();

        for (int right = 0; right < len; right++) {
            char c = content.charAt(right);
            if (c == ',' || c == '，' ||
                c == '.' || c == '。' ||
                c == '!' || c == '！') {
                sentenceList.add(content.substring(left, right));
                left = right + 1;
            }
        }
        if (left < len) {
            sentenceList.add(content.substring(left, len));
        }
        return sentenceList;
    }

    /* 查找关键字 */
    private static int findKeyWord(String content) {
        String[] keyWords = new String[]{"验证码", "校验码", "認証コード", "码", "碼", " code"};
        int resPos = -1, len = keyWords.length;

        for (int i = 0; i < len; i++) {
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
        int left = -1, right = len;
        // 向右查找
        for (int i = startPos; i < len; i++) {
            if (isCaptchaChar(content.charAt(i))) {
                if (left == -1) {
                    left = i;
                } else {
                    right = i;
                }
            } else {
                if (left != -1) {
                    break;
                }
            }
        }
        if (left == -1 || right - left < 3) {
            // 右边找不到就向左查找
            left = 0; right = -1;
            for (int i = startPos; i >= 0; i--) {
                if (isCaptchaChar(content.charAt(i))) {
                    if (right == -1) {
                        right = i;
                    } else {
                        left = i;
                    }
                } else {
                    if (right != -1) {
                        break;
                    }
                }
            }
        }

        if (left != -1 && right - left >= 3) {
            return content.substring(left, right + 1);
        } else {
            return "";
        }
    }
}
