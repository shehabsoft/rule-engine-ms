package com.emu.rule_engine_ms.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.emu.rule_engine_ms.domain.StpMessage;
import com.emu.rule_engine_ms.repository.StpMessageRepository;
import com.emu.rule_engine_ms.repository.search.StpMessageSearchRepository;
import com.emu.rule_engine_ms.service.StpMessageService;
import com.emu.rule_engine_ms.service.dto.StpMessageDTO;
import com.emu.rule_engine_ms.service.mapper.StpMessageMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link StpMessage}.
 */
@Service
@Transactional
public class StpMessageServiceImpl implements StpMessageService {

    private final Logger log = LoggerFactory.getLogger(StpMessageServiceImpl.class);

    private final StpMessageRepository stpMessageRepository;

    private final StpMessageMapper stpMessageMapper;

    private final StpMessageSearchRepository stpMessageSearchRepository;

    public StpMessageServiceImpl(
        StpMessageRepository stpMessageRepository,
        StpMessageMapper stpMessageMapper,
        StpMessageSearchRepository stpMessageSearchRepository
    ) {
        this.stpMessageRepository = stpMessageRepository;
        this.stpMessageMapper = stpMessageMapper;
        this.stpMessageSearchRepository = stpMessageSearchRepository;
    }

    @Override
    public StpMessageDTO save(StpMessageDTO stpMessageDTO) {
        log.debug("Request to save StpMessage : {}", stpMessageDTO);
        StpMessage stpMessage = stpMessageMapper.toEntity(stpMessageDTO);
        stpMessage = stpMessageRepository.save(stpMessage);
        StpMessageDTO result = stpMessageMapper.toDto(stpMessage);
        stpMessageSearchRepository.save(stpMessage);
        return result;
    }

    @Override
    public Optional<StpMessageDTO> partialUpdate(StpMessageDTO stpMessageDTO) {
        log.debug("Request to partially update StpMessage : {}", stpMessageDTO);

        return stpMessageRepository
            .findById(stpMessageDTO.getId())
            .map(
                existingStpMessage -> {
                    stpMessageMapper.partialUpdate(existingStpMessage, stpMessageDTO);
                    return existingStpMessage;
                }
            )
            .map(stpMessageRepository::save)
            .map(
                savedStpMessage -> {
                    stpMessageSearchRepository.save(savedStpMessage);

                    return savedStpMessage;
                }
            )
            .map(stpMessageMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StpMessageDTO> findAll(Pageable pageable) {
        log.debug("Request to get all StpMessages");
        return stpMessageRepository.findAll(pageable).map(stpMessageMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StpMessageDTO> findOne(Long id) {
        log.debug("Request to get StpMessage : {}", id);
        return stpMessageRepository.findById(id).map(stpMessageMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete StpMessage : {}", id);
        stpMessageRepository.deleteById(id);
        stpMessageSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StpMessageDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of StpMessages for query {}", query);
        return stpMessageSearchRepository.search(queryStringQuery(query), pageable).map(stpMessageMapper::toDto);
    }

    @Override
    public StpMessageDTO getCorrectValidationMessage() {
        StpMessage stpMessage =  stpMessageRepository.findByKey("000");
        return stpMessageMapper.toDto(stpMessage);
    }
}
