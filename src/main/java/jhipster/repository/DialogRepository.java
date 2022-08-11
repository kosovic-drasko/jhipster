package jhipster.repository;

import jhipster.domain.Dialog;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Dialog entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DialogRepository extends JpaRepository<Dialog, Long> {}
