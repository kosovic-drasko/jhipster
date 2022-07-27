package jhipster.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link jhipster.domain.Subjects} entity. This class is used
 * in {@link jhipster.web.rest.SubjectsResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /subjects?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class SubjectsCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter subjectName;

    private IntegerFilter numberSemestars;

    private Boolean distinct;

    public SubjectsCriteria() {}

    public SubjectsCriteria(SubjectsCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.subjectName = other.subjectName == null ? null : other.subjectName.copy();
        this.numberSemestars = other.numberSemestars == null ? null : other.numberSemestars.copy();
        this.distinct = other.distinct;
    }

    @Override
    public SubjectsCriteria copy() {
        return new SubjectsCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getSubjectName() {
        return subjectName;
    }

    public StringFilter subjectName() {
        if (subjectName == null) {
            subjectName = new StringFilter();
        }
        return subjectName;
    }

    public void setSubjectName(StringFilter subjectName) {
        this.subjectName = subjectName;
    }

    public IntegerFilter getNumberSemestars() {
        return numberSemestars;
    }

    public IntegerFilter numberSemestars() {
        if (numberSemestars == null) {
            numberSemestars = new IntegerFilter();
        }
        return numberSemestars;
    }

    public void setNumberSemestars(IntegerFilter numberSemestars) {
        this.numberSemestars = numberSemestars;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SubjectsCriteria that = (SubjectsCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(subjectName, that.subjectName) &&
            Objects.equals(numberSemestars, that.numberSemestars) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, subjectName, numberSemestars, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SubjectsCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (subjectName != null ? "subjectName=" + subjectName + ", " : "") +
            (numberSemestars != null ? "numberSemestars=" + numberSemestars + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
