package com.anubhav.techblog.Techblogging.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.anubhav.techblog.Techblogging.entity.Category;
import com.anubhav.techblog.Techblogging.entity.User;
import com.anubhav.techblog.Techblogging.service.CategoryService;

import jakarta.servlet.http.HttpSession;

@Controller
public class CategoryController {

	private CategoryService categoryService;

	@Autowired
	public CategoryController(CategoryService categoryService) {
		super();
		this.categoryService = categoryService;
	}
	
	@PostMapping("/addCategory")
	public ResponseEntity<String> addCategory(@ModelAttribute("category") Category category,
						  @RequestParam("picFile") MultipartFile file, 
						  HttpSession session) {
		User currentUser = (User) session.getAttribute("currentUser");
		return categoryService.saveCategory(category, file, currentUser);
	}
	
}
