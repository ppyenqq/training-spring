
package com.donald.duck;

import java.util.Date;

public class MessageOutput {
	private String content;
	private String author;
	private String date;
	public MessageOutput() {
		
	}
	public MessageOutput(String author2, String content2) {
		// TODO Auto-generated constructor stub
		this.content = content2;
		this.author=author2;
	}
	public MessageOutput(String author2, String content2, String time) {
		// TODO Auto-generated constructor stub
		this.content = content2;
		this.author=author2;
		this.date=time;
	}
	public MessageOutput(String aanyContent) {
		// TODO Auto-generated constructor stub
		this.content = aanyContent;
		
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}

}
