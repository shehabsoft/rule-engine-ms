package com.emu.rule_engine_ms.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.emu.rule_engine_ms.repository.StpMessageRepository;
import com.emu.rule_engine_ms.service.StpMessageService;
import com.emu.rule_engine_ms.service.dto.StpMessageDTO;
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
 * REST controller for managing {@link com.emu.rule_engine_ms.domain.StpMessage}.
 */
@RestController
@RequestMapping("/api")
public class StpMessageResource {

    private final Logger log = LoggerFactory.getLogger(StpMessageResource.class);

    private static final String ENTITY_NAME = "ruleEngineMsStpMessage";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StpMessageService stpMessageService;

    private final StpMessageRepository stpMessageRepository;

    public StpMessageResource(StpMessageService stpMessageService, StpMessageRepository stpMessageRepository) {
        this.stpMessageService = stpMessageService;
        this.stpMessageRepository = stpMessageRepository;
    }

    /**
     * {@code POST  /stp-messages} : Create a new stpMessage.
     *
     * @param stpMessageDTO the stpMessageDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new stpMessageDTO, or with status {@code 400 (Bad Request)} if the stpMessage has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/stp-messages")
    public ResponseEntity<StpMessageDTO> createStpMessage(@RequestBody StpMessageDTO stpMessageDTO) throws URISyntaxException {
        log.debug("REST request to save StpMessage : {}", stpMessageDTO);
        if (stpMessageDTO.getId() != null) {
            throw new BadRequestAlertException("A new stpMessage cannot already have an ID", ENTITY_NAME, "idexists");
        }
        StpMessageDTO result = stpMessageService.save(stpMessageDTO);
        return ResponseEntity
            .created(new URI("/api/stp-messages/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /stp-messages/:id} : Updates an existing stpMessage.
     *
     * @param id the id of the stpMessageDTO to save.
     * @param stpMessageDTO the stpMessageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stpMessageDTO,
     * or with status {@code 400 (Bad Request)} if the stpMessageDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the stpMessageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/stp-messages/{id}")
    public ResponseEntity<StpMessageDTO> updateStpMessage(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody StpMessageDTO stpMessageDTO
    ) throws URISyntaxException {
        log.debug("REST request to update StpMessage : {}, {}", id, stpMessageDTO);
        if (stpMessageDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stpMessageDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!stpMessageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        StpMessageDTO result = stpMessageService.save(stpMessageDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, stpMessageDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /stp-messages/:id} : Partial updates given fields of an existing stpMessage, field will ignore if it is null
     *
     * @param id the id of the stpMessageDTO to save.
     * @param stpMessageDTO the stpMessageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stpMessageDTO,
     * or with status {@code 400 (Bad Request)} if the stpMessageDTO is not valid,
     * or with status {@code 404 (Not Found)} if the stpMessageDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the stpMessageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/stp-messages/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<StpMessageDTO> partialUpdateStpMessage(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody StpMessageDTO stpMessageDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update StpMessage partially : {}, {}", id, stpMessageDTO);
        if (stpMessageDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stpMessageDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!stpMessageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<StpMessageDTO> result = stpMessageService.partialUpdate(stpMessageDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, stpMessageDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /stp-messages} : get all the stpMessages.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of stpMessages in body.
     */
    @GetMapping("/stp-messages")
    public ResponseEntity<List<StpMessageDTO>> getAllStpMessages(Pageable pageable) {
        log.debug("REST request to get a page of StpMessages");
        Page<StpMessageDTO> page = stpMessageService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /stp-messages/:id} : get the "id" stpMessage.
     *
     * @param id the id of the stpMessageDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the stpMessageDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/stp-messages/{id}")
    public ResponseEntity<StpMessageDTO> getStpMessage(@PathVariable Long id) {
        log.debug("REST request to get StpMessage : {}", id);
        Optional<StpMessageDTO> stpMessageDTO = stpMessageService.findOne(id);
        return ResponseUtil.wrapOrNotFound(stpMessageDTO);
    }

    /**
     * {@code DELETE  /stp-messages/:id} : delete the "id" stpMessage.
     *
     * @param id the id of the stpMessageDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/stp-messages/{id}")
    public ResponseEntity<Void> deleteStpMessage(@PathVariable Long id) {
        log.debug("REST request to delete StpMessage : {}", id);
        stpMessageService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/stp-messages?query=:query} : search for the stpMessage corresponding
     * to the query.
     *
     * @param query the query of the stpMessage search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/stp-messages")
    public ResponseEntity<List<StpMessageDTO>> searchStpMessages(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of StpMessages for query {}", query);
        Page<StpMessageDTO> page = stpMessageService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
