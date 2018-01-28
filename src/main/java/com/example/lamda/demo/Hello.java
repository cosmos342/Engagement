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
    	
    	return sim.run_once(input != null ? Integer.valueOf(input) : 10 );
    	
    }

}
