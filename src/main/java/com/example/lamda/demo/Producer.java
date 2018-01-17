package com.example.lamda.demo;

import java.util.stream.IntStream;

public class Producer {

    /**
     * @param src produce the message
     */
    public void produce_msg(FeedSource src)
    {
         byte [] msg = (src == FeedSource.FB) ? new FBFeed().encode() :  new TwitterFeed().encode();
         if(msg.length > 0) {
             MsgQ.getInstance().enqueue(msg);
             // System.out.println(msg);
         }
    }
    /**
     * @param num Number of messages to simulate
     * @param src Source of Message(FB,Twtr etc)
     */
    public void produce_feed(int num, FeedSource src)
    {
        IntStream.range(0,num)
                .parallel()
                .forEach(i -> produce_msg(src));
    }

}
