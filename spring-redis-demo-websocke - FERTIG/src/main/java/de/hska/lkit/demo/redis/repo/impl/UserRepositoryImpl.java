package de.hska.lkit.demo.redis.repo.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisZSetCommands.Range;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Repository;

import de.hska.lkit.demo.redis.model.User;
import de.hska.lkit.demo.redis.repo.UserRepository;

/**
 * @author knad0001
 *
 */
/**
 * @author knad0001
 *
 */
@Repository
public class UserRepositoryImpl implements UserRepository {

	/**
	 * das sind vordefinierte keys, die teilweise später mit anderen Strings kombiniert werden um verschiedene keys zu erzeugen
	 */
	private static final String KEY_SET_ALL_USERNAMES 	= "all:usernames";

	private static final String KEY_ZSET_ALL_USERNAMES 	= "all:usernames:sorted";
	
	private static final String KEY_HASH_ALL_USERS 		= "all:user";
	
	private static final String KEY_PREFIX_USER 	= "user:";

	/**
	 * to generate unique ids for user/ 
	 * ich glaube man benutzt diese Klasse anstatt normalen int 
	 * weil wenn der Fall auftreten sollte, dass zwei Personen zu exakt gleichem Zeitpunkt sich registriert,
	 * kann es passieren dass Sie beide den gleichen ID ekommen, während man mit dieser Klasse "atomic"/atomar 
	 * rechnet, das heißt die Rechnungen werden nacheinander ausgeführt, wenn der einer seine ID noch nicht fertig berechnet und
	 * gespeichert wurde kommt der nächste nicht dran, wenn ihr den kleinen Redis Tutorial von deren webseite
	 * gemacht habt, da wurde das mit dem atomar ja auch erwähnt in zusammenhang mit INCR-Operation
	 */
	private RedisAtomicLong userid;
	
	

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
	
	

	/**
	 * hash operations for redisTemplate
	 */
	@Resource(name="redisTemplate")
	private HashOperations<String, String, User> rt_hashOps;
	
	
	/* 
	 * 
	 */
	@Autowired
	public UserRepositoryImpl(RedisTemplate<String, Object> redisTemplate, StringRedisTemplate stringRedisTemplate) {
		this.redisTemplate = redisTemplate;
		this.stringRedisTemplate = stringRedisTemplate;
		this.userid = new RedisAtomicLong("userid", stringRedisTemplate.getConnectionFactory());
	}

