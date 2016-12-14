/**
 * Copyright (C) 1999-2016 Rubens Gomes <rubens.s.gomes@gmail.com>.
 * All Rights Reserved.
 *
 * File: ThreadPoolTest.java
 *
 * Author: Rubens Gomes
 */
package com.softlagos.test.threadpool;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import com.softlagos.threadpool.Task;
import com.softlagos.threadpool.TaskListener;
import com.softlagos.threadpool.OnDemandTaskThread;
import com.softlagos.threadpool.ThreadPool;

/**
 * Test cases for the Thread Pool classes.
 *
 * @author Rubens Gomes
 */
public final class ThreadPoolTest
{

    private static final Logger logger =
            LogManager.getLogger(ThreadPoolTest.class);

    /**
     * Test on demand task thread - A.
     */
    @Ignore("OnDemandTaskThread Test A being ignored.")
    @Test
    public final void testOnDemandTaskThread_A()
    {
        if(logger.isTraceEnabled())
        {
            logger.trace("simple task being created ");
        }

        Task task = new SimpleTask();

        TaskListener listener1 = new SimpleTaskListener();
        task.addListener(listener1);

        TaskListener listener2 = new SimpleTaskListener();
        task.addListener(listener2);

        if(logger.isTraceEnabled())
        {
            logger.trace("instantiating OnDemandTaskThread ...");
        }

        OnDemandTaskThread task_thread =
                new OnDemandTaskThread(task);

        if(logger.isTraceEnabled())
        {
            logger.trace("calling stopMe on OnDemandTaskThread ...");
        }

        task_thread.stopMe();
    }


    /**
     * Test on demand task thread - B.
     */
    @Ignore("OnDemandTaskThread Test B being ignored.")
    @Test
    public final void testOnDemandTaskThread_B()
    {
        if(logger.isTraceEnabled())
        {
            logger.trace("simple task being created ");
        }

        Task task = new SimpleTask();

        if(logger.isTraceEnabled())
        {
            logger.trace("new stack frame...");
        }

        newStackFrame(task);

        if(logger.isTraceEnabled())
        {
            logger.trace("running GC...");
        }

        System.gc();

        try
        {
            if(logger.isTraceEnabled())
            {
                logger.trace("sleeping ...");
            }
            Thread.sleep(3000); // msecs
        }
        catch(InterruptedException ex)
        {

            if(logger.isInfoEnabled())
            {
                logger.info("handling exception: " + ex.getMessage());
            }

        }

        if(logger.isTraceEnabled())
        {
            logger.trace("ending");
        }

    }

    public void newStackFrame(final Task task)
    {
        TaskListener listener1 = new SimpleTaskListener();
        task.addListener(listener1);

        TaskListener listener2 = new SimpleTaskListener();
        task.addListener(listener2);

        if(logger.isTraceEnabled())
        {
            logger.trace("instantiating OnDemandTaskThread ...");
        }

        OnDemandTaskThread task_thread =
                new OnDemandTaskThread(task);

        long id = task_thread.getId();

        if(logger.isTraceEnabled())
        {
            logger.trace("task_thread id: " + id);
        }

        if(logger.isTraceEnabled())
        {
            logger.trace("returning from newStackFrame()...");
        }

        return;
    }

    /**
     * Test task thread pool - A.
     */
    @Test
    public final void testThreadPool_A()
    {

        if(logger.isTraceEnabled())
        {
            logger.trace("instantiating ThreadPool ...");
        }

        ThreadPool thread_pool = ThreadPool.instance();

        if(logger.isTraceEnabled())
        {
            logger.trace("creating task");
        }

        Task task = new SimpleTask();

        TaskListener listener1 = new SimpleTaskListener();
        task.addListener(listener1);

        TaskListener listener2 = new SimpleTaskListener();
        task.addListener(listener2);

        if(logger.isTraceEnabled())
        {
            logger.trace("pushing task to queue.");
        }

        thread_pool.pushTask(task);

        if(logger.isTraceEnabled())
        {
            logger.trace("calling shutdown.");
        }

        thread_pool.shutdown();

        if(logger.isTraceEnabled())
        {
            logger.trace("ending.");
        }

    }

}
