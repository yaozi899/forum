package com.cyh.forum.persistence.dao;

import com.cyh.forum.persistence.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
@Mapper
public interface UserMapper {

	int save(User user);

	int update(User user);

	List<User> findAll();

	User findById(Long id);

	User findByUsername(String username);

	User findByEmail(String email);

	int deleteUser(Integer userId);

	int deleteToken(Integer userId);

}
