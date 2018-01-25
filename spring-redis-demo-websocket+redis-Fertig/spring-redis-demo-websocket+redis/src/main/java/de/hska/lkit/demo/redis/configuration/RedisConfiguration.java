package de.hska.lkit.demo.redis.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/*
 * Kurzgesagt: die Klasse ermöglicht den Verbindungsaufbau und den Datenaustausch
 * 			   zwischen dem Projekt und den Redisserver
 * 
 * Die @Configuration annotation sagt , dass der Spring IoC container diese Klasse als Quelle 
 * für Beans-Definitionen benutzt werden kann 
 * (es gibt ne Alternative für ne Configuration, ihr werdet vllt Projekte begegnen bei denen 
 * die Spring Beans in ein XML Datei konfiguriert wurde,da werden die Beans in Tags/Namensumgebeungen deklariert
 * Bsp: <beans>
   			<bean id = "helloWorld" class = "com.tutorialspoint.HelloWorld" />
   			<constructor-arg></...>
   			<properties></...>
		</beans>
	)
	
 */
@Configuration

public class RedisConfiguration {
	
	/*
	 * Um mit Redis arbeiten zu könne müssen wir eine Verbindung zu Redis aufbauen,
	 * dazu brauchen wir 2 wichtige Dinge , eine ConnectionFactory und einen Template.
	 */
	
	/*
	 * - ConnectionFactory class gehört zu den Java Message Servive
	 *   (bei Interesse an JMS http://www.straub.as/java/jms/basic.html)
	 * - Javaclients müssen mit dem JMS-System Verbindung aufnehmen und
	 *   dann ihre Nachrichten schicken bzw. abholen. 
	 *   
	 * - ConnectionsFactories ist ein Mechanismus in JMS die  eine Verbindungsaufnahme ermöglicht 
	 * - RedisConnectionfactory (die Klasse ist ein interface) erstellt dementsprechen Verbindung zu Redis
	 * - JedisConnection implementiert von RedisConnectionfactory 
	 *   (Jedis ist ne populare open source Java libraries für Redis, grob ezu Jedis : https://github.com/xetorthio/jedis)
	 * wie ihr unten sehen könnt , kann man dementsprechen mit den Bean Host, Port usw. setten
	 */
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
	 * nicht mit den vielen komplizierten Binärdaten und andere Details rumschlagen muss.
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