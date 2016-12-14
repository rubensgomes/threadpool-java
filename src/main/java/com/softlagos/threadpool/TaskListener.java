/**
 * Copyright (C) 1999-2016 Rubens Gomes <rubens.s.gomes@gmail.com>.
 * All Rights Reserved.
 *
 * File: TaskListener.java
 *
 * Author: Rubens Gomes
 */
package com.softlagos.threadpool;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A listener that is interested in knowing when its task is done
 * running, or when task fails or is interrupted.
 *
 * @author Rubens Gomes
 */
public abstract class TaskListener
{

    private static final Logger logger =
            LogManager.getLogger(TaskListener.class);

    /**
     * The listener will get a call back on this method when the
     * corresponding task is completed running.
     *
     * @param msg some informational message.
     */
    public abstract void notifyTaskDone(String msg);

    /**
     * The listener will get a call back on this method when the
     * corresponding task was interrupted while executing.  This
     * could occur if the task was placed on "wait" pending on
     * some I/O (for example), and got interrupted by the
     * corresponding thread manager.
     *
     * @param msg some informational message.
     */
    public abstract void notifyTaskInterrupted(String msg);

    /**
     * The listener will get a call back on this method when the
     * corresponding task failed during execution.
     *
     * @param msg some informational message.
     */
    public abstract void notifyTaskFailed(String msg);

    /* (non-Javadoc)
     * @see java.lang.Object#finalize()
     */
    @Override
    public void finalize()
    {
        if(logger.isTraceEnabled())
        {
            logger.trace("GC collected.");
        }
    }
}
