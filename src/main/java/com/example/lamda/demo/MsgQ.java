package com.example.lamda.demo;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;


public class MsgQ {
    private static volatile MsgQ instance;
    final private static Object mutex = new Object();
    /**
     * Max QueueSize 
     */
    final private static int QUEUE_SIZE=50000;

    /**
     * If QueueSize is reached the sender blocks. If queue is empty receiver blocks
     */
    final BlockingQueue<byte[]> que;

    private MsgQ () {
        que = new ArrayBlockingQueue<>(QUEUE_SIZE,true);
    }

    public static MsgQ getInstance() {
        MsgQ result = instance;
        if (result == null) {
            synchronized (mutex) {
                result = instance;
                if (result == null)
                    instance = result = new MsgQ();
            }
        }
        return result;
    }

    /**
     * @param msg : enque. Blocking call if queue is full
     */
    public void enqueue(byte [] msg)
    {
        // System.out.println(msg);
        try {
            que.put(msg);
        } catch(InterruptedException e)
        {
            System.out.println("MsgQ: enqueu" + e);
        }
    }

    /**
     * @return Non blocking dequeue
     */
    public byte [] dequeue()
    {
        // consider using drainTo
        return que.poll();
    }

    /**
     * @return blocking dequeue
     */
    public byte [] take()
    {
        byte [] msg = null;
        try {
            msg = que.take();
        }
        catch(InterruptedException e)
        {
           // System.out.println(e);
        }
        return msg;
    }

    /**
     * @return Queue Status
     */
    public int size()
    {
        // System.out.(println("msg_que size" + que.size());
        return que.size();
    }
    
    public static int max_q_size()
    {
    	return QUEUE_SIZE;
    }

}


