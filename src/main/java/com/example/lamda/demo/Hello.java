package com.example.lamda.demo;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class Hello implements RequestHandler<String, String> {

    /* (non-Javadoc)
     * @see com.amazonaws.services.lambda.runtime.RequestHandler#handleRequest(java.lang.Object, com.amazonaws.services.lambda.runtime.Context)
     */
    @Override
    public String handleRequest(String input, Context context) {
    	EngagementSim sim = new EngagementSim();
    	int numevents =  100;
    	if(input!= null)
    	{
        	try {
        		numevents = Integer.parseInt(input);
        	}
        	catch(Exception e) {
        		return "Provide numevents";
        	}
        }
    	
    	return sim.run_once(numevents);
    	
    }

}
