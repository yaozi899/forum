package com.cyh.forum.service;


import com.cyh.forum.persistence.model.Category;
import com.cyh.forum.web.dto.PostDto;

import java.util.Map;

public interface DashboardService {

	Map<String, Object> getDashboard(String tab, String start, String end);

	Map<String, Object> getPostEditJson(Long postId);

	Map<String, Object> editPost(PostDto newPostForm);

	Map<String, Object> getNumOfPostsByCategoriesForPieChart();

	Map<String, Object> getNumOfPostsByMonthForBarChart();

	void delete(Integer userId);

	void deleteCategorie(Integer categorieId);

	Long maxCategorieId();

	void saveCategorie(Category category);

	Category queryCategorieInfo(String categorieId);

	void updateCategorie(Category category);

}
