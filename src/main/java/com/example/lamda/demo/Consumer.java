package com.example.lamda.demo;

import java.util.stream.IntStream;
import java.util.ArrayList;
import java.util.Collections;
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

    private boolean terminate=false;
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
        System.out.println("starting consumer run" );
        while(true)
        {
        	try {
        		byte [] msg = MsgQ.getInstance().take();
        		// System.out.println("consumer got one queue");
          
                try {
                    Object obj = SerDes.deserialize(msg);
                    if (obj instanceof Feed) {
                    		add_msg(obj);
                    		process_msgs();
                    		if(terminate==true)
                    		{
                    			System.out.println("Consumer done! " + Thread.currentThread().getName());
                    			return;
                    		}
                    }
                    else if(obj instanceof Terminate) {
                        System.out.println("Consumer done!! " + Thread.currentThread().getName());
                        return;
                    }
                    else {
                    	System.out.println("ERROR");
                    }
                } catch (Exception e) {
                    System.out.println("consumer:run " + e);
                    // return;
                }
        	}
        	catch(Exception e) {
        		System.out.println("Consumer Done !!! " + e);
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
        catch(Exception e) {
            System.out.println("db_insert: " + e);
        }

    }
    
    
    /**
     *  Utility function to store msg in message list
     */
    private void add_msg(Object obj)
    {
    	if(msg_list == null)
    		msg_list=new ArrayList<>(MSGS_PER_ATTEMPT);
    	
    	msg_list.add(obj);
    	
    }
    
    /**
     *  Utility function to reset message list
     */
    private void reset_msg()
    {
    	msg_list=null;
    }

    /**
     * Deque message from the queue between producer and consumer
     */
    private void process_msg()
    {
        // System.out.println("Consumer:process_msg");
        byte [] msg = MsgQ.getInstance().dequeue();
        
        // System.out.println("process_msg: " + count_rcvd_msgs);
        if(msg != null) {
	        try {
	            Object obj = SerDes.deserialize(msg);
	            if(obj instanceof Feed ) {
	            	add_msg(obj);
	            }
	            else {
	                // System.out.println("process_msg: terminate message rcvd");
	                terminate=true;
	                return;
	            }
	        } catch (Exception e) {
	            System.out.println("process_msg " + e);
	        }    
        }
    }

    /**
     *  Process and batch messages filter,transform before inserting into DataBase
     */
    public void process_msgs()
    {
    	// System.out.println("con process_msgs " + Thread.currentThread().getName());
   
        while(MsgQ.getInstance().size() > 0) {
        	      
            int size=MsgQ.getInstance().size();
            try {

            	IntStream.range(0, Math.min(size, MSGS_PER_ATTEMPT))
                	.forEach(i -> process_msg());
            }
            catch(Exception e) {
            	System.out.println("process_msgs: " + e);
            }
                               
            if (msg_list != null) {
                // System.out.println("consumer.msgsize " + tlist.size());
            	try {
            		
            		msg_list.stream().
                		filter(i -> i != null).	
                        	map(i -> { 
                        	((Feed)i).set_txt(null); 
                        	count_rcvd_msgs++;
                        	return i;}).
                        	forEach(i -> db_insert(i));
                        
            	}
            	catch(Exception e) {
            		System.out.println("process_msgs " + e);
            	}
             
            }
 
            reset_msg();
        }
        // System.out.println("con done process_msgs");

    }
    
    public int get_rcvd_msgs()
    {
    	return count_rcvd_msgs;
    }
}
