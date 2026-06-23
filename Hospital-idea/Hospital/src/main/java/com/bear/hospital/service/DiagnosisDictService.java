package com.bear.hospital.service;

import com.bear.hospital.pojo.DiagnosisDict;
import java.util.List;

public interface DiagnosisDictService {
    List<DiagnosisDict> findAll(String query);
    Boolean addDiagnosisDict(DiagnosisDict dict);
    Boolean modifyDiagnosisDict(DiagnosisDict dict);
    Boolean deleteDiagnosisDict(Integer ddId);
}
