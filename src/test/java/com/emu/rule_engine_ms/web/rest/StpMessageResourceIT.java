package com.emu.rule_engine_ms.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.emu.rule_engine_ms.IntegrationTest;
import com.emu.rule_engine_ms.domain.StpMessage;
import com.emu.rule_engine_ms.repository.StpMessageRepository;
import com.emu.rule_engine_ms.repository.search.StpMessageSearchRepository;
import com.emu.rule_engine_ms.service.dto.StpMessageDTO;
import com.emu.rule_engine_ms.service.mapper.StpMessageMapper;
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
 * Integration tests for the {@link StpMessageResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class StpMessageResourceIT {

    private static final String DEFAULT_KEY = "AAAAAAAAAA";
    private static final String UPDATED_KEY = "BBBBBBBBBB";

    private static final String DEFAULT_DESC_AR = "AAAAAAAAAA";
    private static final String UPDATED_DESC_AR = "BBBBBBBBBB";

    private static final String DEFAULT_DESC_EN = "AAAAAAAAAA";
    private static final String UPDATED_DESC_EN = "BBBBBBBBBB";

    private static final String DEFAULT_MESSAGE_AR = "AAAAAAAAAA";
    private static final String UPDATED_MESSAGE_AR = "BBBBBBBBBB";

    private static final String DEFAULT_MESSAGE_EN = "AAAAAAAAAA";
    private static final String UPDATED_MESSAGE_EN = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/stp-messages";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/stp-messages";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private StpMessageRepository stpMessageRepository;

    @Autowired
    private StpMessageMapper stpMessageMapper;

    /**
     * This repository is mocked in the com.emu.rule_engine_ms.repository.search test package.
     *
     * @see com.emu.rule_engine_ms.repository.search.StpMessageSearchRepositoryMockConfiguration
     */
    @Autowired
    private StpMessageSearchRepository mockStpMessageSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStpMessageMockMvc;

    private StpMessage stpMessage;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StpMessage createEntity(EntityManager em) {
        StpMessage stpMessage = new StpMessage()
            .key(DEFAULT_KEY)
            .descAr(DEFAULT_DESC_AR)
            .descEn(DEFAULT_DESC_EN)
            .messageAr(DEFAULT_MESSAGE_AR)
            .messageEn(DEFAULT_MESSAGE_EN);
        return stpMessage;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StpMessage createUpdatedEntity(EntityManager em) {
        StpMessage stpMessage = new StpMessage()
            .key(UPDATED_KEY)
            .descAr(UPDATED_DESC_AR)
            .descEn(UPDATED_DESC_EN)
            .messageAr(UPDATED_MESSAGE_AR)
            .messageEn(UPDATED_MESSAGE_EN);
        return stpMessage;
    }

    @BeforeEach
    public void initTest() {
        stpMessage = createEntity(em);
    }

    @Test
    @Transactional
    void createStpMessage() throws Exception {
        int databaseSizeBeforeCreate = stpMessageRepository.findAll().size();
        // Create the StpMessage
        StpMessageDTO stpMessageDTO = stpMessageMapper.toDto(stpMessage);
        restStpMessageMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stpMessageDTO))
            )
            .andExpect(status().isCreated());

        // Validate the StpMessage in the database
        List<StpMessage> stpMessageList = stpMessageRepository.findAll();
        assertThat(stpMessageList).hasSize(databaseSizeBeforeCreate + 1);
        StpMessage testStpMessage = stpMessageList.get(stpMessageList.size() - 1);
        assertThat(testStpMessage.getKey()).isEqualTo(DEFAULT_KEY);
        assertThat(testStpMessage.getDescAr()).isEqualTo(DEFAULT_DESC_AR);
        assertThat(testStpMessage.getDescEn()).isEqualTo(DEFAULT_DESC_EN);
        assertThat(testStpMessage.getMessageAr()).isEqualTo(DEFAULT_MESSAGE_AR);
        assertThat(testStpMessage.getMessageEn()).isEqualTo(DEFAULT_MESSAGE_EN);

        // Validate the StpMessage in Elasticsearch
        verify(mockStpMessageSearchRepository, times(1)).save(testStpMessage);
    }

    @Test
    @Transactional
    void createStpMessageWithExistingId() throws Exception {
        // Create the StpMessage with an existing ID
        stpMessage.setId(1L);
        StpMessageDTO stpMessageDTO = stpMessageMapper.toDto(stpMessage);

        int databaseSizeBeforeCreate = stpMessageRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStpMessageMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stpMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StpMessage in the database
        List<StpMessage> stpMessageList = stpMessageRepository.findAll();
        assertThat(stpMessageList).hasSize(databaseSizeBeforeCreate);

        // Validate the StpMessage in Elasticsearch
        verify(mockStpMessageSearchRepository, times(0)).save(stpMessage);
    }

    @Test
    @Transactional
    void getAllStpMessages() throws Exception {
        // Initialize the database
        stpMessageRepository.saveAndFlush(stpMessage);

        // Get all the stpMessageList
        restStpMessageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stpMessage.getId().intValue())))
            .andExpect(jsonPath("$.[*].key").value(hasItem(DEFAULT_KEY)))
            .andExpect(jsonPath("$.[*].descAr").value(hasItem(DEFAULT_DESC_AR)))
            .andExpect(jsonPath("$.[*].descEn").value(hasItem(DEFAULT_DESC_EN)))
            .andExpect(jsonPath("$.[*].messageAr").value(hasItem(DEFAULT_MESSAGE_AR)))
            .andExpect(jsonPath("$.[*].messageEn").value(hasItem(DEFAULT_MESSAGE_EN)));
    }

    @Test
    @Transactional
    void getStpMessage() throws Exception {
        // Initialize the database
        stpMessageRepository.saveAndFlush(stpMessage);

        // Get the stpMessage
        restStpMessageMockMvc
            .perform(get(ENTITY_API_URL_ID, stpMessage.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(stpMessage.getId().intValue()))
            .andExpect(jsonPath("$.key").value(DEFAULT_KEY))
            .andExpect(jsonPath("$.descAr").value(DEFAULT_DESC_AR))
            .andExpect(jsonPath("$.descEn").value(DEFAULT_DESC_EN))
            .andExpect(jsonPath("$.messageAr").value(DEFAULT_MESSAGE_AR))
            .andExpect(jsonPath("$.messageEn").value(DEFAULT_MESSAGE_EN));
    }

    @Test
    @Transactional
    void getNonExistingStpMessage() throws Exception {
        // Get the stpMessage
        restStpMessageMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewStpMessage() throws Exception {
        // Initialize the database
        stpMessageRepository.saveAndFlush(stpMessage);

        int databaseSizeBeforeUpdate = stpMessageRepository.findAll().size();

        // Update the stpMessage
        StpMessage updatedStpMessage = stpMessageRepository.findById(stpMessage.getId()).get();
        // Disconnect from session so that the updates on updatedStpMessage are not directly saved in db
        em.detach(updatedStpMessage);
        updatedStpMessage
            .key(UPDATED_KEY)
            .descAr(UPDATED_DESC_AR)
            .descEn(UPDATED_DESC_EN)
            .messageAr(UPDATED_MESSAGE_AR)
            .messageEn(UPDATED_MESSAGE_EN);
        StpMessageDTO stpMessageDTO = stpMessageMapper.toDto(updatedStpMessage);

        restStpMessageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, stpMessageDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stpMessageDTO))
            )
            .andExpect(status().isOk());

        // Validate the StpMessage in the database
        List<StpMessage> stpMessageList = stpMessageRepository.findAll();
        assertThat(stpMessageList).hasSize(databaseSizeBeforeUpdate);
        StpMessage testStpMessage = stpMessageList.get(stpMessageList.size() - 1);
        assertThat(testStpMessage.getKey()).isEqualTo(UPDATED_KEY);
        assertThat(testStpMessage.getDescAr()).isEqualTo(UPDATED_DESC_AR);
        assertThat(testStpMessage.getDescEn()).isEqualTo(UPDATED_DESC_EN);
        assertThat(testStpMessage.getMessageAr()).isEqualTo(UPDATED_MESSAGE_AR);
        assertThat(testStpMessage.getMessageEn()).isEqualTo(UPDATED_MESSAGE_EN);

        // Validate the StpMessage in Elasticsearch
        verify(mockStpMessageSearchRepository).save(testStpMessage);
    }

    @Test
    @Transactional
    void putNonExistingStpMessage() throws Exception {
        int databaseSizeBeforeUpdate = stpMessageRepository.findAll().size();
        stpMessage.setId(count.incrementAndGet());

        // Create the StpMessage
        StpMessageDTO stpMessageDTO = stpMessageMapper.toDto(stpMessage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStpMessageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, stpMessageDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stpMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StpMessage in the database
        List<StpMessage> stpMessageList = stpMessageRepository.findAll();
        assertThat(stpMessageList).hasSize(databaseSizeBeforeUpdate);

        // Validate the StpMessage in Elasticsearch
        verify(mockStpMessageSearchRepository, times(0)).save(stpMessage);
    }

    @Test
    @Transactional
    void putWithIdMismatchStpMessage() throws Exception {
        int databaseSizeBeforeUpdate = stpMessageRepository.findAll().size();
        stpMessage.setId(count.incrementAndGet());

        // Create the StpMessage
        StpMessageDTO stpMessageDTO = stpMessageMapper.toDto(stpMessage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStpMessageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stpMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StpMessage in the database
        List<StpMessage> stpMessageList = stpMessageRepository.findAll();
        assertThat(stpMessageList).hasSize(databaseSizeBeforeUpdate);

        // Validate the StpMessage in Elasticsearch
        verify(mockStpMessageSearchRepository, times(0)).save(stpMessage);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStpMessage() throws Exception {
        int databaseSizeBeforeUpdate = stpMessageRepository.findAll().size();
        stpMessage.setId(count.incrementAndGet());

        // Create the StpMessage
        StpMessageDTO stpMessageDTO = stpMessageMapper.toDto(stpMessage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStpMessageMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stpMessageDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the StpMessage in the database
        List<StpMessage> stpMessageList = stpMessageRepository.findAll();
        assertThat(stpMessageList).hasSize(databaseSizeBeforeUpdate);

        // Validate the StpMessage in Elasticsearch
        verify(mockStpMessageSearchRepository, times(0)).save(stpMessage);
    }

    @Test
    @Transactional
    void partialUpdateStpMessageWithPatch() throws Exception {
        // Initialize the database
        stpMessageRepository.saveAndFlush(stpMessage);

        int databaseSizeBeforeUpdate = stpMessageRepository.findAll().size();

        // Update the stpMessage using partial update
        StpMessage partialUpdatedStpMessage = new StpMessage();
        partialUpdatedStpMessage.setId(stpMessage.getId());

        partialUpdatedStpMessage.messageAr(UPDATED_MESSAGE_AR).messageEn(UPDATED_MESSAGE_EN);

        restStpMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStpMessage.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStpMessage))
            )
            .andExpect(status().isOk());

        // Validate the StpMessage in the database
        List<StpMessage> stpMessageList = stpMessageRepository.findAll();
        assertThat(stpMessageList).hasSize(databaseSizeBeforeUpdate);
        StpMessage testStpMessage = stpMessageList.get(stpMessageList.size() - 1);
        assertThat(testStpMessage.getKey()).isEqualTo(DEFAULT_KEY);
        assertThat(testStpMessage.getDescAr()).isEqualTo(DEFAULT_DESC_AR);
        assertThat(testStpMessage.getDescEn()).isEqualTo(DEFAULT_DESC_EN);
        assertThat(testStpMessage.getMessageAr()).isEqualTo(UPDATED_MESSAGE_AR);
        assertThat(testStpMessage.getMessageEn()).isEqualTo(UPDATED_MESSAGE_EN);
    }

    @Test
    @Transactional
    void fullUpdateStpMessageWithPatch() throws Exception {
        // Initialize the database
        stpMessageRepository.saveAndFlush(stpMessage);

        int databaseSizeBeforeUpdate = stpMessageRepository.findAll().size();

        // Update the stpMessage using partial update
        StpMessage partialUpdatedStpMessage = new StpMessage();
        partialUpdatedStpMessage.setId(stpMessage.getId());

        partialUpdatedStpMessage
            .key(UPDATED_KEY)
            .descAr(UPDATED_DESC_AR)
            .descEn(UPDATED_DESC_EN)
            .messageAr(UPDATED_MESSAGE_AR)
            .messageEn(UPDATED_MESSAGE_EN);

        restStpMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStpMessage.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStpMessage))
            )
            .andExpect(status().isOk());

        // Validate the StpMessage in the database
        List<StpMessage> stpMessageList = stpMessageRepository.findAll();
        assertThat(stpMessageList).hasSize(databaseSizeBeforeUpdate);
        StpMessage testStpMessage = stpMessageList.get(stpMessageList.size() - 1);
        assertThat(testStpMessage.getKey()).isEqualTo(UPDATED_KEY);
        assertThat(testStpMessage.getDescAr()).isEqualTo(UPDATED_DESC_AR);
        assertThat(testStpMessage.getDescEn()).isEqualTo(UPDATED_DESC_EN);
        assertThat(testStpMessage.getMessageAr()).isEqualTo(UPDATED_MESSAGE_AR);
        assertThat(testStpMessage.getMessageEn()).isEqualTo(UPDATED_MESSAGE_EN);
    }

    @Test
    @Transactional
    void patchNonExistingStpMessage() throws Exception {
        int databaseSizeBeforeUpdate = stpMessageRepository.findAll().size();
        stpMessage.setId(count.incrementAndGet());

        // Create the StpMessage
        StpMessageDTO stpMessageDTO = stpMessageMapper.toDto(stpMessage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStpMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, stpMessageDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(stpMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StpMessage in the database
        List<StpMessage> stpMessageList = stpMessageRepository.findAll();
        assertThat(stpMessageList).hasSize(databaseSizeBeforeUpdate);

        // Validate the StpMessage in Elasticsearch
        verify(mockStpMessageSearchRepository, times(0)).save(stpMessage);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStpMessage() throws Exception {
        int databaseSizeBeforeUpdate = stpMessageRepository.findAll().size();
        stpMessage.setId(count.incrementAndGet());

        // Create the StpMessage
        StpMessageDTO stpMessageDTO = stpMessageMapper.toDto(stpMessage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStpMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(stpMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StpMessage in the database
        List<StpMessage> stpMessageList = stpMessageRepository.findAll();
        assertThat(stpMessageList).hasSize(databaseSizeBeforeUpdate);

        // Validate the StpMessage in Elasticsearch
        verify(mockStpMessageSearchRepository, times(0)).save(stpMessage);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStpMessage() throws Exception {
        int databaseSizeBeforeUpdate = stpMessageRepository.findAll().size();
        stpMessage.setId(count.incrementAndGet());

        // Create the StpMessage
        StpMessageDTO stpMessageDTO = stpMessageMapper.toDto(stpMessage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStpMessageMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(stpMessageDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the StpMessage in the database
        List<StpMessage> stpMessageList = stpMessageRepository.findAll();
        assertThat(stpMessageList).hasSize(databaseSizeBeforeUpdate);

        // Validate the StpMessage in Elasticsearch
        verify(mockStpMessageSearchRepository, times(0)).save(stpMessage);
    }

    @Test
    @Transactional
    void deleteStpMessage() throws Exception {
        // Initialize the database
        stpMessageRepository.saveAndFlush(stpMessage);

        int databaseSizeBeforeDelete = stpMessageRepository.findAll().size();

        // Delete the stpMessage
        restStpMessageMockMvc
            .perform(delete(ENTITY_API_URL_ID, stpMessage.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<StpMessage> stpMessageList = stpMessageRepository.findAll();
        assertThat(stpMessageList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the StpMessage in Elasticsearch
        verify(mockStpMessageSearchRepository, times(1)).deleteById(stpMessage.getId());
    }

    @Test
    @Transactional
    void searchStpMessage() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        stpMessageRepository.saveAndFlush(stpMessage);
        when(mockStpMessageSearchRepository.search(queryStringQuery("id:" + stpMessage.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(stpMessage), PageRequest.of(0, 1), 1));

        // Search the stpMessage
        restStpMessageMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + stpMessage.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stpMessage.getId().intValue())))
            .andExpect(jsonPath("$.[*].key").value(hasItem(DEFAULT_KEY)))
            .andExpect(jsonPath("$.[*].descAr").value(hasItem(DEFAULT_DESC_AR)))
            .andExpect(jsonPath("$.[*].descEn").value(hasItem(DEFAULT_DESC_EN)))
            .andExpect(jsonPath("$.[*].messageAr").value(hasItem(DEFAULT_MESSAGE_AR)))
            .andExpect(jsonPath("$.[*].messageEn").value(hasItem(DEFAULT_MESSAGE_EN)));
    }
}
