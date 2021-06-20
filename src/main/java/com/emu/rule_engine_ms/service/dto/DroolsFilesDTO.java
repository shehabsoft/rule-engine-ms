package com.emu.rule_engine_ms.service.dto;

import javax.validation.constraints.*;
import java.io.Serializable;
import javax.persistence.Lob;
import com.emu.rule_engine_ms.domain.enumeration.FileValidationType;

/**
 * A DTO for the {@link com.emu.rule_engine_ms.domain.DroolsFiles} entity.
 */
public class DroolsFilesDTO implements Serializable {
    
    private Long id;

    private String fileName;

    private String fileType;

    @Lob
    private byte[] fileContent;

    private String fileContentContentType;
    @NotNull
    private FileValidationType fileValidationType;

    private String simpleClassName;

    private String fullClassName;

    private Integer status;

    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public byte[] getFileContent() {
        return fileContent;
    }

    public void setFileContent(byte[] fileContent) {
        this.fileContent = fileContent;
    }

    public String getFileContentContentType() {
        return fileContentContentType;
    }

    public void setFileContentContentType(String fileContentContentType) {
        this.fileContentContentType = fileContentContentType;
    }

    public FileValidationType getFileValidationType() {
        return fileValidationType;
    }

    public void setFileValidationType(FileValidationType fileValidationType) {
        this.fileValidationType = fileValidationType;
    }

    public String getSimpleClassName() {
        return simpleClassName;
    }

    public void setSimpleClassName(String simpleClassName) {
        this.simpleClassName = simpleClassName;
    }

    public String getFullClassName() {
        return fullClassName;
    }

    public void setFullClassName(String fullClassName) {
        this.fullClassName = fullClassName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DroolsFilesDTO)) {
            return false;
        }

        return id != null && id.equals(((DroolsFilesDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DroolsFilesDTO{" +
            "id=" + getId() +
            ", fileName='" + getFileName() + "'" +
            ", fileType='" + getFileType() + "'" +
            ", fileContent='" + getFileContent() + "'" +
            ", fileValidationType='" + getFileValidationType() + "'" +
            ", simpleClassName='" + getSimpleClassName() + "'" +
            ", fullClassName='" + getFullClassName() + "'" +
            ", status=" + getStatus() +
            "}";
    }
}
