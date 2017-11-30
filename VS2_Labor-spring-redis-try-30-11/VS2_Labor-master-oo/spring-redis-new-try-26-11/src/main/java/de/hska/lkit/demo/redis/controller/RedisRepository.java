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
 * RedisRepository: zur Speicherung temporärer Session Tokens
 *  Das Repository speichert User Hashes (uid:1:user) sowie Auth Hashes
 *  (uid:1:auth) und reverse Keys (auth:X:uid) mit Verfallsdatum.
 *  Darauf basiert Authentifizierung und Token Management (add, delete).
 */
@Repository
public class RedisRepository {
	@Autowired
	private StringRedisTemplate template;

	public boolean auth(String uname, String pass) {
		//String uid = template.opsForValue().get("uname:" + uname + ":uid");
		BoundHashOperations<String, String, String> userOps = template.boundHashOps("user:" + uname );
		return userOps.get("password").equals(pass);
	}

	public String addAuth(String uname, long timeout, TimeUnit tUnit) {
		//String uid = template.opsForValue().get("uname:" + uname + ":uid");
		String auth = UUID.randomUUID().toString();
		template.boundHashOps("uname:" + uname + ":auth").put("auth", auth);
		template.expire("uname:" + uname + ":auth", timeout, tUnit);
		template.opsForValue().set("auth:" + auth + ":uid", uname, timeout, tUnit);
		return auth;
	}

	public void deleteAuth(String uname) {
		//String uid = template.opsForValue().get("uname:" + uname + ":uid");
		String authKey = "uname:" + uname + ":auth";
		String auth = (String) template.boundHashOps(authKey).get("auth");
		List<String> keysToDelete = Arrays.asList(authKey, "auth:" + auth + ":uid");
		template.delete(keysToDelete);
	}
}
