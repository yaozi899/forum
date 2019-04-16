package com.cyh.forum.persistence.model;


import com.cyh.forum.util.TimeUtil;

import java.io.Serializable;
import java.sql.Timestamp;

public class Post implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String title;
	private String body;
	private Timestamp dateCreated;
	private Category category;
	private User user;
	private Long commentCount;
	private Long hitCount;
	private String fileName;

	public Post() {

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Timestamp getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Timestamp dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Long getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(Long commentCount) {
		this.commentCount = commentCount;
	}

	public Long getHitCount() {
		return hitCount;
	}

	public void setHitCount(Long hitCount) {
		this.hitCount = hitCount;
	}

	@Override
	public String toString() {
		return "Post [id=" + id + ", title=" + title + ", body=" + body + ", dateCreated=" + dateCreated + ", category="
				+ category + ", user=" + user + ", commentCount=" + commentCount + ", hitCount=" + hitCount + "]";
	}

	public String numDaysAgo() {
		return TimeUtil.numDaysAgo(this.getDateCreated());
	}

	public String dateFormat() {
		return TimeUtil.dateFormat(this.getDateCreated());
	}

}
