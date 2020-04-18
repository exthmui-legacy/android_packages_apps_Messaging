LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := MessagingStudio
LOCAL_MODULE_CLASS := FAKE
LOCAL_MODULE_SUFFIX := -timestamp

gen_studio_tool_path := $(abspath $(LOCAL_PATH))/gen-studio.sh

messaging_system_libs_path := $(abspath $(LOCAL_PATH))/system_libs
messaging_system_libs_deps := $(call java-lib-deps,colorpicker) \
                           $(call java-lib-deps,libchips) \
                           $(call java-lib-deps,libphotoviewer) \
                           $(call java-lib-deps,android-common) \
                           $(call java-lib-deps,android-common-framesequence) \
                           $(call java-lib-deps,com.android.vcard)

messaging_system_res_path := $(abspath $(LOCAL_PATH))/system_res
messaging_system_res_deps := $(abspath $(TOPDIR)frameworks/opt/colorpicker/res):colorpicker/res \
                          $(abspath $(TOPDIR)frameworks/opt/chips/res):libchips/res \
                          $(abspath $(TOPDIR)frameworks/opt/photoviewer/res):libphotoviewer/res \
                          $(abspath $(TOPDIR)frameworks/opt/photoviewer/activity/res):libphotoviewer/activity/res \
                          $(abspath $(LOCAL_PATH)/androidLibraryTemplate):colorpicker \
                          $(abspath $(LOCAL_PATH)/androidLibraryTemplate):libchips \
                          $(abspath $(LOCAL_PATH)/androidLibraryTemplate):libphotoviewer

messaging_library_replaces := $(abspath $(messaging_system_res_path))/colorpicker:com.android.colorpicker:'"res"' \
	$(abspath $(messaging_system_res_path))/libchips:com.android.ex.chips:'"res"' \
                           $(abspath $(messaging_system_res_path))/libphotoviewer:com.android.ex.photo:'"res"','"activity\/res"'

include $(BUILD_SYSTEM)/base_rules.mk

$(LOCAL_BUILT_MODULE): $(messaging_system_libs_deps) 
	$(hide) $(gen_studio_tool_path) "$(messaging_system_libs_path)" "$(messaging_system_libs_deps)" "$(messaging_system_res_path)" "$(messaging_system_res_deps)" "$(messaging_library_replaces)" 
	$(hide) echo "Fake: $@"
	$(hide) mkdir -p $(dir $@)
	$(hide) touch $@
