package com.emu.rule_engine_ms.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.emu.rule_engine_ms.IntegrationTest;
import com.emu.rule_engine_ms.domain.ExceptionLogs;
import com.emu.rule_engine_ms.repository.ExceptionLogsRepository;
import com.emu.rule_engine_ms.repository.search.ExceptionLogsSearchRepository;
import com.emu.rule_engine_ms.service.dto.ExceptionLogsDTO;
import com.emu.rule_engine_ms.service.mapper.ExceptionLogsMapper;
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
 * Integration tests for the {@link ExceptionLogsResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ExceptionLogsResourceIT {

    private static final String DEFAULT_EXCEPTION_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_EXCEPTION_MESSAGE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/exception-logs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/exception-logs";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ExceptionLogsRepository exceptionLogsRepository;

    @Autowired
    private ExceptionLogsMapper exceptionLogsMapper;

    /**
     * This repository is mocked in the com.emu.rule_engine_ms.repository.search test package.
     *
     * @see com.emu.rule_engine_ms.repository.search.ExceptionLogsSearchRepositoryMockConfiguration
     */
    @Autowired
    private ExceptionLogsSearchRepository mockExceptionLogsSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restExceptionLogsMockMvc;

    private ExceptionLogs exceptionLogs;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ExceptionLogs createEntity(EntityManager em) {
        ExceptionLogs exceptionLogs = new ExceptionLogs().exceptionMessage(DEFAULT_EXCEPTION_MESSAGE);
        return exceptionLogs;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ExceptionLogs createUpdatedEntity(EntityManager em) {
        ExceptionLogs exceptionLogs = new ExceptionLogs().exceptionMessage(UPDATED_EXCEPTION_MESSAGE);
        return exceptionLogs;
    }

    @BeforeEach
    public void initTest() {
        exceptionLogs = createEntity(em);
    }

    @Test
    @Transactional
    void createExceptionLogs() throws Exception {
        int databaseSizeBeforeCreate = exceptionLogsRepository.findAll().size();
        // Create the ExceptionLogs
        ExceptionLogsDTO exceptionLogsDTO = exceptionLogsMapper.toDto(exceptionLogs);
        restExceptionLogsMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(exceptionLogsDTO))
            )
            .andExpect(status().isCreated());

        // Validate the ExceptionLogs in the database
        List<ExceptionLogs> exceptionLogsList = exceptionLogsRepository.findAll();
        assertThat(exceptionLogsList).hasSize(databaseSizeBeforeCreate + 1);
        ExceptionLogs testExceptionLogs = exceptionLogsList.get(exceptionLogsList.size() - 1);
        assertThat(testExceptionLogs.getExceptionMessage()).isEqualTo(DEFAULT_EXCEPTION_MESSAGE);

        // Validate the ExceptionLogs in Elasticsearch
        verify(mockExceptionLogsSearchRepository, times(1)).save(testExceptionLogs);
    }

    @Test
    @Transactional
    void createExceptionLogsWithExistingId() throws Exception {
        // Create the ExceptionLogs with an existing ID
        exceptionLogs.setId(1L);
        ExceptionLogsDTO exceptionLogsDTO = exceptionLogsMapper.toDto(exceptionLogs);

        int databaseSizeBeforeCreate = exceptionLogsRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restExceptionLogsMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(exceptionLogsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ExceptionLogs in the database
        List<ExceptionLogs> exceptionLogsList = exceptionLogsRepository.findAll();
        assertThat(exceptionLogsList).hasSize(databaseSizeBeforeCreate);

        // Validate the ExceptionLogs in Elasticsearch
        verify(mockExceptionLogsSearchRepository, times(0)).save(exceptionLogs);
    }

    @Test
    @Transactional
    void getAllExceptionLogs() throws Exception {
        // Initialize the database
        exceptionLogsRepository.saveAndFlush(exceptionLogs);

        // Get all the exceptionLogsList
        restExceptionLogsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(exceptionLogs.getId().intValue())))
            .andExpect(jsonPath("$.[*].exceptionMessage").value(hasItem(DEFAULT_EXCEPTION_MESSAGE)));
    }

    @Test
    @Transactional
    void getExceptionLogs() throws Exception {
        // Initialize the database
        exceptionLogsRepository.saveAndFlush(exceptionLogs);

        // Get the exceptionLogs
        restExceptionLogsMockMvc
            .perform(get(ENTITY_API_URL_ID, exceptionLogs.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(exceptionLogs.getId().intValue()))
            .andExpect(jsonPath("$.exceptionMessage").value(DEFAULT_EXCEPTION_MESSAGE));
    }

    @Test
    @Transactional
    void getNonExistingExceptionLogs() throws Exception {
        // Get the exceptionLogs
        restExceptionLogsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewExceptionLogs() throws Exception {
        // Initialize the database
        exceptionLogsRepository.saveAndFlush(exceptionLogs);

        int databaseSizeBeforeUpdate = exceptionLogsRepository.findAll().size();

        // Update the exceptionLogs
        ExceptionLogs updatedExceptionLogs = exceptionLogsRepository.findById(exceptionLogs.getId()).get();
        // Disconnect from session so that the updates on updatedExceptionLogs are not directly saved in db
        em.detach(updatedExceptionLogs);
        updatedExceptionLogs.exceptionMessage(UPDATED_EXCEPTION_MESSAGE);
        ExceptionLogsDTO exceptionLogsDTO = exceptionLogsMapper.toDto(updatedExceptionLogs);

        restExceptionLogsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, exceptionLogsDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(exceptionLogsDTO))
            )
            .andExpect(status().isOk());

        // Validate the ExceptionLogs in the database
        List<ExceptionLogs> exceptionLogsList = exceptionLogsRepository.findAll();
        assertThat(exceptionLogsList).hasSize(databaseSizeBeforeUpdate);
        ExceptionLogs testExceptionLogs = exceptionLogsList.get(exceptionLogsList.size() - 1);
        assertThat(testExceptionLogs.getExceptionMessage()).isEqualTo(UPDATED_EXCEPTION_MESSAGE);

        // Validate the ExceptionLogs in Elasticsearch
        verify(mockExceptionLogsSearchRepository).save(testExceptionLogs);
    }

    @Test
    @Transactional
    void putNonExistingExceptionLogs() throws Exception {
        int databaseSizeBeforeUpdate = exceptionLogsRepository.findAll().size();
        exceptionLogs.setId(count.incrementAndGet());

        // Create the ExceptionLogs
        ExceptionLogsDTO exceptionLogsDTO = exceptionLogsMapper.toDto(exceptionLogs);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExceptionLogsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, exceptionLogsDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(exceptionLogsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ExceptionLogs in the database
        List<ExceptionLogs> exceptionLogsList = exceptionLogsRepository.findAll();
        assertThat(exceptionLogsList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ExceptionLogs in Elasticsearch
        verify(mockExceptionLogsSearchRepository, times(0)).save(exceptionLogs);
    }

    @Test
    @Transactional
    void putWithIdMismatchExceptionLogs() throws Exception {
        int databaseSizeBeforeUpdate = exceptionLogsRepository.findAll().size();
        exceptionLogs.setId(count.incrementAndGet());

        // Create the ExceptionLogs
        ExceptionLogsDTO exceptionLogsDTO = exceptionLogsMapper.toDto(exceptionLogs);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExceptionLogsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(exceptionLogsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ExceptionLogs in the database
        List<ExceptionLogs> exceptionLogsList = exceptionLogsRepository.findAll();
        assertThat(exceptionLogsList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ExceptionLogs in Elasticsearch
        verify(mockExceptionLogsSearchRepository, times(0)).save(exceptionLogs);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamExceptionLogs() throws Exception {
        int databaseSizeBeforeUpdate = exceptionLogsRepository.findAll().size();
        exceptionLogs.setId(count.incrementAndGet());

        // Create the ExceptionLogs
        ExceptionLogsDTO exceptionLogsDTO = exceptionLogsMapper.toDto(exceptionLogs);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExceptionLogsMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(exceptionLogsDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ExceptionLogs in the database
        List<ExceptionLogs> exceptionLogsList = exceptionLogsRepository.findAll();
        assertThat(exceptionLogsList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ExceptionLogs in Elasticsearch
        verify(mockExceptionLogsSearchRepository, times(0)).save(exceptionLogs);
    }

    @Test
    @Transactional
    void partialUpdateExceptionLogsWithPatch() throws Exception {
        // Initialize the database
        exceptionLogsRepository.saveAndFlush(exceptionLogs);

        int databaseSizeBeforeUpdate = exceptionLogsRepository.findAll().size();

        // Update the exceptionLogs using partial update
        ExceptionLogs partialUpdatedExceptionLogs = new ExceptionLogs();
        partialUpdatedExceptionLogs.setId(exceptionLogs.getId());

        restExceptionLogsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedExceptionLogs.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedExceptionLogs))
            )
            .andExpect(status().isOk());

        // Validate the ExceptionLogs in the database
        List<ExceptionLogs> exceptionLogsList = exceptionLogsRepository.findAll();
        assertThat(exceptionLogsList).hasSize(databaseSizeBeforeUpdate);
        ExceptionLogs testExceptionLogs = exceptionLogsList.get(exceptionLogsList.size() - 1);
        assertThat(testExceptionLogs.getExceptionMessage()).isEqualTo(DEFAULT_EXCEPTION_MESSAGE);
    }

    @Test
    @Transactional
    void fullUpdateExceptionLogsWithPatch() throws Exception {
        // Initialize the database
        exceptionLogsRepository.saveAndFlush(exceptionLogs);

        int databaseSizeBeforeUpdate = exceptionLogsRepository.findAll().size();

        // Update the exceptionLogs using partial update
        ExceptionLogs partialUpdatedExceptionLogs = new ExceptionLogs();
        partialUpdatedExceptionLogs.setId(exceptionLogs.getId());

        partialUpdatedExceptionLogs.exceptionMessage(UPDATED_EXCEPTION_MESSAGE);

        restExceptionLogsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedExceptionLogs.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedExceptionLogs))
            )
            .andExpect(status().isOk());

        // Validate the ExceptionLogs in the database
        List<ExceptionLogs> exceptionLogsList = exceptionLogsRepository.findAll();
        assertThat(exceptionLogsList).hasSize(databaseSizeBeforeUpdate);
        ExceptionLogs testExceptionLogs = exceptionLogsList.get(exceptionLogsList.size() - 1);
        assertThat(testExceptionLogs.getExceptionMessage()).isEqualTo(UPDATED_EXCEPTION_MESSAGE);
    }

    @Test
    @Transactional
    void patchNonExistingExceptionLogs() throws Exception {
        int databaseSizeBeforeUpdate = exceptionLogsRepository.findAll().size();
        exceptionLogs.setId(count.incrementAndGet());

        // Create the ExceptionLogs
        ExceptionLogsDTO exceptionLogsDTO = exceptionLogsMapper.toDto(exceptionLogs);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExceptionLogsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, exceptionLogsDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(exceptionLogsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ExceptionLogs in the database
        List<ExceptionLogs> exceptionLogsList = exceptionLogsRepository.findAll();
        assertThat(exceptionLogsList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ExceptionLogs in Elasticsearch
        verify(mockExceptionLogsSearchRepository, times(0)).save(exceptionLogs);
    }

    @Test
    @Transactional
    void patchWithIdMismatchExceptionLogs() throws Exception {
        int databaseSizeBeforeUpdate = exceptionLogsRepository.findAll().size();
        exceptionLogs.setId(count.incrementAndGet());

        // Create the ExceptionLogs
        ExceptionLogsDTO exceptionLogsDTO = exceptionLogsMapper.toDto(exceptionLogs);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExceptionLogsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(exceptionLogsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ExceptionLogs in the database
        List<ExceptionLogs> exceptionLogsList = exceptionLogsRepository.findAll();
        assertThat(exceptionLogsList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ExceptionLogs in Elasticsearch
        verify(mockExceptionLogsSearchRepository, times(0)).save(exceptionLogs);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamExceptionLogs() throws Exception {
        int databaseSizeBeforeUpdate = exceptionLogsRepository.findAll().size();
        exceptionLogs.setId(count.incrementAndGet());

        // Create the ExceptionLogs
        ExceptionLogsDTO exceptionLogsDTO = exceptionLogsMapper.toDto(exceptionLogs);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExceptionLogsMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(exceptionLogsDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ExceptionLogs in the database
        List<ExceptionLogs> exceptionLogsList = exceptionLogsRepository.findAll();
        assertThat(exceptionLogsList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ExceptionLogs in Elasticsearch
        verify(mockExceptionLogsSearchRepository, times(0)).save(exceptionLogs);
    }

    @Test
    @Transactional
    void deleteExceptionLogs() throws Exception {
        // Initialize the database
        exceptionLogsRepository.saveAndFlush(exceptionLogs);

        int databaseSizeBeforeDelete = exceptionLogsRepository.findAll().size();

        // Delete the exceptionLogs
        restExceptionLogsMockMvc
            .perform(delete(ENTITY_API_URL_ID, exceptionLogs.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ExceptionLogs> exceptionLogsList = exceptionLogsRepository.findAll();
        assertThat(exceptionLogsList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the ExceptionLogs in Elasticsearch
        verify(mockExceptionLogsSearchRepository, times(1)).deleteById(exceptionLogs.getId());
    }

    @Test
    @Transactional
    void searchExceptionLogs() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        exceptionLogsRepository.saveAndFlush(exceptionLogs);
        when(mockExceptionLogsSearchRepository.search(queryStringQuery("id:" + exceptionLogs.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(exceptionLogs), PageRequest.of(0, 1), 1));

        // Search the exceptionLogs
        restExceptionLogsMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + exceptionLogs.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(exceptionLogs.getId().intValue())))
            .andExpect(jsonPath("$.[*].exceptionMessage").value(hasItem(DEFAULT_EXCEPTION_MESSAGE)));
    }
}
