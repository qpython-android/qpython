package org.qpython.qsl4a.qsl4a.interpreter;

import java.io.FileDescriptor;

public interface InProcessInterpreter {
  public FileDescriptor getStdOut();

  public FileDescriptor getStdIn();

  public boolean runInteractive();

  public void runScript(String filename);
}
