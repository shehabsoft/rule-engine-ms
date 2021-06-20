package com.emu.rule_engine_ms.repository;

import com.emu.rule_engine_ms.domain.OperationsLogs;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the OperationsLogs entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OperationsLogsRepository extends JpaRepository<OperationsLogs, Long> {}
