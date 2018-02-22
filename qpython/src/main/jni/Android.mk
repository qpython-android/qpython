LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := androidterm
LOCAL_LDFLAGS := -Wl,--build-id
LOCAL_LDLIBS := \
	-llog \

LOCAL_SRC_FILES := \
	Android.mk \
	common.cpp \
	fileCompat.cpp \
	termExec.cpp \

LOCAL_C_INCLUDES += ../jni

include $(BUILD_SHARED_LIBRARY)
