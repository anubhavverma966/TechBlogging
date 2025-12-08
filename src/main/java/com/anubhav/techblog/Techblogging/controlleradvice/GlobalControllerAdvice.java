package com.anubhav.techblog.Techblogging.controlleradvice;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.anubhav.techblog.Techblogging.entity.User;
import com.anubhav.techblog.Techblogging.security.CustomUserDetails;

@ControllerAdvice
public class GlobalControllerAdvice {

	@ModelAttribute("currentUser")
	public User addCurrentUser(Authentication auth) {

	    if (auth == null || auth.getPrincipal() == null)
	        return null;

	    Object principal = auth.getPrincipal();

	    if (principal instanceof CustomUserDetails cud) {
	        return cud.getDomainUser();
	    }

	    // If principal is a String (anonymous user)
	    return null;
	}

}