PROJ_PATH	:= $(call my-dir)
include $(CLEAR_VARS)

#include $(PROJ_PATH)/libjpeg-turbo-1.5.0/Android.mk
include $(PROJ_PATH)/libjpeg-turbo/Android.mk

include $(PROJ_PATH)/libusb/android/jni/Android.mk
include $(PROJ_PATH)/libuvc/android/jni/Android.mk

#include $(V_CORE_DIR)/../VideoTelemetryShared/Android.mk
include $(PROJ_PATH)/uvcintegration/Android.mk
