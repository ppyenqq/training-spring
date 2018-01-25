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
	 * 
	 * find all users with characters in username
	 * 
	 * @param characters
	 * @return
	 */
	public Set<String> findUsersWith(String characters);
	
	public void saveFollower(String user, String otherUser);
	public void removeFollower(String user, String otherUser);
	public Set<String> getAllFollowers(String vonUser);
	public Set<String> getPeopleIFollow(String vonUser);
	public boolean isFollowingOtherUser(String loggedInUser, String otherUser);
	public Set<String> displayUserZSET();	
	
}
