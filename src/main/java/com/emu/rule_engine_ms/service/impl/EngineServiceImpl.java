package com.emu.rule_engine_ms.service.impl;

import com.emu.rule_engine_ms.domain.DroolsFiles;
import com.emu.rule_engine_ms.domain.FILE_STATUS;
import com.emu.rule_engine_ms.domain.StpMessage;
import com.emu.rule_engine_ms.domain.enumeration.FileValidationType;
import com.emu.rule_engine_ms.repository.DroolsFilesRepository;
import com.emu.rule_engine_ms.repository.StpMessageRepository;
import com.emu.rule_engine_ms.repository.search.DroolsFilesSearchRepository;
import com.emu.rule_engine_ms.repository.search.StpMessageSearchRepository;
import com.emu.rule_engine_ms.service.DroolsFilesService;
import com.emu.rule_engine_ms.service.EngineService;
import com.emu.rule_engine_ms.service.dto.Member;
import com.emu.rule_engine_ms.service.mapper.DroolsFilesMapper;
import com.emu.rule_engine_ms.singleton.DroolFilesMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EngineServiceImpl implements EngineService {

    private final Logger log = LoggerFactory.getLogger(EngineServiceImpl.class);

    private final DroolsFilesRepository droolsFilesRepository;

    private final DroolsFilesMapper droolsFilesMapper;

    private final DroolsFilesSearchRepository droolsFilesSearchRepository;

    @Autowired
    private DroolFilesMap filesMap;

    @Autowired
    private DroolsFilesService droolsFilesService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StpMessageRepository stpMessageRepository;

    @Autowired
    private StpMessageSearchRepository stpMessageSearchRepository;

    public EngineServiceImpl(
        DroolsFilesRepository droolsFilesRepository,
        DroolsFilesMapper droolsFilesMapper,
        DroolsFilesSearchRepository droolsFilesSearchRepository
    ) {
        this.droolsFilesRepository = droolsFilesRepository;
        this.droolsFilesMapper = droolsFilesMapper;
        this.droolsFilesSearchRepository = droolsFilesSearchRepository;
    }

    @Override
    public DroolsFiles getMatchedDrl(String simpleClassName, FileValidationType fileValidationType) throws Exception {
        DroolsFiles drl = null;

        HashMap<String, DroolsFiles> map = filesMap.getMap();
        if (map.containsKey(simpleClassName)) {
            drl = map.get(simpleClassName);
        } else {
            //            drl = droolsFilesService.findBySimpleClassName(simpleClassName);
            try {
                drl =
                    droolsFilesService.findBySimpleClassNameAndFileValidationTypeAndStatus(
                        simpleClassName,
                        fileValidationType,
                        FILE_STATUS.ACTVIE
                    );
                if (drl != null) {
                    map.putIfAbsent(simpleClassName, drl);
                    filesMap.setMap(map);
                }
            } catch (Exception e) {
                throw new Exception("there is no drl with these  params");
            }
        }
        return drl;
    }

    @Override
    public Object wrapToSpecificType(Object request, String simpleClassName) {
        Object converted = null;
        if (simpleClassName.equals("Member")) {
            converted = objectMapper.convertValue(request, Member.class);
        }
        return converted;
    }

    @Override
    public void syncIndicesWithDB() {
        // try retain all
        droolsFilesSearchRepository.deleteAll();
        stpMessageSearchRepository.deleteAll();

        droolsFilesSearchRepository.saveAll(droolsFilesRepository.findAll());
        stpMessageSearchRepository.saveAll(stpMessageRepository.findAll());
    }
}
