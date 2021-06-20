package com.emu.rule_engine_ms.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.emu.rule_engine_ms.IntegrationTest;
import com.emu.rule_engine_ms.domain.DroolsFiles;
import com.emu.rule_engine_ms.domain.enumeration.FileValidationType;
import com.emu.rule_engine_ms.repository.DroolsFilesRepository;
import com.emu.rule_engine_ms.repository.search.DroolsFilesSearchRepository;
import com.emu.rule_engine_ms.service.dto.DroolsFilesDTO;
import com.emu.rule_engine_ms.service.mapper.DroolsFilesMapper;
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
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link DroolsFilesResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class DroolsFilesResourceIT {

    private static final String DEFAULT_FILE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FILE_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_FILE_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_FILE_TYPE = "BBBBBBBBBB";

    private static final byte[] DEFAULT_FILE_CONTENT = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_FILE_CONTENT = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_FILE_CONTENT_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_FILE_CONTENT_CONTENT_TYPE = "image/png";

    private static final FileValidationType DEFAULT_FILE_VALIDATION_TYPE = FileValidationType.DTO_VALIDATION;
    private static final FileValidationType UPDATED_FILE_VALIDATION_TYPE = FileValidationType.BUSINESS_VALIDATION;

    private static final String DEFAULT_DTO_NAME = "AAAAAAAAAA";
    private static final String UPDATED_DTO_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/drools-files";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/drools-files";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private DroolsFilesRepository droolsFilesRepository;

    @Autowired
    private DroolsFilesMapper droolsFilesMapper;

    /**
     * This repository is mocked in the com.emu.rule_engine_ms.repository.search test package.
     *
     * @see com.emu.rule_engine_ms.repository.search.DroolsFilesSearchRepositoryMockConfiguration
     */
    @Autowired
    private DroolsFilesSearchRepository mockDroolsFilesSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDroolsFilesMockMvc;

    private DroolsFiles droolsFiles;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DroolsFiles createEntity(EntityManager em) {
        DroolsFiles droolsFiles = new DroolsFiles()
            .fileName(DEFAULT_FILE_NAME)
            .fileType(DEFAULT_FILE_TYPE)
            .fileContent(DEFAULT_FILE_CONTENT)
            .fileContentContentType(DEFAULT_FILE_CONTENT_CONTENT_TYPE)
            .fileValidationType(DEFAULT_FILE_VALIDATION_TYPE);
        return droolsFiles;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DroolsFiles createUpdatedEntity(EntityManager em) {
        DroolsFiles droolsFiles = new DroolsFiles()
            .fileName(UPDATED_FILE_NAME)
            .fileType(UPDATED_FILE_TYPE)
            .fileContent(UPDATED_FILE_CONTENT)
            .fileContentContentType(UPDATED_FILE_CONTENT_CONTENT_TYPE)
            .fileValidationType(UPDATED_FILE_VALIDATION_TYPE);
        return droolsFiles;
    }

    @BeforeEach
    public void initTest() {
        droolsFiles = createEntity(em);
    }

    @Test
    @Transactional
    void createDroolsFiles() throws Exception {
        int databaseSizeBeforeCreate = droolsFilesRepository.findAll().size();
        // Create the DroolsFiles
        DroolsFilesDTO droolsFilesDTO = droolsFilesMapper.toDto(droolsFiles);
        restDroolsFilesMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(droolsFilesDTO))
            )
            .andExpect(status().isCreated());

        // Validate the DroolsFiles in the database
        List<DroolsFiles> droolsFilesList = droolsFilesRepository.findAll();
        assertThat(droolsFilesList).hasSize(databaseSizeBeforeCreate + 1);
        DroolsFiles testDroolsFiles = droolsFilesList.get(droolsFilesList.size() - 1);
        assertThat(testDroolsFiles.getFileName()).isEqualTo(DEFAULT_FILE_NAME);
        assertThat(testDroolsFiles.getFileType()).isEqualTo(DEFAULT_FILE_TYPE);
        assertThat(testDroolsFiles.getFileContent()).isEqualTo(DEFAULT_FILE_CONTENT);
        assertThat(testDroolsFiles.getFileContentContentType()).isEqualTo(DEFAULT_FILE_CONTENT_CONTENT_TYPE);
        assertThat(testDroolsFiles.getFileValidationType()).isEqualTo(DEFAULT_FILE_VALIDATION_TYPE);

        // Validate the DroolsFiles in Elasticsearch
        verify(mockDroolsFilesSearchRepository, times(1)).save(testDroolsFiles);
    }

    @Test
    @Transactional
    void createDroolsFilesWithExistingId() throws Exception {
        // Create the DroolsFiles with an existing ID
        droolsFiles.setId(1L);
        DroolsFilesDTO droolsFilesDTO = droolsFilesMapper.toDto(droolsFiles);

        int databaseSizeBeforeCreate = droolsFilesRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDroolsFilesMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(droolsFilesDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DroolsFiles in the database
        List<DroolsFiles> droolsFilesList = droolsFilesRepository.findAll();
        assertThat(droolsFilesList).hasSize(databaseSizeBeforeCreate);

        // Validate the DroolsFiles in Elasticsearch
        verify(mockDroolsFilesSearchRepository, times(0)).save(droolsFiles);
    }

    @Test
    @Transactional
    void checkFileValidationTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = droolsFilesRepository.findAll().size();
        // set the field null
        droolsFiles.setFileValidationType(null);

        // Create the DroolsFiles, which fails.
        DroolsFilesDTO droolsFilesDTO = droolsFilesMapper.toDto(droolsFiles);

        restDroolsFilesMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(droolsFilesDTO))
            )
            .andExpect(status().isBadRequest());

        List<DroolsFiles> droolsFilesList = droolsFilesRepository.findAll();
        assertThat(droolsFilesList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllDroolsFiles() throws Exception {
        // Initialize the database
        droolsFilesRepository.saveAndFlush(droolsFiles);

        // Get all the droolsFilesList
        restDroolsFilesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(droolsFiles.getId().intValue())))
            .andExpect(jsonPath("$.[*].fileName").value(hasItem(DEFAULT_FILE_NAME)))
            .andExpect(jsonPath("$.[*].fileType").value(hasItem(DEFAULT_FILE_TYPE)))
            .andExpect(jsonPath("$.[*].fileContentContentType").value(hasItem(DEFAULT_FILE_CONTENT_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].fileContent").value(hasItem(Base64Utils.encodeToString(DEFAULT_FILE_CONTENT))))
            .andExpect(jsonPath("$.[*].fileValidationType").value(hasItem(DEFAULT_FILE_VALIDATION_TYPE.toString())))
            .andExpect(jsonPath("$.[*].dtoName").value(hasItem(DEFAULT_DTO_NAME)));
    }

    @Test
    @Transactional
    void getDroolsFiles() throws Exception {
        // Initialize the database
        droolsFilesRepository.saveAndFlush(droolsFiles);

        // Get the droolsFiles
        restDroolsFilesMockMvc
            .perform(get(ENTITY_API_URL_ID, droolsFiles.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(droolsFiles.getId().intValue()))
            .andExpect(jsonPath("$.fileName").value(DEFAULT_FILE_NAME))
            .andExpect(jsonPath("$.fileType").value(DEFAULT_FILE_TYPE))
            .andExpect(jsonPath("$.fileContentContentType").value(DEFAULT_FILE_CONTENT_CONTENT_TYPE))
            .andExpect(jsonPath("$.fileContent").value(Base64Utils.encodeToString(DEFAULT_FILE_CONTENT)))
            .andExpect(jsonPath("$.fileValidationType").value(DEFAULT_FILE_VALIDATION_TYPE.toString()))
            .andExpect(jsonPath("$.dtoName").value(DEFAULT_DTO_NAME));
    }

    @Test
    @Transactional
    void getNonExistingDroolsFiles() throws Exception {
        // Get the droolsFiles
        restDroolsFilesMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewDroolsFiles() throws Exception {
        // Initialize the database
        droolsFilesRepository.saveAndFlush(droolsFiles);

        int databaseSizeBeforeUpdate = droolsFilesRepository.findAll().size();

        // Update the droolsFiles
        DroolsFiles updatedDroolsFiles = droolsFilesRepository.findById(droolsFiles.getId()).get();
        // Disconnect from session so that the updates on updatedDroolsFiles are not directly saved in db
        em.detach(updatedDroolsFiles);
        updatedDroolsFiles
            .fileName(UPDATED_FILE_NAME)
            .fileType(UPDATED_FILE_TYPE)
            .fileContent(UPDATED_FILE_CONTENT)
            .fileContentContentType(UPDATED_FILE_CONTENT_CONTENT_TYPE)
            .fileValidationType(UPDATED_FILE_VALIDATION_TYPE);
        DroolsFilesDTO droolsFilesDTO = droolsFilesMapper.toDto(updatedDroolsFiles);

        restDroolsFilesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, droolsFilesDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(droolsFilesDTO))
            )
            .andExpect(status().isOk());

        // Validate the DroolsFiles in the database
        List<DroolsFiles> droolsFilesList = droolsFilesRepository.findAll();
        assertThat(droolsFilesList).hasSize(databaseSizeBeforeUpdate);
        DroolsFiles testDroolsFiles = droolsFilesList.get(droolsFilesList.size() - 1);
        assertThat(testDroolsFiles.getFileName()).isEqualTo(UPDATED_FILE_NAME);
        assertThat(testDroolsFiles.getFileType()).isEqualTo(UPDATED_FILE_TYPE);
        assertThat(testDroolsFiles.getFileContent()).isEqualTo(UPDATED_FILE_CONTENT);
        assertThat(testDroolsFiles.getFileContentContentType()).isEqualTo(UPDATED_FILE_CONTENT_CONTENT_TYPE);
        assertThat(testDroolsFiles.getFileValidationType()).isEqualTo(UPDATED_FILE_VALIDATION_TYPE);
        // Validate the DroolsFiles in Elasticsearch
        verify(mockDroolsFilesSearchRepository).save(testDroolsFiles);
    }

    @Test
    @Transactional
    void putNonExistingDroolsFiles() throws Exception {
        int databaseSizeBeforeUpdate = droolsFilesRepository.findAll().size();
        droolsFiles.setId(count.incrementAndGet());

        // Create the DroolsFiles
        DroolsFilesDTO droolsFilesDTO = droolsFilesMapper.toDto(droolsFiles);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDroolsFilesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, droolsFilesDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(droolsFilesDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DroolsFiles in the database
        List<DroolsFiles> droolsFilesList = droolsFilesRepository.findAll();
        assertThat(droolsFilesList).hasSize(databaseSizeBeforeUpdate);

        // Validate the DroolsFiles in Elasticsearch
        verify(mockDroolsFilesSearchRepository, times(0)).save(droolsFiles);
    }

    @Test
    @Transactional
    void putWithIdMismatchDroolsFiles() throws Exception {
        int databaseSizeBeforeUpdate = droolsFilesRepository.findAll().size();
        droolsFiles.setId(count.incrementAndGet());

        // Create the DroolsFiles
        DroolsFilesDTO droolsFilesDTO = droolsFilesMapper.toDto(droolsFiles);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDroolsFilesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(droolsFilesDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DroolsFiles in the database
        List<DroolsFiles> droolsFilesList = droolsFilesRepository.findAll();
        assertThat(droolsFilesList).hasSize(databaseSizeBeforeUpdate);

        // Validate the DroolsFiles in Elasticsearch
        verify(mockDroolsFilesSearchRepository, times(0)).save(droolsFiles);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDroolsFiles() throws Exception {
        int databaseSizeBeforeUpdate = droolsFilesRepository.findAll().size();
        droolsFiles.setId(count.incrementAndGet());

        // Create the DroolsFiles
        DroolsFilesDTO droolsFilesDTO = droolsFilesMapper.toDto(droolsFiles);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDroolsFilesMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(droolsFilesDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the DroolsFiles in the database
        List<DroolsFiles> droolsFilesList = droolsFilesRepository.findAll();
        assertThat(droolsFilesList).hasSize(databaseSizeBeforeUpdate);

        // Validate the DroolsFiles in Elasticsearch
        verify(mockDroolsFilesSearchRepository, times(0)).save(droolsFiles);
    }

    @Test
    @Transactional
    void partialUpdateDroolsFilesWithPatch() throws Exception {
        // Initialize the database
        droolsFilesRepository.saveAndFlush(droolsFiles);

        int databaseSizeBeforeUpdate = droolsFilesRepository.findAll().size();

        // Update the droolsFiles using partial update
        DroolsFiles partialUpdatedDroolsFiles = new DroolsFiles();
        partialUpdatedDroolsFiles.setId(droolsFiles.getId());

        partialUpdatedDroolsFiles.fileType(UPDATED_FILE_TYPE);

        restDroolsFilesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDroolsFiles.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDroolsFiles))
            )
            .andExpect(status().isOk());

        // Validate the DroolsFiles in the database
        List<DroolsFiles> droolsFilesList = droolsFilesRepository.findAll();
        assertThat(droolsFilesList).hasSize(databaseSizeBeforeUpdate);
        DroolsFiles testDroolsFiles = droolsFilesList.get(droolsFilesList.size() - 1);
        assertThat(testDroolsFiles.getFileName()).isEqualTo(DEFAULT_FILE_NAME);
        assertThat(testDroolsFiles.getFileType()).isEqualTo(UPDATED_FILE_TYPE);
        assertThat(testDroolsFiles.getFileContent()).isEqualTo(DEFAULT_FILE_CONTENT);
        assertThat(testDroolsFiles.getFileContentContentType()).isEqualTo(DEFAULT_FILE_CONTENT_CONTENT_TYPE);
        assertThat(testDroolsFiles.getFileValidationType()).isEqualTo(DEFAULT_FILE_VALIDATION_TYPE);
    }

    @Test
    @Transactional
    void fullUpdateDroolsFilesWithPatch() throws Exception {
        // Initialize the database
        droolsFilesRepository.saveAndFlush(droolsFiles);

        int databaseSizeBeforeUpdate = droolsFilesRepository.findAll().size();

        // Update the droolsFiles using partial update
        DroolsFiles partialUpdatedDroolsFiles = new DroolsFiles();
        partialUpdatedDroolsFiles.setId(droolsFiles.getId());

        partialUpdatedDroolsFiles
            .fileName(UPDATED_FILE_NAME)
            .fileType(UPDATED_FILE_TYPE)
            .fileContent(UPDATED_FILE_CONTENT)
            .fileContentContentType(UPDATED_FILE_CONTENT_CONTENT_TYPE)
            .fileValidationType(UPDATED_FILE_VALIDATION_TYPE);

        restDroolsFilesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDroolsFiles.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDroolsFiles))
            )
            .andExpect(status().isOk());

        // Validate the DroolsFiles in the database
        List<DroolsFiles> droolsFilesList = droolsFilesRepository.findAll();
        assertThat(droolsFilesList).hasSize(databaseSizeBeforeUpdate);
        DroolsFiles testDroolsFiles = droolsFilesList.get(droolsFilesList.size() - 1);
        assertThat(testDroolsFiles.getFileName()).isEqualTo(UPDATED_FILE_NAME);
        assertThat(testDroolsFiles.getFileType()).isEqualTo(UPDATED_FILE_TYPE);
        assertThat(testDroolsFiles.getFileContent()).isEqualTo(UPDATED_FILE_CONTENT);
        assertThat(testDroolsFiles.getFileContentContentType()).isEqualTo(UPDATED_FILE_CONTENT_CONTENT_TYPE);
        assertThat(testDroolsFiles.getFileValidationType()).isEqualTo(UPDATED_FILE_VALIDATION_TYPE); }

    @Test
    @Transactional
    void patchNonExistingDroolsFiles() throws Exception {
        int databaseSizeBeforeUpdate = droolsFilesRepository.findAll().size();
        droolsFiles.setId(count.incrementAndGet());

        // Create the DroolsFiles
        DroolsFilesDTO droolsFilesDTO = droolsFilesMapper.toDto(droolsFiles);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDroolsFilesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, droolsFilesDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(droolsFilesDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DroolsFiles in the database
        List<DroolsFiles> droolsFilesList = droolsFilesRepository.findAll();
        assertThat(droolsFilesList).hasSize(databaseSizeBeforeUpdate);

        // Validate the DroolsFiles in Elasticsearch
        verify(mockDroolsFilesSearchRepository, times(0)).save(droolsFiles);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDroolsFiles() throws Exception {
        int databaseSizeBeforeUpdate = droolsFilesRepository.findAll().size();
        droolsFiles.setId(count.incrementAndGet());

        // Create the DroolsFiles
        DroolsFilesDTO droolsFilesDTO = droolsFilesMapper.toDto(droolsFiles);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDroolsFilesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(droolsFilesDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DroolsFiles in the database
        List<DroolsFiles> droolsFilesList = droolsFilesRepository.findAll();
        assertThat(droolsFilesList).hasSize(databaseSizeBeforeUpdate);

        // Validate the DroolsFiles in Elasticsearch
        verify(mockDroolsFilesSearchRepository, times(0)).save(droolsFiles);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDroolsFiles() throws Exception {
        int databaseSizeBeforeUpdate = droolsFilesRepository.findAll().size();
        droolsFiles.setId(count.incrementAndGet());

        // Create the DroolsFiles
        DroolsFilesDTO droolsFilesDTO = droolsFilesMapper.toDto(droolsFiles);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDroolsFilesMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(droolsFilesDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the DroolsFiles in the database
        List<DroolsFiles> droolsFilesList = droolsFilesRepository.findAll();
        assertThat(droolsFilesList).hasSize(databaseSizeBeforeUpdate);

        // Validate the DroolsFiles in Elasticsearch
        verify(mockDroolsFilesSearchRepository, times(0)).save(droolsFiles);
    }

    @Test
    @Transactional
    void deleteDroolsFiles() throws Exception {
        // Initialize the database
        droolsFilesRepository.saveAndFlush(droolsFiles);

        int databaseSizeBeforeDelete = droolsFilesRepository.findAll().size();

        // Delete the droolsFiles
        restDroolsFilesMockMvc
            .perform(delete(ENTITY_API_URL_ID, droolsFiles.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<DroolsFiles> droolsFilesList = droolsFilesRepository.findAll();
        assertThat(droolsFilesList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the DroolsFiles in Elasticsearch
        verify(mockDroolsFilesSearchRepository, times(1)).deleteById(droolsFiles.getId());
    }

    @Test
    @Transactional
    void searchDroolsFiles() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        droolsFilesRepository.saveAndFlush(droolsFiles);
        when(mockDroolsFilesSearchRepository.search(queryStringQuery("id:" + droolsFiles.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(droolsFiles), PageRequest.of(0, 1), 1));

        // Search the droolsFiles
        restDroolsFilesMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + droolsFiles.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(droolsFiles.getId().intValue())))
            .andExpect(jsonPath("$.[*].fileName").value(hasItem(DEFAULT_FILE_NAME)))
            .andExpect(jsonPath("$.[*].fileType").value(hasItem(DEFAULT_FILE_TYPE)))
            .andExpect(jsonPath("$.[*].fileContentContentType").value(hasItem(DEFAULT_FILE_CONTENT_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].fileContent").value(hasItem(Base64Utils.encodeToString(DEFAULT_FILE_CONTENT))))
            .andExpect(jsonPath("$.[*].fileValidationType").value(hasItem(DEFAULT_FILE_VALIDATION_TYPE.toString())))
            .andExpect(jsonPath("$.[*].dtoName").value(hasItem(DEFAULT_DTO_NAME)));
    }
}
