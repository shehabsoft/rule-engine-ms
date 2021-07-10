package com.emu.rule_engine_ms.service.mapper;

import com.emu.rule_engine_ms.domain.*;
import com.emu.rule_engine_ms.service.dto.DroolsFilesDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link DroolsFiles} and its DTO {@link DroolsFilesDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface DroolsFilesMapper extends EntityMapper<DroolsFilesDTO, DroolsFiles> {}
