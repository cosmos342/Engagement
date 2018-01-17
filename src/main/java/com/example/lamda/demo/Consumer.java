package com.example.lamda.demo;

import java.util.stream.IntStream;
import java.util.ArrayList;
import java.util.List;

public class Consumer implements Runnable {

    private static volatile Consumer instance;
    final private static Object mutex = new Object();
    /**
     * MSGS_PER_ATTEMPT is for batch processing to db
     */
    final private static int MSGS_PER_ATTEMPT=5;
    /**
     *  Msgs batched before inserting in db
     */
    private List<Object> msg_list;
    private int count_rcvd_msgs=0;

    private Consumer() {
        msg_list=null;
    }


    /* (non-Javadoc)
     * @see java.lang.Runnable#run() 
     * Consumer thread runs and asynchronously processes messages from producer. 
     * Consumer however synchronously writes to DataBase
     */
    public void run()
    {
        System.out.println("starting consumer run");
        while(true)
        {
            System.out.println("consumer waiting for queue");
            byte [] msg = MsgQ.getInstance().take();
            System.out.println("consumer got one queue");
            if(msg != null) {
                msg_list = new ArrayList<>(MSGS_PER_ATTEMPT);

                try {
                    Object obj = SerDes.deserialize(msg);
                    if (obj instanceof Feed) {
                        msg_list.add(obj);
                        process_msgs();
                    }
                    else
                        System.out.println("Consumer msg is of wrong type");
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
            else
            {
                System.out.println("Shutting down consumer");
                return;
            }
        }
    }

    /**
     * @return
     */
    public static Consumer getInstance() {
        Consumer result = instance;
        if (result == null) {
            synchronized (mutex) {
                result = instance;
                if (result == null)
                    instance = result = new Consumer();
            }
        }
        return result;
    }

    /**
     * @param obj serialize the obj and send byte array to Database
     */
    public void db_insert(Object obj) {

        byte [] msg = null;
        try {
            msg = SerDes.serialize(obj);
            DataBase.getInstance().insert(msg);
        }
        catch(Exception e)
        {
            System.out.println(e);
        }

    }

    /**
     * Deque message from the queue between producer and consumer
     */
    private void process_msg()
    {
        System.out.println("Consumer:process_msg");
        byte [] msg = MsgQ.getInstance().dequeue();
        if(msg != null)
        {
	        try {
	            Object obj = SerDes.deserialize(msg);
	            if(obj instanceof Feed)
	                msg_list.add(obj);
	            else
	                System.out.println("Consumer msg is of wrong type");
	        } catch (Exception e) {
	            System.out.println(e);
	        }
        }
    }

    /**
     *  Process and batch messages filter,transform before inserting into DataBase
     */
    public void process_msgs()
    {

        // System.out.println("con process_msgs");
        while(!MsgQ.getInstance().is_empty()) {
            if(msg_list==null)
                msg_list = new ArrayList<>(MSGS_PER_ATTEMPT);

            IntStream.range(0, MSGS_PER_ATTEMPT)
                    .forEach(i -> process_msg());

            
            List<Object> tlist = msg_list;

            if (tlist != null) {
                System.out.println("consumer.msgsize " + tlist.size());
                tlist.stream().
                        filter(i -> i != null).
                        map(i -> { 
                        	((Feed)i).set_txt(null); 
                        	count_rcvd_msgs++;
                        	return i;}).
                        forEach(i -> db_insert(i));
            }
            msg_list=null;
        }
        System.out.println("con done process_msgs");

    }
    public int get_rcvd_msgs()
    {
    	return count_rcvd_msgs;
    }
}
