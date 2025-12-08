package com.anubhav.techblog.Techblogging.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.anubhav.techblog.Techblogging.entity.Category;
import com.anubhav.techblog.Techblogging.entity.Post;
import com.anubhav.techblog.Techblogging.entity.User;
import com.anubhav.techblog.Techblogging.service.CategoryService;
import com.anubhav.techblog.Techblogging.service.PostService;
import com.anubhav.techblog.Techblogging.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ProfileController {

	private UserService userService;
	private PostService postService;
	private CategoryService categoryService;
	
	@Autowired
	public ProfileController(UserService userService, PostService postService, CategoryService categoryService) {
		super();
		this.userService = userService;
		this.postService = postService;
		this.categoryService = categoryService;
	}
	
	@GetMapping("/profilePage")
	public String profilePage(Model theModel, HttpSession session) {
		User user =(User) session.getAttribute("currentUser");
		if(user == null)
			return "redirect:/loginPage";
		theModel.addAttribute("user", user);
		
		List<Category> allCategories = categoryService.getAllCategories();
		theModel.addAttribute("categories", allCategories);
		
		List<Post> allPosts = postService.getAllPost();
		theModel.addAttribute("posts", allPosts);
		
		Post post = new Post();
		theModel.addAttribute("post", post);
		
		Category category = new Category();
		theModel.addAttribute("category", category);
		
		return "profilepage";
	}
	
	@PostMapping("/editProfile")
	public String editUserProfile(@ModelAttribute("user") User user,
						  @RequestParam("profile_pic") MultipartFile file, 
						  HttpSession session,
						  RedirectAttributes redirectAttributes) {
		User currentUser = (User) session.getAttribute("currentUser");
		
		boolean update =  userService.updateUser(user, file, currentUser);
		
		if (update) {
	        session.setAttribute("currentUser", currentUser);
	        redirectAttributes.addFlashAttribute("successMsg", "Profile updated successfully!");
	    } else {
	        redirectAttributes.addFlashAttribute("errorMsg", "Error updating profile. Please try again.");
	    }

	    return "redirect:/profilePage";
	}
}
