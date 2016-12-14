/**
 * Copyright (C) 1999-2016 Rubens Gomes <rubens.s.gomes@gmail.com>.
 * All Rights Reserved.
 *
 * File: Task.java
 *
 * Author: Rubens Gomes
 */
package com.softlagos.threadpool;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A task to be executed in a separate thread.
 *
 * Clients of the "ThreadPool" will add materialized instances of the
 * Task class into the thread pool queue.  Whatever the task needs to
 * perform should be implemented inside the run() method.
 *
 * Once the task is completed the task will notify any registered
 * listener.
 *
 * @author Rubens Gomes
 */
public abstract class Task
    implements Runnable
{

    private static final Logger logger =
            LogManager.getLogger(Task.class);

    public enum Status
    {
        DONE,
        INTERRUPTED,
        FAILED;
    };

    /**
     * This method should contain whatever code the task needs to
     * execute in its own thread.
     */
    public abstract void run();

    /**
     * Register a listener that will be notified once the task is done
     * running.
     *
     * @param listener adds a listener to be notified once the task is
     * done running.
     */
    public void addListener(TaskListener listener)
    {

        if(logger.isTraceEnabled())
        {
            logger.trace("Adding listener.");
        }

        v_notifiers.add(listener);
    }

    /**
     * Un-registers a previously registered listener with this task.
     *
     * @param listener the listener to be removed from this task
     * listeners registry.
     */
    public void removeListener(TaskListener listener)
    {
        if(logger.isTraceEnabled())
        {
            logger.trace("Removing listener.");
        }

        v_notifiers.remove(listener);
    }

    /**
     * Notifies its listeners if a task is done or not.  A task may
     * not complete running in case it is interrupted while
     * waiting for some resource.  In that case, the task may
     * want to notify its listeners setting done to false.
     *
     * @param status the status of the task (completed,
     * interrupted, failed)
     * @param msg some informational messaging.
     */
    public void notifyListeners(Status status, String msg)
    {
        for(TaskListener listener : v_notifiers)
        {
            switch(status)
            {
                case DONE:
                    if(logger.isTraceEnabled())
                    {
                        logger.trace("Notifying listener: DONE.");
                    }
                    listener.notifyTaskDone(msg);
                    break;

                case INTERRUPTED:
                    if(logger.isTraceEnabled())
                    {
                        logger.trace("Notifying listener: INTERRUPTED.");
                    }
                    listener.notifyTaskInterrupted(msg);
                    break;

                case FAILED:
                    if(logger.isTraceEnabled())
                    {
                        logger.trace("Notifying listener: FAILED.");
                    }
                    listener.notifyTaskFailed(msg);
                    break;

                default:
                    if(logger.isErrorEnabled())
                    {
                        logger.error("Invalid Task status.");
                    }
            }
        }
    }

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

    // ------ >>> Protected <<< ------
    protected Task()
    {
        v_notifiers = new ArrayList<TaskListener>();

        if(logger.isTraceEnabled())
        {
            logger.trace("constructed.");
        }
    }

    // ------ >>> Private <<< ------

    /** The v_notifiers. */
    private final List<TaskListener> v_notifiers;
}
