package com.cyh.forum.web.controller;

import com.cyh.forum.exception.BadRequestException;
import com.cyh.forum.exception.ResourceNotFoundException;
import com.cyh.forum.persistence.model.User;
import com.cyh.forum.service.PostService;
import com.cyh.forum.service.UserService;
import com.cyh.forum.util.NewUserFormValidator;
import com.cyh.forum.web.dto.UserRegistrationDto;
import com.cyh.forum.web.dto.UserSettingsDto;
import com.cyh.forum.web.vo.HotPostVo;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Controller
public class UserController {

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserService userService;
	@Autowired
	private PostService postService;

	@Autowired
	private NewUserFormValidator userValidator;

	@RequestMapping(value = "/user/{userId}", method = RequestMethod.GET)
	public String showUserProfilePage(@RequestParam(value = "tab", required = false) String tabType,
			@PathVariable Long userId, Model model) {
		if (null == userId) {
			throw new BadRequestException("Path variable userId cound not be null.");
		}
		Map<String, Object> attributes = this.userService.getUserProfileAndPostsByUserIdByTabType(userId, tabType);
		if (null == attributes) {
			throw new ResourceNotFoundException("attributes not found.");
		}
		model.addAllAttributes(attributes);
		return "forum/user-profile";
	}

	@RequestMapping(value = "/user/registration", method = RequestMethod.GET)
	public String showRegistrationPage(Model model) {
		model.addAttribute("userDto", new UserRegistrationDto());
		List<HotPostVo> hotPostVos = postService.hotPostVos();
		model.addAttribute("hotPostVos", hotPostVos);
		return "forum/user-registration";
	}

	@RequestMapping(value = "/user/registration", method = RequestMethod.POST)
	public String registerNewUser(@Valid @ModelAttribute("userDto") UserRegistrationDto userDto,
			BindingResult bindingResult, Model model, HttpServletRequest request) {
		/*
		 * form validation, check username and email uniqueness
		 */
		this.userValidator.validate(userDto, bindingResult);
		List<HotPostVo> hotPostVos = postService.hotPostVos();
		model.addAttribute("hotPostVos", hotPostVos);
		if (bindingResult.hasErrors()) {
			logger.info("BindingResult has errors >> " + bindingResult.getFieldError());
			return "forum/user-registration";
		}
		Map<String, Object> attributes = this.userService.registerUserAccount(userDto, request);
		if (null == attributes) {
			throw new ResourceNotFoundException("attributes not found.");
		}
		model.addAllAttributes(attributes);
		return "forum/user-registration-result";
	}

	@RequestMapping(value = "/user/login", method = RequestMethod.GET)
	public String displayLoginPage(Model model) {
		model.addAttribute("title", "用户登录");
		return "forum/user-login";
	}

	@RequestMapping(value = "/user/login-success", method = RequestMethod.GET)
	public String showAdminPage() {
		return "forum/user-login";
	}

	@RequestMapping(value = "/user/registration-confirm", method = RequestMethod.GET)
	public String confirmRegistration(@RequestParam("token") String token, Model model) {
		if (null == token || token.equals("")) {
			throw new BadRequestException("Invalid user registration confirmation token.");
		}
		Map<String, Object> attributes = this.userService.confirmUserRegistrationWithToken(token);
		if (null == attributes) {
			throw new ResourceNotFoundException("attributes not found.");
		}
		model.addAllAttributes(attributes);
		return "forum/user-registration-confirm";
	}

	@RequestMapping(value = "/user/settings", method = RequestMethod.GET)
	public String showUserSettingsPage(Model model) {
		Map<String, Object> attributes = this.userService.getUserSettingPage();
		if (null == attributes) {
			throw new ResourceNotFoundException("attributes not found.");
		}
		User user = userService.findAuthenticatedUser();
		model.addAllAttributes(attributes);
		model.addAttribute("user", user);
		return "forum/user-settings";
	}

	@RequestMapping(value = "/user/settings", method = RequestMethod.POST)
	public String handleFileUpload(@ModelAttribute("userSettingsDto") UserSettingsDto userSettingsDto, Model model) {
		if (null == userSettingsDto) {
			throw new BadRequestException("UserSettingsDto cound not be null.");
		}
		Map<String, Object> attributes = this.userService.updateUserProfile(userSettingsDto);
		if (null == attributes) {
			throw new ResourceNotFoundException("attributes not found.");
		}
		User user = userService.findAuthenticatedUser();
		model.addAttribute("user", user);
		model.addAllAttributes(attributes);
		return "forum/user-settings";
	}

}
