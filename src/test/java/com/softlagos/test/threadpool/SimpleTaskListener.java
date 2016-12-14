/**
 * Copyright (C) 1999-2016 Rubens Gomes <rubens.s.gomes@gmail.com>.
 * All Rights Reserved.
 *
 * File: SimpleTaskListener.java
 *
 * Author: Rubens Gomes
 */
package com.softlagos.test.threadpool;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.softlagos.threadpool.Task;
import com.softlagos.threadpool.TaskListener;

/**
 * The listener interface for receiving Task events.
 * The class that is interested in processing a Task
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addSimpleTaskListener<code> method. When
 * the simpleTask event occurs, that object's appropriate
 * method is invoked.
 *
 * @see Task
 * @author Rubens Gomes
 */
public class SimpleTaskListener extends TaskListener
{

    private static final Logger logger =
            LogManager.getLogger(SimpleTaskListener.class);

    /**
     * Instantiates a new simple task listener.
     */
    public SimpleTaskListener()
    {
        if(logger.isTraceEnabled())
        {
            logger.trace("constructed.");
        }
    }

    /* (non-Javadoc)
     * @see com.softlagos.threadpool.ITaskListener#notifyTaskDone(java.lang.String)
     */
    @Override
    public void notifyTaskDone(String msg)
    {
        if(logger.isInfoEnabled())
        {
            logger.info("Task done:  " + msg);
        }
    }

    /* (non-Javadoc)
     * @see com.softlagos.threadpool.ITaskListener#notifyTaskInterrupted(java.lang.String)
     */
    @Override
    public void notifyTaskInterrupted(String msg)
    {
        if(logger.isInfoEnabled())
        {
            logger.info("Task interrupted:  " + msg);
        }
    }

    /* (non-Javadoc)
     * @see com.softlagos.threadpool.ITaskListener#notifyTaskFailed(java.lang.String)
     */
    @Override
    public void notifyTaskFailed(String msg)
    {
        if(logger.isInfoEnabled())
        {
            logger.info("Task failed:  " + msg);
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

}
