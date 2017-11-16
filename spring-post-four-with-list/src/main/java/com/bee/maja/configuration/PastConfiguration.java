package com.bee.maja.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class PastConfiguration {
	@Bean
	public JedisConnectionFactory getConnectionFactory() {
		// falls andere als die Default Werte gesetzt werden sollen
//		JedisConnectionFactory jRedisConnectionFactory = new JedisConnectionFactory(new JedisPoolConfig());
//		jRedisConnectionFactory.setHostName("localhost");
//		jRedisConnectionFactory.setPort(6379);
//		jRedisConnectionFactory.setPassword("");
//		return jRedisConnectionFactory;
		return new JedisConnectionFactory();
	}
	/* So wie ich verstanden habe bietet RedisConnection(Factory)irgendwie "low-level" Methoden,
	 * die nur Binärwerte (byte arrays) akzeptiert und zurückgibt, deswegen übernimmt 
	 * das Template die Serialisierung/Deserialisierung und Connection Management, sodass man sich 
	 * nicht mit den vielen komplizierten Details rumschlagen muss.
	 * 
	 * Bei anderen Datenbanken in Spring werdet ihr auch Templates begegnen.
	 * Die Templates dienen dazu den Spring data access code zu vereinfachen.
	 * Später im Repository wird es wieder benutzt, dann versteht ihrs vllt besser.
	 * 
	 * Unten sind 2 Templates, das Redistemplate und StringRedisTemplate.
	 * Das StringTemplate ist eine Unterklasse von RedisTemplate, that is focused on 
	 * the common use of Redis where both keys and values are `String`s, man arbeitet also nur mit Strings.
	 * Während man mit RedisTemplate<K,V> keys K hat (die normalerweise auch string ist) aber werte V hat, 
	 * die auch Objekte sein können.
	 *  
	 * Man muss das Template mit dem JedisConnectionFactory in verbindung setzen, daher 
	 * wird die getConnectionFactory(), also das JedisConnectionFactory Bean im Kontruktor bzw durch 
	 * Setter-Methode gesetzt.
	 * 
	 */

	@Bean(name = "stringRedisTemplate")
	public StringRedisTemplate getStringRedisTemplate() {
		StringRedisTemplate stringRedisTemplate = new StringRedisTemplate(getConnectionFactory());
		stringRedisTemplate.setKeySerializer(new StringRedisSerializer());
		stringRedisTemplate.setHashValueSerializer(new StringRedisSerializer());
		stringRedisTemplate.setValueSerializer(new StringRedisSerializer());
		return stringRedisTemplate;
	}

	
	@Bean(name = "redisTemplate")
	public RedisTemplate<String, Object> getRedisTemplate() {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
		redisTemplate.setConnectionFactory(getConnectionFactory());
		return redisTemplate;
	}

}
