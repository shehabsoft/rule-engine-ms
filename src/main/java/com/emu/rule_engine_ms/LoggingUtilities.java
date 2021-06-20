package com.emu.rule_engine_ms;


import com.emu.rule_engine_ms.service.dto.OperationsLogsDTO;

/**
 * Copyright 2021-2022 By Dirac Systems.
 *
 * Created by {@khalid.nouh on 18/3/2021}.
 */
public class LoggingUtilities {
    static OperationsLogsDTO operationsLogsDTO=null;
    public static OperationsLogsDTO createOperationsLogsDTO( String processName,String logDetails){
        operationsLogsDTO=new OperationsLogsDTO();
        operationsLogsDTO.setOperationName(processName);
        operationsLogsDTO.setLogDetails(logDetails);
        return operationsLogsDTO;
    }

}
