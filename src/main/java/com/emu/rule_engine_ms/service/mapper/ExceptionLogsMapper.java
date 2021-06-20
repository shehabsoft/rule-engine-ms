package com.emu.rule_engine_ms.service.mapper;

import com.emu.rule_engine_ms.domain.*;
import com.emu.rule_engine_ms.service.dto.ExceptionLogsDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ExceptionLogs} and its DTO {@link ExceptionLogsDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ExceptionLogsMapper extends EntityMapper<ExceptionLogsDTO, ExceptionLogs> {}
