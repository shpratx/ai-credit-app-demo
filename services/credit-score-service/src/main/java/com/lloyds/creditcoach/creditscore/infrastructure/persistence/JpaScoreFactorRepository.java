package com.lloyds.creditcoach.creditscore.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaScoreFactorRepository extends JpaRepository<ScoreFactorEntity, UUID> {

    List<ScoreFactorEntity> findByScoreId(UUID scoreId);
}
