package com.anubhav.techblog.Techblogging.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anubhav.techblog.Techblogging.dao.RefreshTokenDao;
import com.anubhav.techblog.Techblogging.entity.RefreshToken;
import com.anubhav.techblog.Techblogging.entity.User;

@Service
@Transactional
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private static final long REFRESH_TOKEN_VALIDITY_DAYS = 7;

    private final RefreshTokenDao refreshTokenDao;

    public RefreshTokenServiceImpl(RefreshTokenDao repo) {
        this.refreshTokenDao = repo;
    }

    @Override
    public RefreshToken createRefreshToken(String username) {

        
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUsername(username);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plus(REFRESH_TOKEN_VALIDITY_DAYS, ChronoUnit.DAYS)
        );

        return refreshTokenDao.save(refreshToken);
    }
    
    @Override
    public RefreshToken verifyRefreshToken(String token) {

        RefreshToken refreshToken = refreshTokenDao
					                .findByToken(token)
					                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (refreshToken.isRevoked()) {
            throw new RuntimeException("Refresh token revoked");
        }

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            throw new RuntimeException("Refresh token expired");
        }

        return refreshToken;
    }

    @Override
    public void revokeToken(RefreshToken token) {
        token.setRevoked(true);
        refreshTokenDao.save(token);
    }

	@Override
	public void revokeAllByUser(User user) {
		refreshTokenDao.revokeAllByUser(user.getEmail());
	}
}
