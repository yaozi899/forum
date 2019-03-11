package com.cyh.forum.persistence.dao;


import com.cyh.forum.persistence.model.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {

	int save(Comment comment);

	int deleteCommentsByPostId(Long postId);

	int countNumCommentsByPostId(Long postId);

	List<Comment> findCommentsByPostId(Long postId);

	List<Comment> findCommentsByUserId(Long userId);

}
