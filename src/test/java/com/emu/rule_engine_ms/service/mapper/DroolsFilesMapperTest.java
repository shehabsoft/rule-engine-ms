package com.emu.rule_engine_ms.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DroolsFilesMapperTest {

    private DroolsFilesMapper droolsFilesMapper;

    @BeforeEach
    public void setUp() {
        droolsFilesMapper = new DroolsFilesMapperImpl();
    }
}
