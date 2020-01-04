#include "SDL.h"
#include "SDL_image.h"
#include "Python.h"
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <jni.h>
#include "android/log.h"
#include "jniwrapperstuff.h"

extern char **environ;

/**
 * This is true if the environment is malfunctioning and we need to work
 * around that.
 */
static int environ_workaround = 0;


SDL_Window *window = NULL;


#define LOG(x) __android_log_write(ANDROID_LOG_INFO, "python", (x))

static PyObject *androidembed_log(PyObject *self, PyObject *args) {
    char *logstr = NULL;
    if (!PyArg_ParseTuple(args, "s", &logstr)) {
        return NULL;
    }
    LOG(logstr);
    Py_RETURN_NONE;
}

static PyObject *androidembed_close_window(PyObject *self, PyObject *args) {
    char *logstr = NULL;
    if (!PyArg_ParseTuple(args, "")) {
        return NULL;
    }

    if (window) {
		SDL_DestroyWindow(window);
		window = NULL;
    }

    Py_RETURN_NONE;
}

static PyMethodDef AndroidEmbedMethods[] = {
	    {"log", androidembed_log, METH_VARARGS, "Log on android platform."},
	    {"close_window", androidembed_close_window, METH_VARARGS, "Close the initial window."},
    {NULL, NULL, 0, NULL}
};

PyMODINIT_FUNC initandroidembed(void) {
    (void) Py_InitModule("androidembed", AndroidEmbedMethods);
}

int file_exists(const char * filename) {
	FILE *file;
    if (file = fopen(filename, "r")) {
        fclose(file);
        return 1;
    }
    return 0;
}

int start_python(void) {
    char *env_argument = NULL;
	char *pri_argument = NULL;
    char *main_py = NULL;


    int ret = 0;
    FILE *fd;

    main_py = getenv("ANDROID_MAIN");

    env_argument = getenv("ANDROID_ARGUMENT");
	pri_argument = getenv("ANDROID_PRIVATE");
    setenv("ANDROID_APP_PATH", env_argument, 1);

    /* The / is required to stop python from doing a search that causes
     * a crash on ARC.
     */
    char python[2048];
    snprintf(python, 2048, "%s/python", env_argument);

    char *args[] = { python, NULL };

	LOG("Initialize QPython pygame for Android");

    //setenv("PYTHONVERBOSE", "2", 1);
    Py_SetProgramName(args[0]);
    Py_Initialize();
    PySys_SetArgvEx(1, args, 0);

    /* ensure threads will work.
     */
    PyEval_InitThreads();

	/* our logging module for android
     */
    initandroidembed();

    /* inject our bootstrap code to redirect python stdin/stdout
     * replace sys.path with our path
     */
    PyRun_SimpleString(
        "import sys, posix\n" \
		"sys.platform='linux2'\n" \
		"private = posix.environ['ANDROID_PRIVATE']\n" \
		"public = posix.environ['ANDROID_PUBLIC']\n" \
		"argument = posix.environ['ANDROID_ARGUMENT']\n" \
        "log_path = posix.environ['ANDROID_LOG']\n" \

		"logfile = '%s' % (log_path,)\n" \
		"sys.path[:] = [ \n" \
        "    private + '/lib/python2.7/site-packages/', \n" \
		"    private + '/lib/python2.7/', \n" \
		"    private + '/lib/python27.zip', \n" \
        "    private + '/lib/notebook.zip', \n" \
        "    private + '/lib/python2.7/qpyutil.zip', \n" \
		"    private + '/lib/python2.7/lib-dynload/', \n" \
		"    public  + '/lib/python2.7/site-packages/', \n"
		"    argument ]\n" \
        "import androidembed\n" \
        "class LogFile(object):\n" \
        "    def __init__(self):\n" \
        "        self.buffer = ''\n" \
        "    def write(self, s):\n" \
        "        s = s.replace(\"\\0\", \"\\\\0\")\n" \
        "        s = self.buffer + s\n" \
        "        lines = s.split(\"\\n\")\n" \
		"        output = open(logfile,\"w\")\n" \
        "        for l in lines[:-1]:\n" \
        "            androidembed.log(l)\n" \
		"            output.write(\"%s\\n\" % (l,))\n" \
		"        output.close()\n" \
        "        self.buffer = lines[-1]\n" \
        "    def flush(self):\n" \
        "        return\n" \
        "sys.stdout = sys.stderr = LogFile()\n" \
		"import site; import qpy #print site.getsitepackages()\n"\
		"#print '2...'\n"\
		"#print '1...'\n"\
		"#print 'Android path', sys.path\n" \
		"import pygame_sdl2\n"\
		"pygame_sdl2.import_as_pygame()\n"\
        "print '# QPython pygame bootstrap done. __name__ is', __name__\n"\
    	"");

    /* run it !
     */
    LOG("Run user program, change dir and execute main.py");
    chdir(env_argument);

	/* search the initial main.py
	 */
    if (! file_exists(main_py) )

        main_py = NULL;

	if ( main_py == NULL ) {
		LOG("No main script found.");
		return 1;
	}

    fd = fopen(main_py, "r");
    if ( fd == NULL ) {
        LOG("Open the main.py failed");
        return 1;
    }

    /* run python !
     */
    ret = PyRun_SimpleFile(fd, main_py);

    if (PyErr_Occurred() != NULL) {
        ret = 1;
        PyErr_Print(); /* This exits with the right code if SystemExit. */
        if (Py_FlushLine())
			PyErr_Clear();
    }

    /* close everything
     */
	Py_Finalize();
    fclose(fd);

    LOG("QPython for android ended.");
    return ret;
}


