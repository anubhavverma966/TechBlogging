package com.anubhav.techblog.Techblogging.service;

import com.anubhav.techblog.Techblogging.entity.RefreshToken;
import com.anubhav.techblog.Techblogging.entity.User;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(String username);
    
    RefreshToken verifyRefreshToken(String token);

    void revokeToken(RefreshToken token);
    
    void revokeAllByUser(User user);

}
