package de.hska.lkit.demo.redis.configuration;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Receiver {
    private static final Logger LOGGER = LoggerFactory.getLogger(Receiver.class);

    //private CountDownLatch latch;
    public String smessage;

   

    public String receiveMessage(String message) {
    	System.out.println("Inside Receive Message");
    	System.out.println(message);
        LOGGER.info("Received <" + message + ">");
        smessage=message;
        return message;
    }
}
