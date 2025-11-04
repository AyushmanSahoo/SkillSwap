package com.skillswap.reputationservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "ratings") // As defined in the architecture doc
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long exchangeId; // Foreign key to the exchange

    @Column(nullable = false)
    private Long raterId; // User who GAVE the rating

    @Column(nullable = false)
    private Long rateeId; // User who RECEIVED the rating

    @Column(nullable = false)
    private int score; // e.g., 1-5

    @Lob
    private String comment;

    @Column(nullable = false)
    private LocalDateTime timestamp;
}