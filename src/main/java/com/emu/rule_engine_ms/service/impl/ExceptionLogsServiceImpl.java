package com.emu.rule_engine_ms.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.emu.rule_engine_ms.domain.ExceptionLogs;
import com.emu.rule_engine_ms.repository.ExceptionLogsRepository;
import com.emu.rule_engine_ms.repository.search.ExceptionLogsSearchRepository;
import com.emu.rule_engine_ms.service.ExceptionLogsService;
import com.emu.rule_engine_ms.service.dto.ExceptionLogsDTO;
import com.emu.rule_engine_ms.service.mapper.ExceptionLogsMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ExceptionLogs}.
 */
@Service
@Transactional
public class ExceptionLogsServiceImpl implements ExceptionLogsService {

    private final Logger log = LoggerFactory.getLogger(ExceptionLogsServiceImpl.class);

    private final ExceptionLogsRepository exceptionLogsRepository;

    private final ExceptionLogsMapper exceptionLogsMapper;

    private final ExceptionLogsSearchRepository exceptionLogsSearchRepository;

    public ExceptionLogsServiceImpl(
        ExceptionLogsRepository exceptionLogsRepository,
        ExceptionLogsMapper exceptionLogsMapper,
        ExceptionLogsSearchRepository exceptionLogsSearchRepository
    ) {
        this.exceptionLogsRepository = exceptionLogsRepository;
        this.exceptionLogsMapper = exceptionLogsMapper;
        this.exceptionLogsSearchRepository = exceptionLogsSearchRepository;
    }

    @Override
    public ExceptionLogsDTO save(ExceptionLogsDTO exceptionLogsDTO) {
        log.debug("Request to save ExceptionLogs : {}", exceptionLogsDTO);
        ExceptionLogs exceptionLogs = exceptionLogsMapper.toEntity(exceptionLogsDTO);
        exceptionLogs = exceptionLogsRepository.save(exceptionLogs);
        ExceptionLogsDTO result = exceptionLogsMapper.toDto(exceptionLogs);
        exceptionLogsSearchRepository.save(exceptionLogs);
        return result;
    }

    @Override
    public Optional<ExceptionLogsDTO> partialUpdate(ExceptionLogsDTO exceptionLogsDTO) {
        log.debug("Request to partially update ExceptionLogs : {}", exceptionLogsDTO);

        return exceptionLogsRepository
            .findById(exceptionLogsDTO.getId())
            .map(
                existingExceptionLogs -> {
                    exceptionLogsMapper.partialUpdate(existingExceptionLogs, exceptionLogsDTO);
                    return existingExceptionLogs;
                }
            )
            .map(exceptionLogsRepository::save)
            .map(
                savedExceptionLogs -> {
                    exceptionLogsSearchRepository.save(savedExceptionLogs);

                    return savedExceptionLogs;
                }
            )
            .map(exceptionLogsMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExceptionLogsDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ExceptionLogs");
        return exceptionLogsRepository.findAll(pageable).map(exceptionLogsMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ExceptionLogsDTO> findOne(Long id) {
        log.debug("Request to get ExceptionLogs : {}", id);
        return exceptionLogsRepository.findById(id).map(exceptionLogsMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete ExceptionLogs : {}", id);
        exceptionLogsRepository.deleteById(id);
        exceptionLogsSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExceptionLogsDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ExceptionLogs for query {}", query);
        return exceptionLogsSearchRepository.search(queryStringQuery(query), pageable).map(exceptionLogsMapper::toDto);
    }
}
