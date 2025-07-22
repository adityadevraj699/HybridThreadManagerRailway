package com.railway.reservation.thread;

import org.objectweb.asm.*;

public abstract class SmartTask {

    // 🔧 To be executed via thread manager
    public abstract void runTask();

    // 🚫 No longer running the task here (to avoid double execution)
    // This will only be used by thread pools once assigned properly
}
