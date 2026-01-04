package com.anubhav.techblog.Techblogging.service;

import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.anubhav.techblog.Techblogging.entity.User;

public interface UserService {

	ResponseEntity<String> saveUser(User theUser, MultipartFile file);
	User getUserByEmailAndPassword(String email, String password);
	User getUserByEmail(String Email);
	boolean updateUser(User theUser, MultipartFile file, User currentUser);
	User getUserByUserId(int id);
	User findOrCreateOAuthUser(String email, String name, String providerId, String provider);
	void syncUserRoles(User user, Set<String> roleNames);

}
