package de.hska.lkit.demo.redis.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;


@Component
public class PostSubscriber {
	private static final Logger LOGGER = LoggerFactory.getLogger(PostSubscriber.class);
	private String msgg ;
	private Post postM;
	
	// If only one method defined, it must be named: handleMessage
	//@MessageMapping("/chat-room")
	//@SendTo("/topic/chat-room")
	public Post handleMessage(Post msg){
		System.out.println("inside handleMessage");
		// Process message here ...
		 LOGGER.info("PostSubscriber received <" + msg + ">");
		 this.msgg= msg.getContent();
		postM = msg;
		 
		System.out.println("I am inside PostSubscriber: I got the Author/Message: "+ msg.getAuthorId() +"  "+ msg.getContent());
		return msg;
	}
	public Post getPost() {
		return postM;
	}

}
