package com.emu.rule_engine_ms.domain;


import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;

import com.emu.rule_engine_ms.domain.enumeration.FileValidationType;

/**
 * A DroolsFiles.
 */
@Entity
@Table(name = "drools_files")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "droolsfiles")
public class DroolsFiles extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "drools_files_seq")
    @SequenceGenerator(name = "drools_files_seq",allocationSize = 1)
    private Long id;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_type")
    private String fileType;

    @Lob
    @Column(name = "file_content")
    private byte[] fileContent;

    @Column(name = "file_content_content_type")
    private String fileContentContentType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "file_validation_type", nullable = false)
    private FileValidationType fileValidationType;

    @Column(name = "simple_class_name")
    private String simpleClassName;

    @Column(name = "full_class_name")
    private String fullClassName;

    @Column(name = "status")
    private Integer status;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public DroolsFiles fileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public DroolsFiles fileType(String fileType) {
        this.fileType = fileType;
        return this;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public byte[] getFileContent() {
        return fileContent;
    }

    public DroolsFiles fileContent(byte[] fileContent) {
        this.fileContent = fileContent;
        return this;
    }

    public void setFileContent(byte[] fileContent) {
        this.fileContent = fileContent;
    }

    public String getFileContentContentType() {
        return fileContentContentType;
    }

    public DroolsFiles fileContentContentType(String fileContentContentType) {
        this.fileContentContentType = fileContentContentType;
        return this;
    }

    public void setFileContentContentType(String fileContentContentType) {
        this.fileContentContentType = fileContentContentType;
    }

    public FileValidationType getFileValidationType() {
        return fileValidationType;
    }

    public DroolsFiles fileValidationType(FileValidationType fileValidationType) {
        this.fileValidationType = fileValidationType;
        return this;
    }

    public void setFileValidationType(FileValidationType fileValidationType) {
        this.fileValidationType = fileValidationType;
    }

    public String getSimpleClassName() {
        return simpleClassName;
    }

    public DroolsFiles simpleClassName(String simpleClassName) {
        this.simpleClassName = simpleClassName;
        return this;
    }

    public void setSimpleClassName(String simpleClassName) {
        this.simpleClassName = simpleClassName;
    }

    public String getFullClassName() {
        return fullClassName;
    }

    public DroolsFiles fullClassName(String fullClassName) {
        this.fullClassName = fullClassName;
        return this;
    }

    public void setFullClassName(String fullClassName) {
        this.fullClassName = fullClassName;
    }

    public Integer getStatus() {
        return status;
    }

    public DroolsFiles status(Integer status) {
        this.status = status;
        return this;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DroolsFiles)) {
            return false;
        }
        return id != null && id.equals(((DroolsFiles) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DroolsFiles{" +
            "id=" + getId() +
            ", fileName='" + getFileName() + "'" +
            ", fileType='" + getFileType() + "'" +
            ", fileContent='" + getFileContent() + "'" +
            ", fileContentContentType='" + getFileContentContentType() + "'" +
            ", fileValidationType='" + getFileValidationType() + "'" +
            ", simpleClassName='" + getSimpleClassName() + "'" +
            ", fullClassName='" + getFullClassName() + "'" +
            ", status=" + getStatus() +
            "}";
    }
}
