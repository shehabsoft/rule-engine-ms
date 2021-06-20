package com.emu.rule_engine_ms.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link DroolsFilesSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class DroolsFilesSearchRepositoryMockConfiguration {

    @MockBean
    private DroolsFilesSearchRepository mockDroolsFilesSearchRepository;
}
