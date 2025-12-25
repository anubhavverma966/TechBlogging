package com.anubhav.techblog.Techblogging.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.anubhav.techblog.Techblogging.entity.PasswordResetToken;

@Repository
public interface PasswordResetTokenDao extends JpaRepository<PasswordResetToken, Long> {
	Optional<PasswordResetToken> findByTokenHashAndUsedFalse(String tokenHash);
}
