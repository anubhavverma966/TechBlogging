package com.anubhav.techblog.Techblogging.service;

import com.anubhav.techblog.Techblogging.entity.User;

public interface PasswordResetTokenService {

	String createToken(User user);

	void validateToken(String rawToken);

	void resetPassword(String rawToken, String newPassword);
}
