package com.apress.messaging.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import com.apress.messaging.domain.ChatMessage;



@Component
public class ChatMessageSubscriber {
	private static final Logger LOGGER = LoggerFactory.getLogger(ChatMessageSubscriber.class);
	public String msgg ;
	public ChatMessage chatM;
	/*@Autowired
    public ChatMessageSubscriber() {
       
    }*/
	// If only one method defined, it must be named: handleMessage
	@MessageMapping("${apress.ws.mapping}")
	@SendTo("/topic/chat-room")
	public ChatMessage handleMessage(ChatMessage msg){
		System.out.println("inside handleMessage");
		// Process message here ...
		 LOGGER.info("Received <" + msg + ">");
		 this.msgg= msg.getMessage();
		chatM = msg;
		 
		System.out.println("I cam a CMSubscriber: I got the Message: "+ msg.getUser() +" "+ msg.getMessage());
		return msg;
	}
	public ChatMessage getMSG() {
		return chatM;
	}
}

