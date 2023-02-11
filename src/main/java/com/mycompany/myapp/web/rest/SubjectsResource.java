package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Subjects;
import com.mycompany.myapp.repository.SubjectsRepository;
import com.mycompany.myapp.service.SubjectsQueryService;
import com.mycompany.myapp.service.SubjectsService;
import com.mycompany.myapp.service.criteria.SubjectsCriteria;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Subjects}.
 */
@RestController
@RequestMapping("/api")
public class SubjectsResource {

    private final Logger log = LoggerFactory.getLogger(SubjectsResource.class);

    private static final String ENTITY_NAME = "subjects";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SubjectsService subjectsService;

    private final SubjectsRepository subjectsRepository;

    private final SubjectsQueryService subjectsQueryService;

    public SubjectsResource(
        SubjectsService subjectsService,
        SubjectsRepository subjectsRepository,
        SubjectsQueryService subjectsQueryService
    ) {
        this.subjectsService = subjectsService;
        this.subjectsRepository = subjectsRepository;
        this.subjectsQueryService = subjectsQueryService;
    }

    /**
     * {@code POST  /subjects} : Create a new subjects.
     *
     * @param subjects the subjects to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new subjects, or with status {@code 400 (Bad Request)} if the subjects has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/subjects")
    public ResponseEntity<Subjects> createSubjects(@Valid @RequestBody Subjects subjects) throws URISyntaxException {
        log.debug("REST request to save Subjects : {}", subjects);
        if (subjects.getId() != null) {
            throw new BadRequestAlertException("A new subjects cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Subjects result = subjectsService.save(subjects);
        return ResponseEntity
            .created(new URI("/api/subjects/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /subjects/:id} : Updates an existing subjects.
     *
     * @param id the id of the subjects to save.
     * @param subjects the subjects to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated subjects,
     * or with status {@code 400 (Bad Request)} if the subjects is not valid,
     * or with status {@code 500 (Internal Server Error)} if the subjects couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/subjects/{id}")
    public ResponseEntity<Subjects> updateSubjects(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Subjects subjects
    ) throws URISyntaxException {
        log.debug("REST request to update Subjects : {}, {}", id, subjects);
        if (subjects.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, subjects.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!subjectsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Subjects result = subjectsService.update(subjects);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, subjects.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /subjects/:id} : Partial updates given fields of an existing subjects, field will ignore if it is null
     *
     * @param id the id of the subjects to save.
     * @param subjects the subjects to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated subjects,
     * or with status {@code 400 (Bad Request)} if the subjects is not valid,
     * or with status {@code 404 (Not Found)} if the subjects is not found,
     * or with status {@code 500 (Internal Server Error)} if the subjects couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/subjects/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Subjects> partialUpdateSubjects(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Subjects subjects
    ) throws URISyntaxException {
        log.debug("REST request to partial update Subjects partially : {}, {}", id, subjects);
        if (subjects.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, subjects.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!subjectsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Subjects> result = subjectsService.partialUpdate(subjects);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, subjects.getId().toString())
        );
    }

    /**
     * {@code GET  /subjects} : get all the subjects.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of subjects in body.
     */
    @GetMapping("/subjects")
    public ResponseEntity<List<Subjects>> getAllSubjects(
        SubjectsCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Subjects by criteria: {}", criteria.toString().replaceAll("[\n\r\t]", "_"));
        Page<Subjects> page = subjectsQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /subjects/count} : count all the subjects.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/subjects/count")
    public ResponseEntity<Long> countSubjects(SubjectsCriteria criteria) {
        log.debug("REST request to count Subjects by criteria: {}", criteria.toString().replaceAll("[\n\r\t]", "_"));
        return ResponseEntity.ok().body(subjectsQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /subjects/:id} : get the "id" subjects.
     *
     * @param id the id of the subjects to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the subjects, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/subjects/{id}")
    public ResponseEntity<Subjects> getSubjects(@PathVariable Long id) {
        log.debug("REST request to get Subjects : {}", id);
        Optional<Subjects> subjects = subjectsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(subjects);
    }

    /**
     * {@code DELETE  /subjects/:id} : delete the "id" subjects.
     *
     * @param id the id of the subjects to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/subjects/{id}")
    public ResponseEntity<Void> deleteSubjects(@PathVariable Long id) {
        log.debug("REST request to delete Subjects : {}", id);
        subjectsService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
