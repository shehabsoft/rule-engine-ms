package com.emu.rule_engine_ms.repository.search;

import com.emu.rule_engine_ms.domain.DroolsFiles;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * Spring Data Elasticsearch repository for the {@link DroolsFiles} entity.
 */
public interface DroolsFilesSearchRepository extends ElasticsearchRepository<DroolsFiles, Long> {
}
