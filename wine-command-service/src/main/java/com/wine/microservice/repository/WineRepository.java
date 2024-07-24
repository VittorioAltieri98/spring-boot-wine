package com.wine.microservice.repository;


import com.wine.microservice.model.Wine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WineRepository extends JpaRepository<Wine, Long> {

    Optional<Wine> findByWineName(String wineName);
}