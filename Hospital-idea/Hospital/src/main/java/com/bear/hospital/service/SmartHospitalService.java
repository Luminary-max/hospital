package com.bear.hospital.service;

import java.util.List;
import java.util.Map;

public interface SmartHospitalService {
    Map<String, Object> saveRolePermission(Map<String, Object> payload);
    List<Map<String, Object>> listRolePermissions();
    Map<String, Object> currentRolePermission(String roleCode);
    String permissionsForRole(String roleCode);
    Map<String, Object> buildHealthProfile(Integer pId);
    List<Map<String, Object>> listHealthProfiles(String query, Integer pId);
    Map<String, Object> saveAnnouncement(Map<String, Object> payload);
    List<Map<String, Object>> listAnnouncements(String role);
    Map<String, Object> aiDiagnosis(Map<String, Object> payload);
    Map<String, Object> queuePrediction(String dId, Integer pId);
    Map<String, Object> reportAnalysis(Map<String, Object> payload);
    Map<String, Object> prescriptionReview(Map<String, Object> payload);
    Map<String, Object> createReferral(Map<String, Object> payload);
    List<Map<String, Object>> listReferrals(Integer pId);
    Map<String, Object> insuranceEstimate(Map<String, Object> payload);
}
