package com.mycompany.myapp.domain;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Subjects.
 */
@Entity
@Table(name = "subjects")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Subjects implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name_subject", nullable = false)
    private String nameSubject;

    @Column(name = "grade")
    private Integer grade;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Subjects id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNameSubject() {
        return this.nameSubject;
    }

    public Subjects nameSubject(String nameSubject) {
        this.setNameSubject(nameSubject);
        return this;
    }

    public void setNameSubject(String nameSubject) {
        this.nameSubject = nameSubject;
    }

    public Integer getGrade() {
        return this.grade;
    }

    public Subjects grade(Integer grade) {
        this.setGrade(grade);
        return this;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Subjects)) {
            return false;
        }
        return id != null && id.equals(((Subjects) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Subjects{" +
            "id=" + getId() +
            ", nameSubject='" + getNameSubject() + "'" +
            ", grade=" + getGrade() +
            "}";
    }
}
