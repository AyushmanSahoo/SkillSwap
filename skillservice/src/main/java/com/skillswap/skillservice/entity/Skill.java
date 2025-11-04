package com.skillswap.skillservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "skills") // Maps this class to the "skills" table 
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Auto-incrementing primary key

    @Column(nullable = false, unique = true)
    private String name; // e.g., "Java", "Python", "Graphic Design"

    @Column(nullable = false)
    private String category; // e.g., "Programming", "Design"

    @Lob
    private String description;
}