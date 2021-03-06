package com.cyh.forum.service.impl;

import com.cyh.forum.constant.PageMessage;
import com.cyh.forum.persistence.dao.CategoryMapper;
import com.cyh.forum.persistence.dao.PostMapper;
import com.cyh.forum.persistence.model.Category;
import com.cyh.forum.service.CategoryService;
import com.cyh.forum.web.dto.PostDto;
import com.cyh.forum.web.vo.HotPostVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("categoryService")
public class CategoryServiceImpl implements CategoryService {

	@Autowired
	private CategoryMapper categoryMapper;
	@Autowired
	private PostMapper postMapper;

	@Override
	public int save(Category category) {
		return this.categoryMapper.save(category);
	}

	@Override
	public Map<String, Object> getNewPostPageWithCategoryName(String categoryName) {
		Map<String, Object> attributes = new HashMap<>();
		Category category = this.categoryMapper.findByName(categoryName);
		List<HotPostVo> hotPostVos = postMapper.hotPost();
		attributes.put("title", PageMessage.MESSAGE_NEW_POST_CN);
		PostDto newPostForm = new PostDto();
		newPostForm.setCategory(category.getName());
		attributes.put("postDto", newPostForm);
		attributes.put("isQuickNewPost", false);
		attributes.put("hotPostVos", hotPostVos);
		return attributes;
	}

	@Override
	public Map<String, Object> getNewPostPageWithCategorySelect() {
		List<Category> categories = this.categoryMapper.findAll();
		List<HotPostVo> hotPostVos = postMapper.hotPost();
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("title", PageMessage.MESSAGE_NEW_POST_CN);
		attributes.put("categories", categories);
		attributes.put("postDto", new PostDto());
		attributes.put("isQuickNewPost", true);
		attributes.put("hotPostVos", hotPostVos);
		return attributes;
	}

	@Override
	public List<Category> findAll() {
		return this.categoryMapper.findAll();
	}

}
