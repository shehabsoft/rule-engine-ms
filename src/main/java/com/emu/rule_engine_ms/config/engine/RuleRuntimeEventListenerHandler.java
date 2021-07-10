package com.emu.rule_engine_ms.config.engine;

import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;

/**
 * Copyright 2021-2022 By Dirac Systems.
 * <p>
 * Created by {@khalid.nouh on 22/3/2021}.
 */
public class RuleRuntimeEventListenerHandler implements RuleRuntimeEventListener {
    @Override
    public void objectInserted(ObjectInsertedEvent event) {
        System.out.println("Object inserted \n "
            + event.getObject().toString());
    }

    @Override
    public void objectUpdated(ObjectUpdatedEvent event) {
        System.out.println("Object was updated \n"
            + "New Content \n"
            + event.getObject().toString());
    }

    @Override
    public void objectDeleted(ObjectDeletedEvent event) {
        System.out.println("Object retracted \n"
            + event.getOldObject().toString());
    }
}
