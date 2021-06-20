package com.emu.rule_engine_ms.service.mapper;

import com.emu.rule_engine_ms.domain.*;
import com.emu.rule_engine_ms.service.dto.OperationsLogsDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link OperationsLogs} and its DTO {@link OperationsLogsDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface OperationsLogsMapper extends EntityMapper<OperationsLogsDTO, OperationsLogs> {}
