package com.bear.hospital.service;

import com.bear.hospital.pojo.EmrTemplate;
import java.util.List;

public interface EmrTemplateService {
    List<EmrTemplate> findAll(String dept);
    Boolean addEmrTemplate(EmrTemplate template);
    Boolean modifyEmrTemplate(EmrTemplate template);
    Boolean deleteEmrTemplate(Integer etId);
}
