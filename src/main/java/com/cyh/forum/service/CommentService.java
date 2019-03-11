package com.cyh.forum.service;


import com.cyh.forum.persistence.model.Comment;
import com.cyh.forum.web.dto.CommentDto;

public interface CommentService {

	void save(Comment comment);

	int countNumCommentsByPostId(Long postId);

	Comment createNewCommentOnPost(Long postId, CommentDto newCommentForm);

}
