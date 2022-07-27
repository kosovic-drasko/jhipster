package jhipster.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import jhipster.IntegrationTest;
import jhipster.domain.Subjects;
import jhipster.repository.SubjectsRepository;
import jhipster.service.criteria.SubjectsCriteria;
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

    private static final String DEFAULT_SUBJECT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_SUBJECT_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_NUMBER_SEMESTARS = 1;
    private static final Integer UPDATED_NUMBER_SEMESTARS = 2;
    private static final Integer SMALLER_NUMBER_SEMESTARS = 1 - 1;

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
        Subjects subjects = new Subjects().subjectName(DEFAULT_SUBJECT_NAME).numberSemestars(DEFAULT_NUMBER_SEMESTARS);
        return subjects;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Subjects createUpdatedEntity(EntityManager em) {
        Subjects subjects = new Subjects().subjectName(UPDATED_SUBJECT_NAME).numberSemestars(UPDATED_NUMBER_SEMESTARS);
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
        assertThat(testSubjects.getSubjectName()).isEqualTo(DEFAULT_SUBJECT_NAME);
        assertThat(testSubjects.getNumberSemestars()).isEqualTo(DEFAULT_NUMBER_SEMESTARS);
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
    void checkSubjectNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = subjectsRepository.findAll().size();
        // set the field null
        subjects.setSubjectName(null);

        // Create the Subjects, which fails.

        restSubjectsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(subjects)))
            .andExpect(status().isBadRequest());

        List<Subjects> subjectsList = subjectsRepository.findAll();
        assertThat(subjectsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNumberSemestarsIsRequired() throws Exception {
        int databaseSizeBeforeTest = subjectsRepository.findAll().size();
        // set the field null
        subjects.setNumberSemestars(null);

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
            .andExpect(jsonPath("$.[*].subjectName").value(hasItem(DEFAULT_SUBJECT_NAME)))
            .andExpect(jsonPath("$.[*].numberSemestars").value(hasItem(DEFAULT_NUMBER_SEMESTARS)));
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
            .andExpect(jsonPath("$.subjectName").value(DEFAULT_SUBJECT_NAME))
            .andExpect(jsonPath("$.numberSemestars").value(DEFAULT_NUMBER_SEMESTARS));
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
    void getAllSubjectsBySubjectNameIsEqualToSomething() throws Exception {
        // Initialize the database
        subjectsRepository.saveAndFlush(subjects);

        // Get all the subjectsList where subjectName equals to DEFAULT_SUBJECT_NAME
        defaultSubjectsShouldBeFound("subjectName.equals=" + DEFAULT_SUBJECT_NAME);

        // Get all the subjectsList where subjectName equals to UPDATED_SUBJECT_NAME
        defaultSubjectsShouldNotBeFound("subjectName.equals=" + UPDATED_SUBJECT_NAME);
    }

    @Test
    @Transactional
    void getAllSubjectsBySubjectNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        subjectsRepository.saveAndFlush(subjects);

        // Get all the subjectsList where subjectName not equals to DEFAULT_SUBJECT_NAME
        defaultSubjectsShouldNotBeFound("subjectName.notEquals=" + DEFAULT_SUBJECT_NAME);

        // Get all the subjectsList where subjectName not equals to UPDATED_SUBJECT_NAME
        defaultSubjectsShouldBeFound("subjectName.notEquals=" + UPDATED_SUBJECT_NAME);
    }

    @Test
    @Transactional
    void getAllSubjectsBySubjectNameIsInShouldWork() throws Exception {
        // Initialize the database
        subjectsRepository.saveAndFlush(subjects);

        // Get all the subjectsList where subjectName in DEFAULT_SUBJECT_NAME or UPDATED_SUBJECT_NAME
        defaultSubjectsShouldBeFound("subjectName.in=" + DEFAULT_SUBJECT_NAME + "," + UPDATED_SUBJECT_NAME);

        // Get all the subjectsList where subjectName equals to UPDATED_SUBJECT_NAME
        defaultSubjectsShouldNotBeFound("subjectName.in=" + UPDATED_SUBJECT_NAME);
    }

    @Test
    @Transactional
    void getAllSubjectsBySubjectNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        subjectsRepository.saveAndFlush(subjects);

        // Get all the subjectsList where subjectName is not null
        defaultSubjectsShouldBeFound("subjectName.specified=true");

        // Get all the subjectsList where subjectName is null
        defaultSubjectsShouldNotBeFound("subjectName.specified=false");
    }

    @Test
    @Transactional
    void getAllSubjectsBySubjectNameContainsSomething() throws Exception {
        // Initialize the database
        subjectsRepository.saveAndFlush(subjects);

        // Get all the subjectsList where subjectName contains DEFAULT_SUBJECT_NAME
        defaultSubjectsShouldBeFound("subjectName.contains=" + DEFAULT_SUBJECT_NAME);

        // Get all the subjectsList where subjectName contains UPDATED_SUBJECT_NAME
        defaultSubjectsShouldNotBeFound("subjectName.contains=" + UPDATED_SUBJECT_NAME);
    }

    @Test
    @Transactional
    void getAllSubjectsBySubjectNameNotContainsSomething() throws Exception {
        // Initialize the database
        subjectsRepository.saveAndFlush(subjects);

        // Get all the subjectsList where subjectName does not contain DEFAULT_SUBJECT_NAME
        defaultSubjectsShouldNotBeFound("subjectName.doesNotContain=" + DEFAULT_SUBJECT_NAME);

        // Get all the subjectsList where subjectName does not contain UPDATED_SUBJECT_NAME
        defaultSubjectsShouldBeFound("subjectName.doesNotContain=" + UPDATED_SUBJECT_NAME);
    }

    @Test
    @Transactional
    void getAllSubjectsByNumberSemestarsIsEqualToSomething() throws Exception {
        // Initialize the database
        subjectsRepository.saveAndFlush(subjects);

        // Get all the subjectsList where numberSemestars equals to DEFAULT_NUMBER_SEMESTARS
        defaultSubjectsShouldBeFound("numberSemestars.equals=" + DEFAULT_NUMBER_SEMESTARS);

        // Get all the subjectsList where numberSemestars equals to UPDATED_NUMBER_SEMESTARS
        defaultSubjectsShouldNotBeFound("numberSemestars.equals=" + UPDATED_NUMBER_SEMESTARS);
    }

    @Test
    @Transactional
    void getAllSubjectsByNumberSemestarsIsNotEqualToSomething() throws Exception {
        // Initialize the database
        subjectsRepository.saveAndFlush(subjects);

        // Get all the subjectsList where numberSemestars not equals to DEFAULT_NUMBER_SEMESTARS
        defaultSubjectsShouldNotBeFound("numberSemestars.notEquals=" + DEFAULT_NUMBER_SEMESTARS);

        // Get all the subjectsList where numberSemestars not equals to UPDATED_NUMBER_SEMESTARS
        defaultSubjectsShouldBeFound("numberSemestars.notEquals=" + UPDATED_NUMBER_SEMESTARS);
    }

    @Test
    @Transactional
    void getAllSubjectsByNumberSemestarsIsInShouldWork() throws Exception {
        // Initialize the database
        subjectsRepository.saveAndFlush(subjects);

        // Get all the subjectsList where numberSemestars in DEFAULT_NUMBER_SEMESTARS or UPDATED_NUMBER_SEMESTARS
        defaultSubjectsShouldBeFound("numberSemestars.in=" + DEFAULT_NUMBER_SEMESTARS + "," + UPDATED_NUMBER_SEMESTARS);

        // Get all the subjectsList where numberSemestars equals to UPDATED_NUMBER_SEMESTARS
        defaultSubjectsShouldNotBeFound("numberSemestars.in=" + UPDATED_NUMBER_SEMESTARS);
    }

    @Test
    @Transactional
    void getAllSubjectsByNumberSemestarsIsNullOrNotNull() throws Exception {
        // Initialize the database
        subjectsRepository.saveAndFlush(subjects);

        // Get all the subjectsList where numberSemestars is not null
        defaultSubjectsShouldBeFound("numberSemestars.specified=true");

        // Get all the subjectsList where numberSemestars is null
        defaultSubjectsShouldNotBeFound("numberSemestars.specified=false");
    }

    @Test
    @Transactional
    void getAllSubjectsByNumberSemestarsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        subjectsRepository.saveAndFlush(subjects);

        // Get all the subjectsList where numberSemestars is greater than or equal to DEFAULT_NUMBER_SEMESTARS
        defaultSubjectsShouldBeFound("numberSemestars.greaterThanOrEqual=" + DEFAULT_NUMBER_SEMESTARS);

        // Get all the subjectsList where numberSemestars is greater than or equal to UPDATED_NUMBER_SEMESTARS
        defaultSubjectsShouldNotBeFound("numberSemestars.greaterThanOrEqual=" + UPDATED_NUMBER_SEMESTARS);
    }

    @Test
    @Transactional
    void getAllSubjectsByNumberSemestarsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        subjectsRepository.saveAndFlush(subjects);

        // Get all the subjectsList where numberSemestars is less than or equal to DEFAULT_NUMBER_SEMESTARS
        defaultSubjectsShouldBeFound("numberSemestars.lessThanOrEqual=" + DEFAULT_NUMBER_SEMESTARS);

        // Get all the subjectsList where numberSemestars is less than or equal to SMALLER_NUMBER_SEMESTARS
        defaultSubjectsShouldNotBeFound("numberSemestars.lessThanOrEqual=" + SMALLER_NUMBER_SEMESTARS);
    }

    @Test
    @Transactional
    void getAllSubjectsByNumberSemestarsIsLessThanSomething() throws Exception {
        // Initialize the database
        subjectsRepository.saveAndFlush(subjects);

        // Get all the subjectsList where numberSemestars is less than DEFAULT_NUMBER_SEMESTARS
        defaultSubjectsShouldNotBeFound("numberSemestars.lessThan=" + DEFAULT_NUMBER_SEMESTARS);

        // Get all the subjectsList where numberSemestars is less than UPDATED_NUMBER_SEMESTARS
        defaultSubjectsShouldBeFound("numberSemestars.lessThan=" + UPDATED_NUMBER_SEMESTARS);
    }

    @Test
    @Transactional
    void getAllSubjectsByNumberSemestarsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        subjectsRepository.saveAndFlush(subjects);

        // Get all the subjectsList where numberSemestars is greater than DEFAULT_NUMBER_SEMESTARS
        defaultSubjectsShouldNotBeFound("numberSemestars.greaterThan=" + DEFAULT_NUMBER_SEMESTARS);

        // Get all the subjectsList where numberSemestars is greater than SMALLER_NUMBER_SEMESTARS
        defaultSubjectsShouldBeFound("numberSemestars.greaterThan=" + SMALLER_NUMBER_SEMESTARS);
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
            .andExpect(jsonPath("$.[*].subjectName").value(hasItem(DEFAULT_SUBJECT_NAME)))
            .andExpect(jsonPath("$.[*].numberSemestars").value(hasItem(DEFAULT_NUMBER_SEMESTARS)));

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
        updatedSubjects.subjectName(UPDATED_SUBJECT_NAME).numberSemestars(UPDATED_NUMBER_SEMESTARS);

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
        assertThat(testSubjects.getSubjectName()).isEqualTo(UPDATED_SUBJECT_NAME);
        assertThat(testSubjects.getNumberSemestars()).isEqualTo(UPDATED_NUMBER_SEMESTARS);
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

        partialUpdatedSubjects.subjectName(UPDATED_SUBJECT_NAME).numberSemestars(UPDATED_NUMBER_SEMESTARS);

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
        assertThat(testSubjects.getSubjectName()).isEqualTo(UPDATED_SUBJECT_NAME);
        assertThat(testSubjects.getNumberSemestars()).isEqualTo(UPDATED_NUMBER_SEMESTARS);
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

        partialUpdatedSubjects.subjectName(UPDATED_SUBJECT_NAME).numberSemestars(UPDATED_NUMBER_SEMESTARS);

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
        assertThat(testSubjects.getSubjectName()).isEqualTo(UPDATED_SUBJECT_NAME);
        assertThat(testSubjects.getNumberSemestars()).isEqualTo(UPDATED_NUMBER_SEMESTARS);
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
