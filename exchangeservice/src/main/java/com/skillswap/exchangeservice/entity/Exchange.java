package com.skillswap.exchangeservice.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "exchanges") // As defined in the architecture
public class Exchange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long requesterId; // User who initiated the swap

    @Column(nullable = false)
    private Long providerId; // User who receives the swap request

    @Column(nullable = false)
    private Long requesterSkillId; // Skill offered by the requester

    @Column(nullable = false)
    private Long providerSkillId; // Skill requested from the provider

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExchangeStatus status;

    @Column(nullable = false)
    private LocalDateTime requestTimestamp;
    
    private LocalDateTime responseTimestamp; // When it was accepted/rejected
    
    private LocalDateTime completedTimestamp; // When it was marked complete
}