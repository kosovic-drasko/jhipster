package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Subjects;
import com.mycompany.myapp.repository.SubjectsRepository;
import com.mycompany.myapp.service.criteria.SubjectsCriteria;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link SubjectsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SubjectsResourceIT {

    private static final String DEFAULT_NAME_SUBJECT = "AAAAAAAAAA";
    private static final String UPDATED_NAME_SUBJECT = "BBBBBBBBBB";

    private static final Integer DEFAULT_GRADE = 1;
    private static final Integer UPDATED_GRADE = 2;
    private static final Integer SMALLER_GRADE = 1 - 1;

    private static final String ENTITY_API_URL = "/api/subjects";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SubjectsRepository subjectsRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSubjectsMockMvc;

    private Subjects subjects;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Subjects createEntity(EntityManager em) {
        Subjects subjects = new Subjects().nameSubject(DEFAULT_NAME_SUBJECT).grade(DEFAULT_GRADE);
        return subjects;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Subjects createUpdatedEntity(EntityManager em) {
        Subjects subjects = new Subjects().nameSubject(UPDATED_NAME_SUBJECT).grade(UPDATED_GRADE);
        return subjects;
    }

    @BeforeEach
    public void initTest() {
        subjects = createEntity(em);
    }

    @Test
    @Transactional
    void createSubjects() throws Exception {
        int databaseSizeBeforeCreate = subjectsRepository.findAll().size();
        // Create the Subjects
        restSubjectsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(subjects)))
            .andExpect(status().isCreated());

        // Validate the Subjects in the database
        List<Subjects> subjectsList = subjectsRepository.findAll();
        assertThat(subjectsList).hasSize(databaseSizeBeforeCreate + 1);
        Subjects testSubjects = subjectsList.get(subjectsList.size() - 1);
        assertThat(testSubjects.getNameSubject()).isEqualTo(DEFAULT_NAME_SUBJECT);
        assertThat(testSubjects.getGrade()).isEqualTo(DEFAULT_GRADE);
    }

    @Test
    @Transactional
    void createSubjectsWithExistingId() throws Exception {
        // Create the Subjects with an existing ID
        subjects.setId(1L);

        int databaseSizeBeforeCreate = subjectsRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSubjectsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(subjects)))
            .andExpect(status().isBadRequest());

        // Validate the Subjects in the database
        List<Subjects> subjectsList = subjectsRepository.findAll();
        assertThat(subjectsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameSubjectIsRequired() throws Exception {
        int databaseSizeBeforeTest = subjectsRepository.findAll().size();
        // set the field null
        subjects.setNameSubject(null);

        // Create the Subjects, which fails.

        restSubjectsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(subjects)))
            .andExpect(status().isBadRequest());

        List<Subjects> subjectsList = subjectsRepository.findAll();
        assertThat(subjectsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSubjects() throws Exception {
        // Initialize the database
        subjectsRepository.saveAndFlush(subjects);

        // Get all the subjectsList
        restSubjectsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(subjects.getId().intValue())))
            .andExpect(jsonPath("$.[*].nameSubject").value(hasItem(DEFAULT_NAME_SUBJECT)))
            .andExpect(jsonPath("$.[*].grade").value(hasItem(DEFAULT_GRADE)));
    }

    @Test
    @Transactional
    void getSubjects() throws Exception {
        // Initialize the database
        subjectsRepository.saveAndFlush(subjects);

        // Get the subjects
        restSubjectsMockMvc
            .perform(get(ENTITY_API_URL_ID, subjects.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(subjects.getId().intValue()))
            .andExpect(jsonPath("$.nameSubject").value(DEFAULT_NAME_SUBJECT))
            .andExpect(jsonPath("$.grade").value(DEFAULT_GRADE));
    }

    @Test
    @Transactional
    void getSubjectsByIdFiltering() throws Exception {
        // Initialize the database
        subjectsRepository.saveAndFlush(subjects);

        Long id = subjects.getId();

        defaultSubjectsShouldBeFound("id.equals=" + id);
        defaultSubjectsShouldNotBeFound("id.notEquals=" + id);

        defaultSubjectsShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultSubjectsShouldNotBeFound("id.greaterThan=" + id);

        defaultSubjectsShouldBeFound("id.lessThanOrEqual=" + id);
        defaultSubjectsShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSubjectsByNameSubjectIsEqualToSomething() throws Exception {
        // Initialize the database
        subjectsRepository.saveAndFlush(subjects);

        // Get all the subjectsList where nameSubject equals to DEFAULT_NAME_SUBJECT
        defaultSubjectsShouldBeFound("nameSubject.equals=" + DEFAULT_NAME_SUBJECT);

        // Get all the subjectsList where nameSubject equals to UPDATED_NAME_SUBJECT
        defaultSubjectsShouldNotBeFound("nameSubject.equals=" + UPDATED_NAME_SUBJECT);
    }

    @Test
    @Transactional
    void getAllSubjectsByNameSubjectIsInShouldWork() throws Exception {
        // Initialize the database
        subjectsRepository.saveAndFlush(subjects);

        // Get all the subjectsList where nameSubject in DEFAULT_NAME_SUBJECT or UPDATED_NAME_SUBJECT
        defaultSubjectsShouldBeFound("nameSubject.in=" + DEFAULT_NAME_SUBJECT + "," + UPDATED_NAME_SUBJECT);

        // Get all the subjectsList where nameSubject equals to UPDATED_NAME_SUBJECT
        defaultSubjectsShouldNotBeFound("nameSubject.in=" + UPDATED_NAME_SUBJECT);
    }

    @Test
    @Transactional
    void getAllSubjectsByNameSubjectIsNullOrNotNull() throws Exception {
        // Initialize the database
        subjectsRepository.saveAndFlush(subjects);

        // Get all the subjectsList where nameSubject is not null
        defaultSubjectsShouldBeFound("nameSubject.specified=true");

        // Get all the subjectsList where nameSubject is null
        defaultSubjectsShouldNotBeFound("nameSubject.specified=false");
    }

    @Test
    @Transactional
    void getAllSubjectsByNameSubjectContainsSomething() throws Exception {
        // Initialize the database
        subjectsRepository.saveAndFlush(subjects);

        // Get all the subjectsList where nameSubject contains DEFAULT_NAME_SUBJECT
        defaultSubjectsShouldBeFound("nameSubject.contains=" + DEFAULT_NAME_SUBJECT);

        // Get all the subjectsList where nameSubject contains UPDATED_NAME_SUBJECT
        defaultSubjectsShouldNotBeFound("nameSubject.contains=" + UPDATED_NAME_SUBJECT);
    }

    @Test
    @Transactional
    void getAllSubjectsByNameSubjectNotContainsSomething() throws Exception {
        // Initialize the database
        subjectsRepository.saveAndFlush(subjects);

        // Get all the subjectsList where nameSubject does not contain DEFAULT_NAME_SUBJECT
        defaultSubjectsShouldNotBeFound("nameSubject.doesNotContain=" + DEFAULT_NAME_SUBJECT);

        // Get all the subjectsList where nameSubject does not contain UPDATED_NAME_SUBJECT
        defaultSubjectsShouldBeFound("nameSubject.doesNotContain=" + UPDATED_NAME_SUBJECT);
    }

    @Test
    @Transactional
    void getAllSubjectsByGradeIsEqualToSomething() throws Exception {
        // Initialize the database
        subjectsRepository.saveAndFlush(subjects);

        // Get all the subjectsList where grade equals to DEFAULT_GRADE
        defaultSubjectsShouldBeFound("grade.equals=" + DEFAULT_GRADE);

        // Get all the subjectsList where grade equals to UPDATED_GRADE
        defaultSubjectsShouldNotBeFound("grade.equals=" + UPDATED_GRADE);
    }

    @Test
    @Transactional
    void getAllSubjectsByGradeIsInShouldWork() throws Exception {
        // Initialize the database
        subjectsRepository.saveAndFlush(subjects);

        // Get all the subjectsList where grade in DEFAULT_GRADE or UPDATED_GRADE
        defaultSubjectsShouldBeFound("grade.in=" + DEFAULT_GRADE + "," + UPDATED_GRADE);

        // Get all the subjectsList where grade equals to UPDATED_GRADE
        defaultSubjectsShouldNotBeFound("grade.in=" + UPDATED_GRADE);
    }

    @Test
    @Transactional
    void getAllSubjectsByGradeIsNullOrNotNull() throws Exception {
        // Initialize the database
        subjectsRepository.saveAndFlush(subjects);

        // Get all the subjectsList where grade is not null
        defaultSubjectsShouldBeFound("grade.specified=true");

        // Get all the subjectsList where grade is null
        defaultSubjectsShouldNotBeFound("grade.specified=false");
    }

    @Test
    @Transactional
    void getAllSubjectsByGradeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        subjectsRepository.saveAndFlush(subjects);

        // Get all the subjectsList where grade is greater than or equal to DEFAULT_GRADE
        defaultSubjectsShouldBeFound("grade.greaterThanOrEqual=" + DEFAULT_GRADE);

        // Get all the subjectsList where grade is greater than or equal to UPDATED_GRADE
        defaultSubjectsShouldNotBeFound("grade.greaterThanOrEqual=" + UPDATED_GRADE);
    }

    @Test
    @Transactional
    void getAllSubjectsByGradeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        subjectsRepository.saveAndFlush(subjects);

        // Get all the subjectsList where grade is less than or equal to DEFAULT_GRADE
        defaultSubjectsShouldBeFound("grade.lessThanOrEqual=" + DEFAULT_GRADE);

        // Get all the subjectsList where grade is less than or equal to SMALLER_GRADE
        defaultSubjectsShouldNotBeFound("grade.lessThanOrEqual=" + SMALLER_GRADE);
    }

    @Test
    @Transactional
    void getAllSubjectsByGradeIsLessThanSomething() throws Exception {
        // Initialize the database
        subjectsRepository.saveAndFlush(subjects);

        // Get all the subjectsList where grade is less than DEFAULT_GRADE
        defaultSubjectsShouldNotBeFound("grade.lessThan=" + DEFAULT_GRADE);

        // Get all the subjectsList where grade is less than UPDATED_GRADE
        defaultSubjectsShouldBeFound("grade.lessThan=" + UPDATED_GRADE);
    }

    @Test
    @Transactional
    void getAllSubjectsByGradeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        subjectsRepository.saveAndFlush(subjects);

        // Get all the subjectsList where grade is greater than DEFAULT_GRADE
        defaultSubjectsShouldNotBeFound("grade.greaterThan=" + DEFAULT_GRADE);

        // Get all the subjectsList where grade is greater than SMALLER_GRADE
        defaultSubjectsShouldBeFound("grade.greaterThan=" + SMALLER_GRADE);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSubjectsShouldBeFound(String filter) throws Exception {
        restSubjectsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(subjects.getId().intValue())))
            .andExpect(jsonPath("$.[*].nameSubject").value(hasItem(DEFAULT_NAME_SUBJECT)))
            .andExpect(jsonPath("$.[*].grade").value(hasItem(DEFAULT_GRADE)));

        // Check, that the count call also returns 1
        restSubjectsMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSubjectsShouldNotBeFound(String filter) throws Exception {
        restSubjectsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSubjectsMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSubjects() throws Exception {
        // Get the subjects
        restSubjectsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewSubjects() throws Exception {
        // Initialize the database
        subjectsRepository.saveAndFlush(subjects);

        int databaseSizeBeforeUpdate = subjectsRepository.findAll().size();

        // Update the subjects
        Subjects updatedSubjects = subjectsRepository.findById(subjects.getId()).get();
        // Disconnect from session so that the updates on updatedSubjects are not directly saved in db
        em.detach(updatedSubjects);
        updatedSubjects.nameSubject(UPDATED_NAME_SUBJECT).grade(UPDATED_GRADE);

        restSubjectsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSubjects.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedSubjects))
            )
            .andExpect(status().isOk());

        // Validate the Subjects in the database
        List<Subjects> subjectsList = subjectsRepository.findAll();
        assertThat(subjectsList).hasSize(databaseSizeBeforeUpdate);
        Subjects testSubjects = subjectsList.get(subjectsList.size() - 1);
        assertThat(testSubjects.getNameSubject()).isEqualTo(UPDATED_NAME_SUBJECT);
        assertThat(testSubjects.getGrade()).isEqualTo(UPDATED_GRADE);
    }

    @Test
    @Transactional
    void putNonExistingSubjects() throws Exception {
        int databaseSizeBeforeUpdate = subjectsRepository.findAll().size();
        subjects.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSubjectsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, subjects.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(subjects))
            )
            .andExpect(status().isBadRequest());

        // Validate the Subjects in the database
        List<Subjects> subjectsList = subjectsRepository.findAll();
        assertThat(subjectsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSubjects() throws Exception {
        int databaseSizeBeforeUpdate = subjectsRepository.findAll().size();
        subjects.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubjectsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(subjects))
            )
            .andExpect(status().isBadRequest());

        // Validate the Subjects in the database
        List<Subjects> subjectsList = subjectsRepository.findAll();
        assertThat(subjectsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSubjects() throws Exception {
        int databaseSizeBeforeUpdate = subjectsRepository.findAll().size();
        subjects.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubjectsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(subjects)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Subjects in the database
        List<Subjects> subjectsList = subjectsRepository.findAll();
        assertThat(subjectsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSubjectsWithPatch() throws Exception {
        // Initialize the database
        subjectsRepository.saveAndFlush(subjects);

        int databaseSizeBeforeUpdate = subjectsRepository.findAll().size();

        // Update the subjects using partial update
        Subjects partialUpdatedSubjects = new Subjects();
        partialUpdatedSubjects.setId(subjects.getId());

        partialUpdatedSubjects.nameSubject(UPDATED_NAME_SUBJECT).grade(UPDATED_GRADE);

        restSubjectsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSubjects.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSubjects))
            )
            .andExpect(status().isOk());

        // Validate the Subjects in the database
        List<Subjects> subjectsList = subjectsRepository.findAll();
        assertThat(subjectsList).hasSize(databaseSizeBeforeUpdate);
        Subjects testSubjects = subjectsList.get(subjectsList.size() - 1);
        assertThat(testSubjects.getNameSubject()).isEqualTo(UPDATED_NAME_SUBJECT);
        assertThat(testSubjects.getGrade()).isEqualTo(UPDATED_GRADE);
    }

    @Test
    @Transactional
    void fullUpdateSubjectsWithPatch() throws Exception {
        // Initialize the database
        subjectsRepository.saveAndFlush(subjects);

        int databaseSizeBeforeUpdate = subjectsRepository.findAll().size();

        // Update the subjects using partial update
        Subjects partialUpdatedSubjects = new Subjects();
        partialUpdatedSubjects.setId(subjects.getId());

        partialUpdatedSubjects.nameSubject(UPDATED_NAME_SUBJECT).grade(UPDATED_GRADE);

        restSubjectsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSubjects.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSubjects))
            )
            .andExpect(status().isOk());

        // Validate the Subjects in the database
        List<Subjects> subjectsList = subjectsRepository.findAll();
        assertThat(subjectsList).hasSize(databaseSizeBeforeUpdate);
        Subjects testSubjects = subjectsList.get(subjectsList.size() - 1);
        assertThat(testSubjects.getNameSubject()).isEqualTo(UPDATED_NAME_SUBJECT);
        assertThat(testSubjects.getGrade()).isEqualTo(UPDATED_GRADE);
    }

    @Test
    @Transactional
    void patchNonExistingSubjects() throws Exception {
        int databaseSizeBeforeUpdate = subjectsRepository.findAll().size();
        subjects.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSubjectsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, subjects.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(subjects))
            )
            .andExpect(status().isBadRequest());

        // Validate the Subjects in the database
        List<Subjects> subjectsList = subjectsRepository.findAll();
        assertThat(subjectsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSubjects() throws Exception {
        int databaseSizeBeforeUpdate = subjectsRepository.findAll().size();
        subjects.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubjectsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(subjects))
            )
            .andExpect(status().isBadRequest());

        // Validate the Subjects in the database
        List<Subjects> subjectsList = subjectsRepository.findAll();
        assertThat(subjectsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSubjects() throws Exception {
        int databaseSizeBeforeUpdate = subjectsRepository.findAll().size();
        subjects.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubjectsMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(subjects)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Subjects in the database
        List<Subjects> subjectsList = subjectsRepository.findAll();
        assertThat(subjectsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSubjects() throws Exception {
        // Initialize the database
        subjectsRepository.saveAndFlush(subjects);

        int databaseSizeBeforeDelete = subjectsRepository.findAll().size();

        // Delete the subjects
        restSubjectsMockMvc
            .perform(delete(ENTITY_API_URL_ID, subjects.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Subjects> subjectsList = subjectsRepository.findAll();
        assertThat(subjectsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
