package com.wine.wine_command.repository;


import com.wine.wine_command.model.Wine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WineRepository extends JpaRepository<Wine, Long> {

    Optional<Wine> findByWineName(String wineName);
}