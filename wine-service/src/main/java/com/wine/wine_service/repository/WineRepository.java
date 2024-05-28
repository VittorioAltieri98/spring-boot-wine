package com.wine.wine_service.repository;

import com.wine.wine_service.model.Wine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface WineRepository extends JpaRepository<Wine, Long>, JpaSpecificationExecutor<Wine> {

    Optional<Wine> findByWineName(String wineName);
}