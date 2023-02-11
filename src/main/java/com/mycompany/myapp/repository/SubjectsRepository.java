package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Subjects;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Subjects entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SubjectsRepository extends JpaRepository<Subjects, Long>, JpaSpecificationExecutor<Subjects> {}
