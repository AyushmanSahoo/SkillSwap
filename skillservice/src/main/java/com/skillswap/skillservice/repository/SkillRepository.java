package com.skillswap.skillservice.repository;

import com.skillswap.skillservice.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillRepository extends JpaRepository<Skill,Long> {
    List<Skill> findByIdIn(List<Long> ids);
}
