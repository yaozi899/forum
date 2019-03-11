package com.cyh.forum.service.impl;

import com.cyh.forum.persistence.dao.CategoryMapper;
import com.cyh.forum.persistence.dao.PostMapper;
import com.cyh.forum.persistence.dao.UserMapper;
import com.cyh.forum.persistence.model.Category;
import com.cyh.forum.persistence.model.Post;
import com.cyh.forum.persistence.model.User;
import com.cyh.forum.service.DashboardService;
import com.cyh.forum.web.dto.PostDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;

@Service("dashboardService")
@Transactional
public class DashboardServiceImpl implements DashboardService {

	private static final Logger logger = LoggerFactory.getLogger(DashboardServiceImpl.class);

	@Autowired
	private PostMapper postMapper;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private CategoryMapper categoryMapper;

	@Override
	public Map<String, Object> getDashboard(String tab, String startDate, String endDate) {
		String activeTab = tab == null ? "stats" : tab; // default tab
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("activeTab", activeTab);
		attributes.put("newPostForm", new PostDto());
		switch (activeTab) {
		case "stats":
			attributes.put("stats", null);
			break;
		case "posts":
			List<Post> posts = this.postMapper.findPostsBetweenRange(startDate + " 00:00:00",
					endDate + " 23:59:59");
			attributes.put("posts", posts);
			break;
		case "users":
			List<User> users = this.userMapper.findAll();
			attributes.put("users", users);
			break;
		case "categories":
			List<Category> categories = this.categoryMapper.findAll();
			attributes.put("categories", categories);
			break;
		}
		return attributes;
	}

	@Override
	public Map<String, Object> getPostEditJson(Long postId) {
		Post post = this.postMapper.findById(postId);
		List<Category> categories = this.categoryMapper.findAll();
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("postId", post.getId());
		attributes.put("postTitle", post.getTitle());
		attributes.put("postUsername", post.getUser().getUsername());
		attributes.put("postBody", post.getBody());
		attributes.put("postCategoryName", post.getCategory().getName());
		attributes.put("postCategoryDisplayName", post.getCategory().getDisplayName());
		attributes.put("postDateCreated", post.getDateCreated());
		attributes.put("categories", categories);
		return attributes;
	}

	@Override
	public Map<String, Object> editPost(PostDto newPostForm) {
		Post post = this.postMapper.findById(newPostForm.getPostId());
		Category category = this.categoryMapper.findByName(newPostForm.getCategory());
		post.setTitle(newPostForm.getTitle());
		post.setBody(newPostForm.getBody());
		post.setCategory(category);
		int rowAffected = this.postMapper.update(post);
		Map<String, Object> attributes = new HashMap<>();
		if (rowAffected == 0) {
			attributes.put("editResult", "failure");
			logger.info("Post has failed to be edited >> Post Id: " + post.getId());
		} else {
			attributes.put("editResult", "success");
			logger.info("Post has been edited successfully >> Post Id: " + post.getId());
		}
		return attributes;
	}

	@Override
	public Map<String, Object> getNumOfPostsByCategoriesForPieChart() {
		Map<String, Object> attributes = new HashMap<>();
		List<Category> categories = this.categoryMapper.findAll();
		for (Category category : categories) {
			Long categoryId = category.getId();
			String categoryDisplayName = category.getDisplayName();
			Long numOfPosts = this.postMapper.countNumOfPostsByCategoryId(categoryId);
			attributes.put(categoryDisplayName, numOfPosts);
		}
		return attributes;
	}

	@Override
	public Map<String, Object> getNumOfPostsByMonthForBarChart() {
		Map<String, Object> attributes = new HashMap<>();
		List<Long> postsNumList = new ArrayList<>();
		List<String> monthsList = new ArrayList<>();
		int currMonth = Calendar.getInstance().get(Calendar.MONTH);
		for (int i = currMonth; i < currMonth + 12; i++) {
			Month month = Month.values()[i < 11 ? i + 1 : i - 11]; // January = 0
			Long numOfPosts = this.postMapper.countNumOfPostsByMonth(month.getValue());
			postsNumList.add(numOfPosts);
			monthsList.add(month.getDisplayName(TextStyle.FULL, Locale.CHINESE));
		}
		attributes.put("postsNumList", postsNumList);
		attributes.put("monthsList", monthsList);
		return attributes;
	}

	public void delete(Integer userId){
		userMapper.deleteToken(userId);
		userMapper.deleteUser(userId);
	}

    public void deleteCategorie(Integer categorieId){
        categoryMapper.deleteCategorie(categorieId);
    }

	public Long maxCategorieId(){
		return categoryMapper.maxCategorieId();
	}

	public void saveCategorie(Category category){
        Long longId = maxCategorieId();
        Integer id = longId.intValue() + 1;
        category.setId(id.longValue());
		category.setWeight(id);

        // find authenticated user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = this.userMapper.findByUsername(username);
		category.setUsername(user.getUsername());
		Date date = new Date();
		category.setDateCreated(new Timestamp(date.getTime()));

        categoryMapper.save(category);

	}

}
