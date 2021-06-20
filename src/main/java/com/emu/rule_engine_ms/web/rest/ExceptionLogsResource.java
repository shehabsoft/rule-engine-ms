package com.emu.rule_engine_ms.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.emu.rule_engine_ms.repository.ExceptionLogsRepository;
import com.emu.rule_engine_ms.service.ExceptionLogsService;
import com.emu.rule_engine_ms.service.dto.ExceptionLogsDTO;
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
 * REST controller for managing {@link com.emu.rule_engine_ms.domain.ExceptionLogs}.
 */
@RestController
@RequestMapping("/api")
public class ExceptionLogsResource {

    private final Logger log = LoggerFactory.getLogger(ExceptionLogsResource.class);

    private static final String ENTITY_NAME = "ruleEngineMsExceptionLogs";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ExceptionLogsService exceptionLogsService;

    private final ExceptionLogsRepository exceptionLogsRepository;

    public ExceptionLogsResource(ExceptionLogsService exceptionLogsService, ExceptionLogsRepository exceptionLogsRepository) {
        this.exceptionLogsService = exceptionLogsService;
        this.exceptionLogsRepository = exceptionLogsRepository;
    }

    /**
     * {@code POST  /exception-logs} : Create a new exceptionLogs.
     *
     * @param exceptionLogsDTO the exceptionLogsDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new exceptionLogsDTO, or with status {@code 400 (Bad Request)} if the exceptionLogs has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/exception-logs")
    public ResponseEntity<ExceptionLogsDTO> createExceptionLogs(@RequestBody ExceptionLogsDTO exceptionLogsDTO) throws URISyntaxException {
        log.debug("REST request to save ExceptionLogs : {}", exceptionLogsDTO);
        if (exceptionLogsDTO.getId() != null) {
            throw new BadRequestAlertException("A new exceptionLogs cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ExceptionLogsDTO result = exceptionLogsService.save(exceptionLogsDTO);
        return ResponseEntity
            .created(new URI("/api/exception-logs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /exception-logs/:id} : Updates an existing exceptionLogs.
     *
     * @param id the id of the exceptionLogsDTO to save.
     * @param exceptionLogsDTO the exceptionLogsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated exceptionLogsDTO,
     * or with status {@code 400 (Bad Request)} if the exceptionLogsDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the exceptionLogsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/exception-logs/{id}")
    public ResponseEntity<ExceptionLogsDTO> updateExceptionLogs(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ExceptionLogsDTO exceptionLogsDTO
    ) throws URISyntaxException {
        log.debug("REST request to update ExceptionLogs : {}, {}", id, exceptionLogsDTO);
        if (exceptionLogsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, exceptionLogsDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!exceptionLogsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ExceptionLogsDTO result = exceptionLogsService.save(exceptionLogsDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, exceptionLogsDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /exception-logs/:id} : Partial updates given fields of an existing exceptionLogs, field will ignore if it is null
     *
     * @param id the id of the exceptionLogsDTO to save.
     * @param exceptionLogsDTO the exceptionLogsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated exceptionLogsDTO,
     * or with status {@code 400 (Bad Request)} if the exceptionLogsDTO is not valid,
     * or with status {@code 404 (Not Found)} if the exceptionLogsDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the exceptionLogsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/exception-logs/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<ExceptionLogsDTO> partialUpdateExceptionLogs(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ExceptionLogsDTO exceptionLogsDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update ExceptionLogs partially : {}, {}", id, exceptionLogsDTO);
        if (exceptionLogsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, exceptionLogsDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!exceptionLogsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ExceptionLogsDTO> result = exceptionLogsService.partialUpdate(exceptionLogsDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, exceptionLogsDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /exception-logs} : get all the exceptionLogs.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of exceptionLogs in body.
     */
    @GetMapping("/exception-logs")
    public ResponseEntity<List<ExceptionLogsDTO>> getAllExceptionLogs(Pageable pageable) {
        log.debug("REST request to get a page of ExceptionLogs");
        Page<ExceptionLogsDTO> page = exceptionLogsService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /exception-logs/:id} : get the "id" exceptionLogs.
     *
     * @param id the id of the exceptionLogsDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the exceptionLogsDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/exception-logs/{id}")
    public ResponseEntity<ExceptionLogsDTO> getExceptionLogs(@PathVariable Long id) {
        log.debug("REST request to get ExceptionLogs : {}", id);
        Optional<ExceptionLogsDTO> exceptionLogsDTO = exceptionLogsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(exceptionLogsDTO);
    }

    /**
     * {@code DELETE  /exception-logs/:id} : delete the "id" exceptionLogs.
     *
     * @param id the id of the exceptionLogsDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/exception-logs/{id}")
    public ResponseEntity<Void> deleteExceptionLogs(@PathVariable Long id) {
        log.debug("REST request to delete ExceptionLogs : {}", id);
        exceptionLogsService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/exception-logs?query=:query} : search for the exceptionLogs corresponding
     * to the query.
     *
     * @param query the query of the exceptionLogs search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/exception-logs")
    public ResponseEntity<List<ExceptionLogsDTO>> searchExceptionLogs(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of ExceptionLogs for query {}", query);
        Page<ExceptionLogsDTO> page = exceptionLogsService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
