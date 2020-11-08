package com.amazonaws.serverless.sample.springboot2;


import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.internal.testutils.Timer;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.spring.SpringBootLambdaContainerHandler;
import com.amazonaws.serverless.sample.springboot2.filter.CognitoIdentityFilter;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;


public class StreamLambdaHandler implements RequestStreamHandler {
	  final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private static SpringBootLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler;
    static {
        try {
            handler = SpringBootLambdaContainerHandler.getAwsProxyHandler(Application.class);

            // For applications that take longer than 10 seconds to start, use the async builder:
            // long startTime = Instant.now().toEpochMilli();
            // handler = new SpringBootProxyHandlerBuilder()
            //                    .defaultProxy()
            //                    .asyncInit(startTime)
            //                    .springBootApplication(Application.class)
            //                    .buildAndInitialize();

            // we use the onStartup method of the handler to register our custom filter
//            handler.onStartup(servletContext -> {
//                FilterRegistration.Dynamic registration = servletContext.addFilter("CognitoIdentityFilter", CognitoIdentityFilter.class);
//                registration.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
//            });
        } catch (ContainerInitializationException e) {
            // if we fail here. We re-throw the exception to force another cold start
            e.printStackTrace();
            throw new RuntimeException("Could not initialize Spring Boot application", e);
        }
    }

    public StreamLambdaHandler() {
        // we enable the timer for debugging. This SHOULD NOT be enabled in production.
        Timer.enable();
    }

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context)
            throws IOException {
    	
        LOGGER.info("Invoking handleRequest");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        org.apache.commons.io.IOUtils.copy(inputStream, baos);
        byte[] bytes = baos.toByteArray();

        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        String s = new String(bytes, StandardCharsets.UTF_8);
        LOGGER.info(s);

        // reading from API GW object
//        Request apiRequest = objectMapper.readValue(s, Request.class);
/*
        LOGGER.info("input body after converting API GW object" + apiRequest.getBody().toString());
        String reqBodyAsString = apiRequest.getBody().toString();
*/

//       outputStream.write(objectMapper.writeValueAsString(apiRequest.getBody()).getBytes());
//        LOGGER.info("Lambda process is completed");

        handler.proxyStream(bais, outputStream, context);
        
      
    }
}
