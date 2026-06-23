package com.bear.hospital.service;

import com.bear.hospital.pojo.PrescriptionTemplate;
import java.util.List;

public interface PrescriptionTemplateService {
    List<PrescriptionTemplate> findAll(String dId);
    Boolean addPrescriptionTemplate(PrescriptionTemplate template);
    Boolean modifyPrescriptionTemplate(PrescriptionTemplate template);
    Boolean deletePrescriptionTemplate(Integer ptId);
}
