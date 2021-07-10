package com.emu.rule_engine_ms.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.emu.rule_engine_ms.domain.DroolsFiles;
import com.emu.rule_engine_ms.domain.enumeration.FileValidationType;
import com.emu.rule_engine_ms.repository.DroolsFilesRepository;
import com.emu.rule_engine_ms.repository.search.DroolsFilesSearchRepository;
import com.emu.rule_engine_ms.service.DroolsFilesService;
import com.emu.rule_engine_ms.service.dto.DroolsFilesDTO;
import com.emu.rule_engine_ms.service.mapper.DroolsFilesMapper;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link DroolsFiles}.
 */
@Service
@Transactional
public class DroolsFilesServiceImpl implements DroolsFilesService {

    private final Logger log = LoggerFactory.getLogger(DroolsFilesServiceImpl.class);

    private final DroolsFilesRepository droolsFilesRepository;

    private final DroolsFilesMapper droolsFilesMapper;

    private final DroolsFilesSearchRepository droolsFilesSearchRepository;

    public DroolsFilesServiceImpl(
        DroolsFilesRepository droolsFilesRepository,
        DroolsFilesMapper droolsFilesMapper,
        DroolsFilesSearchRepository droolsFilesSearchRepository
    ) {
        this.droolsFilesRepository = droolsFilesRepository;
        this.droolsFilesMapper = droolsFilesMapper;
        this.droolsFilesSearchRepository = droolsFilesSearchRepository;
    }

    @Override
    public DroolsFilesDTO save(DroolsFilesDTO droolsFilesDTO) {
        log.debug("Request to save DroolsFiles : {}", droolsFilesDTO);
        DroolsFiles droolsFiles = droolsFilesMapper.toEntity(droolsFilesDTO);
        droolsFiles = droolsFilesRepository.save(droolsFiles);
        DroolsFilesDTO result = droolsFilesMapper.toDto(droolsFiles);
        //  droolsFilesSearchRepository.save(droolsFiles);
        return result;
    }

    @Override
    public Optional<DroolsFilesDTO> partialUpdate(DroolsFilesDTO droolsFilesDTO) {
        log.debug("Request to partially update DroolsFiles : {}", droolsFilesDTO);

        return droolsFilesRepository
            .findById(droolsFilesDTO.getId())
            .map(
                existingDroolsFiles -> {
                    droolsFilesMapper.partialUpdate(existingDroolsFiles, droolsFilesDTO);
                    return existingDroolsFiles;
                }
            )
            .map(droolsFilesRepository::save)
            .map(
                savedDroolsFiles -> {
                    droolsFilesSearchRepository.save(savedDroolsFiles);

                    return savedDroolsFiles;
                }
            )
            .map(droolsFilesMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DroolsFilesDTO> findAll(Pageable pageable) {
        log.debug("Request to get all DroolsFiles");
        return droolsFilesRepository.findAll(pageable).map(droolsFilesMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DroolsFilesDTO> findOne(Long id) {
        log.debug("Request to get DroolsFiles : {}", id);
        return droolsFilesRepository.findById(id).map(droolsFilesMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete DroolsFiles : {}", id);
        droolsFilesRepository.deleteById(id);
        droolsFilesSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DroolsFilesDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of DroolsFiles for query {}", query);
        return droolsFilesSearchRepository.search(queryStringQuery(query), pageable).map(droolsFilesMapper::toDto);
    }

    @Override
    public DroolsFiles findByFileName(String fileName) {
        return droolsFilesRepository.findByFileName(fileName);
    }

    @Override
    public List<DroolsFiles> findAllByFileName(String fileName) {
        return droolsFilesRepository.findAllByFileName(fileName);
    }

    @Override
    public DroolsFiles findBySimpleClassName(String simpleClassName) {
        return droolsFilesRepository.findBySimpleClassName(simpleClassName);
    }

    @Override
    public DroolsFiles findBySimpleClassNameAndFileValidationTypeAndStatus(
        String simpleClassName,
        FileValidationType fileValidationType,
        Integer status
    ) {
        return droolsFilesRepository.findBySimpleClassNameAndFileValidationTypeAndStatus(simpleClassName, fileValidationType, status);
    }

    @Override
    public DroolsFiles findDtoValidationFile(String fileName) {
        //        return droolsFilesRepository.findByFileNameAndFileValidationType(fileName, FileValidationType.DTO_VALIDATION);
        return null;
    }

    @Override
    public DroolsFiles findBusinessValidationFile(String fileName) {
        return droolsFilesRepository.findByFileNameAndFileValidationType(fileName, FileValidationType.BUSINESS_VALIDATION.name());
    }
}
