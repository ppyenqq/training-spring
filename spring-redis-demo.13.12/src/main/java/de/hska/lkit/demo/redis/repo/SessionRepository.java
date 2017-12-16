package de.hska.lkit.demo.redis.repo;

import de.hska.lkit.demo.redis.model.Session;

public interface SessionRepository {
	
	public void saveSession (Session session, String cookieValue);
	public Session getSession(String cookieValue);
	
}