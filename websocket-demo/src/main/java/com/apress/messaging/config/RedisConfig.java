package com.apress.messaging.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
/*import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;*/
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import com.apress.messaging.domain.ChatMessage;
import com.apress.messaging.redis.ChatMessageSubscriber;

//import com.apress.messaging.redis.RateSubscriber;
//import com.apress.messaging.redis.Subscriber;

@Configuration
@EnableConfigurationProperties(SimpleRedisProperties.class)
public class RedisConfig {

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
	@Bean
	public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
			MessageListenerAdapter rateListenerAdapter) {

		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);		
		container.addMessageListener(rateListenerAdapter, new PatternTopic("/topic/chat-room"));
		return container;
	}

	
	@Bean
	MessageListenerAdapter rateListenerAdapter(ChatMessageSubscriber subscriber) {
		MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(subscriber, "handleMessage");
		messageListenerAdapter.setSerializer(new Jackson2JsonRedisSerializer<>(ChatMessage.class));
		return messageListenerAdapter;
	}
	/*@Bean
	StringRedisTemplate template(RedisConnectionFactory connectionFactory) {
		return new StringRedisTemplate(connectionFactory);
	}*/
	
	@Bean
	@Primary
	RedisTemplate<String, ChatMessage> redisTemplate(RedisConnectionFactory connectionFactory){
		RedisTemplate<String,ChatMessage> redisTemplate = new RedisTemplate<String,ChatMessage>();
		System.out.println("running in templateBeanConfigurtion");
		redisTemplate.setConnectionFactory(connectionFactory);
		redisTemplate.setDefaultSerializer(new Jackson2JsonRedisSerializer<>(ChatMessage.class));
		redisTemplate.afterPropertiesSet();
		return redisTemplate;
	}
	
}