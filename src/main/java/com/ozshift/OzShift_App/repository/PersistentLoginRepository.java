package com.ozshift.OzShift_App.repository;

import com.ozshift.OzShift_App.entity.PersistentLogin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersistentLoginRepository extends JpaRepository<PersistentLogin, String> {
    void deleteByUsername(String username);
}
