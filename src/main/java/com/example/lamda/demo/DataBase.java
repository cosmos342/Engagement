package com.example.lamda.demo;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.lang.String;

public class DataBase {



    /**
     * src_map: Map from Feeds(such as Twitter/FB etc tp value of another Map
     * Value Map is map from ids(such as twitter/fb ids to List of integer values
     * indicating the number of different FeedType(LIKEs,REPLIES) each tweet or facebook
     * post received
     */
    final private Map<FeedSource, Map<Integer,List<Integer>>> src_map;
    /**
     * user_map: Map from User Id to value of list of Number of each type of Feed(like,retweet)
     * etc the user generated
     */
    final private Map<Integer, List<Integer>> user_map;
    /**
     * db_lock: ReadWrite lock so consumer can write and engagementsim can read
     */
    final private ReadWriteLock db_lock;

    private static volatile DataBase instance;
    final private static Object mutex = new Object();
    private int count_insert;

    private DataBase() {

        src_map = new HashMap<>();
        user_map = new HashMap<>();
        db_lock = new ReentrantReadWriteLock();
        count_insert=0;
    }

    public static DataBase getInstance() {
        DataBase result = instance;
        if (result == null) {
            synchronized (mutex) {
                result = instance;
                if (result == null)
                    instance = result = new DataBase();
            }
        }
        return result;
    }

    private List<Integer> init_feed_type_list()
    {
        List<Integer> feed_lst = new ArrayList<>();
        for(int i =0; i < FeedType.values().length ; i++)
        {
            feed_lst.add(i,0);
        }
        return feed_lst;
    }

    /**
     * @param msg: Insert either the Facebook post or Twitter tweets.  Scalable
     * to other source types
     */
    public void insert(byte [] msg)
    {
        System.out.println("DataBase insert msg");
        Object obj;
        try {
            obj = SerDes.deserialize(msg);
        }
        catch (Exception e) {
            System.out.println(e);
            return;
        }

        if(!(obj instanceof Feed ))
            return;

        count_insert++;
        Feed feed = (Feed)obj;

        db_lock.writeLock().lock();
        try {

            Map<Integer,List<Integer>> sid_map = src_map.getOrDefault(feed.get_source(),new HashMap<Integer,List<Integer>>());
            List<Integer> lst = sid_map.getOrDefault(feed.get_id(),init_feed_type_list());

            int id = feed.get_id();
            int type_idx = feed.get_type().ordinal();

            int count = lst.get(type_idx);
            lst.set(type_idx,++count);
            sid_map.put(id,lst);
            src_map.put(feed.get_source(),sid_map);


            // Todo user info later

        } finally {
            db_lock.writeLock().unlock();
        }
    }

    /**
     * @param srcL FeedSource(FB,TWTR)
     * @param id: Post or Tweet Id.
     * @return String: String returns the #Likes #DisLikes for a post id
     */
    public String get_stats(FeedSource src, int id)
    {
        String res= "";
        db_lock.readLock().lock();
        try {
                if(src_map.containsKey(src)) {
                    Map<Integer, List<Integer>> id_map = src_map.get(src);
                    if (id_map.containsKey(id)) {
                        List<Integer> type_lst = id_map.get(id);
                        res = new String(id + " " + "#LIKES " + type_lst.get(FeedType.LIKE.ordinal())
                                + "#REPLY" + type_lst.get(FeedType.REPLY.ordinal()));
                    }
                }

        } finally {
            db_lock.readLock().unlock();
        }
        return res;

    }
    
    /**
     * @return Aggregation statistics for all different types of Sources
     */
    public String get_all_stats() {

        String res;
        db_lock.readLock().lock();
        try {
            res =   "#TWTS " + (src_map.containsKey(FeedSource.TWTR) ? src_map.get(FeedSource.TWTR).size() : 0 )
                    + " #FBPOSTS " + (src_map.containsKey(FeedSource.FB) ? src_map.get(FeedSource.FB).size() : 0 )
                    + " #USERS " + user_map.size();
        } finally {
            db_lock.readLock().unlock();
        }
        return res;
    }
    
    public int get_insert_count()
    {
    	return count_insert;
    }
}
