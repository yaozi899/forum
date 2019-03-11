package com.cyh.forum.service;


import com.cyh.forum.persistence.model.Category;

import java.util.List;
import java.util.Map;

public interface CategoryService {

	Map<String, Object> getNewPostPageWithCategoryName(String categoryName);

	Map<String, Object> getNewPostPageWithCategorySelect();

	int save(Category category);

	List<Category> findAll();

}
