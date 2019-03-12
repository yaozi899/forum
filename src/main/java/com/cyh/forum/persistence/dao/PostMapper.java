package com.cyh.forum.persistence.dao;

import com.cyh.forum.persistence.model.Post;
import com.cyh.forum.web.vo.HotPostVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
@Mapper
public interface PostMapper {

	int save(Post post);

	int delete(Long postId);

	int update(Post post);

	Post findById(Long postId);

	List<Post> findAll();

	List<Post> findPostsByCategory(String categoryName);

	List<Post> findPostsByUserId(Long userId);

	List<Post> findPostsBetweenRange(@Param("startDateStr") String startDate, @Param("endDateStr") String endDate);

	Long countNumOfPostsByCategoryId(Long categoryId);

	Long countNumOfPostsByMonth(Integer month);

	List<HotPostVo> hotPost();

	List<HotPostVo> newPost();
	
}