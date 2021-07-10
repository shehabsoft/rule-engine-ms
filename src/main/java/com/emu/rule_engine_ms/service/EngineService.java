package com.emu.rule_engine_ms.service;

import com.emu.rule_engine_ms.domain.DroolsFiles;
import com.emu.rule_engine_ms.domain.enumeration.FileValidationType;


public interface EngineService {

    DroolsFiles getMatchedDrl(String simpleClassName, FileValidationType fileValidationType) throws Exception;

    Object wrapToSpecificType(Object request, String simpleClassName);

    void syncIndicesWithDB();
}
