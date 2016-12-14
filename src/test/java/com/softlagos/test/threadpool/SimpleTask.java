/**
 * Copyright (C) 1999-2016 Rubens Gomes <rubens.s.gomes@gmail.com>.
 * All Rights Reserved.
 *
 * File: SimpleTask.java
 *
 * Author: Rubens Gomes
 */
package com.softlagos.test.threadpool;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.softlagos.threadpool.Task;

/**
 * The simple implementation of the ITask type.
 *
 * @author Rubens Gomes
 */
public class SimpleTask extends Task
{

    private static final Logger logger =
            LogManager.getLogger(SimpleTask.class);

    /**
     * Instantiates a new simple task.
     */
    public SimpleTask()
    {
        if(logger.isTraceEnabled())
        {
            logger.trace("constructed.");
        }

    }

    /**
     * Called to execute this task operation in a separate thread.
     */
    @Override
    public void run()
    {

        if(logger.isTraceEnabled())
        {
            logger.trace("Hello World!");
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

    // ------ Private ------
}
