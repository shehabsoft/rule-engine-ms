package com.emu.rule_engine_ms.repository;

import com.emu.rule_engine_ms.domain.StpMessage;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the StpMessage entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StpMessageRepository extends JpaRepository<StpMessage, Long> {

    StpMessage findByKey(String key);
}
