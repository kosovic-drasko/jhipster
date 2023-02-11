package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.Subjects;
import com.mycompany.myapp.repository.SubjectsRepository;
import com.mycompany.myapp.service.criteria.SubjectsCriteria;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Subjects} entities in the database.
 * The main input is a {@link SubjectsCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Subjects} or a {@link Page} of {@link Subjects} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SubjectsQueryService extends QueryService<Subjects> {

    private final Logger log = LoggerFactory.getLogger(SubjectsQueryService.class);

    private final SubjectsRepository subjectsRepository;

    public SubjectsQueryService(SubjectsRepository subjectsRepository) {
        this.subjectsRepository = subjectsRepository;
    }

    /**
     * Return a {@link List} of {@link Subjects} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Subjects> findByCriteria(SubjectsCriteria criteria) {
        log.debug("find by criteria : {}", criteria.toString().replaceAll("[\n\r\t]", "_"));
        final Specification<Subjects> specification = createSpecification(criteria);
        return subjectsRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Subjects} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Subjects> findByCriteria(SubjectsCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria.toString().replaceAll("[\n\r\t]", "_"), page);
        final Specification<Subjects> specification = createSpecification(criteria);
        return subjectsRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SubjectsCriteria criteria) {
        log.debug("count by criteria : {}", criteria.toString().replaceAll("[\n\r\t]", "_"));
        final Specification<Subjects> specification = createSpecification(criteria);
        return subjectsRepository.count(specification);
    }

    /**
     * Function to convert {@link SubjectsCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Subjects> createSpecification(SubjectsCriteria criteria) {
        Specification<Subjects> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Subjects_.id));
            }
            if (criteria.getNameSubject() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNameSubject(), Subjects_.nameSubject));
            }
            if (criteria.getGrade() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getGrade(), Subjects_.grade));
            }
        }
        return specification;
    }
}
