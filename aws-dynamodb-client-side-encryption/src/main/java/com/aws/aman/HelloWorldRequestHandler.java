package com.aws.aman;
import com.amazonaws.services.lambda.runtime.Context;

import com.amazonaws.services.lambda.runtime.RequestHandler;

public class HelloWorldRequestHandler implements RequestHandler<String,String> {

    @Override
    public String handleRequest(String s, Context context) {

        return "My first request handler: " + s;

    }

}
