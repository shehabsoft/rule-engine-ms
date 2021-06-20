package com.emu.rule_engine_ms.repository.search;

import com.emu.rule_engine_ms.domain.OperationsLogs;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link OperationsLogs} entity.
 */
public interface OperationsLogsSearchRepository extends ElasticsearchRepository<OperationsLogs, Long> {}
