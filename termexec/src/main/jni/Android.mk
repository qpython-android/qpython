LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := termexec
LOCAL_LDFLAGS := -Wl,--build-id
LOCAL_LDLIBS := \
	-llog \
	-lc \

LOCAL_SRC_FILES := \
	process.cpp \

LOCAL_C_INCLUDES += ../jni

include $(BUILD_SHARED_LIBRARY)
