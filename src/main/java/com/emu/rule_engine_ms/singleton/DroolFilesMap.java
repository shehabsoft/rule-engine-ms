package com.emu.rule_engine_ms.singleton;

import com.emu.rule_engine_ms.domain.DroolsFiles;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashMap;


@Service
@Scope("singleton")
public class DroolFilesMap {

    private static HashMap<String, DroolsFiles> map;

    public HashMap<String, DroolsFiles> getMap() {
        if (map == null) {
            return new HashMap<>();
        }
        return map;
    }

    public void setMap(HashMap<String, DroolsFiles> map) {
        this.map = map;
    }

}
