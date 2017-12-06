package de.hska.lkit.demo.redis.controller;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

/**
 * RedisRepository: zur Speicherung temporärer Session Tokens Das Repository speichert User Hashes
 * (uid:1:user) sowie Auth Hashes (uid:1:auth) und reverse Keys (auth:X:uid) mit Verfallsdatum.
 * Darauf basiert Authentifizierung und Token Management (add, delete).
 */
@Repository
public class RedisRepository {
	@Autowired
	private StringRedisTemplate template;

	/**
	 * Checks wether the given password matches the one in the database.
	 * 
	 * @param uname username
	 * @param pass password
	 * @return returns true if the given password matches the password in the database, otherwise
	 *         false.
	 */
	public boolean auth(String uname, String pass) {
		System.out.println("***Klasse:RedisRepository, Methode: auth wurde aufgerufen.***");
		
		BoundHashOperations<String, String, String> userOps = template.boundHashOps("user:" + uname);
		return userOps.get("password").equals(pass);
	}

	public String addAuth(String uname, long timeout, TimeUnit tUnit) {
		System.out.println("***Klasse:RedisRepository, Methode: addAuth wurde aufgerufen.***");
		
		String auth = UUID.randomUUID().toString();
		template.boundHashOps("user:" + uname).put("auth", auth);
		template.expire("user:" + uname +":auth", timeout, tUnit);
		template.opsForValue().set("auth:" + auth + ":" + uname, uname, timeout, tUnit);
		return auth;
	}

	public void deleteAuth(String uname) {
		System.out.println("***Klasse:RedisRepository, Methode: deleteAuth wurde aufgerufen.***");
		String authKey = "user:" + uname;
		String auth = (String) template.boundHashOps(authKey).get("auth");
		List<String> keysToDelete = Arrays.asList(authKey, "auth:" + auth + ":" + uname);
		template.delete(keysToDelete);
	}
}
