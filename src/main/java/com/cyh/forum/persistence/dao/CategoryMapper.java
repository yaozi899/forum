package com.cyh.forum.persistence.dao;

import com.cyh.forum.persistence.model.Category;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
@Mapper
public interface CategoryMapper {

	Category findByName(String categoryName);

	int save(Category category);

	List<Category> findAll();

	Long maxCategorieId();

	void deleteCategorie(Integer categorieId);

}
