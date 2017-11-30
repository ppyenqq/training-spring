package de.hska.lkit.demo.redis.model;

import java.io.Serializable;

public class Session implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String sessionKey;
	private String userId;

	public String getSessionKey() {
		return sessionKey;
	}

	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
}
