package com.emu.rule_engine_ms.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.emu.rule_engine_ms.domain.OperationsLogs} entity.
 */
public class OperationsLogsDTO implements Serializable {

    private Long id;

    private String operationName;

    private String logDetails;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public String getLogDetails() {
        return logDetails;
    }

    public void setLogDetails(String logDetails) {
        this.logDetails = logDetails;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OperationsLogsDTO)) {
            return false;
        }

        OperationsLogsDTO operationsLogsDTO = (OperationsLogsDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, operationsLogsDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OperationsLogsDTO{" +
            "id=" + getId() +
            ", operationName='" + getOperationName() + "'" +
            ", logDetails='" + getLogDetails() + "'" +
            "}";
    }
}
