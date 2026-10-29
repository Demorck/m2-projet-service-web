package fr.isaaconnect.sweb.repositories;

import fr.isaaconnect.sweb.entities.EvaluationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EvaluationRepository extends JpaRepository<EvaluationEntity, UUID> {}
