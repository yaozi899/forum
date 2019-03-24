package com.cyh.forum.web.controller;

import com.cyh.forum.exception.BadRequestException;
import com.cyh.forum.exception.ResourceNotFoundException;
import com.cyh.forum.persistence.model.Comment;
import com.cyh.forum.persistence.model.Post;
import com.cyh.forum.service.CategoryService;
import com.cyh.forum.service.CommentService;
import com.cyh.forum.service.PostService;
import com.cyh.forum.util.NewPostFormValidator;
import com.cyh.forum.web.dto.CommentDto;
import com.cyh.forum.web.dto.PostDto;
import com.cyh.forum.web.vo.HotPostVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Controller
public class PostController {

	private static final Logger logger = LoggerFactory.getLogger(PostController.class);

	@Autowired
	private PostService postService;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private CommentService commentService;

	@Autowired
	private NewPostFormValidator newPostValidator;

	@Autowired
	ApplicationEventPublisher eventPublisher;

	@RequestMapping(value = "/post/list", method = RequestMethod.GET)
	public String index(Model model) {
		Map<String, Object> attributes = postService.findPosts();
		if (null == attributes) {
			throw new ResourceNotFoundException("attributes not found.");
		}
		model.addAttribute(attributes);
		return "fragments/posts-list";
	}

	@RequestMapping(value = "/post/{postId}", method = RequestMethod.GET)
	public String getPost(Model model, @PathVariable Long postId) {
		if (null == postId) {
			throw new BadRequestException("Path variable postId cound not be null.");
		}
		Map<String, Object> attributes = this.postService.findPostDetailsAndCommentsByPostId(postId);
		if (null == attributes) {
			throw new ResourceNotFoundException("attributes not found.");
		}
		List<HotPostVo> hotPostVos = postService.hotPostVos();
		model.addAttribute("hotPostVos", hotPostVos);
		model.addAllAttributes(attributes);
		return "forum/post";
	}

	@RequestMapping(value = "/new/{categoryName}", method = RequestMethod.GET)
	public String displayNewPostPageWithCategory(Model model, @PathVariable String categoryName) {
		if (null == categoryName) {
			throw new BadRequestException("Path variable postId cound not be null.");
		}
		Map<String, Object> attributes = this.categoryService.getNewPostPageWithCategoryName(categoryName);
		if (null == attributes) {
			throw new ResourceNotFoundException("attributes not found.");
		}
		model.addAllAttributes(attributes);
		return "forum/new-post";
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String displayNewPostPage(Model model) {
		Map<String, Object> attributes = this.categoryService.getNewPostPageWithCategorySelect();
		if (null == attributes) {
			throw new ResourceNotFoundException("attributes not found.");
		}
		model.addAllAttributes(attributes);
		return "forum/new-post";
	}

	@RequestMapping(value = "/new", method = RequestMethod.POST)
	public String processNewPost(@Valid @ModelAttribute("postDto") PostDto postDto, BindingResult bindingResult,
								 Model model) {
		if (null == postDto) {
			throw new BadRequestException("NewPostForm cound not be null.");
		}
		Post post = this.postService.createNewPost(postDto);
		if (null == post) {
			throw new ResourceNotFoundException("New post object can't be created.");
		}
		// post form validation
		this.newPostValidator.validate(post, bindingResult);
		if (bindingResult.hasErrors()) {
			logger.info("BindingResult has errors >> " + bindingResult.getFieldError());
			return "forum/new-post";
		} else {
			this.postService.save(post);
		}
		return "redirect:/";
	}

	@RequestMapping(value = "/post/{postId}", method = RequestMethod.POST)
	public String processNewComment(@PathVariable Long postId,
			@Valid @ModelAttribute("commentDto") CommentDto commentDto) {
		if (null == postId && null == commentDto) {
			throw new BadRequestException("Path variable postId and newCommentForm cound not be null.");
		}
		Comment comment = this.commentService.createNewCommentOnPost(postId, commentDto);
		if (null == comment) {
			throw new ResourceNotFoundException("New comment object can't be created.");
		}
		// comment form validation here ...
		this.commentService.save(comment);
		return "redirect:/post/{postId}";
	}

}