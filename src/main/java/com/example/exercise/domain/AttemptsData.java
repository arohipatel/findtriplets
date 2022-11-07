package com.example.exercise.domain;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity(name = "attempt")
public class AttemptsData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String input;

    private String output;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
