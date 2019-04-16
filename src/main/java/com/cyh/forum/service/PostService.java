package com.cyh.forum.service;


import com.cyh.forum.persistence.model.Post;
import com.cyh.forum.web.dto.PostDto;
import com.cyh.forum.web.vo.HotPostVo;

import java.util.List;
import java.util.Map;

public interface PostService {

	void save(Post post);

	void delete(Long postId);

	void update(Post post);

	Post findById(Long id);

	Post createNewPost(PostDto newPostForm, String fileName);

	int deletePostAndComments(Long postId);

	Map<String, Object> findPosts();

	Map<String, Object> findPostsByPage(int currPage, int pageSize);

	Map<String, Object> findPostsByPageSearch(int currPage, int pageSize, String search);

	Map<String, Object> findPostsListByCategoryByPage(String categoryName, int currPage, int pageSize);

	Map<String, Object> findPostDetailsAndCommentsByPostId(Long postId);

	Map<String, Object> findPostsBetweenDateRange(String start, String end);

	List<HotPostVo> hotPostVos();

}
