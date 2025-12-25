package com.anubhav.techblog.Techblogging.service;

import java.time.Duration;
import java.time.Instant;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anubhav.techblog.Techblogging.dao.PasswordResetTokenDao;
import com.anubhav.techblog.Techblogging.dao.UserDao;
import com.anubhav.techblog.Techblogging.entity.PasswordResetToken;
import com.anubhav.techblog.Techblogging.entity.User;
import com.anubhav.techblog.Techblogging.util.PasswordResetTokenGenerator;
import com.anubhav.techblog.Techblogging.util.TokenHashUtil;

@Service
@Transactional
public class PasswordResetTokenServiceImpl implements PasswordResetTokenService {

	private final PasswordResetTokenDao tokenDao;
	private final PasswordResetTokenGenerator generator;
	private final TokenHashUtil hashUtil;
	private final PasswordEncoder passwordEncoder;
	private final UserDao userDao;
	private final RefreshTokenService refreshTokenService;

    public PasswordResetTokenServiceImpl(PasswordResetTokenDao tokenDao, PasswordResetTokenGenerator generator, 
    									TokenHashUtil hashUtil, PasswordEncoder passwordEncoder, UserDao userDao,
    									RefreshTokenService refreshTokenService) {
        this.tokenDao = tokenDao;
        this.generator = generator;
        this.hashUtil = hashUtil;
        this.passwordEncoder = passwordEncoder;
        this.userDao = userDao;
        this.refreshTokenService = refreshTokenService;
    }
    
	@Override
	public String createToken(User user) {

        String plainToken = generator.generateToken();

        String tokenHash = hashUtil.hashToken(plainToken);

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setTokenHash(tokenHash);
        resetToken.setUser(user);
        resetToken.setExpiryDate(
                Instant.now().plus(Duration.ofMinutes(15))
        );
        resetToken.setUsed(false);

        tokenDao.save(resetToken);

        return plainToken;
    }
	
	@Override
	public void validateToken(String rawToken) {

	    String hash = hashUtil.hashToken(rawToken);

	    PasswordResetToken token =
	            tokenDao
	                .findByTokenHashAndUsedFalse(hash)
	                .orElseThrow(() ->
	                        new RuntimeException("Invalid token"));

	    if (token.getExpiryDate().isBefore(Instant.now())) {
	        throw new RuntimeException("Token expired");
	    }
	}

	@Override
	public void resetPassword(String rawToken, String newPassword) {

		String hash = hashUtil.hashToken(rawToken);

	    PasswordResetToken token =
	            tokenDao
	                .findByTokenHashAndUsedFalse(hash)
	                .orElseThrow(() ->
	                        new RuntimeException("Invalid token"));

	    if (token.getExpiryDate().isBefore(Instant.now())) {
	        throw new RuntimeException("Token expired");
	    }

	    User user = token.getUser();
	    user.setPassword(passwordEncoder.encode(newPassword));
	    userDao.save(user);

	    token.setUsed(true);
	    tokenDao.save(token);
	    
	    refreshTokenService.revokeAllByUser(user);

	}


}
