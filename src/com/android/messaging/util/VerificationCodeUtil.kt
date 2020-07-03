/*
 * Copyright 2020 exTHmUI Team
 * Contributor @1552980358
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

package com.android.messaging.util

class VerificationCodeUtil {
    
    companion object {
        private const val EMPTY = ""
        
        @JvmStatic
        fun getVerificationCode(msg: String?): String? {
            if (msg.isNullOrEmpty()) {
                return null
            }
            
            val new = msg.replace(Regex("[a-zA-z]+://[^\\s]*"), EMPTY).replace(Regex("(\\[.*]|【.*】)"), EMPTY)
            
            // Nothing else after replacement
            if (new.isEmpty()) {
                return null
            }
            
            // 删除冗余空白行数
            val list = ArrayList<String>()
            new.split(",", "，", ".", "。", "!", "！").toMutableList().forEach { section ->
                if (section.isNotEmpty() && containsNumber(section)) {
                    list.add(section)
                }
            }
            
            var code: String?
            
            for (section in list) {
                code = getCode(section)
                if (code.isNullOrEmpty()) {
                    continue
                }
                return code
            }
            return null
        }
        
        private fun getCode(section: String): String? {
            var index = -1
            for ((i, j) in section.withIndex()) {
                if (j in '0' .. '9') {
                    index = i
                    break
                }
            }
            
            if (index == -1) {
                return null
            }
            
            val tmp = StringBuilder()
            for (i in index .. section.lastIndex) {
                if (section[i] !in '0' .. '9') {
                    break
                }
                tmp.append(section[i])
            }
            
            return if (tmp.isEmpty()) null else tmp.toString()
        }
        
        private fun containsNumber(section: String): Boolean {
            section.forEach {  char ->
                if (char in '0' .. '9') {
                    return true
                }
            }
            return false
        }
        
    }
    
}