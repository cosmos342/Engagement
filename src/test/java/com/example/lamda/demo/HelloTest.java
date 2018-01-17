package com.example.lamda.demo;

import java.io.IOException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.services.lambda.runtime.Context;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
public class HelloTest {

    private static String input;

    @BeforeClass
    public static void createInput() throws IOException {
        // TODO: set up your sample input object here.
        input = null;
    }

    private Context createContext() {
        TestContext ctx = new TestContext();

        // TODO: customize your context here if needed.
        ctx.setFunctionName("handleRequest");

        return ctx;
    }

    @Test
    public void testHello() {
        Hello handler = new Hello();
        Context ctx = createContext();

        input="50";
        String output = handler.handleRequest(input, ctx);

        System.out.println(output);

    }
}
