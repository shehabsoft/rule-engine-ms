package com.emu.rule_engine_ms.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OperationsLogsMapperTest {

    private OperationsLogsMapper operationsLogsMapper;

    @BeforeEach
    public void setUp() {
        operationsLogsMapper = new OperationsLogsMapperImpl();
    }
}
