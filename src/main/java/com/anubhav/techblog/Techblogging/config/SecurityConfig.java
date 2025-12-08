package com.anubhav.techblog.Techblogging.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.anubhav.techblog.Techblogging.security.CustomUserDetails;
import com.anubhav.techblog.Techblogging.security.CustomUserDetailsService;

import jakarta.servlet.http.HttpSession;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http, DaoAuthenticationProvider provider) throws Exception{
		http.authenticationProvider(provider);
		
		http.authorizeHttpRequests(auth -> 
			auth.requestMatchers("/", "/registerPage", "/loginPage",
                    "/css/**", "/js/**", "/pics/**", "/blog_pics/**").permitAll()
				.requestMatchers("/profilePage/**", "/addPost/**",
					"/editProfile/**", "/showBlogPost/**", "/loadPosts/**", "/post/**", 
					"/editPost").authenticated()
				.requestMatchers("/addCategory", "/deletePost/**").hasRole("ADMIN")
				.anyRequest().permitAll()
			);
		http.formLogin(login ->
			login.loginPage("/loginPage")
				 .loginProcessingUrl("/login")
				 .usernameParameter("email")
				 .passwordParameter("password")
				 .successHandler(storeUserInSession())
				 .failureUrl("/loginPage?error")
				 .permitAll()
			);
		http.logout(logout -> 
			logout.logoutUrl("/logout")
				  .logoutSuccessUrl("/loginPage?logout")
				  .invalidateHttpSession(true)
				  .deleteCookies("JSESSIONID")
				  .permitAll()
			);
		
		http.csrf(Customizer.withDefaults());
		
		return http.build();
	}
	
	@SuppressWarnings("deprecation")
	@Bean 
	DaoAuthenticationProvider daoAuthProvider(CustomUserDetailsService uds, PasswordEncoder encoder) {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(uds);
        p.setPasswordEncoder(encoder);
        return p;
    }

	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	private AuthenticationSuccessHandler storeUserInSession() {
        return (request, response, authentication) -> {
            HttpSession session = request.getSession();
            CustomUserDetails cud = (CustomUserDetails) authentication.getPrincipal();
            session.setAttribute("currentUser", cud.getDomainUser());
            response.sendRedirect("/profilePage");
        };
    }
}
