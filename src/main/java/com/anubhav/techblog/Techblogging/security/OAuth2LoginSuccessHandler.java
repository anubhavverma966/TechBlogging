package com.anubhav.techblog.Techblogging.security;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.anubhav.techblog.Techblogging.entity.RefreshToken;
import com.anubhav.techblog.Techblogging.entity.User;
import com.anubhav.techblog.Techblogging.service.RefreshTokenService;
import com.anubhav.techblog.Techblogging.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AzureGroupRoleMapper azureGroupRoleMapper;

    public OAuth2LoginSuccessHandler(
            UserService userService,
            JwtService jwtService,
            RefreshTokenService refreshTokenService,
            AzureGroupRoleMapper azureGroupRoleMapper
    ) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.azureGroupRoleMapper = azureGroupRoleMapper;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        OAuth2AuthenticationToken oauthToken =
                (OAuth2AuthenticationToken) authentication;

        // 🔐 Provider (AZURE / GOOGLE)
        String provider =
                oauthToken.getAuthorizedClientRegistrationId().toUpperCase();

        Map<String, Object> attributes =
                oauthToken.getPrincipal().getAttributes();

        // 📧 Email (Azure + Google compatible)
        String email = (String) attributes.get("email");
        if (email == null) {
            email = (String) attributes.get("preferred_username");
        }

        String name = (String) attributes.get("name");
        String providerId = (String) attributes.get("sub");

        // 🧠 Create or link user (ONLY place this happens)
        User user = userService.findOrCreateOAuthUser(
                email, name, providerId, provider
        );

        // 🔁 Azure group → role sync (only if Azure)
        if ("AZURE".equals(provider)) {
            List<String> azureGroups =
                    oauthToken.getPrincipal().getAttribute("groups");

            if (azureGroups != null && !azureGroups.isEmpty()) {
                Set<String> mappedRoles =
                        azureGroupRoleMapper.mapGroupsToRoles(azureGroups);
                userService.syncUserRoles(user, mappedRoles);
            }
        }

        // 🔑 Issue JWT
        String accessToken =
                jwtService.generateTokenFromUsername(user.getEmail());

        ResponseCookie accessCookie = ResponseCookie
                .from(jwtService.getCookieName(), accessToken)
                .httpOnly(true)
                .secure(false)          // true in HTTPS
                .sameSite("Lax")
                .path("/")
                .maxAge(3600)
                .build();

        // 🔄 Issue refresh token
        RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(user.getEmail());

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

        // 🚀 Redirect to profile
        response.sendRedirect("/profilePage");
    }
}
