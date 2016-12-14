/**
 * Copyright (C) 1999-2016 Rubens Gomes <rubens.s.gomes@gmail.com>.
 * All Rights Reserved.
 *
 * File: SimpleNeverEndingTask.java
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
public class SimpleNeverEndingTask extends Task
{

    private static final Logger logger =
            LogManager.getLogger(SimpleNeverEndingTask.class);

    /**
     * Instantiates a new never ending simple task.
     */
    public SimpleNeverEndingTask()
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

        if(logger.isTraceEnabled())
        {
            logger.trace("starting never ending loop...");
        }

        while (true)
        {

            try
            {
                if (Thread.currentThread().isInterrupted())
                {

                    if(logger.isTraceEnabled())
                    {
                        logger.trace("thread interrupted.");
                    }

                    throw new InterruptedException("I have been interrupted");
                }
            }
            catch(InterruptedException ex)
            {

                String msg = "handling interrupt: " + ex.getMessage();
                if(logger.isInfoEnabled())
                {
                    logger.info(msg);
                }

                notifyListeners(Status.INTERRUPTED, msg);
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

    // ------ Private ------
}
