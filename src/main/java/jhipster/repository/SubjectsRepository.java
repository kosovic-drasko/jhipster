package jhipster.repository;

import jhipster.domain.Subjects;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Subjects entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SubjectsRepository extends JpaRepository<Subjects, Long> {}
