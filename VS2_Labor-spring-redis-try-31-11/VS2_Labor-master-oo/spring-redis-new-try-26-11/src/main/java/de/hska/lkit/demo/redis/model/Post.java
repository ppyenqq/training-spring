package de.hska.lkit.demo.redis.model;

import java.io.Serializable;

public class Post implements Serializable {
	private static final long serialVersionUID = 1L;

	//private String Attribute;
	private String postId;
	private String content;
	private String authorId;
	//private String date;

	public Post() {

	}

	/*public String getAttribute() {
		return Attribute;
	}

	public void setAttribute(String attribute) {
		Attribute = attribute;
	}*/

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

	public String getAuthorId() {
		return authorId;
	}

	public void setAuthorId(String authorId) {
		this.authorId = authorId;
	}

	/*public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
*/
}