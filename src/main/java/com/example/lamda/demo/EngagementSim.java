package com.example.lamda.demo;

import  java.util.Scanner;
public class EngagementSim {

    final private DataBase db;
    private Thread cons_t;
    
    EngagementSim() {
    	System.out.println("enagementsim");
        db = DataBase.getInstance();

    }

    /**
     * Start consumer thread
     */
    void consumer_start()
    {
        System.out.println("start consumer thread");
        cons_t = new Thread(Consumer.getInstance());
        cons_t.start();
    }

    /**
     * Stop consumer thread
     */
    void consumer_stop()
    {
        System.out.println("stop consumer thread");
        byte [] msg = new Terminate().encode();
        if(msg.length > 0) {
            MsgQ.getInstance().enqueue(msg);
            System.out.println("SENDING TERMINATE");
        }
        
        try {
        	cons_t.join();
        } catch(Exception e) {
        	System.out.println("consumer_stopped "  + e);
        }
        
    
    }

    /**
     * @param num: Number of messages Twitter and Facebook messages to be simulates
     *             Start consumer thread. Shutdown when the simulation is done
     * @return
     */
    String run_once(int num)
    {
        Producer.produce_feed(num,FeedSource.TWTR);
        Producer.produce_feed(num,FeedSource.FB);
        System.out.println("producer done " + Thread.currentThread().getName());
        try {
            Thread.sleep(20);
        }
        catch(InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        // con.process_msgs();
        System.out.println("consumer start");
        consumer_start();
        try {
            Thread.sleep(3000);
        }
        catch(InterruptedException e) {
            System.out.println("consumer interrupted");
            System.out.println(e);
        }
        
        while(db.get_insert_count() < (num*2))
        {
        	try {
        		Thread.sleep(2000);
        	}
            catch(InterruptedException e) {
                System.out.println("count interrupted");
                System.out.println(e);
            }
        	
        }
        consumer_stop();
        
        String res= db.get_all_stats() + "\r\n" + db.get_stats();
        System.out.println(res);
        db.reset();
        
        return res;

    }

    public static void main(String [] args)
    {
        EngagementSim sim = new EngagementSim();
        sim.run_once(args.length > 0 ? Integer.parseInt(args[0]):25000);
        System.out.println("SIM DONE");

    }
}
