package com.anubhav.techblog.Techblogging.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.anubhav.techblog.Techblogging.entity.User;

public interface UserService {

	ResponseEntity<String> saveUser(User theUser, MultipartFile file);
	User getUserByEmailAndPassword(String email, String password);
	boolean updateUser(User theUser, MultipartFile file, User currentUser);
	User getUserByUserId(int id);
}
