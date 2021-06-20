package com.emu.rule_engine_ms.repository.search;

import com.emu.rule_engine_ms.domain.StpMessage;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * Spring Data Elasticsearch repository for the {@link StpMessage} entity.
 */
public interface StpMessageSearchRepository extends ElasticsearchRepository<StpMessage, Long> {


    List<StpMessage> findByDescArContaining(String key);



}
