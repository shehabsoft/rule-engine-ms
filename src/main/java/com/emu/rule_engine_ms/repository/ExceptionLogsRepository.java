package com.emu.rule_engine_ms.repository;

import com.emu.rule_engine_ms.domain.ExceptionLogs;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the ExceptionLogs entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ExceptionLogsRepository extends JpaRepository<ExceptionLogs, Long> {}
