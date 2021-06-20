package com.emu.rule_engine_ms.service;

import com.emu.rule_engine_ms.domain.DroolsFiles;
import com.emu.rule_engine_ms.domain.enumeration.FileValidationType;
import com.emu.rule_engine_ms.service.dto.DroolsFilesDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.emu.rule_engine_ms.domain.DroolsFiles}.
 */
public interface DroolsFilesService {
    /**
     * Save a droolsFiles.
     *
     * @param droolsFilesDTO the entity to save.
     * @return the persisted entity.
     */
    DroolsFilesDTO save(DroolsFilesDTO droolsFilesDTO);

    /**
     * Partially updates a droolsFiles.
     *
     * @param droolsFilesDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<DroolsFilesDTO> partialUpdate(DroolsFilesDTO droolsFilesDTO);

    /**
     * Get all the droolsFiles.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<DroolsFilesDTO> findAll(Pageable pageable);

    /**
     * Get the "id" droolsFiles.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<DroolsFilesDTO> findOne(Long id);

    /**
     * Delete the "id" droolsFiles.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the droolsFiles corresponding to the query.
     *
     * @param query    the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<DroolsFilesDTO> search(String query, Pageable pageable);

    DroolsFiles findByFileName(String originalFilename);

    List<DroolsFiles> findAllByFileName(String fileName);

    DroolsFiles findBySimpleClassName(String simpleClassName);

    DroolsFiles findBySimpleClassNameAndFileValidationTypeAndStatus(String simpleClassName, FileValidationType fileValidationType,Integer status);

    DroolsFiles findDtoValidationFile(String fileName);

    DroolsFiles findBusinessValidationFile(String fileName);




}
