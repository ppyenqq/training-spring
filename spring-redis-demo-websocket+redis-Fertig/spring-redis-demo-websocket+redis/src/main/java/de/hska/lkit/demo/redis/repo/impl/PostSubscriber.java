package de.hska.lkit.demo.redis.repo.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import de.hska.lkit.demo.redis.model.Post;


@Component
public class PostSubscriber {
	//private static final Logger LOGGER = LoggerFactory.getLogger(PostSubscriber.class);
	private Post postM;
	@Autowired
	private SimpMessagingTemplate simpleMessTemplate;
	
	
	// If only one method defined, it must be named: handleMessage
	public Post handleMessage(Post msg){
		//LOGGER.info("PostSubscriber received <" + msg + ">");
		postM = msg;		 
		System.out.println("I am inside PostSubscriber: I got the Author/Message: "+ msg.getAuthorId() +"  "+ msg.getContent());
		simpleMessTemplate.convertAndSend("/topic/chat-room"+msg.getAuthorId(), msg);
		return msg;
	}
	public Post getPost() {
		return postM;
	}

}
