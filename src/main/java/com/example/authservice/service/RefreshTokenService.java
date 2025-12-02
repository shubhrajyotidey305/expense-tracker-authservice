package com.example.authservice.service;
import com.example.authservice.entities.RefreshToken;
import com.example.authservice.entities.UserInfo;
import com.example.authservice.repository.RefreshTokenRepository;
import com.example.authservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Autowired
    UserRepository userRepository;

    public RefreshToken createRefreshToken(String username){
        UserInfo userInfoExtracted = userRepository.findByUsername(username);
        if (userInfoExtracted == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found for refresh token");
        }
        RefreshToken refreshToken = refreshTokenRepository.findByUserInfo(userInfoExtracted)
                .orElse(RefreshToken.builder().userInfo(userInfoExtracted).build());

        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(1000 * 60 * 60 * 24 * 2)); // 2 days

        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token){
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token){
        if(token.getExpiryDate().compareTo(Instant.now())<0){
            refreshTokenRepository.delete(token);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token is expired. Please make a new login.");
        }
        return token;
    }
}
