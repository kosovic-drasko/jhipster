package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Subjects;
import com.mycompany.myapp.repository.SubjectsRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Subjects}.
 */
@Service
@Transactional
public class SubjectsService {

    private final Logger log = LoggerFactory.getLogger(SubjectsService.class);

    private final SubjectsRepository subjectsRepository;

    public SubjectsService(SubjectsRepository subjectsRepository) {
        this.subjectsRepository = subjectsRepository;
    }

    /**
     * Save a subjects.
     *
     * @param subjects the entity to save.
     * @return the persisted entity.
     */
    public Subjects save(Subjects subjects) {
        log.debug("Request to save Subjects : {}", subjects);
        return subjectsRepository.save(subjects);
    }

    /**
     * Update a subjects.
     *
     * @param subjects the entity to save.
     * @return the persisted entity.
     */
    public Subjects update(Subjects subjects) {
        log.debug("Request to save Subjects : {}", subjects);
        return subjectsRepository.save(subjects);
    }

    /**
     * Partially update a subjects.
     *
     * @param subjects the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Subjects> partialUpdate(Subjects subjects) {
        log.debug("Request to partially update Subjects : {}", subjects);

        return subjectsRepository
            .findById(subjects.getId())
            .map(existingSubjects -> {
                if (subjects.getNameSubject() != null) {
                    existingSubjects.setNameSubject(subjects.getNameSubject());
                }
                if (subjects.getGrade() != null) {
                    existingSubjects.setGrade(subjects.getGrade());
                }

                return existingSubjects;
            })
            .map(subjectsRepository::save);
    }

    /**
     * Get all the subjects.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Subjects> findAll(Pageable pageable) {
        log.debug("Request to get all Subjects");
        return subjectsRepository.findAll(pageable);
    }

    /**
     * Get one subjects by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Subjects> findOne(Long id) {
        log.debug("Request to get Subjects : {}", id);
        return subjectsRepository.findById(id);
    }

    /**
     * Delete the subjects by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Subjects : {}", id);
        subjectsRepository.deleteById(id);
    }
}