	/*
	 * man braucht die deklarationen um später Operationen aufzurufen um Daten in die Datenbank
	 * reinzusetzen oder was auch immer
	 * alternativ kann man es auch im Konstruktor deklarieren statt im init method 
	 */
	@PostConstruct
	private void init() {
		System.out.println("***Klasse:UserRepositryImpl, Methode: init() wurde aufgerufen.***");
		srt_hashOps = stringRedisTemplate.opsForHash();//Returns the operations performed on hash values.
		srt_setOps = stringRedisTemplate.opsForSet();//Returns the operations performed on set values.
		srt_zSetOps = stringRedisTemplate.opsForZSet();
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * hska.iwi.vslab.repo.UserRepository#saveUser(hska.iwi.vslab.model.User)
	 */
	@Override
	public void saveUser(User user) {
		System.out.println("***Klasse:UserRepositoryImpl, Methode: saveUser() wurde aufgerufen.***");
		/* generate a unique id , jedes mal wenn ein neuer User geaddet wird, bekommt der nen ID, der eins höher ist 
		   als der User vorher, der vorherige Wert von dem vorherigen User wurde in Variable userid gespeichert,
		   und das +1 wird von der Methode incrementAndGet durchgeführt
		*/
		String id = String.valueOf(userid.incrementAndGet());

		user.setId(id);

		// to show how objects can be saved
		// be careful, if username already exists it's not added another time
		//Hier wird der key indem man den user ablegt deklariert
		String key = KEY_PREFIX_USER + user.getUsername();
		//man ruft die methode "put()" auf um Daten in das Hash zu speichern
		srt_hashOps.put(key, "id", id);
		srt_hashOps.put(key, "username", user.getUsername());
		srt_hashOps.put(key, "password", user.getPassword());

		// the key for a new user is added to the set for all usernames, hier werden alle Usernamen in Set gespeichert
		srt_setOps.add(KEY_SET_ALL_USERNAMES, user.getUsername());
		
		// the key for a new user is added to the sorted set for all usernames
		srt_zSetOps.add(KEY_ZSET_ALL_USERNAMES, user.getUsername(), 0);

		// to show how objects can be saved hier wird ins redisTemplategespeichert nicht springTemplate,
		//wie im Configuration class erwähnt kann man in redisTemplate ganze Objekte speichern
		rt_hashOps.put(KEY_HASH_ALL_USERS, key, user);

	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * hska.iwi.vslab.repo.UserRepository#saveUser(hska.iwi.vslab.model.User)
	 */
	@Override
	public void saveFollower(String user, String otherUser) {
		System.out.println("***Klasse:UserRepositoryImpl, Methode: saveFollower() wurde aufgerufen.***");
		
		// deklariert den Key fuer den aktuellen User
		String key = KEY_PREFIX_USER + user + ":following";
		
		// deklariert den Key fuer den User, dem gefolgt werden moechte
		String keyFollower = KEY_PREFIX_USER + otherUser +":follower";
		
		
		/*
		 * Es existiert folgende Beziehung:
		 * user --folgt--> username  (dies entspricht "follow")
		 * username --wird gefolgt--> von user  (dies entspricht "following")
		 */
		
		// der User, dem gefolgt werden moechte, wird hinzugefuegt
		srt_setOps.add(key, otherUser);
		
		// der User, dem gefolgt wird, wird ein Follower ergaenzt
		srt_setOps.add(keyFollower, user);
	}
	
	@Override
	public void removeFollower(String user, String otherUser){
		// deklariert den Key fuer den aktuellen User
		String key = KEY_PREFIX_USER + user + ":following";
				
		// deklariert den Key fuer den User, dem gefolgt werden moechte
		String keyFollower = KEY_PREFIX_USER + otherUser +":follower";
		
		srt_setOps.remove(key, otherUser);
		srt_setOps.remove(keyFollower, user);
	}
	
	/*
	 * gibt alle meiner Follower zurück
	 */
	@Override
	public Set<String> getAllFollowers(String vonUser) {
		System.out.println("***Klasse:UserRepositoryImpl, Methode: getAllFollowers() wurde aufgerufen.***");
		return srt_setOps.members(KEY_PREFIX_USER + vonUser + ":follower");
	}
	/*
	 * gibt alle, die ich folge zurück
	 */
	@Override
	public Set<String> getPeopleIFollow(String vonUser) {
		System.out.println("***Klasse:UserRepositoryImpl, Methode: getAllFollowers() wurde aufgerufen.***");
		return srt_setOps.members(KEY_PREFIX_USER + vonUser + ":following");
	}
		
	/*
	 * (non-Javadoc)
	 * @see de.hska.lkit.demo.redis.repo.PostRepository#getAllUsers()
	 * die Methode gibt mir alle User zurück mit deren informationen 
	 * die Methode .entries() des redisTemplate gibt mir alle User zurück die ich grad oben gespeichert habe als Map<S,U>
	 */
	@Override
	public Map<String, User> getAllUsers() {
		System.out.println("***Klasse:UserRepositoryImpl, Methode: getAllUsers() wurde aufgerufen.***");
		return rt_hashOps.entries(KEY_HASH_ALL_USERS);
	}


	@Override
	public Set<String> findUsersWith(String pattern) {
		System.out.println("***Klasse:UserRepositoryImpl, Methode: findUsersWith() wurde aufgerufen.***");

		System.out.println("Searching for pattern  " + pattern);

		Set<byte[]> result = null;
		Map<String, User> mapResult = new HashMap<String, User>();
		Set <String> sresult = null ;

		if (pattern.equals("")){
			
			// get all user
			mapResult = rt_hashOps.entries(KEY_HASH_ALL_USERS);
	
		} else {
			// search for user with pattern
			
			char[] chars = pattern.toCharArray();
			chars[pattern.length() - 1] = (char) (chars[pattern.length() - 1] + 1);
			String searchto = new String(chars);

			 sresult = srt_zSetOps.rangeByLex(KEY_ZSET_ALL_USERNAMES, Range.range().gte(pattern).lt(searchto));
			for (Iterator iterator = sresult.iterator(); iterator.hasNext();) {
				String username = (String) iterator.next();
				System.out.println("key found: "+ username);
				User user = (User) rt_hashOps.get(KEY_HASH_ALL_USERS, KEY_PREFIX_USER + username);
	
				mapResult.put(user.getUsername(), user);
			}

		}
		
		return sresult;

	}
	
	/*
	 * wird für anzeige von user bei search-sidebar benutzt
	 */
	@Override
	public Set<String> displayUserZSET() {
		Set <String> zsresult = srt_zSetOps.range(KEY_ZSET_ALL_USERNAMES, 0, -1);		
		return zsresult;
	}
	@Override
	public boolean isFollowingOtherUser(String loggedInUser, String otherUser) {
		String key = KEY_PREFIX_USER + loggedInUser + ":following";
		return stringRedisTemplate.opsForSet().isMember(key, otherUser);
	}
		
}