package de.hska.lkit.demo.redis.repo;

import java.util.Map;
import java.util.Set;

import de.hska.lkit.demo.redis.model.User;

public interface UserRepository {
	
	/**
	 * save user to repository
	 * 
	 * @param user
	 */
	public void saveUser(User user);
	
	
	/**
	 * returns a list of all users
	 * 
	 * @return
	 */
	public Map<String, User> getAllUsers();
	
	
	/**
	 * find the user with username
	 * 
	 * @param username
	 * @return
	 */
	public User getUser(String username);


	/**
	 * 
	 * find all users with characters in username
	 * 
	 * @param characters
	 * @return
	 */
	public Set<String> findUsersWith(String characters);
	
	public void saveFollower(String user, String otherUser);
	public void removeFollower(String user, String otherUser);
	public Map<String, User> getAllFollowers(User vonUser);


	public Set<String> displayUserZSET();


	boolean isFollowingOtherUser(String loggedInUser, String otherUser);
	
}
