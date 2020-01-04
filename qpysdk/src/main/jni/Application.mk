APP_PROJECT_PATH := $(call my-dir)/..

ifneq ($(filter $(NDK_KNOWN_DEVICE_ABI64S),$(TARGET_ARCH_ABI)),)

APP_MODULES := qpysdk main

else

APP_MODULES := qpysdk SDL2 png16 jpeg libwebp SDL2_image SDL2_gfx SDL2_ttf SDL2_mixer freetype main

endif

APP_ABI := $(ARCH)
APP_STL := c++_static
APP_CFLAGS += $(OFLAG)

APP_ABI := armeabi-v7a arm64-v8a
APP_PLATFORM := android-15
