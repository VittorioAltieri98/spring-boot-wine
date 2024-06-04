package com.wine.ai_service.repository;

import com.wine.ai_service.model.WinePairing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WinePairingRepository extends JpaRepository<WinePairing, Long> {

    Optional<WinePairing> findByWineId(Long wineId);
}
