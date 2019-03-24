package com.cyh.forum.web.controller;

import com.cyh.forum.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class CategoryController {

	private static Integer pageSize = 10;

	@Autowired
	private PostService postService;

	@RequestMapping(value = "/category/{categoryName}", method = RequestMethod.GET)
	public String getCategoryPostsByPage(@PathVariable String categoryName, Model model,
			@RequestParam(value = "p", required = false) Integer pageNum) {
		if (null == categoryName) {
			return "error/404";
		}
		int currPage = pageNum == null ? 1 : pageNum;
		Map<String, Object> attributes = this.postService.findPostsListByCategoryByPage(categoryName, currPage,
				pageSize);
		if (null == attributes) {
			return "error/404";
		}
		model.addAllAttributes(attributes);
		return "forum/home";
	}
	@RequestMapping(value = "/category/search/", method = RequestMethod.GET)
	public String search(@RequestParam(value = "search", required = false) String search,
						 Model model,
						 @RequestParam(value = "p", required = false, defaultValue="1") Integer pageNum,
						 @RequestParam(value = "size", required = false, defaultValue="10") Integer pageSize) {
		Map<String, Object> attributes = this.postService.findPostsByPageSearch(pageNum, pageSize, search);
		if (null == attributes) {
			return "error/404";
		}
		model.addAllAttributes(attributes);
		return "forum/home";
	}

}
