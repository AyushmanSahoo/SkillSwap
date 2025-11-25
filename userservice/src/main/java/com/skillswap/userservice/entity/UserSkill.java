package com.skillswap.userservice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@NoArgsConstructor
@Entity
@Table(name = "user_skills")
public class UserSkill {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * @ManyToOne: The other side of the relationship. "Many UserSkill
     * records link to One UserProfile."
     *
     * @JoinColumn(name = "user_id"): This is the foreign key. It tells
     * JPA that the 'user_id' column in this table links to the
     * UserProfile's primary key.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference           // <--- 1. Breaks JSON recursion (Back part)
    @ToString.Exclude            // <--- 2. Prevents StackOverflow in logs
    @EqualsAndHashCode.Exclude   // <--- 3. Prevents StackOverflow in collections
    private UserProfile user;

    @Column(nullable = false)
    private Long skillId; // The ID of the skill (from SkillService)

    /**
     * @Enumerated(EnumType.STRING): Stores the enum as a readable string
     * in the database (e.g., "OFFERED" or "WANTED") instead of a number.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SkillType type;
}