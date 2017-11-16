package de.hska.lkit.demo.redis.repo;

import java.util.Map;

import de.hska.lkit.demo.redis.model.Post;

public interface PostRepository {
	
	/**
	 * save user to repository
	 * 
	 * @param user
	 */
	public void saveUser(Post user);
	
	
	/**
	 * returns a list of all users
	 * 
	 * @return
	 */
	public Map<String, Post> getAllUsers();
	
	
	/**
	 * find the user with username
	 * 
	 * @param username
	 * @return
	 */
	public Post getUser(String username);


	/**
	 * 
	 * find all users with characters in username
	 * 
	 * @param characters
	 * @return
	 */
	public Map<String, Post> findUsersWith(String characters);
	
}
