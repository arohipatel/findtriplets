package com.example.exercise.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttemptDataRepository extends JpaRepository<AttemptsData, Integer> {
}
