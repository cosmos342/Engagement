package com.example.lamda.demo;

import java.io.IOException;
import java.io.Serializable;
import java.util.Random;

public class TwitterFeed implements Feed, Serializable {
    /**
	 * serializationUID for serialization
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * TweetId
	 */
	private int twt_id;
    /**
     * User email_id who engaged about tweet
     */
    private int email_id;
    /**
     *  FeedType: Like,Reply..
     */
    private FeedType type;
    /**
     * User comment text
     */
    private String txt;

    /**
     * Randomizer for simulation
     */
    final static Random rand = new Random();
    TwitterFeed()
    {
        twt_id=rand.nextInt(100);
        email_id = rand.nextInt(100);
        type=FeedType.REPLY;
        txt="this is tweet";
    }
    public int get_id()
    {
        return twt_id;
    }
    public int get_user_id()
    {
        return email_id;
    }
    public FeedSource get_source()
    {
        return FeedSource.TWTR;
    }
    public FeedType get_type()
    {
        return type;
    }

    public void set_txt(String txt)
    {
        this.txt = txt;
    }

    /**
     * @return serialize object and send over to queue
     */
    public byte [] encode()
    {
        try {
            return SerDes.serialize(this);
        } catch (IOException e) {
            System.out.println(e);
            return new byte[0];
        }
    }
}
