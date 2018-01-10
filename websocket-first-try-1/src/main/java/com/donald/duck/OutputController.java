package com.donald.duck;



import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class OutputController {


    @MessageMapping("/hello")
    @SendTo("/topic/greetingss")
    public MessageOutput greeting(Message message) throws Exception {
    	String time = new SimpleDateFormat("HH:mm").format(new Date());
    	
        Thread.sleep(1000); // simulated delay
        return new MessageOutput(message.getAuthor(),  message.getContent(), time );
    }

}

