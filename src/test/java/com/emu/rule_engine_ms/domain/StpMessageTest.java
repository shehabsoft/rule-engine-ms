package com.emu.rule_engine_ms.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.emu.rule_engine_ms.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StpMessageTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(StpMessage.class);
        StpMessage stpMessage1 = new StpMessage();
        stpMessage1.setId(1L);
        StpMessage stpMessage2 = new StpMessage();
        stpMessage2.setId(stpMessage1.getId());
        assertThat(stpMessage1).isEqualTo(stpMessage2);
        stpMessage2.setId(2L);
        assertThat(stpMessage1).isNotEqualTo(stpMessage2);
        stpMessage1.setId(null);
        assertThat(stpMessage1).isNotEqualTo(stpMessage2);
    }
}
