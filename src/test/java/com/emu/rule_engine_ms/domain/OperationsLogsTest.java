package com.emu.rule_engine_ms.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.emu.rule_engine_ms.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OperationsLogsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OperationsLogs.class);
        OperationsLogs operationsLogs1 = new OperationsLogs();
        operationsLogs1.setId(1L);
        OperationsLogs operationsLogs2 = new OperationsLogs();
        operationsLogs2.setId(operationsLogs1.getId());
        assertThat(operationsLogs1).isEqualTo(operationsLogs2);
        operationsLogs2.setId(2L);
        assertThat(operationsLogs1).isNotEqualTo(operationsLogs2);
        operationsLogs1.setId(null);
        assertThat(operationsLogs1).isNotEqualTo(operationsLogs2);
    }
}
