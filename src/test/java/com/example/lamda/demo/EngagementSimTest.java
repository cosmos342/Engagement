package com.example.lamda.demo;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author osboxes Start the simulator generate 50 messages
 *
 */
public class EngagementSimTest {
	EngagementSim sim;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		sim = new EngagementSim();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		sim.run_once(1000);
		
	}

}
