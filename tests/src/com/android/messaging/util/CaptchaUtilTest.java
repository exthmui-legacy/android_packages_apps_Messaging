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

import android.test.suitebuilder.annotation.SmallTest;

import com.android.messaging.BugleTestCase;

/*
 * Class for testing CaptchaUtil.
 */
@SmallTest
public class CaptchaUtilTest extends BugleTestCase {
    public void testCaptchaGet() {
        assertEquals(CaptchaUtil.getCaptcha("145879 is your Twitter login code."),
                "145879");
        assertEquals(CaptchaUtil.getCaptcha("你的 Tumblr 驗證碼為「145879」，此碼將會在兩分鐘後失效。"),
                "145879");
        assertEquals(CaptchaUtil.getCaptcha("【饿了么】您的验证码是917440，在5分钟内有效。如非本人操作请忽略本短信。"),
                "917440");
        assertEquals(CaptchaUtil.getCaptcha("验证码347828，下载尚WiFi客户端，一键免费上网更便捷，点击http://t.cn/RhpFIU2 下载，赠送更多免费时长【验证助手】"),
                "347828");
        assertEquals(CaptchaUtil.getCaptcha("12306用户注册或既有用户手机核验专用验证码：060973。如非本人直接访问12306，请停止操作，切勿将验证码提供给第三方。【铁路客服】"),
                "060973");
        assertEquals(CaptchaUtil.getCaptcha("【美团网】986359（登录验证码）。工作人员不会向您索要，请勿向任何人泄露。"),
                "370000");
        assertEquals(CaptchaUtil.getCaptcha("G-282391 是您的 Google 验证码"),
                "282391");

        assertNull(CaptchaUtil.getCaptcha("这是一条普通短信～"));
        assertNull(CaptchaUtil.getCaptcha("这不是验证码短信～"));
    }
}