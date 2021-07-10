package com.emu.rule_engine_ms.service.mapper;

import com.emu.rule_engine_ms.domain.*;
import com.emu.rule_engine_ms.service.dto.StpMessageDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link StpMessage} and its DTO {@link StpMessageDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface StpMessageMapper extends EntityMapper<StpMessageDTO, StpMessage> {}
