package com.cyh.forum.service;

import com.cyh.forum.persistence.model.User;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

	void init();

	User store(MultipartFile file, String path);

	void deleteAll();

}