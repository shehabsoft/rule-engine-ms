package com.emu.rule_engine_ms.web.rest;

import com.emu.rule_engine_ms.LoggingUtilities;
import com.emu.rule_engine_ms.config.engine.Utility;
import com.emu.rule_engine_ms.domain.DroolsFiles;
import com.emu.rule_engine_ms.domain.FILE_STATUS;
import com.emu.rule_engine_ms.domain.StpMessage;
import com.emu.rule_engine_ms.domain.enumeration.FileValidationType;
import com.emu.rule_engine_ms.repository.search.StpMessageSearchRepository;
import com.emu.rule_engine_ms.service.DroolsFilesService;
import com.emu.rule_engine_ms.service.EngineService;
import com.emu.rule_engine_ms.service.OperationsLogsService;
import com.emu.rule_engine_ms.service.StpMessageService;
import com.emu.rule_engine_ms.service.dto.DroolsFilesDTO;
import com.emu.rule_engine_ms.service.dto.StpMessageDTO;
import com.emu.rule_engine_ms.service.utils.RuleUtils;
import com.emu.rule_engine_ms.singleton.DroolFilesMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class EngineResource {

    private final Logger log = LoggerFactory.getLogger(EngineResource.class);

    @Autowired
    private DroolsFilesService droolsFilesService;

    @Autowired
    private OperationsLogsService operationsLogsService;

    @Autowired
    private RuleUtils ruleUtils;

    @Autowired
    private StpMessageService stpMessageService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DroolFilesMap filesMap;

    @Autowired
    private EngineService engineService;

    @Autowired
    private StpMessageSearchRepository stpMessageSearchRepository;

    /**
     * Save the Drool File To The Database
     * Parameter is a MultipartFile passed to be saved
     */
    @ApiOperation(value = "Saving The Drool File To The Database ")
    /*Optional below comments*/
    @ApiResponses(
        value = {
            @ApiResponse(code = 200, message = "Request successful."),
            @ApiResponse(code = 400, message = "Returned if some of the query parameters are invalid."),
        }
    )
    @PostMapping(value = "/saveDroolFile", consumes = "multipart/form-data")
    DroolsFilesDTO droolsFilesDTO(
        @RequestParam("simpleClassName") String simpleClassName,
        @RequestParam("fileValidationType") FileValidationType fileValidationType,
        @RequestPart MultipartFile mFile
    ) throws Exception {
        log.info(String.format("Saving the Drool File To The Database"));
        String originalFilename = mFile.getOriginalFilename();
        //        DroolsFiles tempDroolsFiles = droolsFilesService.findByFileName(Utility.getFileName(originalFilename));
        //        if (tempDroolsFiles != null) {
        //            log.info(String.format("the file " + originalFilename + " with the same name already exists"));
        //            throw new Exception("the file with the same name (" + originalFilename + ") already exists");
        //        }
        List<DroolsFiles> oldDroolsFiles = droolsFilesService.findAllByFileName(Utility.getFileName(originalFilename));
        if (oldDroolsFiles.size() > 0) {
            log.info(String.format("the file " + originalFilename + " with the same name already exists"));
            DroolsFilesDTO addedDroolsFilesDTO;
            for (DroolsFiles temp : oldDroolsFiles) {
                addedDroolsFilesDTO = new DroolsFilesDTO();
                addedDroolsFilesDTO.setId(temp.getId());
                addedDroolsFilesDTO.setFileName(temp.getFileName());
                addedDroolsFilesDTO.setFileType(temp.getFileType());
                addedDroolsFilesDTO.setFileContent(temp.getFileContent());
                addedDroolsFilesDTO.setFileContentContentType(temp.getFileContentContentType());
                addedDroolsFilesDTO.setFileValidationType(temp.getFileValidationType());
                addedDroolsFilesDTO.setSimpleClassName(temp.getSimpleClassName());
                addedDroolsFilesDTO.setStatus(FILE_STATUS.INACTVIE);
                droolsFilesService.save(addedDroolsFilesDTO);
            }
        }
        // operationsLogsService.save(LoggingUtilities.createOperationsLogsDTO("@PostMapping(/saveDroolFile/)", "Save the Drool File To The Database"));
        DroolsFilesDTO tempDroolsFilesDTO = new DroolsFilesDTO();
        if (mFile.getBytes().length > 0) {
            tempDroolsFilesDTO.setStatus(FILE_STATUS.ACTVIE);
            tempDroolsFilesDTO.setFileName(Utility.getFileName(originalFilename));
            tempDroolsFilesDTO.setFileType(Utility.getFileExtension(originalFilename));
            tempDroolsFilesDTO.setFileContent(mFile.getBytes());
            tempDroolsFilesDTO.setFileContentContentType(mFile.getContentType());
            tempDroolsFilesDTO.setFileValidationType(fileValidationType);
            tempDroolsFilesDTO.setSimpleClassName(simpleClassName);
            tempDroolsFilesDTO = droolsFilesService.save(tempDroolsFilesDTO);
        }
        return tempDroolsFilesDTO;
    }

    @GetMapping("/printHello")
    String printHello() {
        log.debug("REST request to print Hello inside rul engine ms..");
        return "HELLOOO";
    }

    @PostMapping("/validate-dto")
    public List<StpMessageDTO> validateDto(
        @RequestBody Object request,
        @RequestParam("fileValidationType") FileValidationType fileValidationType,
        @RequestParam String simpleClassName
    ) throws Exception {
        DroolsFiles drl = engineService.getMatchedDrl(simpleClassName, fileValidationType);
        if (drl != null) {
            Object convertedObject = engineService.wrapToSpecificType(request, simpleClassName);

            KieSession kieSession = ruleUtils.fireAllRulesOn(drl, convertedObject);

            Collection<StpMessageDTO> results = (Collection<StpMessageDTO>) kieSession.getObjects(
                new ClassObjectFilter(StpMessageDTO.class)
            );
            Iterator<StpMessageDTO> iterator = results.iterator();
            List<StpMessageDTO> allValidationMessages = new ArrayList<>(results);

            return allValidationMessages;
        }
        return null;
    }

    @PostMapping("/database_index_sync")
    public String syncIndicesWithDB() throws Exception {
        engineService.syncIndicesWithDB();
        return "done";
    }

    @GetMapping("/search/{key}")
    public List<StpMessage> searchInStpMessages(@PathVariable String key) throws Exception {
        return stpMessageSearchRepository.findByDescArContaining(key);
    }
    //    @PostMapping("/database_index_sync")
    //    public String databaseAndIndexSync() throws Exception {
    //
    //        final ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false) {
    //            @Override
    //            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
    //                return beanDefinition.getMetadata().isInterface();
    //            }
    //        };
    //        provider.addIncludeFilter(new RegexPatternTypeFilter(Pattern.compile(".*")));
    //        final Set<BeanDefinition> jpaRepos = provider.findCandidateComponents("com.emu.rule_engine_ms.repository");
    //        final Set<BeanDefinition> searchRepos = provider.findCandidateComponents("com.emu.rule_engine_ms.repository.search");
    //        // by default jpa repos contains all interfaces under repository (jpa + search) so we need to filer it
    //        jpaRepos.removeAll(searchRepos);
    //
    //        // no paramater
    //        Class<?> noparams[] = {};
    //
    //        // String parameter
    //        Class[] paramString = new Class[1];
    //        paramString[0] = String.class;
    //
    //
    //        for (BeanDefinition bean : jpaRepos) {
    //            Class<?> clazz = Class.forName(bean.getBeanClassName());
    ////            Method findAllMethod = clazz.getDeclaredMethod("findAll", noparams);
    //
    //
    //            if (bean.getBeanClassName().equals("com.emu.rule_engine_ms.repository.StpMessageRepository")) {
    //                Method findByKeyMethod = clazz.getDeclaredMethod("findByKey", paramString);
    //                Object result = findByKeyMethod.invoke(clazz, new String("000"));
    //
    //            }
    //
    //
    //            System.out.println("");
    //        }
    //
    //
    //        return "Done";
    //    }

}
