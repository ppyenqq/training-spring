package de.hska.lkit.demo.redis.model;

import org.springframework.core.NamedThreadLocal;

/**
 * 
 * SimpleSecurity: Utility-Klasse für Session Informationen
 *  Einfache Utility-Klasse, die den Login-Status pro Zugriff verwaltet.
 *  Die Klasse wird durch einen Interzeptor initialisiert und zurückgesetzt.
 */
public abstract class SimpleSecurity {
	private static final ThreadLocal<UserInfo> user = new NamedThreadLocal<UserInfo>("microblog-id");

	private static class UserInfo {
		String name;
		String uid;
	}

	public static void setUser(String name, String uid) {
		System.out.println("***Klasse:SimpleSecurity, Methode: setUser() wurde aufgerufen.***");
		UserInfo userInfo = new UserInfo();
		userInfo.name = name;
		userInfo.uid = uid;
		user.set(userInfo);
	}

	public static boolean isUserSignedIn(String name) {
		System.out.println("***Klasse:SimpleSecurity, Methode: isUserSignedIn wurde aufgerufen.***");
		UserInfo userInfo = user.get();
		return userInfo != null && userInfo.name.equals(name);
	}

public static boolean isSignedIn() { 
	UserInfo userInfo = user.get();
	return userInfo != null && (userInfo.name!=null); 
	}

public static String getName() {
	UserInfo userInfo = user.get();
	return userInfo.name;
	}

public static String getUid() { 
	UserInfo userInfo = user.get();
	return userInfo.uid;
	}

}