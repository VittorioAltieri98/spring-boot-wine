package com.wine.ai_service.repository;

import com.wine.ai_service.model.PairingRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PairingRequestRepository extends JpaRepository<PairingRequest, Long> {

    @Query("""
            SELECT p.wineType, p.region, COUNT(p)
            FROM PairingRequest p
            GROUP BY p.wineType, p.region
            ORDER BY COUNT(p) DESC
            """)
    List<Object[]> findGroupedPairingRequests();
}
