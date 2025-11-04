package com.skillswap.skillservice.controler;

import com.skillswap.skillservice.entity.dto.SkillDTO;
import com.skillswap.skillservice.entity.Skill;
import com.skillswap.skillservice.service.SkillService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;


    @GetMapping
    public List<Skill> getAllSkills() {
        return skillService.getAllSkills();
    }

    /**
     * GET /api/v1/skills/batch
     * Gets a list of skills by their IDs.
     * This is the endpoint the UserService calls. [cite: 26]
     * Example: /api/v1/skills/batch?ids=1,2,3
     */
    @GetMapping("/batch")
    public List<Skill> getSkillsByIds(@RequestParam("ids") List<Long> ids) {
        return skillService.getSkillsByIds(ids);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Skill> getSkillById(@PathVariable Long id) {
        try {
            Skill skill = skillService.getSkillById(id);
            return ResponseEntity.ok(skill);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping
    public ResponseEntity<Skill> createSkill(@RequestBody SkillDTO skillDTO) {
        Skill createdSkill = skillService.createSkill(skillDTO);
        return new ResponseEntity<>(createdSkill, HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Skill> updateSkill(@PathVariable Long id, @RequestBody SkillDTO skillDTO) {
        try {
            Skill updatedSkill = skillService.updateSkill(id, skillDTO);
            return ResponseEntity.ok(updatedSkill);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSkill(@PathVariable Long id) {
        try {
            skillService.deleteSkill(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}