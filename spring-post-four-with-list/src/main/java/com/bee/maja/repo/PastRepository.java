package com.bee.maja.repo;

import java.util.List;
import java.util.Map;

import com.bee.maja.model.Past;

public interface PastRepository {
	
	/**
	 * save user to repository
	 * 
	 * @param user
	 */
	public void savePast(Past past);
	
	
	/**
	 * returns a list of all users
	 * 
	 * @return
	 */
	public Map<String, Past> getAllPast();
	public List<String> getAllPastList();
	public List<String> getOneUsersPost(String authorname);


	List<String> getOneUsersPost22(String authorname);
}
