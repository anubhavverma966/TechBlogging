package com.anubhav.techblog.Techblogging.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.anubhav.techblog.Techblogging.dto.PostDto;
import com.anubhav.techblog.Techblogging.entity.Category;
import com.anubhav.techblog.Techblogging.entity.Post;
import com.anubhav.techblog.Techblogging.entity.User;
import com.anubhav.techblog.Techblogging.service.PostService;

import jakarta.servlet.http.HttpSession;

@Controller
public class PostsController {

	private PostService postService;
	
	@Autowired
	public PostsController(PostService postService) {
		super();
		this.postService = postService;
	}
	
	@GetMapping("/loadPosts")
	public String loadPosts(@RequestParam("id") int catId, Model model) {
		List<Post> posts = null;
		
		if(catId == 0 ) {
			posts = postService.getAllPost();
			model.addAttribute("posts", posts);
		}
		else {
			posts = postService.getPostByCategoryId(catId);
			model.addAttribute("posts", posts);
		}
		
		return "fragments/loadposts :: postList";
	}

	@GetMapping("/showBlogPost/{postId}")
	public String showBlogPost(@PathVariable("postId") int postId, Model theModel, HttpSession session) {
		
		User user =(User) session.getAttribute("currentUser");
		if(user == null)
			return "redirect:/loginPage";
		theModel.addAttribute("user", user);
		
		Post showpost = postService.getPostByPostId(postId);
		theModel.addAttribute("showpost", showpost);
		
		Post post = new Post();
		theModel.addAttribute("post", post);
		
		Category category = new Category();
		theModel.addAttribute("category", category);
		
		return "showpost";
	}
	
	@PostMapping("/addPost")
	public ResponseEntity<String> addPost(@ModelAttribute("post") Post post, 
						  @RequestParam("cid") int cid,
						  @RequestParam("pic") MultipartFile file, 
						  HttpSession session) {
		User currentUser = (User) session.getAttribute("currentUser");
        return postService.savePost(post, cid, file, currentUser);
	}
	
	@GetMapping("/post/{id}")
	@ResponseBody
	public ResponseEntity<PostDto> getPost(@PathVariable int id) {
		Post post = postService.getPostByPostId(id);
		if (post == null) return ResponseEntity.notFound().build();
		// convert to DTO that doesn't contain lazy fields or sensitive info
		PostDto dto = PostDto.from(post);
		return ResponseEntity.ok(dto);
	}

	@PostMapping("/editPost")
	public String editPost(@ModelAttribute Post post,
                         @RequestParam(value = "file", required = false) MultipartFile file,
                     HttpSession session,
                     RedirectAttributes redirectAttrs) {

		User currentUser = (User) session.getAttribute("currentUser");
		if (currentUser == null) {
			redirectAttrs.addFlashAttribute("errorMsg", "Please login.");
			return "redirect:/loginPage";
		}
		
		String res = postService.updatePost(post, file, currentUser);
		if (res.equals("Success")) {
			redirectAttrs.addFlashAttribute("successMsg", "Post updated Successfully...");
		} else if(res.equals("Failed")) {
			redirectAttrs.addFlashAttribute("errorMsg", "Failed to update post. Not an Owner!!");
		}
		else {
			redirectAttrs.addFlashAttribute("errorMsg", "No Post Found");			
		}
		return "redirect:/profilePage";
	}
	
	@DeleteMapping("/deletePost/{id}")
	@ResponseBody
	public String deletePost(@PathVariable int id, HttpSession session) {
		
		User currentUser = (User)session.getAttribute("currentUser");
		
		if(currentUser == null)
			return "Unauthorised";
		
		boolean deleted = postService.deletePostById(id, currentUser);
		
		if(deleted)
			return "Deleted Successfully...";
		else
			return "You are not allowed to delete this post";
	}
	
}
