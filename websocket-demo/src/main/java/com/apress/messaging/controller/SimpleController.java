package com.apress.messaging.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.apress.messaging.domain.ChatMessage;
import com.apress.messaging.redis.ChatMessageSubscriber;


@Controller
public class SimpleController {
	//aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
/*	
	private SimpMessagingTemplate template;
	@Autowired
	public SimpleController(SimpMessagingTemplate template) {
	this.template = template;
	}
	
	@MessageMapping("${apress.ws.mapping}")
	//@SendTo("/topic/chat-room")
	@RequestMapping(path="/rate/new", method=RequestMethod.POST)
	public void newRates(ChatMessage msg) {
	this.template.convertAndSend("/topic/chat-room", msg);
	}*/
	
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ChatMessageSubscriber.class);
	//private StringRedisTemplate strTemplate;
	private RedisTemplate<String, ChatMessage> reTemplate;
	
	@Autowired
	public SimpleController(RedisTemplate<String, ChatMessage> template) {
	this.reTemplate = template;
	}
	@Autowired
	ChatMessageSubscriber subs;
	
	@MessageMapping("${apress.ws.mapping}")
	@SendTo("/topic/chat-room")
	@RequestMapping(path="/rate/new", method=RequestMethod.POST)
	public ChatMessage newRates(ChatMessage msg) throws Exception {
		System.out.println("still working ??");
		
	//this.reTemplate.convertAndSend("/topic/chat-room", "hello There");
		//System.out.println("Topic: " +topic);
		System.out.println(msg.toString());
		
		
		this.reTemplate.convertAndSend("/topic/chat-room", msg);
		System.out.println("whats inside charmessagesubscriber: "+ subs.getMSG());
	System.out.println("still wrkeing 22222222222222?????????");
	Thread.sleep(500);
	System.out.println("heyyyyyyyy");
	System.out.println(subs.msgg);
	return subs.getMSG();
	
	}
	
	/*@MessageMapping("${apress.ws.mapping}")
	@SendTo("/topic/chat-room")
	@RequestMapping(path="/rate/new", method=RequestMethod.POST)
	public ChatMessage newRates(ChatMessage msg) {
		System.out.println("still working ??");
	//this.reTemplate.convertAndSend("/topic/chat-room", "hello There");
		//System.out.println("Topic: " +topic);
		System.out.println(msg.toString());
		
		this.reTemplate.convertAndSend("/topic/chat-room", msg);
	System.out.println("still wrkeing 22222222222222?????????");
	return msg;
	}
	*/
	
	/*private RedisvalueSerializer =null;
	private byte[] rawValue(Object value) {
		if (valueSerializer == null && value instanceof byte[]) {
			return (byte[]) value;
		}
		return valueSerializer.serialize(value);
	}*/
	/*@MessageMapping("${apress.ws.mapping}")
	@SendTo("/topic/chat-room")
	public ChatMessage chatRoom(ChatMessage message) {
		return message;
	}*/
	
	
	
	
	
	/*@Bean
	CommandLineRunner sendMessage(StringRedisTemplate template, @Value("${apress.redis.topic}")String topic){
		return args -> {
			template.convertAndSend(topic, "Hello Redis with Spring Boot!");
		};
	}*/
	/* Enable for a dynamic destination */
	/*
	@MessageMapping("${apress.ws.mapping}/{room}")
	@SendTo("/topic/chat-room/{user}")
	public ChatMessage directChatRoom(@DestinationVariable("room")String room
			,@DestinationVariable("user")String user,ChatMessage message) {
		
		return message;
	}
	*/
}
