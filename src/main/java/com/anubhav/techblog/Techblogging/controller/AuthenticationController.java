package com.anubhav.techblog.Techblogging.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.anubhav.techblog.Techblogging.entity.RefreshToken;
import com.anubhav.techblog.Techblogging.entity.User;
import com.anubhav.techblog.Techblogging.security.JwtService;
import com.anubhav.techblog.Techblogging.service.EmailService;
import com.anubhav.techblog.Techblogging.service.PasswordResetTokenService;
import com.anubhav.techblog.Techblogging.service.RefreshTokenService;
import com.anubhav.techblog.Techblogging.service.UserService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Controller
public class AuthenticationController {
	
	private final UserService userService;
	private final RefreshTokenService refreshTokenService;
	private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordResetTokenService passwordResetTokenService;
    private final EmailService emailService;
    

    public AuthenticationController(UserService userService, AuthenticationManager authenticationManager, 
    								JwtService jwtService, RefreshTokenService refreshTokenService, 
    								PasswordResetTokenService passwordResetTokenService, EmailService emailService) {
        this.userService = userService;
    	this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.passwordResetTokenService = passwordResetTokenService;
        this.emailService = emailService;
    }

    @GetMapping("/loginPage")
    public String loginPage() {
        return "loginpage";
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestParam String email,
                                   @RequestParam String password,
                                   HttpServletResponse response) {
        try {
            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(email, password));

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            
            String accessToken = jwtService.generateToken(userDetails);

            ResponseCookie accessCookie = ResponseCookie
                    .from(jwtService.getCookieName(), accessToken)
                    .httpOnly(true)
                    .secure(false)
                    .sameSite("Lax")
                    .path("/")
                    .maxAge(3600)
                    .build();

            String username = userDetails.getUsername();

            RefreshToken refreshToken =
                    refreshTokenService.createRefreshToken(username);

            ResponseCookie refreshCookie = ResponseCookie
                    .from("REFRESH_TOKEN", refreshToken.getToken())
                    .httpOnly(true)
                    .secure(false)
                    .sameSite("Lax")
                    .path("/")
                    .maxAge(7 * 24 * 60 * 60)
                    .build();
            
            response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
            response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

            return ResponseEntity.ok().build();

        } catch (AuthenticationException ex) {
        	return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
        }
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
	
	@PostMapping("/logout")
	public String logout(
			@CookieValue(name = "REFRESH_TOKEN", required = false) String refreshTokenValue,
	        HttpServletResponse response
	) {
	    
	    if (refreshTokenValue != null) {
	        try {
	            RefreshToken refreshToken =
	                    refreshTokenService.verifyRefreshToken(refreshTokenValue);
	            refreshTokenService.revokeToken(refreshToken);
	        } catch (Exception ignored) {
	            
	        }
	    }

	    ResponseCookie accessTokenCookie = ResponseCookie
	            .from("AUTH_TOKEN", "")
	            .path("/")
	            .httpOnly(true)
	            .maxAge(0)
	            .build();

	    ResponseCookie refreshTokenCookie = ResponseCookie
	            .from("REFRESH_TOKEN", "")
	            .path("/")
	            .httpOnly(true)
	            .maxAge(0)
	            .build();

	    response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
	    response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

	    return "redirect:/loginPage?logout";
	}
	
	@GetMapping("/forgot-password")
	public String forgotPasswordPage() {
	    return "forgotPassword";
	}
	
	@PostMapping("/auth/forgot-password")
	public String handleForgotPassword(@RequestParam String email, Model model) {

	    try {
	        User user = userService.getUserByEmail(email);

	        // Generate token (Step 2 service)
	        String token =
	                passwordResetTokenService.createToken(user);

	        // Build reset link
	        String resetEndpoint =
	                "/reset-password?token=" + token;

	        emailService.sendPasswordResetEmail(user.getEmail(), resetEndpoint);


	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }

	    model.addAttribute(
	        "message", "The reset link has been sent successfully on your email."
	    );

	    return "forgotPassword";
	}

	@GetMapping("/reset-password")
	public String resetPasswordPage(@RequestParam String token, Model model) {

	    try {
	        passwordResetTokenService.validateToken(token);
	        model.addAttribute("token", token);
	        return "resetPassword";

	    } catch (Exception ex) {
	        model.addAttribute("error", "Invalid or expired reset link");
	        return "redirect:/loginPage?resetError";
	    }
	}

	@PostMapping("/auth/reset-password")
	public String handleResetPassword(
	        @RequestParam String token,
	        @RequestParam String password,
	        @RequestParam String confirmPassword,
	        Model model) {

	    if (!password.equals(confirmPassword)) {
	        model.addAttribute("error", "Passwords do not match");
	        model.addAttribute("token", token);
	        return "resetPassword";
	    }

	    try {
	        passwordResetTokenService.resetPassword(token, password);
	        return "redirect:/loginPage?resetSuccess";

	    } catch (Exception ex) {
	        model.addAttribute("error", "Invalid or expired reset link");
	        return "redirect:/loginPage?resetError";
	    }
	}

}
