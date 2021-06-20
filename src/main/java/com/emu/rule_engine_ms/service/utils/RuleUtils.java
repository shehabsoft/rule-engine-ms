package com.emu.rule_engine_ms.service.utils;


import com.emu.rule_engine_ms.domain.DroolsFiles;
import com.emu.rule_engine_ms.domain.StpMessage;
import com.emu.rule_engine_ms.repository.StpMessageRepository;
import com.emu.rule_engine_ms.service.DroolsFilesService;
import com.emu.rule_engine_ms.service.dto.Member;
import com.emu.rule_engine_ms.service.dto.StpMessageDTO;
import com.emu.rule_engine_ms.service.mapper.StpMessageMapper;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Copyright 2021-2022 By Dirac Systems.
 * <p>
 * Created by {@Amr.Ibrahim on 29/3/2021}.
 */

@Service
public class RuleUtils {

    @Autowired
    private StpMessageRepository stpMessageRepository;
    @Autowired
    private StpMessageMapper stpMessageMapper;
    @Autowired
    private DroolsFilesService droolsFilesService;

    public RuleUtils() {
    }

    public static void printHelloWorld() {
        System.out.println("HELLO WORLD!!!!");
    }

    public static void printHelloWorld(String mes) {
        System.out.println("HELLO WORLD!!!! "+mes);
    }
    public StpMessageDTO getStpMessage(String messageKey) {
        StpMessage stpMessage = stpMessageRepository.findByKey(messageKey);
        return stpMessageMapper.toDto(stpMessage);
    }

    /**
     * Returns KieSession object after firing all rules exists in specific drl file.
     *
     * @param drlFile The exact drl file as configured in database.
     * @param obj     the object(POJO) in which validation will done.
     * @return the KieSession after firing all rules exists in specific drl file.
     */
    public KieSession fireAllRulesOn(DroolsFiles drlFile, Object obj) throws Exception {

        KieServices kieServices = KieServices.Factory.get();
        Resource resource1 = kieServices.getResources().newByteArrayResource(drlFile.getFileContent());
        KieHelper kieHelper = new KieHelper();
        kieHelper.addResource(resource1, ResourceType.DRL);
        KieBase kieBase = kieHelper.build();
        KieSession kieSession = kieBase.newKieSession();
        kieSession.insert(obj);
        KieRuntime kieRuntime = (KieRuntime) kieSession;
        kieRuntime.setGlobal("ruleUtils", this);
        int rules = kieSession.fireAllRules();
        System.out.println("number of fired rules: " + rules);
        Member member= (Member) obj;
        System.out.println(member.getFirstName());
        return kieSession;

    }

}
