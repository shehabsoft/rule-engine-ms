package com.emu.rule_engine_ms.service;

import com.emu.rule_engine_ms.service.dto.OperationsLogsDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.emu.rule_engine_ms.domain.OperationsLogs}.
 */
public interface OperationsLogsService {
    /**
     * Save a operationsLogs.
     *
     * @param operationsLogsDTO the entity to save.
     * @return the persisted entity.
     */
    OperationsLogsDTO save(OperationsLogsDTO operationsLogsDTO);

    /**
     * Partially updates a operationsLogs.
     *
     * @param operationsLogsDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<OperationsLogsDTO> partialUpdate(OperationsLogsDTO operationsLogsDTO);

    /**
     * Get all the operationsLogs.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<OperationsLogsDTO> findAll(Pageable pageable);

    /**
     * Get the "id" operationsLogs.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<OperationsLogsDTO> findOne(Long id);

    /**
     * Delete the "id" operationsLogs.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the operationsLogs corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<OperationsLogsDTO> search(String query, Pageable pageable);
}
