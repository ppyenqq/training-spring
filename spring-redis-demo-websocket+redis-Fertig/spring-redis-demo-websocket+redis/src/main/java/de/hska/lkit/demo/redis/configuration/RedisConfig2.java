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
import de.hska.lkit.demo.redis.repo.impl.PostSubscriber;

//Die Klasse enthält Konfigurationen für Redis PubSub. Mit dem Rediscontainer und MessageListener kann man
//nachrichten, die auf einen channel im redis gepublish wurde abfangen 
//Die Klasse wurde erstellt weil es Probleme gab sie zusammen mit der RedisConfiguration.java Klasse zu vereinigen
@Configuration
@EnableConfigurationProperties(SimpleRedisProperties.class)
public class RedisConfig2 {
	
	@Bean
	RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
			MessageListenerAdapter listenerAdapter) {

		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);				
		container.addMessageListener(listenerAdapter, new PatternTopic("/topic/chat-room"));//chat is der Channel name		
		return container;
	}
	
	@Bean
	MessageListenerAdapter rateListenerAdapter(PostSubscriber subscriber) {
		MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(subscriber, "handleMessage");
		messageListenerAdapter.setSerializer(new Jackson2JsonRedisSerializer<>(Post.class));
		return messageListenerAdapter;
	}
	
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
