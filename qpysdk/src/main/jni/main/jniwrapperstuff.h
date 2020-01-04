
/* JNI-C++ wrapper stuff */
#ifndef _JNI_WRAPPER_STUFF_H_
#define _JNI_WRAPPER_STUFF_H_

#ifndef SDL_JAVA_PACKAGE_PATH
#define SDL_JAVA_PACKAGE_PATH org_renpy_android
#endif
#define JAVA_EXPORT_NAME2(name,package) Java_##package##_##name
#define JAVA_EXPORT_NAME1(name,package) JAVA_EXPORT_NAME2(name,package)
#define JAVA_EXPORT_NAME(name) JAVA_EXPORT_NAME1(name,SDL_JAVA_PACKAGE_PATH)

#endif
