package org.qpython.qpy.main.utils;


import org.greenrobot.eventbus.EventBus;

public class Bus {
    private static final EventBus INSTANCE = new EventBus();

    public static EventBus getDefault() {
        return INSTANCE;
    }
}