void init_environ() {
	setenv("TEST_ENV_VAR", "The test worked.", 1);

	JNIEnv* env = (JNIEnv*) SDL_AndroidGetJNIEnv();
	jobject activity = (jobject) SDL_AndroidGetActivity();
	jclass clazz = (*env)->GetObjectClass(env, activity);
	jmethodID method_id = (*env)->GetMethodID(env, clazz, "initEnviron", "()V");
	(*env)->CallVoidMethod(env, activity, method_id);
	(*env)->DeleteLocalRef(env, activity);
	(*env)->DeleteLocalRef(env, clazz);

    char *env_argument;
    env_argument = getenv("ANDROID_ARGUMENT");

    chdir(env_argument);

    if (*environ) {
		return;
	}

	environ_workaround = 1;
	environ = calloc(50, sizeof(char *));
}

//JNIEXPORT void JNICALL JAVA_EXPORT_NAME(PythonSDLActivity_nativeSetEnv) (
JNIEXPORT void JNICALL Java_org_renpy_android_PythonSDLActivity_nativeSetEnv (
		JNIEnv*  env, jobject thiz,
		jstring variable,
		jstring value) {

	jboolean iscopy;
    const char *c_variable = (*env)->GetStringUTFChars(env, variable, &iscopy);
    const char *c_value  = (*env)->GetStringUTFChars(env, value, &iscopy);
    const char buf[2048];
    char **e = environ;

    setenv(c_variable, c_value, 1);

    if (environ_workaround) {
    	snprintf(buf, 2048, "%s=%s", c_variable, c_value);

    	while (*e) {
    		e++;
    	}

    	*e = strdup(buf);
    }
}

void call_prepare_python(void) {
	JNIEnv* env = (JNIEnv*) SDL_AndroidGetJNIEnv();
	jobject activity = (jobject) SDL_AndroidGetActivity();
	jclass clazz = (*env)->GetObjectClass(env, activity);
	jmethodID method_id = (*env)->GetMethodID(env, clazz, "preparePython", "()V");
	(*env)->CallVoidMethod(env, activity, method_id);
	(*env)->DeleteLocalRef(env, activity);
	(*env)->DeleteLocalRef(env, clazz);
}

int SDL_main(int argc, char **argv) {
	SDL_Surface *surface;
	SDL_RWops *rwops = NULL;
	SDL_Surface *presplash = NULL;
	SDL_Surface *presplash2 = NULL;
	SDL_Rect pos;
	Uint32 pixel;

	init_environ();

	if (SDL_Init(SDL_INIT_EVERYTHING) < 0) {
		return 1;
	}

	IMG_Init(IMG_INIT_JPG|IMG_INIT_PNG);

	window = SDL_CreateWindow("pygame_sdl2 starting...", 0, 0, 0, 0, SDL_WINDOW_SHOWN);
	surface = SDL_GetWindowSurface(window);
	pixel = SDL_MapRGB(surface->format, 128, 128, 128);

	rwops = SDL_RWFromFile("android-presplash.png", "r");

	if (!rwops) {
		rwops = SDL_RWFromFile("android-presplash.jpg", "r");
	}

	if (!rwops) goto done;

	presplash = IMG_Load_RW(rwops, 1);
	if (!presplash) goto done;

	presplash2 = SDL_ConvertSurfaceFormat(presplash, SDL_PIXELFORMAT_RGB888, 0);
	Uint8 *pp = (Uint8 *) presplash2->pixels;

#if SDL_BYTEORDER == SDL_LIL_ENDIAN
	pixel = SDL_MapRGB(surface->format, pp[2], pp[1], pp[0]);
#else
	pixel = SDL_MapRGB(surface->format, pp[0], pp[1], pp[2]);
#endif

	SDL_FreeSurface(presplash2);

done:

	SDL_FillRect(surface, NULL, pixel);

	if (presplash) {
		pos.x = (surface->w - presplash->w) / 2;
		pos.y = (surface->h - presplash->h) / 2;
		SDL_BlitSurface(presplash, NULL, surface, &pos);
		SDL_FreeSurface(presplash);
	}

	SDL_UpdateWindowSurface(window);

	SDL_GL_MakeCurrent(NULL, NULL);

	call_prepare_python();

	return start_python();
}
