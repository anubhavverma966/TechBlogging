package com.anubhav.techblog.Techblogging.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.anubhav.techblog.Techblogging.entity.RefreshToken;

@Repository
public interface RefreshTokenDao extends JpaRepository<RefreshToken, Long> {

	Optional<RefreshToken> findByToken(String token);

    void deleteByUsername(String username);
    
    @Modifying
    @Query("""
        update RefreshToken r
        set r.revoked = true
        where r.username = :username
    """)
    void revokeAllByUser(@Param("username") String username);

}
