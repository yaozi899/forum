package com.cyh.forum.service;


import com.cyh.forum.persistence.model.User;
import com.cyh.forum.web.dto.UserRegistrationDto;
import com.cyh.forum.web.dto.UserSettingsDto;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface UserService {

	int save(User user);

	User findById(Long id);

	User findByUsername(String username);

	User findByEmail(String email);

	User findAuthenticatedUser();

	Map<String, Object> getUserProfileAndPostsByUserIdByTabType(Long userId, String tabType);

	Map<String, Object> updateUserProfile(UserSettingsDto newUserSettingsForm);

	Map<String, Object> getUserSettingPage();

	Map<String, Object> registerUserAccount(UserRegistrationDto userDto, HttpServletRequest request);

	Map<String, Object> confirmUserRegistrationWithToken(String token);


}
