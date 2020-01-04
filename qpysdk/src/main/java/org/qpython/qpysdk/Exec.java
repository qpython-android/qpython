/*
 * Copyright (C) 2007 The Android Open Source Project
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

package org.qpython.qpysdk;

import java.io.FileDescriptor;

/**
 * Tools for executing commands.
 */
public class Exec {
  /**
   * @param command
   *          The command to execute
   * @param arguments
   *          The first argument to the command, may be null
   * @param environmentVariables
   *          the second argument to the command, may be null
   * @param workingDirectory
   * @return the file descriptor of the started process.
   * 
   */
  public static FileDescriptor createSubprocess(String command, String[] arguments,
      String[] environmentVariables, String workingDirectory) {
    return createSubprocess(command, arguments, environmentVariables, workingDirectory, null);
  }

  /**
   * @param command
   *          The command to execute
   * @param arguments
   *          Array of arguments, may be null
   * @param environmentVariables
   *          Array of environment variables, may be null
   * @param processId
   *          A one-element array to which the process ID of the started process will be written.
   * @return the file descriptor of the opened process's psuedo-terminal.
   * 
   */
  public static native FileDescriptor createSubprocess(String command, String[] arguments,
      String[] environmentVariables, String workingDirectory, int[] processId);

  public static native void setPtyWindowSize(FileDescriptor fd, int row, int col, int xpixel,
      int ypixel);

  /**
   * Causes the calling thread to wait for the process associated with the receiver to finish
   * executing.
   * 
   * @return The exit value of the Process being waited on
   * 
   */
  public static native int waitFor(int processId);
  public static native void nativeSetEnv(String name, String value);
  public static native void nativeInit();

  static {
    System.loadLibrary("qpysdk");
  }
}
