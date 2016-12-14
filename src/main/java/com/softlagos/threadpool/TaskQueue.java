/**
 * Copyright (C) 1999-2016 Rubens Gomes <rubens.s.gomes@gmail.com>.
 * All Rights Reserved.
 *
 * File: TaskQueue.java
 *
 * Author: Rubens Gomes
 */
package com.softlagos.threadpool;

import java.util.ArrayDeque;
import java.util.Queue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Singleton.
 *
 * The Class TaskQueue is used to store and manage tasks that
 * are to run by separate threads in the thread pool.  Adding elements
 * to the queue "notifyAll" threads that are waiting for tasks.  If no
 * tasks are available the queue "wait".
 *
 * @author Rubens Gomes
 */
public final class TaskQueue
{

    /** The Constant logger. */
    private static final Logger logger =
            LogManager.getLogger(TaskQueue.class);

    /** The Constant s_singleton. */
    private static final TaskQueue s_singleton = new TaskQueue();

    /**
     * Singleton Instance.
     *
     * @return the task queue singleton.
     */
    public static TaskQueue instance()
    {
        return s_singleton;
    }

    /**
     * Add a task to the task FIFO queue.  Once a task
     * is called it calls notify_all to unblock any thread
     * that is pending on a task to be available.
     *
     * @param task a task to be run by a thread in the pool.
     */
    public synchronized void push(final Task task)
    {
        if( task == null )
        {
            throw new IllegalArgumentException("task cannot be null.");
        }

        if(logger.isTraceEnabled())
        {
            logger.trace("adding task to the queue.");
        }

        v_queue.add(task);

        if(logger.isTraceEnabled())
        {
            logger.trace("notify single thread waiting.");
        }

        // only a single thread can execute the task; therefore,
        // we notify only one of the threads that might be waiting.
        this.notify();
    }

    /**
     * Pops a task from the queue.  If no task is
     * available this call blocks the current thread
     * by placing the thread on "wait".  The thread
     * is "woken up" once a task is added to the queue
     * via the corresponding push(task) call.
     *
     * @return pops out the next task in the FIFO queue
     * to be executed by a task thread.
     * @throws InterruptedException if the queue is empty any
     * thread interrupted the current thread before or while the
     * current thread was waiting for a notification.
     */
    public synchronized Task pop() throws InterruptedException
    {
        if(logger.isTraceEnabled())
        {
            logger.trace("polling task from queue...");
        }

        Task task = v_queue.poll();

        if( task == null )
        {

            if(logger.isTraceEnabled())
            {
                logger.trace("queue is empty: waiting...");
            }

            this.wait();

            if(logger.isTraceEnabled())
            {
                logger.trace("wait released, polling task again now...");
            }

            task = v_queue.poll();
        }

        return task;
    }

    // ------ >>> private <<< ------
    /**
     * Instantiates a new task queue.
     */
    private TaskQueue()
    {
        v_queue = new ArrayDeque<Task> ();

        if(logger.isTraceEnabled())
        {
            logger.trace("constructed.");
        }

    }

    /** The v_queue. */
    private final Queue<Task> v_queue;
}
