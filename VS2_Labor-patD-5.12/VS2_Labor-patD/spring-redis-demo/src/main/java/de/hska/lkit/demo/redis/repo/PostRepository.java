package de.hska.lkit.demo.redis.repo;

import java.util.List;
import java.util.Map;

import de.hska.lkit.demo.redis.model.Post;
import de.hska.lkit.demo.redis.model.User;


public interface PostRepository {
	
	public void savePost (Post post);
	public Map<String, Post> getAllPost();
	public List<String> getAllPostList();
	public List<Post> getOneUsersPost(String authorname);
	//public List<String> getOneUsersPost22(String authorname);
	//public Post getPost(String username);
	List<Post> getAllUsersPost();
	List<Post> getOnlyOneUsersPost(String authorname);
}
