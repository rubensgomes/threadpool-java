/**
 * Copyright (C) 1999-2016 Rubens Gomes <rubens.s.gomes@gmail.com>.
 * All Rights Reserved.
 *
 * File: ThreadPool.java
 *
 * Author: Rubens Gomes
 */
package com.softlagos.threadpool;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.softlagos.Constants;
import com.softlagos.util.SystemProperties;

/**
 * This class implements the Thread Pool Pattern.
 *
 * In the class, a number of N threads are created to
 * perform a number of M tasks, organized in a FIFO
 * (First In-First out) queue. Typically, N lt;lt; M. As
 * soon as a thread completes its task, it will request
 * the next task from the queue until all tasks have been
 * completed. The thread can then terminate, or
 * sleep until there are new tasks available.
 *
 * The number of threads used (N) is a parameter that
 * can be tuned to provide the best performance.
 *
 * The advantage of using a Thread Pool over creating a
 * new thread for each task, is that thread creation
 * and destruction overhead is negated, which may
 * result in better performance and better system
 * stability.
 *
 * @author Rubens Gomes
 */
public final class ThreadPool
{

    /** The Constant logger. */
    private static final Logger logger =
            LogManager.getLogger(ThreadPool.class);

    /** The Constant s_instance. */
    private final static ThreadPool s_instance =
            new ThreadPool();

    /**
     * Singleton i8nstance.
     *
     * @return the thread pool singleton.
     */
    public static ThreadPool instance()
    {
        return s_instance;
    }

    /**
     * Delegates to the TaskQueue to add a
     * task to the task FIFO queue.  This task will
     * be executed by a free thread in the thread pool.
     *
     * @param task a task to be run by a thread in the pool.
     */
    public void pushTask(final Task task)
    {

        if(task == null)
        {
            throw new IllegalArgumentException("task cannot be null.");
        }

        if(v_is_shutdown)
        {
            throw new RuntimeException("ThreadPool has been shutdown.");
        }

        TaskQueue task_queue = TaskQueue.instance();

        if(logger.isTraceEnabled())
        {
            logger.trace("adding task to queue.");
        }

        task_queue.push(task);
    }

    /**
     * @return the total number of threads in the pool.
     */
    public int getTotalThreads()
    {
        return v_nr_threads;
    }

    /**
     * Nicely stops all the running threads and shuts down
     * the thread pool  Once the thread pool is shutdown
     * it is no longer available to be used.
     *
     * The user might call this function prior to exiting
     * the application.
     */
    public void shutdown()
    {

        if(v_is_shutdown)
        {
            throw new RuntimeException(
                             "ThreadPool has already been shutdown.");
        }

        if(logger.isTraceEnabled())
        {
            logger.trace("shutdown started.");
        }

        v_thread_group.interrupt();

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
            Thread.sleep(shutdown_time);
        }
        catch(InterruptedException ex)
        {

            if(logger.isInfoEnabled())
            {
                logger.info("interrupted exception: " + ex.getMessage());
            }

        }
        finally
        {

            if(logger.isTraceEnabled())
            {
                logger.trace("destroying thread group.");
            }

            v_thread_group.destroy();
        }

        v_is_shutdown = true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#finalize()
     */
    @Override
    public void finalize()
    {
        // because this is a singleton the instance should only
        // be garbage collected when the class is unloaded at
        // the end of the application.
        shutdown();

        if(logger.isTraceEnabled())
        {
            logger.trace("GC collected.");
        }
    }

    // ------ >>> Private <<< ------
    /**
     * Instantiates a new thread pool.
     */
    private ThreadPool()
    {
        SystemProperties prop = SystemProperties.instance();

        v_nr_threads = prop.getPropertyAsInt(Constants.THREADPOOL_SIZE);
        v_thread_group = new ThreadGroup("ThreadPool");
        v_is_shutdown = false;

        if(logger.isTraceEnabled())
        {
            logger.trace("instantiating task threads...  ");
        }

        Thread task_thread = null;
        for (int i=0; i<v_nr_threads; i++)
        {

            if(logger.isTraceEnabled())
            {
                logger.trace("instantiating thread:  " + i);
            }

            task_thread = new Thread(v_thread_group,
                                     new TaskThread(),
                                     "TaskThread-" + i);

            if(logger.isTraceEnabled())
            {
                logger.trace("launching thread:  " + i);
            }

            task_thread.start();
        }

        if(logger.isTraceEnabled())
        {
            logger.trace("constructed.");
        }

    }

    private final int v_nr_threads;
    private final ThreadGroup v_thread_group;
    private boolean v_is_shutdown;
}
