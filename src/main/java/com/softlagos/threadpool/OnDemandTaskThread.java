/**
 * Copyright (C) 1999-2016 Rubens Gomes <rubens.s.gomes@gmail.com>.
 * All Rights Reserved.
 *
 * File: OnDemandTaskThread.java
 *
 * Author: Rubens Gomes
 */
package com.softlagos.threadpool;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.softlagos.Constants;
import com.softlagos.util.SystemProperties;

/**
 * An instance of this task represents a thread that is spawned using an
 * on demand thread strategy.  This is a self starting thread that gets
 * launched to run the given task.
 * <p>
 * This class should be instantiated as follows:
 * <p>
 * <pre>
 * OnDemandTaskThread task_thread =
 *   new OnDemandTaskThread(someTask);
 * task_thread.start();
 *
 * task_thread.stopMe(); // only if needed.
 * </pre>
 *
 * @author Rubens Gomes
 */
public final class OnDemandTaskThread extends Thread
{
    private static final Logger logger =
            LogManager.getLogger(OnDemandTaskThread.class);

    /**
     * Instantiates a new on demand task thread.
     *
     * @param task the task to run on its own thread.
     */
    public OnDemandTaskThread(final Task task)
    {

        if (task == null)
        {
            throw new IllegalArgumentException("task cannot be null.");
        }

        if(logger.isTraceEnabled())
        {
            logger.trace("instantiating...");
        }

        v_task = task;
        v_stop_me = false;

        try
        {

            if(logger.isTraceEnabled())
            {
                logger.trace("joining child thread.");
            }

            // need to join this thread because we want the starting
            // thread to wait for the newly launched thread to terminate.
            this.join();
        }
        catch(InterruptedException ex)
        {

            if(logger.isInfoEnabled())
            {
                logger.info("thread interrupted: " + ex.getMessage());
            }

            v_stop_me = true;
        }

    }

    /* (non-Javadoc)
     * @see java.lang.Thread#start()
     */
    @Override
    public void start()
    {

        if( !v_stop_me )
        {
            if(logger.isTraceEnabled())
            {
                logger.trace("launching thread.");
            }

            super.start();

            try
            {

                SystemProperties props = SystemProperties.instance();
                int start_time = props.getPropertyAsInt(Constants.THREADPOOL_START_UP_TIME);

                if(logger.isTraceEnabled())
                {
                    logger.trace("sleeping for [" +
                            start_time + "] msecs");
                }

                // I need to put myself to sleep for awhile to give the
                // other thread a chance to start nicely.
                Thread.sleep(start_time);
            }
            catch(InterruptedException ex)
            {

                if(logger.isInfoEnabled())
                {
                    logger.info("interrupted exception: " + ex.getMessage());
                }

                v_stop_me = true;
            }

            if(logger.isTraceEnabled())
            {
                logger.trace("constructed.");
            }

        }

    }

    /**
     * The thread will run this method after being started.
     */
    @Override
    public void run()
    {

        if(logger.isTraceEnabled())
        {
            logger.trace("thread run called.");
        }

        while (!v_stop_me)
        {

            try
            {
                // depending on what run does, it may cause the thread to go
                // into an "wait" state.  And if the thread is in "wait" state, it
                // may be "woken up" by an interrupt.

                if(logger.isTraceEnabled())
                {
                    logger.trace("thread checking isInterrupted");
                }

                if (Thread.currentThread().isInterrupted())
                {

                    if(logger.isTraceEnabled())
                    {
                        logger.trace("thread interrupted.");
                    }

                    throw new InterruptedException("I have been interrupted");
                }

                if(logger.isTraceEnabled())
                {
                    logger.trace("calling task run()...");
                }

                v_task.run();
                v_task.notifyListeners(Task.Status.DONE,  "Done");
                v_stop_me = true;
            }
            catch(InterruptedException ex)
            {

                if(logger.isInfoEnabled())
                {
                    logger.info("handling interrupt: " + ex.getMessage());
                }

                v_task.notifyListeners(Task.Status.INTERRUPTED,
                                       ex.getMessage());
                v_stop_me = true;
            }
            catch(Exception ex)
            {
                if(logger.isInfoEnabled())
                {
                    logger.info("handling some error: " + ex.getMessage());
                }

                v_task.notifyListeners(Task.Status.FAILED,
                                       ex.getMessage());
                v_stop_me = true;
            }
        }
    }

    /**
     * sets a flag to stop this task thread.
     */
    public void stopMe()
    {
        if(logger.isInfoEnabled())
        {
            logger.info("interrupting ");
        }

        this.interrupt();

        SystemProperties props = SystemProperties.instance();
        int shutdown_time = props
                .getPropertyAsInt(Constants.THREADPOOL_SHUTDOWN_WAIT_TIME);

        if(logger.isTraceEnabled())
        {
            logger.trace("sleeping for [" +
                    shutdown_time + "] msecs");
        }

        try
        {
            // I need to put myself to sleep for awhile to give the
            // other threads a chance to stop themselves nicely.
            Thread .sleep(shutdown_time);
        }
        catch(InterruptedException ex)
        {

            if(logger.isInfoEnabled())
            {
                logger.info("interrupted exception: " + ex.getMessage());
            }

        }

        v_stop_me = true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#finalize()
     */
    @Override
    public void finalize()
    {
        if(logger.isTraceEnabled())
        {
            logger.trace("finalize called.");
        }

        if(logger.isTraceEnabled())
        {
            logger.trace("calling stopMe()...");
        }

        stopMe();

        if(logger.isTraceEnabled())
        {
            logger.trace("GC collected.");
        }
    }

    // ---------->> Private <<---------- //

    private final Task v_task;
    private volatile boolean v_stop_me;
}
