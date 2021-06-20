package com.emu.rule_engine_ms.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ExceptionLogsMapperTest {

    private ExceptionLogsMapper exceptionLogsMapper;

    @BeforeEach
    public void setUp() {
        exceptionLogsMapper = new ExceptionLogsMapperImpl();
    }
}
