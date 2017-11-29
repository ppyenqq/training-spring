package de.hska.lkit.demo.redis.repo;

import java.util.List;
import java.util.Map;

import de.hska.lkit.demo.redis.model.Post;
import de.hska.lkit.demo.redis.model.User;


public interface PostRepository {
	
	public void addPost (Post post);
	
	public List<String> getAllPosts();
	
	public Post getPost(String username);
}
