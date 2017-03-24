package org.qpython.qpy.main.event;

public class RunProgramEvent {
    public final String path;
    public final boolean isProject;

    public RunProgramEvent( String path, boolean isProject) {
        this.path = path;
        this.isProject = isProject;
    }

}
