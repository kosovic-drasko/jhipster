package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SubjectsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Subjects.class);
        Subjects subjects1 = new Subjects();
        subjects1.setId(1L);
        Subjects subjects2 = new Subjects();
        subjects2.setId(subjects1.getId());
        assertThat(subjects1).isEqualTo(subjects2);
        subjects2.setId(2L);
        assertThat(subjects1).isNotEqualTo(subjects2);
        subjects1.setId(null);
        assertThat(subjects1).isNotEqualTo(subjects2);
    }
}
