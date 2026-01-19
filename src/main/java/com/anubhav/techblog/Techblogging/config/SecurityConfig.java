package com.anubhav.techblog.Techblogging.config;

import com.anubhav.techblog.Techblogging.security.JwtAuthenticationFilter;
import com.anubhav.techblog.Techblogging.security.JwtService;
import com.anubhav.techblog.Techblogging.security.JwtSilentRefreshFilter;
import com.anubhav.techblog.Techblogging.security.OAuth2LoginSuccessHandler;
import com.anubhav.techblog.Techblogging.service.RefreshTokenService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtSilentRefreshFilter jwtSilentRefreshFilter,
			JwtAuthenticationFilter jwtAuthenticationFilter , OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler ) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm ->
                sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/", "/loginPage", "/registerPage", "/register", "/infra/**", "/actuator/health",
                        "/auth/login", "/logout", "/image/**", "/forgot-password", "/auth/forgot-password",
                        "/css/**", "/js/**", "/pics/**", "/blog_pics/**", "/reset-password",
                        "/auth/reset-password", "/oauth2/**"
                ).permitAll()
                .requestMatchers("/deletePost/**", "/addCategory/**")
                .hasRole("ADMIN")
                .requestMatchers(
                		"/profilePage/**", "/addPost/**",
                		"/editProfile/**", "/showBlogPost/**", "/loadPosts/**", "/post/**", 
                		"/editPost")
                .authenticated()
                .anyRequest().denyAll()
            )
            .exceptionHandling(ex -> ex
                    .authenticationEntryPoint((req, res, e) ->
                        res.sendRedirect("/loginPage")
                    )
             )
            .logout(logout -> logout.disable());
        
        http
	        .oauth2Login(oauth -> oauth
	            .loginPage("/loginPage")
	            .successHandler(oAuth2LoginSuccessHandler)
	        );

            

            http.addFilterBefore(
            	    jwtSilentRefreshFilter,
            	    UsernamePasswordAuthenticationFilter.class
            	);

            http.addFilterBefore(jwtAuthenticationFilter,
                    UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }
    
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(
            JwtService jwtService,
            UserDetailsService userDetailsService
    ) {
        return new JwtAuthenticationFilter(jwtService, userDetailsService);
    }

    @Bean
    public JwtSilentRefreshFilter jwtSilentRefreshFilter(
            JwtService jwtService,
            RefreshTokenService refreshTokenService,
            UserDetailsService userDetailsService
    ) {
        return new JwtSilentRefreshFilter(jwtService, refreshTokenService, userDetailsService);
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
