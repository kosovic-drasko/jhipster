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
import jhipster.domain.Dialog;
import jhipster.repository.DialogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link DialogResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DialogResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/dialogs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private DialogRepository dialogRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDialogMockMvc;

    private Dialog dialog;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Dialog createEntity(EntityManager em) {
        Dialog dialog = new Dialog().name(DEFAULT_NAME);
        return dialog;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Dialog createUpdatedEntity(EntityManager em) {
        Dialog dialog = new Dialog().name(UPDATED_NAME);
        return dialog;
    }

    @BeforeEach
    public void initTest() {
        dialog = createEntity(em);
    }

    @Test
    @Transactional
    void createDialog() throws Exception {
        int databaseSizeBeforeCreate = dialogRepository.findAll().size();
        // Create the Dialog
        restDialogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(dialog)))
            .andExpect(status().isCreated());

        // Validate the Dialog in the database
        List<Dialog> dialogList = dialogRepository.findAll();
        assertThat(dialogList).hasSize(databaseSizeBeforeCreate + 1);
        Dialog testDialog = dialogList.get(dialogList.size() - 1);
        assertThat(testDialog.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    void createDialogWithExistingId() throws Exception {
        // Create the Dialog with an existing ID
        dialog.setId(1L);

        int databaseSizeBeforeCreate = dialogRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDialogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(dialog)))
            .andExpect(status().isBadRequest());

        // Validate the Dialog in the database
        List<Dialog> dialogList = dialogRepository.findAll();
        assertThat(dialogList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllDialogs() throws Exception {
        // Initialize the database
        dialogRepository.saveAndFlush(dialog);

        // Get all the dialogList
        restDialogMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dialog.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    @Transactional
    void getDialog() throws Exception {
        // Initialize the database
        dialogRepository.saveAndFlush(dialog);

        // Get the dialog
        restDialogMockMvc
            .perform(get(ENTITY_API_URL_ID, dialog.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(dialog.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @Transactional
    void getNonExistingDialog() throws Exception {
        // Get the dialog
        restDialogMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewDialog() throws Exception {
        // Initialize the database
        dialogRepository.saveAndFlush(dialog);

        int databaseSizeBeforeUpdate = dialogRepository.findAll().size();

        // Update the dialog
        Dialog updatedDialog = dialogRepository.findById(dialog.getId()).get();
        // Disconnect from session so that the updates on updatedDialog are not directly saved in db
        em.detach(updatedDialog);
        updatedDialog.name(UPDATED_NAME);

        restDialogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedDialog.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedDialog))
            )
            .andExpect(status().isOk());

        // Validate the Dialog in the database
        List<Dialog> dialogList = dialogRepository.findAll();
        assertThat(dialogList).hasSize(databaseSizeBeforeUpdate);
        Dialog testDialog = dialogList.get(dialogList.size() - 1);
        assertThat(testDialog.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void putNonExistingDialog() throws Exception {
        int databaseSizeBeforeUpdate = dialogRepository.findAll().size();
        dialog.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDialogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, dialog.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(dialog))
            )
            .andExpect(status().isBadRequest());

        // Validate the Dialog in the database
        List<Dialog> dialogList = dialogRepository.findAll();
        assertThat(dialogList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDialog() throws Exception {
        int databaseSizeBeforeUpdate = dialogRepository.findAll().size();
        dialog.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDialogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(dialog))
            )
            .andExpect(status().isBadRequest());

        // Validate the Dialog in the database
        List<Dialog> dialogList = dialogRepository.findAll();
        assertThat(dialogList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDialog() throws Exception {
        int databaseSizeBeforeUpdate = dialogRepository.findAll().size();
        dialog.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDialogMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(dialog)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Dialog in the database
        List<Dialog> dialogList = dialogRepository.findAll();
        assertThat(dialogList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDialogWithPatch() throws Exception {
        // Initialize the database
        dialogRepository.saveAndFlush(dialog);

        int databaseSizeBeforeUpdate = dialogRepository.findAll().size();

        // Update the dialog using partial update
        Dialog partialUpdatedDialog = new Dialog();
        partialUpdatedDialog.setId(dialog.getId());

        restDialogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDialog.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDialog))
            )
            .andExpect(status().isOk());

        // Validate the Dialog in the database
        List<Dialog> dialogList = dialogRepository.findAll();
        assertThat(dialogList).hasSize(databaseSizeBeforeUpdate);
        Dialog testDialog = dialogList.get(dialogList.size() - 1);
        assertThat(testDialog.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    void fullUpdateDialogWithPatch() throws Exception {
        // Initialize the database
        dialogRepository.saveAndFlush(dialog);

        int databaseSizeBeforeUpdate = dialogRepository.findAll().size();

        // Update the dialog using partial update
        Dialog partialUpdatedDialog = new Dialog();
        partialUpdatedDialog.setId(dialog.getId());

        partialUpdatedDialog.name(UPDATED_NAME);

        restDialogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDialog.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDialog))
            )
            .andExpect(status().isOk());

        // Validate the Dialog in the database
        List<Dialog> dialogList = dialogRepository.findAll();
        assertThat(dialogList).hasSize(databaseSizeBeforeUpdate);
        Dialog testDialog = dialogList.get(dialogList.size() - 1);
        assertThat(testDialog.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingDialog() throws Exception {
        int databaseSizeBeforeUpdate = dialogRepository.findAll().size();
        dialog.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDialogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, dialog.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(dialog))
            )
            .andExpect(status().isBadRequest());

        // Validate the Dialog in the database
        List<Dialog> dialogList = dialogRepository.findAll();
        assertThat(dialogList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDialog() throws Exception {
        int databaseSizeBeforeUpdate = dialogRepository.findAll().size();
        dialog.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDialogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(dialog))
            )
            .andExpect(status().isBadRequest());

        // Validate the Dialog in the database
        List<Dialog> dialogList = dialogRepository.findAll();
        assertThat(dialogList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDialog() throws Exception {
        int databaseSizeBeforeUpdate = dialogRepository.findAll().size();
        dialog.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDialogMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(dialog)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Dialog in the database
        List<Dialog> dialogList = dialogRepository.findAll();
        assertThat(dialogList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDialog() throws Exception {
        // Initialize the database
        dialogRepository.saveAndFlush(dialog);

        int databaseSizeBeforeDelete = dialogRepository.findAll().size();

        // Delete the dialog
        restDialogMockMvc
            .perform(delete(ENTITY_API_URL_ID, dialog.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Dialog> dialogList = dialogRepository.findAll();
        assertThat(dialogList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
