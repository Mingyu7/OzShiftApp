package com.ozshift.OzShift_App.service;

import com.ozshift.OzShift_App.entity.PersistentLogin;
import com.ozshift.OzShift_App.repository.PersistentLoginRepository;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;

@Component
public class JpaTokenRepositoryImpl implements PersistentTokenRepository {

    private final PersistentLoginRepository persistentLoginRepository;

    public JpaTokenRepositoryImpl(PersistentLoginRepository persistentLoginRepository) {
        this.persistentLoginRepository = persistentLoginRepository;
    }

    @Override
    @Transactional
    public void createNewToken(PersistentRememberMeToken token) {
        PersistentLogin newToken = new PersistentLogin();
        newToken.setUsername(token.getUsername());
        newToken.setSeries(token.getSeries());
        newToken.setToken(token.getTokenValue());
        newToken.setLastUsed(LocalDateTime.now());
        persistentLoginRepository.save(newToken);
    }

    @Override
    @Transactional
    public void updateToken(String series, String tokenValue, Date lastUsed) {
        persistentLoginRepository.findById(series).ifPresent(token -> {
            token.setToken(tokenValue);
            token.setLastUsed(lastUsed.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime());
            persistentLoginRepository.save(token);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public PersistentRememberMeToken getTokenForSeries(String seriesId) {
        return persistentLoginRepository.findById(seriesId)
                .map(token -> new PersistentRememberMeToken(
                        token.getUsername(),
                        token.getSeries(),
                        token.getToken(),
                        Date.from(token.getLastUsed().atZone(java.time.ZoneId.systemDefault()).toInstant())))
                .orElse(null);
    }

    @Override
    @Transactional
    public void removeUserTokens(String username) {
        persistentLoginRepository.deleteByUsername(username);
    }
}
