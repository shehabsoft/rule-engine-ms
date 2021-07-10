package com.emu.rule_engine_ms.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.emu.rule_engine_ms.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ExceptionLogsDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ExceptionLogsDTO.class);
        ExceptionLogsDTO exceptionLogsDTO1 = new ExceptionLogsDTO();
        exceptionLogsDTO1.setId(1L);
        ExceptionLogsDTO exceptionLogsDTO2 = new ExceptionLogsDTO();
        assertThat(exceptionLogsDTO1).isNotEqualTo(exceptionLogsDTO2);
        exceptionLogsDTO2.setId(exceptionLogsDTO1.getId());
        assertThat(exceptionLogsDTO1).isEqualTo(exceptionLogsDTO2);
        exceptionLogsDTO2.setId(2L);
        assertThat(exceptionLogsDTO1).isNotEqualTo(exceptionLogsDTO2);
        exceptionLogsDTO1.setId(null);
        assertThat(exceptionLogsDTO1).isNotEqualTo(exceptionLogsDTO2);
    }
}
