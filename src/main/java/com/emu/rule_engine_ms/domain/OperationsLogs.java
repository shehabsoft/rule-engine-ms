package com.emu.rule_engine_ms.domain;

import java.io.Serializable;
import javax.persistence.*;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * A OperationsLogs.
 */
@Entity
@Table(name = "operations_logs")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "operationslogs")
public class OperationsLogs extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "operations_logs_seq")
    @SequenceGenerator(name = "operations_logs_seq",allocationSize = 1)
    private Long id;

    @Column(name = "operation_name")
    private String operationName;

    @Column(name = "log_details")
    private String logDetails;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OperationsLogs id(Long id) {
        this.id = id;
        return this;
    }

    public String getOperationName() {
        return this.operationName;
    }

    public OperationsLogs operationName(String operationName) {
        this.operationName = operationName;
        return this;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public String getLogDetails() {
        return this.logDetails;
    }

    public OperationsLogs logDetails(String logDetails) {
        this.logDetails = logDetails;
        return this;
    }

    public void setLogDetails(String logDetails) {
        this.logDetails = logDetails;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OperationsLogs)) {
            return false;
        }
        return id != null && id.equals(((OperationsLogs) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OperationsLogs{" +
            "id=" + getId() +
            ", operationName='" + getOperationName() + "'" +
            ", logDetails='" + getLogDetails() + "'" +
            "}";
    }
}
