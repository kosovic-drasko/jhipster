package jhipster.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import jhipster.domain.Dialog;
import jhipster.repository.DialogRepository;
import jhipster.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link jhipster.domain.Dialog}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class DialogResource {

    private final Logger log = LoggerFactory.getLogger(DialogResource.class);

    private static final String ENTITY_NAME = "dialog";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DialogRepository dialogRepository;

    public DialogResource(DialogRepository dialogRepository) {
        this.dialogRepository = dialogRepository;
    }

    /**
     * {@code POST  /dialogs} : Create a new dialog.
     *
     * @param dialog the dialog to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new dialog, or with status {@code 400 (Bad Request)} if the dialog has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/dialogs")
    public ResponseEntity<Dialog> createDialog(@RequestBody Dialog dialog) throws URISyntaxException {
        log.debug("REST request to save Dialog : {}", dialog);
        if (dialog.getId() != null) {
            throw new BadRequestAlertException("A new dialog cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Dialog result = dialogRepository.save(dialog);
        return ResponseEntity
            .created(new URI("/api/dialogs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /dialogs/:id} : Updates an existing dialog.
     *
     * @param id the id of the dialog to save.
     * @param dialog the dialog to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated dialog,
     * or with status {@code 400 (Bad Request)} if the dialog is not valid,
     * or with status {@code 500 (Internal Server Error)} if the dialog couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/dialogs/{id}")
    public ResponseEntity<Dialog> updateDialog(@PathVariable(value = "id", required = false) final Long id, @RequestBody Dialog dialog)
        throws URISyntaxException {
        log.debug("REST request to update Dialog : {}, {}", id, dialog);
        if (dialog.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, dialog.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!dialogRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Dialog result = dialogRepository.save(dialog);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, dialog.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /dialogs/:id} : Partial updates given fields of an existing dialog, field will ignore if it is null
     *
     * @param id the id of the dialog to save.
     * @param dialog the dialog to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated dialog,
     * or with status {@code 400 (Bad Request)} if the dialog is not valid,
     * or with status {@code 404 (Not Found)} if the dialog is not found,
     * or with status {@code 500 (Internal Server Error)} if the dialog couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/dialogs/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Dialog> partialUpdateDialog(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Dialog dialog
    ) throws URISyntaxException {
        log.debug("REST request to partial update Dialog partially : {}, {}", id, dialog);
        if (dialog.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, dialog.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!dialogRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Dialog> result = dialogRepository
            .findById(dialog.getId())
            .map(existingDialog -> {
                if (dialog.getName() != null) {
                    existingDialog.setName(dialog.getName());
                }

                return existingDialog;
            })
            .map(dialogRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, dialog.getId().toString())
        );
    }

    /**
     * {@code GET  /dialogs} : get all the dialogs.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of dialogs in body.
     */
    @GetMapping("/dialogs")
    public List<Dialog> getAllDialogs() {
        log.debug("REST request to get all Dialogs");
        return dialogRepository.findAll();
    }

    /**
     * {@code GET  /dialogs/:id} : get the "id" dialog.
     *
     * @param id the id of the dialog to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the dialog, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/dialogs/{id}")
    public ResponseEntity<Dialog> getDialog(@PathVariable Long id) {
        log.debug("REST request to get Dialog : {}", id);
        Optional<Dialog> dialog = dialogRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(dialog);
    }

    /**
     * {@code DELETE  /dialogs/:id} : delete the "id" dialog.
     *
     * @param id the id of the dialog to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/dialogs/{id}")
    public ResponseEntity<Void> deleteDialog(@PathVariable Long id) {
        log.debug("REST request to delete Dialog : {}", id);
        dialogRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
