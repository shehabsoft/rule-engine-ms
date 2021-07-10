package com.emu.rule_engine_ms.repository.search;

import com.emu.rule_engine_ms.domain.ExceptionLogs;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link ExceptionLogs} entity.
 */
public interface ExceptionLogsSearchRepository extends ElasticsearchRepository<ExceptionLogs, Long> {}
