package com.emu.rule_engine_ms.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.emu.rule_engine_ms.domain.OperationsLogs;
import com.emu.rule_engine_ms.repository.OperationsLogsRepository;
import com.emu.rule_engine_ms.repository.search.OperationsLogsSearchRepository;
import com.emu.rule_engine_ms.service.OperationsLogsService;
import com.emu.rule_engine_ms.service.dto.OperationsLogsDTO;
import com.emu.rule_engine_ms.service.mapper.OperationsLogsMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link OperationsLogs}.
 */
@Service
@Transactional
public class OperationsLogsServiceImpl implements OperationsLogsService {

    private final Logger log = LoggerFactory.getLogger(OperationsLogsServiceImpl.class);

    private final OperationsLogsRepository operationsLogsRepository;

    private final OperationsLogsMapper operationsLogsMapper;

    private final OperationsLogsSearchRepository operationsLogsSearchRepository;

    public OperationsLogsServiceImpl(
        OperationsLogsRepository operationsLogsRepository,
        OperationsLogsMapper operationsLogsMapper,
        OperationsLogsSearchRepository operationsLogsSearchRepository
    ) {
        this.operationsLogsRepository = operationsLogsRepository;
        this.operationsLogsMapper = operationsLogsMapper;
        this.operationsLogsSearchRepository = operationsLogsSearchRepository;
    }

    @Override
    public OperationsLogsDTO save(OperationsLogsDTO operationsLogsDTO) {
        log.debug("Request to save OperationsLogs : {}", operationsLogsDTO);
        OperationsLogs operationsLogs = operationsLogsMapper.toEntity(operationsLogsDTO);
        operationsLogs = operationsLogsRepository.save(operationsLogs);
        OperationsLogsDTO result = operationsLogsMapper.toDto(operationsLogs);
        operationsLogsSearchRepository.save(operationsLogs);
        return result;
    }

    @Override
    public Optional<OperationsLogsDTO> partialUpdate(OperationsLogsDTO operationsLogsDTO) {
        log.debug("Request to partially update OperationsLogs : {}", operationsLogsDTO);

        return operationsLogsRepository
            .findById(operationsLogsDTO.getId())
            .map(
                existingOperationsLogs -> {
                    operationsLogsMapper.partialUpdate(existingOperationsLogs, operationsLogsDTO);
                    return existingOperationsLogs;
                }
            )
            .map(operationsLogsRepository::save)
            .map(
                savedOperationsLogs -> {
                    operationsLogsSearchRepository.save(savedOperationsLogs);

                    return savedOperationsLogs;
                }
            )
            .map(operationsLogsMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OperationsLogsDTO> findAll(Pageable pageable) {
        log.debug("Request to get all OperationsLogs");
        return operationsLogsRepository.findAll(pageable).map(operationsLogsMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OperationsLogsDTO> findOne(Long id) {
        log.debug("Request to get OperationsLogs : {}", id);
        return operationsLogsRepository.findById(id).map(operationsLogsMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete OperationsLogs : {}", id);
        operationsLogsRepository.deleteById(id);
        operationsLogsSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OperationsLogsDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of OperationsLogs for query {}", query);
        return operationsLogsSearchRepository.search(queryStringQuery(query), pageable).map(operationsLogsMapper::toDto);
    }
}
