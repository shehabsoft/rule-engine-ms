package com.emu.rule_engine_ms.config.engine;
/**
 * Copyright 2021-2022 By Dirac Systems.
 * <p>
 * Created by {@khalid.nouh on 22/3/2021}.
 */
public class Utility {
    public static String getFileExtension(String originalFilename) {
        int index = originalFilename.lastIndexOf('.');
        String extension = originalFilename.substring(index + 1);
        return extension;
    }

    public static String getFileName(String originalFilename) {
        int index = originalFilename.lastIndexOf('.');
        String fileName = originalFilename.substring(0,index);
        return fileName;
    }
}
