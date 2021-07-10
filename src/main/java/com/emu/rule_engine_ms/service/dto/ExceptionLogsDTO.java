package com.emu.rule_engine_ms.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.emu.rule_engine_ms.domain.ExceptionLogs} entity.
 */
public class ExceptionLogsDTO implements Serializable {

    private Long id;

    private String exceptionMessage;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExceptionLogsDTO)) {
            return false;
        }

        ExceptionLogsDTO exceptionLogsDTO = (ExceptionLogsDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, exceptionLogsDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ExceptionLogsDTO{" +
            "id=" + getId() +
            ", exceptionMessage='" + getExceptionMessage() + "'" +
            "}";
    }
}
