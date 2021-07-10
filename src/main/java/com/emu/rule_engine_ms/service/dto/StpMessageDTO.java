package com.emu.rule_engine_ms.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.emu.rule_engine_ms.domain.StpMessage} entity.
 */
public class StpMessageDTO implements Serializable {

    private Long id;

    private String key;

    private String descAr;

    private String descEn;

    private String messageAr;

    private String messageEn;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDescAr() {
        return descAr;
    }

    public void setDescAr(String descAr) {
        this.descAr = descAr;
    }

    public String getDescEn() {
        return descEn;
    }

    public void setDescEn(String descEn) {
        this.descEn = descEn;
    }

    public String getMessageAr() {
        return messageAr;
    }

    public void setMessageAr(String messageAr) {
        this.messageAr = messageAr;
    }

    public String getMessageEn() {
        return messageEn;
    }

    public void setMessageEn(String messageEn) {
        this.messageEn = messageEn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StpMessageDTO)) {
            return false;
        }

        StpMessageDTO stpMessageDTO = (StpMessageDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, stpMessageDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StpMessageDTO{" +
            "id=" + getId() +
            ", key='" + getKey() + "'" +
            ", descAr='" + getDescAr() + "'" +
            ", descEn='" + getDescEn() + "'" +
            ", messageAr='" + getMessageAr() + "'" +
            ", messageEn='" + getMessageEn() + "'" +
            "}";
    }
}
