package de.hska.lkit.demo.redis.configuration;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import de.hska.lkit.demo.redis.model.Post;
import de.hska.lkit.demo.redis.repo.UserRepository;
import de.hska.lkit.demo.redis.repo.impl.ChatMessageSubscriber;



@Configuration
@EnableConfigurationProperties(SimpleRedisProperties.class)
public class RedisConfig2 {
	@Autowired
	private UserRepository userRepo;
	// Simple Message Listener
	/*@Bean
	public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
			MessageListenerAdapter listenerAdapter, @Value("${apress.redis.topic}") String topic) {

		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);		
		container.addMessageListener(listenerAdapter, new PatternTopic(topic));
		*/
		
		// Uncomment this out section if you need an extra subscriber
		/*
		container.addMessageListener(
				(message, pattern) ->{
				
					System.out.println("Pattern: " + new String(pattern));
					System.out.println("Message: " + message);
				
			}
		, new PatternTopic(topic));
		*/
		
	//	return container;
	//}
	
	/*@Bean
	MessageListenerAdapter listenerAdapter(Subscriber subscriber) {
		return new MessageListenerAdapter(subscriber);
	}*/
	
	
	// This section is about using the JSON format Serialization.
//	@Bean
//	public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
//			MessageListenerAdapter rateListenerAdapter) {
//
//		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
//		container.setConnectionFactory(connectionFactory);		
//		container.addMessageListener(rateListenerAdapter, new PatternTopic("/topic/chat-room"));
//		return container;
//	}
	
	@Bean
	RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
			MessageListenerAdapter listenerAdapter) {

		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		System.out.println("1");
		
		container.addMessageListener(listenerAdapter, new PatternTopic("/topic/chat-room"));//chat is der Channel name
		//container.addMessageListener(listenerAdapter, new PatternTopic("/topic/chat-room/b"));
		System.out.println("3");

		return container;
	}

	
	@Bean
	MessageListenerAdapter rateListenerAdapter(ChatMessageSubscriber subscriber) {
		MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(subscriber, "handleMessage");
		messageListenerAdapter.setSerializer(new Jackson2JsonRedisSerializer<>(Post.class));
		return messageListenerAdapter;
	}
	/*@Bean
	StringRedisTemplate template(RedisConnectionFactory connectionFactory) {
		return new StringRedisTemplate(connectionFactory);
	}*/
	
	@Bean(name="redisTemplate2ForWebsocket")
	RedisTemplate<String, Post> redisTemplate2(RedisConnectionFactory connectionFactory){
		RedisTemplate<String,Post> redisTemplate = new RedisTemplate<String,Post>();
		System.out.println("running in templateBeanConfigurtion");
		redisTemplate.setConnectionFactory(connectionFactory);
		redisTemplate.setDefaultSerializer(new Jackson2JsonRedisSerializer<>(Post.class));
		redisTemplate.afterPropertiesSet();
		return redisTemplate;
	}
	
}
