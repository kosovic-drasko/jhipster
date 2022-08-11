package jhipster.domain;

import static org.assertj.core.api.Assertions.assertThat;

import jhipster.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DialogTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Dialog.class);
        Dialog dialog1 = new Dialog();
        dialog1.setId(1L);
        Dialog dialog2 = new Dialog();
        dialog2.setId(dialog1.getId());
        assertThat(dialog1).isEqualTo(dialog2);
        dialog2.setId(2L);
        assertThat(dialog1).isNotEqualTo(dialog2);
        dialog1.setId(null);
        assertThat(dialog1).isNotEqualTo(dialog2);
    }
}
