package com.emu.rule_engine_ms.domain;

import java.io.Serializable;
import javax.persistence.*;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * A ExceptionLogs.
 */
@Entity
@Table(name = "exception_logs")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "exceptionlogs")
public class ExceptionLogs extends  AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "exception_logs_seq")
    @SequenceGenerator(name = "exception_logs_seq",allocationSize = 1)
    private Long id;

    @Column(name = "exception_message")
    private String exceptionMessage;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ExceptionLogs id(Long id) {
        this.id = id;
        return this;
    }

    public String getExceptionMessage() {
        return this.exceptionMessage;
    }

    public ExceptionLogs exceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
        return this;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExceptionLogs)) {
            return false;
        }
        return id != null && id.equals(((ExceptionLogs) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ExceptionLogs{" +
            "id=" + getId() +
            ", exceptionMessage='" + getExceptionMessage() + "'" +
            "}";
    }
}
