package com.lloyds.creditcoach.creditscore.domain.port;

import com.lloyds.creditcoach.creditscore.domain.model.ScoreFactor;

import java.util.List;
import java.util.UUID;

public interface ScoreFactorRepository {

    List<ScoreFactor> saveAll(List<ScoreFactor> factors);

    List<ScoreFactor> findByScoreId(UUID scoreId);
}
