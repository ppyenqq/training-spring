package de.hska.lkit.demo.redis.repo.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import de.hska.lkit.demo.redis.model.Post;

@Component
public class ChatMessageSubscriber {
	private static final Logger LOGGER = LoggerFactory.getLogger(ChatMessageSubscriber.class);
	public String msgg ;
	public Post chatM;
	@Autowired
	private SimpMessagingTemplate simpleMessTemplate;
	/*@Autowired
    public ChatMessageSubscriber() {
       
    }*/
	// If only one method defined, it must be named: handleMessage
	//@MessageMapping("${apress.ws.mapping}")
	//@SendTo("/topic/chat-room")
	public Post handleMessage(Post msg){
		System.out.println("inside handleMessage");
		// Process message here ...
		 LOGGER.info("Received <" + msg + ">");
		 this.msgg= msg.getContent();
		chatM = msg;
		 
		System.out.println("I cam a CMSubscriber: I got the Message: "+ msg.getAuthorId() +" "+ msg.getContent());
		simpleMessTemplate.convertAndSend("/topic/chat-room", msg);
		return msg;
	}
	public Post getMSG() {
		return chatM;
	}
}