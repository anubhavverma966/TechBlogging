package com.anubhav.techblog.Techblogging.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.anubhav.techblog.Techblogging.entity.User;
import com.anubhav.techblog.Techblogging.service.UserService;

import jakarta.validation.Valid;

@Controller
public class AuthenticatonController {
	
	private UserService userService;
	
	@Autowired
	public AuthenticatonController(UserService userService) {
		super();
		this.userService = userService;
	}

	@GetMapping("/loginPage")
	public String loginPage() {
				
		return "loginpage";
	}
	
	
	@GetMapping("/registerPage")
	public String registerPage(Model theModel) {
		User user = new User();
		theModel.addAttribute("user", user);
		return "registerpage";
	}
	
	@PostMapping("/register")
	public ResponseEntity<String> userRegistration(@ModelAttribute("user") @Valid User theUser,
								   BindingResult bindingResult,
								   @RequestParam("profile_pic") MultipartFile file,
								   @RequestParam(value="check", required = false) String check
	) {
		if(check == null) {
			return ResponseEntity.ok("Please accept terms and conditions");
		}
		
		if (bindingResult.hasErrors()) {
            // Return first error message (or all if you prefer)
            String errorMsg = bindingResult.getFieldError().getDefaultMessage();
            return ResponseEntity.ok(errorMsg);
        }
		
		return userService.saveUser(theUser, file);
	}	
	
}
