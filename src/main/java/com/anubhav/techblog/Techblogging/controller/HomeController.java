package com.anubhav.techblog.Techblogging.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.anubhav.techblog.Techblogging.entity.Post;
import com.anubhav.techblog.Techblogging.service.PostService;

@Controller
public class HomeController {

	private PostService postService;	
	@Autowired
	public HomeController(PostService postService) {
		super();
		this.postService = postService;
	}

	@GetMapping("/")
	public String startIndexPage(Model theModel) {
		
		List<Post> posts = postService.getAllPost();
		
		theModel.addAttribute("posts", posts);
				
		return "index";
	}
	
}
