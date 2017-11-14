package de.hska.lkit.demo.redis.model;

import java.io.Serializable;

public class Post implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String postId;
	String content;
	public Post() {
		
	}
	public String getPostId() {
		return postId;
	}
	public void setPostId(String postId) {
		this.postId = postId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	

	
}
