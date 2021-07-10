package com.emu.rule_engine_ms.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.emu.rule_engine_ms.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StpMessageDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(StpMessageDTO.class);
        StpMessageDTO stpMessageDTO1 = new StpMessageDTO();
        stpMessageDTO1.setId(1L);
        StpMessageDTO stpMessageDTO2 = new StpMessageDTO();
        assertThat(stpMessageDTO1).isNotEqualTo(stpMessageDTO2);
        stpMessageDTO2.setId(stpMessageDTO1.getId());
        assertThat(stpMessageDTO1).isEqualTo(stpMessageDTO2);
        stpMessageDTO2.setId(2L);
        assertThat(stpMessageDTO1).isNotEqualTo(stpMessageDTO2);
        stpMessageDTO1.setId(null);
        assertThat(stpMessageDTO1).isNotEqualTo(stpMessageDTO2);
    }
}
