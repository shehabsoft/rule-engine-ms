package com.emu.rule_engine_ms.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.emu.rule_engine_ms.IntegrationTest;
import com.emu.rule_engine_ms.domain.OperationsLogs;
import com.emu.rule_engine_ms.repository.OperationsLogsRepository;
import com.emu.rule_engine_ms.repository.search.OperationsLogsSearchRepository;
import com.emu.rule_engine_ms.service.dto.OperationsLogsDTO;
import com.emu.rule_engine_ms.service.mapper.OperationsLogsMapper;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link OperationsLogsResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class OperationsLogsResourceIT {

    private static final String DEFAULT_OPERATION_NAME = "AAAAAAAAAA";
    private static final String UPDATED_OPERATION_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LOG_DETAILS = "AAAAAAAAAA";
    private static final String UPDATED_LOG_DETAILS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/operations-logs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/operations-logs";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private OperationsLogsRepository operationsLogsRepository;

    @Autowired
    private OperationsLogsMapper operationsLogsMapper;

    /**
     * This repository is mocked in the com.emu.rule_engine_ms.repository.search test package.
     *
     * @see com.emu.rule_engine_ms.repository.search.OperationsLogsSearchRepositoryMockConfiguration
     */
    @Autowired
    private OperationsLogsSearchRepository mockOperationsLogsSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOperationsLogsMockMvc;

    private OperationsLogs operationsLogs;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OperationsLogs createEntity(EntityManager em) {
        OperationsLogs operationsLogs = new OperationsLogs().operationName(DEFAULT_OPERATION_NAME).logDetails(DEFAULT_LOG_DETAILS);
        return operationsLogs;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OperationsLogs createUpdatedEntity(EntityManager em) {
        OperationsLogs operationsLogs = new OperationsLogs().operationName(UPDATED_OPERATION_NAME).logDetails(UPDATED_LOG_DETAILS);
        return operationsLogs;
    }

    @BeforeEach
    public void initTest() {
        operationsLogs = createEntity(em);
    }

    @Test
    @Transactional
    void createOperationsLogs() throws Exception {
        int databaseSizeBeforeCreate = operationsLogsRepository.findAll().size();
        // Create the OperationsLogs
        OperationsLogsDTO operationsLogsDTO = operationsLogsMapper.toDto(operationsLogs);
        restOperationsLogsMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(operationsLogsDTO))
            )
            .andExpect(status().isCreated());

        // Validate the OperationsLogs in the database
        List<OperationsLogs> operationsLogsList = operationsLogsRepository.findAll();
        assertThat(operationsLogsList).hasSize(databaseSizeBeforeCreate + 1);
        OperationsLogs testOperationsLogs = operationsLogsList.get(operationsLogsList.size() - 1);
        assertThat(testOperationsLogs.getOperationName()).isEqualTo(DEFAULT_OPERATION_NAME);
        assertThat(testOperationsLogs.getLogDetails()).isEqualTo(DEFAULT_LOG_DETAILS);

        // Validate the OperationsLogs in Elasticsearch
        verify(mockOperationsLogsSearchRepository, times(1)).save(testOperationsLogs);
    }

    @Test
    @Transactional
    void createOperationsLogsWithExistingId() throws Exception {
        // Create the OperationsLogs with an existing ID
        operationsLogs.setId(1L);
        OperationsLogsDTO operationsLogsDTO = operationsLogsMapper.toDto(operationsLogs);

        int databaseSizeBeforeCreate = operationsLogsRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restOperationsLogsMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(operationsLogsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OperationsLogs in the database
        List<OperationsLogs> operationsLogsList = operationsLogsRepository.findAll();
        assertThat(operationsLogsList).hasSize(databaseSizeBeforeCreate);

        // Validate the OperationsLogs in Elasticsearch
        verify(mockOperationsLogsSearchRepository, times(0)).save(operationsLogs);
    }

    @Test
    @Transactional
    void getAllOperationsLogs() throws Exception {
        // Initialize the database
        operationsLogsRepository.saveAndFlush(operationsLogs);

        // Get all the operationsLogsList
        restOperationsLogsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(operationsLogs.getId().intValue())))
            .andExpect(jsonPath("$.[*].operationName").value(hasItem(DEFAULT_OPERATION_NAME)))
            .andExpect(jsonPath("$.[*].logDetails").value(hasItem(DEFAULT_LOG_DETAILS)));
    }

    @Test
    @Transactional
    void getOperationsLogs() throws Exception {
        // Initialize the database
        operationsLogsRepository.saveAndFlush(operationsLogs);

        // Get the operationsLogs
        restOperationsLogsMockMvc
            .perform(get(ENTITY_API_URL_ID, operationsLogs.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(operationsLogs.getId().intValue()))
            .andExpect(jsonPath("$.operationName").value(DEFAULT_OPERATION_NAME))
            .andExpect(jsonPath("$.logDetails").value(DEFAULT_LOG_DETAILS));
    }

    @Test
    @Transactional
    void getNonExistingOperationsLogs() throws Exception {
        // Get the operationsLogs
        restOperationsLogsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewOperationsLogs() throws Exception {
        // Initialize the database
        operationsLogsRepository.saveAndFlush(operationsLogs);

        int databaseSizeBeforeUpdate = operationsLogsRepository.findAll().size();

        // Update the operationsLogs
        OperationsLogs updatedOperationsLogs = operationsLogsRepository.findById(operationsLogs.getId()).get();
        // Disconnect from session so that the updates on updatedOperationsLogs are not directly saved in db
        em.detach(updatedOperationsLogs);
        updatedOperationsLogs.operationName(UPDATED_OPERATION_NAME).logDetails(UPDATED_LOG_DETAILS);
        OperationsLogsDTO operationsLogsDTO = operationsLogsMapper.toDto(updatedOperationsLogs);

        restOperationsLogsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, operationsLogsDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(operationsLogsDTO))
            )
            .andExpect(status().isOk());

        // Validate the OperationsLogs in the database
        List<OperationsLogs> operationsLogsList = operationsLogsRepository.findAll();
        assertThat(operationsLogsList).hasSize(databaseSizeBeforeUpdate);
        OperationsLogs testOperationsLogs = operationsLogsList.get(operationsLogsList.size() - 1);
        assertThat(testOperationsLogs.getOperationName()).isEqualTo(UPDATED_OPERATION_NAME);
        assertThat(testOperationsLogs.getLogDetails()).isEqualTo(UPDATED_LOG_DETAILS);

        // Validate the OperationsLogs in Elasticsearch
        verify(mockOperationsLogsSearchRepository).save(testOperationsLogs);
    }

    @Test
    @Transactional
    void putNonExistingOperationsLogs() throws Exception {
        int databaseSizeBeforeUpdate = operationsLogsRepository.findAll().size();
        operationsLogs.setId(count.incrementAndGet());

        // Create the OperationsLogs
        OperationsLogsDTO operationsLogsDTO = operationsLogsMapper.toDto(operationsLogs);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOperationsLogsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, operationsLogsDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(operationsLogsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OperationsLogs in the database
        List<OperationsLogs> operationsLogsList = operationsLogsRepository.findAll();
        assertThat(operationsLogsList).hasSize(databaseSizeBeforeUpdate);

        // Validate the OperationsLogs in Elasticsearch
        verify(mockOperationsLogsSearchRepository, times(0)).save(operationsLogs);
    }

    @Test
    @Transactional
    void putWithIdMismatchOperationsLogs() throws Exception {
        int databaseSizeBeforeUpdate = operationsLogsRepository.findAll().size();
        operationsLogs.setId(count.incrementAndGet());

        // Create the OperationsLogs
        OperationsLogsDTO operationsLogsDTO = operationsLogsMapper.toDto(operationsLogs);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOperationsLogsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(operationsLogsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OperationsLogs in the database
        List<OperationsLogs> operationsLogsList = operationsLogsRepository.findAll();
        assertThat(operationsLogsList).hasSize(databaseSizeBeforeUpdate);

        // Validate the OperationsLogs in Elasticsearch
        verify(mockOperationsLogsSearchRepository, times(0)).save(operationsLogs);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOperationsLogs() throws Exception {
        int databaseSizeBeforeUpdate = operationsLogsRepository.findAll().size();
        operationsLogs.setId(count.incrementAndGet());

        // Create the OperationsLogs
        OperationsLogsDTO operationsLogsDTO = operationsLogsMapper.toDto(operationsLogs);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOperationsLogsMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(operationsLogsDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the OperationsLogs in the database
        List<OperationsLogs> operationsLogsList = operationsLogsRepository.findAll();
        assertThat(operationsLogsList).hasSize(databaseSizeBeforeUpdate);

        // Validate the OperationsLogs in Elasticsearch
        verify(mockOperationsLogsSearchRepository, times(0)).save(operationsLogs);
    }

    @Test
    @Transactional
    void partialUpdateOperationsLogsWithPatch() throws Exception {
        // Initialize the database
        operationsLogsRepository.saveAndFlush(operationsLogs);

        int databaseSizeBeforeUpdate = operationsLogsRepository.findAll().size();

        // Update the operationsLogs using partial update
        OperationsLogs partialUpdatedOperationsLogs = new OperationsLogs();
        partialUpdatedOperationsLogs.setId(operationsLogs.getId());

        partialUpdatedOperationsLogs.operationName(UPDATED_OPERATION_NAME);

        restOperationsLogsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOperationsLogs.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOperationsLogs))
            )
            .andExpect(status().isOk());

        // Validate the OperationsLogs in the database
        List<OperationsLogs> operationsLogsList = operationsLogsRepository.findAll();
        assertThat(operationsLogsList).hasSize(databaseSizeBeforeUpdate);
        OperationsLogs testOperationsLogs = operationsLogsList.get(operationsLogsList.size() - 1);
        assertThat(testOperationsLogs.getOperationName()).isEqualTo(UPDATED_OPERATION_NAME);
        assertThat(testOperationsLogs.getLogDetails()).isEqualTo(DEFAULT_LOG_DETAILS);
    }

    @Test
    @Transactional
    void fullUpdateOperationsLogsWithPatch() throws Exception {
        // Initialize the database
        operationsLogsRepository.saveAndFlush(operationsLogs);

        int databaseSizeBeforeUpdate = operationsLogsRepository.findAll().size();

        // Update the operationsLogs using partial update
        OperationsLogs partialUpdatedOperationsLogs = new OperationsLogs();
        partialUpdatedOperationsLogs.setId(operationsLogs.getId());

        partialUpdatedOperationsLogs.operationName(UPDATED_OPERATION_NAME).logDetails(UPDATED_LOG_DETAILS);

        restOperationsLogsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOperationsLogs.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOperationsLogs))
            )
            .andExpect(status().isOk());

        // Validate the OperationsLogs in the database
        List<OperationsLogs> operationsLogsList = operationsLogsRepository.findAll();
        assertThat(operationsLogsList).hasSize(databaseSizeBeforeUpdate);
        OperationsLogs testOperationsLogs = operationsLogsList.get(operationsLogsList.size() - 1);
        assertThat(testOperationsLogs.getOperationName()).isEqualTo(UPDATED_OPERATION_NAME);
        assertThat(testOperationsLogs.getLogDetails()).isEqualTo(UPDATED_LOG_DETAILS);
    }

    @Test
    @Transactional
    void patchNonExistingOperationsLogs() throws Exception {
        int databaseSizeBeforeUpdate = operationsLogsRepository.findAll().size();
        operationsLogs.setId(count.incrementAndGet());

        // Create the OperationsLogs
        OperationsLogsDTO operationsLogsDTO = operationsLogsMapper.toDto(operationsLogs);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOperationsLogsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, operationsLogsDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(operationsLogsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OperationsLogs in the database
        List<OperationsLogs> operationsLogsList = operationsLogsRepository.findAll();
        assertThat(operationsLogsList).hasSize(databaseSizeBeforeUpdate);

        // Validate the OperationsLogs in Elasticsearch
        verify(mockOperationsLogsSearchRepository, times(0)).save(operationsLogs);
    }

    @Test
    @Transactional
    void patchWithIdMismatchOperationsLogs() throws Exception {
        int databaseSizeBeforeUpdate = operationsLogsRepository.findAll().size();
        operationsLogs.setId(count.incrementAndGet());

        // Create the OperationsLogs
        OperationsLogsDTO operationsLogsDTO = operationsLogsMapper.toDto(operationsLogs);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOperationsLogsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(operationsLogsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OperationsLogs in the database
        List<OperationsLogs> operationsLogsList = operationsLogsRepository.findAll();
        assertThat(operationsLogsList).hasSize(databaseSizeBeforeUpdate);

        // Validate the OperationsLogs in Elasticsearch
        verify(mockOperationsLogsSearchRepository, times(0)).save(operationsLogs);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOperationsLogs() throws Exception {
        int databaseSizeBeforeUpdate = operationsLogsRepository.findAll().size();
        operationsLogs.setId(count.incrementAndGet());

        // Create the OperationsLogs
        OperationsLogsDTO operationsLogsDTO = operationsLogsMapper.toDto(operationsLogs);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOperationsLogsMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(operationsLogsDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the OperationsLogs in the database
        List<OperationsLogs> operationsLogsList = operationsLogsRepository.findAll();
        assertThat(operationsLogsList).hasSize(databaseSizeBeforeUpdate);

        // Validate the OperationsLogs in Elasticsearch
        verify(mockOperationsLogsSearchRepository, times(0)).save(operationsLogs);
    }

    @Test
    @Transactional
    void deleteOperationsLogs() throws Exception {
        // Initialize the database
        operationsLogsRepository.saveAndFlush(operationsLogs);

        int databaseSizeBeforeDelete = operationsLogsRepository.findAll().size();

        // Delete the operationsLogs
        restOperationsLogsMockMvc
            .perform(delete(ENTITY_API_URL_ID, operationsLogs.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<OperationsLogs> operationsLogsList = operationsLogsRepository.findAll();
        assertThat(operationsLogsList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the OperationsLogs in Elasticsearch
        verify(mockOperationsLogsSearchRepository, times(1)).deleteById(operationsLogs.getId());
    }

    @Test
    @Transactional
    void searchOperationsLogs() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        operationsLogsRepository.saveAndFlush(operationsLogs);
        when(mockOperationsLogsSearchRepository.search(queryStringQuery("id:" + operationsLogs.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(operationsLogs), PageRequest.of(0, 1), 1));

        // Search the operationsLogs
        restOperationsLogsMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + operationsLogs.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(operationsLogs.getId().intValue())))
            .andExpect(jsonPath("$.[*].operationName").value(hasItem(DEFAULT_OPERATION_NAME)))
            .andExpect(jsonPath("$.[*].logDetails").value(hasItem(DEFAULT_LOG_DETAILS)));
    }
}
