/**
 * Copyright (C) 1999-2016 Rubens Gomes <rubens.s.gomes@gmail.com>.
 * All Rights Reserved.
 *
 * File: TaskThread.java
 *
 * Author: Rubens Gomes
 */
package com.softlagos.threadpool;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class is intended to be instantiated as a Thread from
 * the TrheadPool.  It is used to execute tasks that are
 * added to the ThreadPool task queue.
 *
 * @author Rubens Gomes
 */
public final class TaskThread implements Runnable
{
    private static final Logger logger =
            LogManager.getLogger(TaskThread.class);

    /**
     * Instantiates a new task thread.
     */
    public TaskThread()
    {
        v_is_stopped = false;
        v_id = "";  // will be assigned within run.

        if(logger.isTraceEnabled())
        {
            logger.trace("constructed.");
        }

    }

    /**
     * Implements the thread Runnable method.
     */
    public void run()
    {
        if( v_is_stopped )
        {
            throw new RuntimeException("This thread has been stopped.");
        }

        // we now expect to have a real thread id for the new thread.
        long id = Thread.currentThread().getId();
        v_id = "" + id;

        if(logger.isTraceEnabled())
        {
            logger.trace("My thread id: " + v_id);
        }

        TaskQueue task_queue = TaskQueue.instance();

        while( ! v_is_stopped )
        {

            if(logger.isTraceEnabled())
            {
                logger.trace("poping task from queue.");
            }

            Task task = null;
            try
            {
                // following call blocks on a wait until a
                // task is available.
                task = task_queue.pop();

                if(logger.isTraceEnabled())
                {
                    logger.trace("running task ...");
                }

                task.run();

                if(logger.isTraceEnabled())
                {
                    logger.trace("notifying listener: DONE");
                }

                task.notifyListeners(Task.Status.DONE,  "Done");
            }
            catch(InterruptedException ex)
            {

                if(logger.isInfoEnabled())
                {
                    logger.info("interrupt exception: " + ex.getMessage());
                }

                stopMe();
            }
            catch(Exception ex)
            {

                String class_name = ex.getClass().getName();
                StringWriter sw = new StringWriter();
                ex.printStackTrace(new PrintWriter(sw));
                String stack_trace = sw.toString();
                String msg = "exception class [" + class_name +
                        "], msg [" + ex.getMessage() + "], stack [" +
                        stack_trace + "].";

                if(logger.isErrorEnabled())
                {
                    logger.error("interrupt exception: " + msg);
                }

                if (task != null)
                {
                    task.notifyListeners(Task.Status.FAILED,
                                         ex.getMessage());
                }

                stopMe();
            }

        }
    }

    /**
     * Sets a flag to prevent this task thread from
     * running.
     */
    public void stopMe()
    {
        v_is_stopped = true;
    }

    /**
     * @return the running status of this task thread.
     */
    public boolean isStopped()
    {
        return v_is_stopped;
    }

    /**
     * @return my own task thread id
     */
    public String getThreadId()
    {
        return v_id;
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

    // ------ >>> Private <<< ------
    private String v_id;
    private volatile boolean v_is_stopped;
}
