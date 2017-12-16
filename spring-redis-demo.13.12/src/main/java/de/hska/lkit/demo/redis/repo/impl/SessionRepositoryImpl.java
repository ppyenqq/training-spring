package de.hska.lkit.demo.redis.repo.impl;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Repository;

import de.hska.lkit.demo.redis.model.Session;
import de.hska.lkit.demo.redis.repo.SessionRepository;

/**
 * The Implementation of SessionRepository Interface.
 * 
 * @author Patricia Djami
 *
 */
@Repository
public class SessionRepositoryImpl implements SessionRepository{
	
	/**
	 * to save data in String format
	 */
	private StringRedisTemplate stringRedisTemplate;

	/**
	 * to save user data as object
	 */
	private RedisTemplate<String, Object> redisTemplate;
	
	/**
	 * value operations for stringRedisTemplate
	 */
	private ValueOperations<String, String> srt_valueOps;

	/**
	 * value operations for redisTemplate
	 */
	@Resource(name="redisTemplate")
	private ValueOperations<String, Session> rt_valueOps;
	
	/**
	 * set operations for stringRedisTemplate
	 */
	private SetOperations<String, String> srt_setOps;
	
	/**
	 * set operations for redisTemplate
	 */
	@Resource(name="redisTemplate")
	private SetOperations<String, Session> rt_setOps;

	
	/**
	 * Constructor for the SessionRespositoryImpl
	 * @param redisTemplate
	 * @param stringRedisTemplate
	 */
	@Autowired
	public SessionRepositoryImpl(RedisTemplate<String, Object> redisTemplate, StringRedisTemplate stringRedisTemplate) {
		this.redisTemplate = redisTemplate;
		this.stringRedisTemplate = stringRedisTemplate;
	}
	
	@PostConstruct
	private void init() {
		srt_valueOps = stringRedisTemplate.opsForValue();
	}
	
	/**
	 * this saves the session of the user and adds to the list of "allSessions".
	 */
	@Override
	public void saveSession(Session session, String cookieValue) {
		
		String key = "session:"+cookieValue;
		srt_valueOps.set(key, session.getUsername());
		
		srt_setOps.add("allSessions", cookieValue);
	}

	/**
	 * this basically returns the username of the session.
	 */
	@Override
	public Session getSession(String cookieValue) {
		Session session = new Session();
		
		if (srt_setOps.isMember("allSessions", cookieValue)) {
			
			String key = "session" + cookieValue;
			session.setUsername(srt_valueOps.get(key));
		} else {
			session = null;
		}
		return session;
	}

}
