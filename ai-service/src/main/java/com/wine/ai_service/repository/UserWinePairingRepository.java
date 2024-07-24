package com.wine.ai_service.repository;

import com.wine.ai_service.model.UserWinePairing;
import com.wine.ai_service.model.WinePairing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserWinePairingRepository extends JpaRepository<UserWinePairing, Long> {

    List<UserWinePairing> findAllByUserId(String userId);

    Optional<UserWinePairing> findByWineNameAndUserId(String wineName,String id);

    void deleteByUserId(String userId);

}
