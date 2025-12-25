package com.anubhav.techblog.Techblogging.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.anubhav.techblog.Techblogging.entity.Category;
import com.anubhav.techblog.Techblogging.entity.User;
import com.anubhav.techblog.Techblogging.security.CustomUserDetails;
import com.anubhav.techblog.Techblogging.service.CategoryService;

@Controller
public class CategoryController {

	private final CategoryService categoryService;

	@Autowired
	public CategoryController(CategoryService categoryService) {
		super();
		this.categoryService = categoryService;
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/addCategory")
	public ResponseEntity<String> addCategory(@ModelAttribute("category") Category category,
						  @RequestParam("picFile") MultipartFile file, 
						  Authentication authentication) {

		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		User currentUser = userDetails.getDomainUser();
		return categoryService.saveCategory(category, file, currentUser);
	}
	
}
