package com.anubhav.techblog.Techblogging.security;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.anubhav.techblog.Techblogging.entity.RefreshToken;
import com.anubhav.techblog.Techblogging.service.RefreshTokenService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public class JwtSilentRefreshFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserDetailsService userDetailsService;

    public JwtSilentRefreshFilter(
            JwtService jwtService,
            RefreshTokenService refreshTokenService,
            UserDetailsService userDetailsService
    ) {
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.userDetailsService = userDetailsService;
    }

//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) {
//        String path = request.getRequestURI();
//        return path.startsWith("/auth/login")
//                || path.startsWith("/loginPage")
//                || path.startsWith("/register")
//                || path.startsWith("/css/")
//                || path.startsWith("/js/")
//                || path.startsWith("/image/")
//                || path.startsWith("/blog_pics/")
//                || path.startsWith("/registerPage")
//                || path.startsWith("/pics/");
//    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }
        
        String accessToken = extractCookie(request, jwtService.getCookieName());
        String refreshTokenValue = extractCookie(request, "REFRESH_TOKEN");

        if (accessToken == null || refreshTokenValue == null) {
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            // 🔥 Extract username from EXPIRED access token safely
            String username =
                    jwtService.extractUsernameAllowExpired(accessToken);

            RefreshToken refreshToken =
                    refreshTokenService.verifyRefreshToken(refreshTokenValue);

            if (!refreshToken.getUsername().equals(username)) {
                filterChain.doFilter(request, response);
                return;
            }
            
            UserDetails userDetails =
                    userDetailsService.loadUserByUsername(username);

            String newAccessToken =
                    jwtService.generateTokenFromUsername(username);

            ResponseCookie newAccessCookie = ResponseCookie
                    .from(jwtService.getCookieName(), newAccessToken)
                    .httpOnly(true)
                    .secure(false)
                    .sameSite("Lax")
                    .path("/")
                    .maxAge(3600)
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE,
                               newAccessCookie.toString());
            
            
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource()
                            .buildDetails(request)
            );

            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);
            

        } catch (Exception ex) {
            // refresh token invalid/expired → let Security redirect
        	
        }
        

        filterChain.doFilter(request, response);
    }

    private String extractCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;

        return Arrays.stream(request.getCookies())
                .filter(c -> c.getName().equals(name))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
