package com.emu.rule_engine_ms.service;

import com.emu.rule_engine_ms.service.dto.ExceptionLogsDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.emu.rule_engine_ms.domain.ExceptionLogs}.
 */
public interface ExceptionLogsService {
    /**
     * Save a exceptionLogs.
     *
     * @param exceptionLogsDTO the entity to save.
     * @return the persisted entity.
     */
    ExceptionLogsDTO save(ExceptionLogsDTO exceptionLogsDTO);

    /**
     * Partially updates a exceptionLogs.
     *
     * @param exceptionLogsDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ExceptionLogsDTO> partialUpdate(ExceptionLogsDTO exceptionLogsDTO);

    /**
     * Get all the exceptionLogs.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ExceptionLogsDTO> findAll(Pageable pageable);

    /**
     * Get the "id" exceptionLogs.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ExceptionLogsDTO> findOne(Long id);

    /**
     * Delete the "id" exceptionLogs.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the exceptionLogs corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ExceptionLogsDTO> search(String query, Pageable pageable);
}
