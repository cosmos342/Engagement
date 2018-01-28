package com.example.lamda.demo;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Test Consumer: msgs received, ingested into database
 *
 */
public class ConsumerTest {
	int num_feeds_sent=100;
	int num_feeds_rcvd=0;
	int num_feeds_rcvd_in_db=0;
	MsgQ que;
	Thread cons_t;
	// Database db;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		que=MsgQ.getInstance();
		cons_t = new Thread(Consumer.getInstance());
	    cons_t.start();
	   		
	}

	@After
	public void tearDown() throws Exception {
		MsgQ.getInstance().enqueue(new Terminate().encode());
	}

	@Test
	public void test() {
		Producer.produce_feed(num_feeds_sent,FeedSource.FB);
		try {
			Thread.sleep(2000);
		} catch(Exception e){
			System.out.println(e);
		}
		num_feeds_rcvd=Consumer.getInstance().get_rcvd_msgs();
		num_feeds_rcvd_in_db=DataBase.getInstance().get_insert_count();
		System.out.println("result " + num_feeds_sent + " "+ num_feeds_rcvd 
				+ " " + num_feeds_rcvd_in_db);
		assertEquals(num_feeds_sent,num_feeds_rcvd);
		assertEquals(num_feeds_rcvd,num_feeds_rcvd_in_db);
		// fail("Not yet implemented");
	}

}
