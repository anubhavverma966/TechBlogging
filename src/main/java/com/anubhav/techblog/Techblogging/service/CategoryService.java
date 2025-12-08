package com.anubhav.techblog.Techblogging.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.anubhav.techblog.Techblogging.entity.Category;
import com.anubhav.techblog.Techblogging.entity.User;

public interface CategoryService {

	List<Category> getAllCategories();
	ResponseEntity<String> saveCategory(Category category, MultipartFile file, User currentUser);	
}
