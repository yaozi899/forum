package com.cyh.forum.web.controller;

import com.cyh.forum.exception.BadRequestException;
import com.cyh.forum.exception.ResourceNotFoundException;
import com.cyh.forum.persistence.model.Category;
import com.cyh.forum.service.DashboardService;
import com.cyh.forum.service.PostService;
import com.cyh.forum.web.dto.PostDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

@Controller
public class DashboardController {

	private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

	@Autowired
	private PostService postService;

	@Autowired
	private DashboardService dashboardService;

	@PreAuthorize("hasAuthority('ADMIN')")
	@RequestMapping(value = "/dashboard", method = RequestMethod.GET)
	public String getDashboardPage(Model model, @RequestParam(value = "tab", required = false) String tab,
			@RequestParam(value = "start", required = false) String start,
			@RequestParam(value = "end", required = false) String end) {
		Map<String, Object> attributes = this.dashboardService.getDashboard(tab, start, end);
		if (null == attributes) {
			throw new ResourceNotFoundException("attributes not found.");
		}
		model.addAllAttributes(attributes);
		model.addAttribute("tab", tab);
		model.addAttribute("start", start);
		model.addAttribute("end", end);
		return "forum/dashboard";
	}
	@PreAuthorize("hasAuthority('ADMIN')")
	@RequestMapping(value = "/dashboard/addCategories", method = RequestMethod.GET)
	public String addCategories(Model model) {
		Map<String, Object> attributes = this.dashboardService.getDashboard("categories", null, null);
		if (null == attributes) {
			throw new ResourceNotFoundException("attributes not found.");
		}
		model.addAllAttributes(attributes);
		model.addAttribute("tab", "categories");
		model.addAttribute("start", null);
		model.addAttribute("end", null);
		model.addAttribute("flag", "add");
		model.addAttribute("category", new Category());
		model.addAttribute("action", "create");
		return "forum/dashboard";
	}
	@PreAuthorize("hasAuthority('ADMIN')")
	@RequestMapping(value = "/dashboard/addCategories/create", method = RequestMethod.POST)
	public String createCategories(Model model,@ModelAttribute Category category) {
        dashboardService.saveCategorie(category);

		Map<String, Object> attributes = this.dashboardService.getDashboard("categories", null, null);
		if (null == attributes) {
			throw new ResourceNotFoundException("attributes not found.");
		}
		model.addAllAttributes(attributes);
		model.addAttribute("tab", "categories");
		model.addAttribute("start", null);
		model.addAttribute("end", null);
		return "forum/dashboard";
	}
	@PreAuthorize("hasAuthority('ADMIN')")
	@RequestMapping(value = "/dashboard/delete/{bookId}", method = RequestMethod.GET)
	public String delete(Model model, @PathVariable String bookId) {
		dashboardService.delete(Integer.valueOf(bookId));
		Map<String, Object> attributes = this.dashboardService.getDashboard("users", null, null);
		if (null == attributes) {
			throw new ResourceNotFoundException("attributes not found.");
		}
		model.addAllAttributes(attributes);
		model.addAttribute("tab", "users");
		model.addAttribute("start", null);
		model.addAttribute("end", null);
		return "forum/dashboard";
	}
	@PreAuthorize("hasAuthority('ADMIN')")
	@RequestMapping(value = "/dashboard/deleteCategorie/{categorieId}", method = RequestMethod.GET)
	public String deleteCategorie(Model model, @PathVariable String categorieId) {
		dashboardService.deleteCategorie(Integer.valueOf(categorieId));
		Map<String, Object> attributes = this.dashboardService.getDashboard("categories", null, null);
		if (null == attributes) {
			throw new ResourceNotFoundException("attributes not found.");
		}
		model.addAllAttributes(attributes);
		model.addAttribute("tab", "categories");
		model.addAttribute("start", null);
		model.addAttribute("end", null);
		return "forum/dashboard";
	}

	/*
	 * return JSON object
	 */
	@PreAuthorize("hasAuthority('ADMIN')")
	@ResponseBody
	@RequestMapping(value = "/post/{postId}/json", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Map<String, Object>> getPostJson(@PathVariable Long postId) {
		if (null == postId) {
			return ResponseEntity.badRequest().build();
		}
		Map<String, Object> attributes = this.dashboardService.getPostEditJson(postId);
		if (null == attributes) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.accepted().body(attributes);
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@RequestMapping(value = "/post/edit", method = RequestMethod.POST)
	public String processPostEdit(@Valid @ModelAttribute("newPostForm") PostDto newPostForm,
			@RequestParam(value = "tab", required = false) String tab,
			@RequestParam(value = "start", required = false) String start,
			@RequestParam(value = "end", required = false) String end, BindingResult bindingResult, Model model,
			RedirectAttributes redirectAttributes) {
		if (null == newPostForm) {
			throw new BadRequestException("newPostForm cound not be null.");
		}
		Map<String, Object> attributes = this.dashboardService.editPost(newPostForm);
		if (null == attributes) {
			throw new ResourceNotFoundException("attributes not found.");
		}
		model.addAllAttributes(attributes);
		// directly add attribute to redirected URL
		redirectAttributes.addFlashAttribute("editResult", "success");
		return "redirect:/dashboard?tab=" + tab + "&start=" + start + "&end=" + end;
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@RequestMapping(value = "/post/{postId}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deletePost(@PathVariable Long postId) {
		if (null == postId) {
			throw new BadRequestException("Path variable postId cound not be null.");
		}
		int rowAffected = this.postService.deletePostAndComments(postId);
		logger.info("Deleted postId = " + postId + " ; rowAffected = " + rowAffected);
		if (0 == rowAffected) {
			return ResponseEntity.badRequest().build();
		}
		return ResponseEntity.ok().build();
	}

	@RequestMapping(value = "/dashboard/numOfPostsByCategories", method = RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> getNumsOfPostsByCategories() {
		Map<String, Object> attributes = this.dashboardService.getNumOfPostsByCategoriesForPieChart();
		if (null == attributes) {
			throw new ResourceNotFoundException("attributes not found.");
		}
		return ResponseEntity.ok().body(attributes);
	}

	@RequestMapping(value = "/dashboard/numOfPostsByMonths", method = RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> getNumsOfPostsBymonths() {
		Map<String, Object> attributes = this.dashboardService.getNumOfPostsByMonthForBarChart();
		if (null == attributes) {
			throw new ResourceNotFoundException("attributes not found.");
		}
		return ResponseEntity.ok().body(attributes);
	}
}
