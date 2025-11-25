package com.skillswap.userservice.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;


@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
public class    UserProfile {


    @Id
    private Long userId; // This ID comes from the AuthService

    /**
     * @Column: Marks a field as a table column.
     * nullable = false: Adds a 'NOT NULL' constraint in PostgreSQL.
     */
    @Column(nullable = false)
    private String firstName;
    
    @Column(nullable = false)
    private String lastName;

    /**
     * unique = true: Adds a 'UNIQUE' constraint.
     */
    @Column(unique = true, nullable = false)
    private String email;
    
    /**
     * @Lob: Stands for "Large Object." This tells JPA to use a 'TEXT'
     * column type in PostgreSQL, which is good for long strings like a bio.
     */
    @Lob
    private String bio;

    /**
     * @OneToMany: Defines the relationship. This says "One UserProfile
     * has Many UserSkill records."
     *
     * mappedBy = "user": Tells JPA, "Look at the 'user' field in the
     * UserSkill class to find the join column."
     *
     * cascade = CascadeType.ALL: This makes it easy! It means:
     * - If I save a UserProfile, also save all UserSkills in this set.
     * - If I delete a UserProfile, also delete all its UserSkills.
     *
     * orphanRemoval = true: Easy cleanup. If you remove a UserSkill
     * from this Set, JPA will automatically delete it from the database.
     */

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference        // <--- 1. Breaks JSON recursion (Forward part)
    @ToString.Exclude            // <--- 2. Prevents StackOverflow in logs
    @EqualsAndHashCode.Exclude   // <--- 3. Prevents StackOverflow in collections
    private Set<UserSkill> skills = new HashSet<>();
}