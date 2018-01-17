package com.example.lamda.demo;

public interface Feed {
    /**
     * @return user_id (email_id of user engaging in the feed)
     */
    public int get_user_id();
    /**
     * @param txt: set the txt for the feed
     */
    public void set_txt(String txt);
    /**
     * @return get source(FB,TWTR etc)
     */
    public FeedSource get_source();
    /**
     * @return get id (twitter id, fb id etc)
     */
    public int get_id();
    /**
     * @return (LIKE,REPLY etc)
     */
    public FeedType get_type();
}
