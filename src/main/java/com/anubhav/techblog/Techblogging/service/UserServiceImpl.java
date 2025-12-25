package com.anubhav.techblog.Techblogging.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.anubhav.techblog.Techblogging.dao.RoleDao;
import com.anubhav.techblog.Techblogging.dao.UserDao;
import com.anubhav.techblog.Techblogging.entity.Role;
import com.anubhav.techblog.Techblogging.entity.User;

@Service
public class UserServiceImpl implements UserService {
	
	private UserDao userDao;
	private RoleDao roleDao;
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	public UserServiceImpl(UserDao theUserDao, RoleDao theRoleDao, PasswordEncoder thePasswordEncoder) {
		super();
		this.userDao = theUserDao;
		this.roleDao = theRoleDao;
		this.passwordEncoder = thePasswordEncoder;
	}
	

	@Override
	public ResponseEntity<String> saveUser(User theUser, MultipartFile file) {
		// TODO Auto-generated method stub
		final String UPLOAD_DIR = "src/main/resources/static/pics/";
		
		try {
			if (file != null && !file.isEmpty()) {
	            String fileName = file.getOriginalFilename();
	            Path uploadPath = Paths.get(UPLOAD_DIR);
	            Files.createDirectories(uploadPath);

	            // Save new pic
	            Files.copy(file.getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
	            theUser.setProfile(fileName);
	        }
			
			Role role = roleDao.findByName("ROLE_USER")
						.orElseThrow(() -> new IllegalStateException("ROLE_USER missing"));
			
			theUser.addRole(role);
			
			theUser.setPassword(passwordEncoder.encode(theUser.getPassword()));
			
			userDao.save(theUser);
			return ResponseEntity.ok("done");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			if(e.getMessage().contains("email"))
				return ResponseEntity.ok("Email address already in use!");
			else
				return ResponseEntity.ok("Something Went Wrong!");
		}
	}


	@Override
	public User getUserByEmailAndPassword(String email, String password) {
		// TODO Auto-generated method stub
		return userDao.findByEmailAndPassword(email, password).orElse(null);
	}

	@Override
	public User getUserByEmail(String Email) {
		// TODO Auto-generated method stub
		return userDao.findByEmail(Email).orElse(null);
	}
	
	@Override
	public User getUserByUserId(int id) {
		// TODO Auto-generated method stub
		Optional<User> result = userDao.findById(id);
		
		User theUser = null;
		
		if (result.isPresent()) {
			theUser = result.get();
		}
		else {
			// we didn't find the user
			throw new RuntimeException("Did not find user with id - " + id);
		}
		
		return theUser;
	}


	@Override
	@Transactional
	public boolean updateUser(User theUser, MultipartFile file, User currentUser) {
		// TODO Auto-generated method stub
		final String UPLOAD_DIR = "src/main/resources/static/pics/";
		
		try {
	        // Fetch fresh DB user (important for persistence)
	        User dbUser = userDao.findById(currentUser.getId()).orElseThrow();

	        dbUser.setEmail(theUser.getEmail());
	        dbUser.setName(theUser.getName());
	        dbUser.setAbout(theUser.getAbout());

	        // Update password only if not blank
	        if (theUser.getPassword() != null && !theUser.getPassword().trim().isEmpty()) {
	            dbUser.setPassword(theUser.getPassword());
	        }

	        // Handle profile picture
	        if (file != null && !file.isEmpty()) {
	            String fileName = file.getOriginalFilename();
	            Path uploadPath = Paths.get(UPLOAD_DIR);
	            Files.createDirectories(uploadPath);

	            // Delete old pic only if not default
	            if (dbUser.getProfile() != null && !dbUser.getProfile().equals("default.png") && !dbUser.getProfile().equals("default3.png")) {
	                Files.deleteIfExists(uploadPath.resolve(dbUser.getProfile()));
	            }

	            // Save new pic
	            Files.copy(file.getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
	            dbUser.setProfile(fileName);
	        }

	        // Save updated user
	        userDao.save(dbUser);

	        // Refresh session
	        currentUser.setProfile(dbUser.getProfile());
	        currentUser.setEmail(dbUser.getEmail());
	        currentUser.setName(dbUser.getName());
	        currentUser.setAbout(dbUser.getAbout());

	        return true;

	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
    }
}
