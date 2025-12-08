package com.anubhav.techblog.Techblogging.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.anubhav.techblog.Techblogging.entity.Post;
import com.anubhav.techblog.Techblogging.entity.User;

public interface PostService {

	List<Post> getAllPost();
	List<Post> getPostByCategoryId(int id);
	ResponseEntity<String> savePost(Post post, int categoryId, MultipartFile file, User currentUser);
	Post getPostByPostId(int id);
	String updatePost(Post formPost, MultipartFile file, User currentUser);
	boolean deletePostById(int id, User currentUser);
}
