package com.skillswap.skillservice.service;

import com.skillswap.skillservice.entity.dto.SkillDTO;
import com.skillswap.skillservice.entity.Skill;
import com.skillswap.skillservice.repository.SkillRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;

    public List<Skill> getAllSkills() {
        return skillRepository.findAll();
    }

    public Skill getSkillById(Long id) {
        return skillRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Skill not found: " + id));
    }

    /**
     * This method is called by UserService to get details for multiple skills.
     */
    public List<Skill> getSkillsByIds(List<Long> ids) {
        return skillRepository.findByIdIn(ids);
    }

    public Skill createSkill(SkillDTO skillDTO) {
        Skill skill = new Skill();
        skill.setName(skillDTO.getName());
        skill.setCategory(skillDTO.getCategory());
        skill.setDescription(skillDTO.getDescription());
        return skillRepository.save(skill);
    }

    public Skill updateSkill(Long id, SkillDTO skillDTO) {
        Skill existingSkill = getSkillById(id);
        
        existingSkill.setName(skillDTO.getName());
        existingSkill.setCategory(skillDTO.getCategory());
        existingSkill.setDescription(skillDTO.getDescription());
        
        return skillRepository.save(existingSkill);
    }

    public void deleteSkill(Long id) {
        if (!skillRepository.existsById(id)) {
            throw new EntityNotFoundException("Skill not found: " + id);
        }
        skillRepository.deleteById(id);
    }
}