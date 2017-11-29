package com.bee.maja.repo.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Repository;

import com.bee.maja.model.Past;
import com.bee.maja.repo.PastRepository;




@Repository
public class PastRepositoryImpl implements PastRepository{
	
	/**
	 * 
	 */
	//private static final String KEY_SET_ALL_CONTENT 	= "all:usernames";

	//private static final String KEY_ZSET_ALL_USERNAMES 	= "all:usernames:sorted";
	
	private static final String KEY_HASH_ALL_PAST 		= "all:past";
	private static final String KEY_LIST_ALL_PAST 		= "all:list:past";
	
	private static final String KEY_PREFIX_PAST 	= "past:";
	private static final String KEY_LIST_ALL_CONTENT 		= "all:content";

	/**
	 * to generate unique ids for user/ ich glaube man benutzt diese Klasse anstatt normalen int ist
	 * weil wenn der Fal auftreten sollte, dass zwei Personen zu exact gleichem Zeitpunkt sich registriert,
	 * kann es passieren dass Sie beide den gleichen ID ekommen, während man mit dieser Klasse "atomic"/atomar 
	 * rechnet, das heißt die Rechnungen werden nacheinender ausgeführt, wenn der einer noch nicht fertig und
	 * gespeichert wurde kommt der nächste nicht dran, wenn ihr den kleinen Redis Tutorial von deren webseite
	 * gemacht habt, da wurde das mit dem atomar ja auch erwähnt in zusammenhang mit INCR-Operation
	 */
	private RedisAtomicLong pastid;
	
	

	/**
	 * to save data in String format
	 */
	private StringRedisTemplate stringRedisTemplate;

	/**
	 * to save user data as object
	 */
	private RedisTemplate<String, Object> redisTemplate;
	
	

	/**
	 * hash operations for stringRedisTemplate
	 */
	private HashOperations<String, String, String> srt_hashOps;

	/**
	 * set operations for stringRedisTemplate
	 */
	private SetOperations<String, String> srt_setOps;
	
	/**
	 * zset operations for stringRedisTemplate
	 */
	private ZSetOperations<String, String> srt_zSetOps;
	private ListOperations<String, String> srt_listOps;
	
	
	

	/**
	 * hash operations for redisTemplate
	 */
	@Resource(name="redisTemplate")
	private HashOperations<String, String, Past> rt_hashOps;
	@Resource(name="redisTemplate")
	private ListOperations< String, Past> rt_listOps;
	
	
	/* 
	 * 
	 */
	@Autowired
	public PastRepositoryImpl(RedisTemplate<String, Object> redisTemplate, StringRedisTemplate stringRedisTemplate) {
		this.redisTemplate = redisTemplate;
		this.stringRedisTemplate = stringRedisTemplate;
		this.pastid = new RedisAtomicLong("pastid", stringRedisTemplate.getConnectionFactory());
	}

	/*
	 * man braucht die deklarationen um später Operationen aufzurufen um Daten in die Datenbank
	 * reinzusetzen oder was auch immer
	 * alternativ kann man es auch im Konstruktor deklarieren statt im init method 
	 */
	@PostConstruct
	private void init() {
		srt_hashOps = stringRedisTemplate.opsForHash();//Returns the operations performed on hash values.
		srt_setOps = stringRedisTemplate.opsForSet();//Returns the operations performed on set values.
		srt_zSetOps = stringRedisTemplate.opsForZSet();
		srt_listOps = stringRedisTemplate.opsForList();
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * hska.iwi.vslab.repo.UserRepository#saveUser(hska.iwi.vslab.model.User)
	 */
	@Override
	public void savePast(Past past) {
		/* generate a unique id , jedes mal wenn ein neuer User geaddet wird, bekommt der nen ID, der eins höher ist 
		   als der User vorher, der vorherige Wert von dem vorherigen User wurde in Variable userid gespeichert,
		   und das +1 wird von der Methode incrementAndGet durchgeführt
		*/
		String id = String.valueOf(pastid.incrementAndGet());

		past.setId(id);

		// to show how objects can be saved
		// be careful, if username already exists it's not added another time
		//Hier wird der key indem man den user ablegt deklariert
		String key = KEY_PREFIX_PAST + id;
		//man ruft die methode "put()" auf um Daten in das Hash zu speichern
		srt_hashOps.put(key, "id", id);
		srt_hashOps.put(key, "content", past.getContent());
		srt_hashOps.put(key, "author", past.getAuthor());
		

		// the key for a new user is added to the set for all usernames, hier werden alle Usernamen in Set gespeichert
		//srt_setOps.add(KEY_SET_ALL_CONTENT, past.getContent());
		
		// the key for a new user is added to the sorted set for all usernames
		//srt_zSetOps.add(KEY_ZSET_ALL_USERNAMES, past.getContent(), 0);
		// push in das globale timeline
		srt_listOps.rightPush(KEY_LIST_ALL_PAST, id);
		// push den content in den key von diesen author , vllt macht es mehr sinn id statt content reinzupushen???????????????
		srt_listOps.rightPush(KEY_LIST_ALL_PAST+past.getAuthor(), past.getContent());
		
		// to show how objects can be saved, hier wird ins redisTemplategespeichert nicht springTemplate,
		//wie im Configuration class erwähnt kann man in redisTemplate ganze Objekte speichern
		rt_hashOps.put(KEY_HASH_ALL_PAST, key, past);
		//rt_listOps.rightPush(KEY_LIST_ALL_PAST, past);

	}
	/*
	 * (non-Javadoc)
	 * @see de.hska.lkit.demo.redis.repo.PostRepository#getAllUsers()
	 * die Methode gibt mir alle User zurück mit deren informationen 
	 * die Methode .entries() des redisTemplate gibt mir alle User zurück die ich grad oben gespeichert habe als Map<S,U>
	 */
	@Override
	public Map<String, Past> getAllPast() {
		return rt_hashOps.entries(KEY_HASH_ALL_PAST);
	}
	/*
	 * (non-Javadoc)
	 * @see com.bee.maja.repo.PastRepository#getAllPastList()
	 * gibt die liste aller post-id die jemals gespeichert wurde , also die globle timeline
	 */
	@Override
	public List<String> getAllPastList() {
		return srt_listOps.range(KEY_LIST_ALL_PAST, 0, -1);
	}
	//diese methode wird nicht funktionieren, die funktioniert nur wenn ich in srt_ListOps mit dem key ...+author nicht den
	// Content reinpushe sondern id, 
	@Override
	public List<String> getOneUsersPost(String authorname){
		List<String> userPost = new LinkedList();
		for(long i=1; i<=srt_listOps.size(KEY_LIST_ALL_PAST+authorname); i++) {
            userPost.add(srt_hashOps.get(KEY_PREFIX_PAST + Long.toString(i), "content"));
            
        }
		return userPost;
		
	}
	// ausgabe der content die gleichen author haben , die älteste reingepushte ist aber oben, muss daher bei timeline geändert werden
	@Override
	public List<String> getOneUsersPost22(String authorname){
		
		return srt_listOps.range(KEY_LIST_ALL_PAST+authorname, 0, -1);
		
	}

}