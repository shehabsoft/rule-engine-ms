package com.emu.rule_engine_ms.service;

import com.emu.rule_engine_ms.service.dto.StpMessageDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.emu.rule_engine_ms.domain.StpMessage}.
 */
public interface StpMessageService {
    /**
     * Save a stpMessage.
     *
     * @param stpMessageDTO the entity to save.
     * @return the persisted entity.
     */
    StpMessageDTO save(StpMessageDTO stpMessageDTO);

    /**
     * Partially updates a stpMessage.
     *
     * @param stpMessageDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<StpMessageDTO> partialUpdate(StpMessageDTO stpMessageDTO);

    /**
     * Get all the stpMessages.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<StpMessageDTO> findAll(Pageable pageable);

    /**
     * Get the "id" stpMessage.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<StpMessageDTO> findOne(Long id);

    /**
     * Delete the "id" stpMessage.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the stpMessage corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<StpMessageDTO> search(String query, Pageable pageable);


   StpMessageDTO getCorrectValidationMessage();

}
