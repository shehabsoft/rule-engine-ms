package com.emu.rule_engine_ms.repository;

import com.emu.rule_engine_ms.domain.DroolsFiles;
import com.emu.rule_engine_ms.domain.enumeration.FileValidationType;
import com.emu.rule_engine_ms.service.dto.DroolsFilesDTO;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data SQL repository for the DroolsFiles entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DroolsFilesRepository extends JpaRepository<DroolsFiles, Long> {

    DroolsFiles findByFileName(String fileName);

    DroolsFiles findBySimpleClassName(String simpleClassName);

    DroolsFiles findByFileNameAndFileValidationType(String fileName,String fileValidationType);

    List<DroolsFiles> findAllByFileName(String fileName);

    DroolsFiles findBySimpleClassNameAndFileValidationTypeAndStatus(String simpleClassName, FileValidationType fileValidationType,Integer status);
}
