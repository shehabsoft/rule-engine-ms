package com.emu.rule_engine_ms.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.emu.rule_engine_ms.repository.OperationsLogsRepository;
import com.emu.rule_engine_ms.service.OperationsLogsService;
import com.emu.rule_engine_ms.service.dto.OperationsLogsDTO;
import com.emu.rule_engine_ms.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.emu.rule_engine_ms.domain.OperationsLogs}.
 */
@RestController
@RequestMapping("/api")
public class OperationsLogsResource {

    private final Logger log = LoggerFactory.getLogger(OperationsLogsResource.class);

    private static final String ENTITY_NAME = "ruleEngineMsOperationsLogs";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OperationsLogsService operationsLogsService;

    private final OperationsLogsRepository operationsLogsRepository;

    public OperationsLogsResource(OperationsLogsService operationsLogsService, OperationsLogsRepository operationsLogsRepository) {
        this.operationsLogsService = operationsLogsService;
        this.operationsLogsRepository = operationsLogsRepository;
    }

    /**
     * {@code POST  /operations-logs} : Create a new operationsLogs.
     *
     * @param operationsLogsDTO the operationsLogsDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new operationsLogsDTO, or with status {@code 400 (Bad Request)} if the operationsLogs has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/operations-logs")
    public ResponseEntity<OperationsLogsDTO> createOperationsLogs(@RequestBody OperationsLogsDTO operationsLogsDTO)
        throws URISyntaxException {
        log.debug("REST request to save OperationsLogs : {}", operationsLogsDTO);
        if (operationsLogsDTO.getId() != null) {
            throw new BadRequestAlertException("A new operationsLogs cannot already have an ID", ENTITY_NAME, "idexists");
        }
        OperationsLogsDTO result = operationsLogsService.save(operationsLogsDTO);
        return ResponseEntity
            .created(new URI("/api/operations-logs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /operations-logs/:id} : Updates an existing operationsLogs.
     *
     * @param id the id of the operationsLogsDTO to save.
     * @param operationsLogsDTO the operationsLogsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated operationsLogsDTO,
     * or with status {@code 400 (Bad Request)} if the operationsLogsDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the operationsLogsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/operations-logs/{id}")
    public ResponseEntity<OperationsLogsDTO> updateOperationsLogs(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody OperationsLogsDTO operationsLogsDTO
    ) throws URISyntaxException {
        log.debug("REST request to update OperationsLogs : {}, {}", id, operationsLogsDTO);
        if (operationsLogsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, operationsLogsDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!operationsLogsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        OperationsLogsDTO result = operationsLogsService.save(operationsLogsDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, operationsLogsDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /operations-logs/:id} : Partial updates given fields of an existing operationsLogs, field will ignore if it is null
     *
     * @param id the id of the operationsLogsDTO to save.
     * @param operationsLogsDTO the operationsLogsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated operationsLogsDTO,
     * or with status {@code 400 (Bad Request)} if the operationsLogsDTO is not valid,
     * or with status {@code 404 (Not Found)} if the operationsLogsDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the operationsLogsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/operations-logs/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<OperationsLogsDTO> partialUpdateOperationsLogs(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody OperationsLogsDTO operationsLogsDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update OperationsLogs partially : {}, {}", id, operationsLogsDTO);
        if (operationsLogsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, operationsLogsDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!operationsLogsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<OperationsLogsDTO> result = operationsLogsService.partialUpdate(operationsLogsDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, operationsLogsDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /operations-logs} : get all the operationsLogs.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of operationsLogs in body.
     */
    @GetMapping("/operations-logs")
    public ResponseEntity<List<OperationsLogsDTO>> getAllOperationsLogs(Pageable pageable) {
        log.debug("REST request to get a page of OperationsLogs");
        Page<OperationsLogsDTO> page = operationsLogsService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /operations-logs/:id} : get the "id" operationsLogs.
     *
     * @param id the id of the operationsLogsDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the operationsLogsDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/operations-logs/{id}")
    public ResponseEntity<OperationsLogsDTO> getOperationsLogs(@PathVariable Long id) {
        log.debug("REST request to get OperationsLogs : {}", id);
        Optional<OperationsLogsDTO> operationsLogsDTO = operationsLogsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(operationsLogsDTO);
    }

    /**
     * {@code DELETE  /operations-logs/:id} : delete the "id" operationsLogs.
     *
     * @param id the id of the operationsLogsDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/operations-logs/{id}")
    public ResponseEntity<Void> deleteOperationsLogs(@PathVariable Long id) {
        log.debug("REST request to delete OperationsLogs : {}", id);
        operationsLogsService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/operations-logs?query=:query} : search for the operationsLogs corresponding
     * to the query.
     *
     * @param query the query of the operationsLogs search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/operations-logs")
    public ResponseEntity<List<OperationsLogsDTO>> searchOperationsLogs(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of OperationsLogs for query {}", query);
        Page<OperationsLogsDTO> page = operationsLogsService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
