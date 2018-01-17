package com.example.lamda.demo;

import  java.util.Scanner;
public class EngagementSim {

    final private Producer prod;
    final private DataBase db;
    private Consumer con;
    private Thread cons_t;
    
    EngagementSim()
    {
        prod = new Producer();
        // con =  Consumer.getInstance();
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
        cons_t.interrupt();
    }

    /**
     * @param num: Number of messages Twitter and Facebook messages to be simulateds 
     *             Start consumer thread. Shutdown when the simulation is done
     * @return
     */
    String run_once(int num)
    {
        prod.produce_feed(num,FeedSource.TWTR);
        prod.produce_feed(num,FeedSource.FB);
        try {
            Thread.sleep(20);
        }
        catch(InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        // con.process_msgs();

        consumer_start();
        try {
            Thread.sleep(3000);
        }
        catch(InterruptedException e)
        {
            System.out.println("consumer interrupted");
            System.out.println(e);
        }
        consumer_stop();
        
        String indiv= db.get_stats(FeedSource.TWTR,10);
        
        
        return db.get_all_stats() + '\n' + indiv;

    }

    public static void main(String [] args)
    {
        EngagementSim sim = new EngagementSim();
        // sim.run();
        System.out.println(sim.run_once(args.length > 0 ? Integer.parseInt(args[0]):50));

    }

}
