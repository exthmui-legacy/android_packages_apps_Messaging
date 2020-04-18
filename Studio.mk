LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := MessagingStudio
LOCAL_MODULE_CLASS := FAKE
LOCAL_MODULE_SUFFIX := -timestamp

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

messaging_library_replaces := $(abspath $(messaging_system_res_path))/colorpicker:com.android.colorpicker:'\\\"res\\\"' \
	$(abspath $(messaging_system_res_path))/libchips:com.android.ex.chips:'\\\"res\\\"' \
                           $(abspath $(messaging_system_res_path))/libphotoviewer:com.android.ex.photo:'\\\"res\\\",\\\"activity\\\/res\\\"'

include $(BUILD_SYSTEM)/base_rules.mk

$(LOCAL_BUILT_MODULE): $(messaging_system_libs_deps) $(shell echo -n $(messaging_system_res_deps) | xargs -d" " -I{target} sh -c "echo {target} | awk -F \":\" '{print \$$(NF-1)}'")
	$(hide) mkdir -p $(messaging_system_libs_path)
	$(hide) rm -rf $(messaging_system_libs_path)/*.jar
	# Copy jars
	$(hide) echo -n $(messaging_system_libs_deps) | xargs -d" " -n1 -I{target} sh -c "cp {target} $(messaging_system_libs_path)/\`echo {target} | awk -F \"/\" '{gsub(/\_intermediates/,\"\"); print \$$(NF-1)}'\`.jar"
	
	$(hide) mkdir -p $(messaging_system_res_path)
	$(hide) rm -rf $(messaging_system_res_path)/*
	# Makedirs of library modules
	$(hide) echo -n $(messaging_system_res_deps) | xargs -d" " -n1 -I{target} sh -c "mkdir -p $(messaging_system_res_path)/\`echo {target} | awk -F \":\" '{print \$$(NF)}'\`"
	# Copy resources of library modules
	$(hide) echo -n $(messaging_system_res_deps) | xargs -d" " -n1 -I{target} sh -c "cp -r \`echo {target} | awk -F \":\" '{print \$$(NF-1)}'\`/* $(messaging_system_res_path)/\`echo {target} | awk -F \":\" '{print \$$(NF)}'\`"
	# Replace packagename and resources_dirs
	$(hide) echo -n $(messaging_library_replaces) | xargs -d" " -n1 -I{target} sh -c "sed  -i -e \"s/{resources_dirs}/\`echo {target} | awk -F \":\" '{print \$$(NF)}'\`/g\" \`echo {target} | awk -F \":\" '{print \$$(NF-2)}'\`/build.gradle && sed  -i -e \"s/{resources_dirs}/\`echo {target} | awk -F \":\" '{print \$$(NF-1)}'\`/g\" \`echo {target} | awk -F \":\" '{print \$$(NF-2)}'\`/AndroidManifest.xml"
	$(hide) echo "Fake: $@"
	$(hide) mkdir -p $(dir $@)
	$(hide) touch $@
