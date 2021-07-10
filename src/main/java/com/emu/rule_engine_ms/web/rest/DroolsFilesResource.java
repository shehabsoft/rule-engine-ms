package com.emu.rule_engine_ms.web.rest;

import com.emu.rule_engine_ms.service.DroolsFilesService;
import com.emu.rule_engine_ms.web.rest.errors.BadRequestAlertException;
import com.emu.rule_engine_ms.service.dto.DroolsFilesDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing {@link com.emu.rule_engine_ms.domain.DroolsFiles}.
 */
@RestController
@RequestMapping("/api")
public class DroolsFilesResource {

    private final Logger log = LoggerFactory.getLogger(DroolsFilesResource.class);

    private static final String ENTITY_NAME = "ruleEngineMsDroolsFiles";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DroolsFilesService droolsFilesService;

    public DroolsFilesResource(DroolsFilesService droolsFilesService) {
        this.droolsFilesService = droolsFilesService;
    }

    /**
     * {@code POST  /drools-files} : Create a new droolsFiles.
     *
     * @param droolsFilesDTO the droolsFilesDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new droolsFilesDTO, or with status {@code 400 (Bad Request)} if the droolsFiles has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/drools-files")
    public ResponseEntity<DroolsFilesDTO> createDroolsFiles(@Valid @RequestBody DroolsFilesDTO droolsFilesDTO) throws URISyntaxException {
        log.debug("REST request to save DroolsFiles : {}", droolsFilesDTO);
        if (droolsFilesDTO.getId() != null) {
            throw new BadRequestAlertException("A new droolsFiles cannot already have an ID", ENTITY_NAME, "idexists");
        }
        DroolsFilesDTO result = droolsFilesService.save(droolsFilesDTO);
        return ResponseEntity.created(new URI("/api/drools-files/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /drools-files} : Updates an existing droolsFiles.
     *
     * @param droolsFilesDTO the droolsFilesDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated droolsFilesDTO,
     * or with status {@code 400 (Bad Request)} if the droolsFilesDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the droolsFilesDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/drools-files")
    public ResponseEntity<DroolsFilesDTO> updateDroolsFiles(@Valid @RequestBody DroolsFilesDTO droolsFilesDTO) throws URISyntaxException {
        log.debug("REST request to update DroolsFiles : {}", droolsFilesDTO);
        if (droolsFilesDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        DroolsFilesDTO result = droolsFilesService.save(droolsFilesDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, droolsFilesDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /drools-files} : get all the droolsFiles.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of droolsFiles in body.
     */
    @GetMapping("/drools-files")
    public ResponseEntity<List<DroolsFilesDTO>> getAllDroolsFiles(Pageable pageable) {
        log.debug("REST request to get a page of DroolsFiles");
        Page<DroolsFilesDTO> page = droolsFilesService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /drools-files/:id} : get the "id" droolsFiles.
     *
     * @param id the id of the droolsFilesDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the droolsFilesDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/drools-files/{id}")
    public ResponseEntity<DroolsFilesDTO> getDroolsFiles(@PathVariable Long id) {
        log.debug("REST request to get DroolsFiles : {}", id);
        Optional<DroolsFilesDTO> droolsFilesDTO = droolsFilesService.findOne(id);
        return ResponseUtil.wrapOrNotFound(droolsFilesDTO);
    }

    /**
     * {@code DELETE  /drools-files/:id} : delete the "id" droolsFiles.
     *
     * @param id the id of the droolsFilesDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/drools-files/{id}")
    public ResponseEntity<Void> deleteDroolsFiles(@PathVariable Long id) {
        log.debug("REST request to delete DroolsFiles : {}", id);
        droolsFilesService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/drools-files?query=:query} : search for the droolsFiles corresponding
     * to the query.
     *
     * @param query the query of the droolsFiles search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/drools-files")
    public ResponseEntity<List<DroolsFilesDTO>> searchDroolsFiles(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of DroolsFiles for query {}", query);
        Page<DroolsFilesDTO> page = droolsFilesService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
        }
}
