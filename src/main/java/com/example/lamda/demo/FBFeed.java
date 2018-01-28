package com.example.lamda.demo;

import java.io.IOException;
import java.io.Serializable;
import java.util.Random;

public class FBFeed implements Feed, Serializable {
   /**
    * email_id: the user who is engaging about the post
    * FeedType: Like,Reply
    */
    private static final long serialVersionUID = 1L;
    private int fb_post_id;
    private int email_id;
    private FeedType type;
    private String txt;
    
    final static Random rand = new Random();

    FBFeed() {
        fb_post_id = rand.nextInt(100);
        email_id = rand.nextInt(100);
        type = rand.nextInt(2)==0 ? FeedType.REPLY : FeedType.LIKE;
        txt = "this is fb";
    }

    public int get_id()
    {
        return fb_post_id;
    }

    public int get_user_id()
    {
        return email_id;
    }

    public FeedType get_type()
    {
        return type;
    }

    public FeedSource get_source()
    {
        return FeedSource.FB;
    }

    public void set_txt(String txt)
    {
        this.txt = txt;
    }

    /**
     * @return: Serialize the object to be before sending to queue
     */
    public  byte [] encode()
    {
        try {
            return SerDes.serialize(this);
        } catch (IOException e) {
            System.out.println("FBFeed: encode " + e);
            return new byte[0];
        }
    }
}
