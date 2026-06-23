package com.bear.hospital.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;

@TableName("emr_template")
public class EmrTemplate {
    @TableId(value = "et_id")
    @JsonProperty("etId")
    private Integer etId;
    @JsonProperty("etName")
    private String etName;
    @JsonProperty("etChiefComplaint")
    private String etChiefComplaint;
    @JsonProperty("etPresentIllness")
    private String etPresentIllness;
    @JsonProperty("etPastHistory")
    private String etPastHistory;
    @JsonProperty("etPhysicalExam")
    private String etPhysicalExam;
    @JsonProperty("etDiagnosis")
    private String etDiagnosis;
    @JsonProperty("etTreatmentPlan")
    private String etTreatmentPlan;
    @JsonProperty("etDept")
    private String etDept;
    @JsonProperty("dId")
    private String dId;
    @JsonProperty("etCreateTime")
    private String etCreateTime;

    public EmrTemplate() {}

    public Integer getEtId() { return etId; }
    public void setEtId(Integer etId) { this.etId = etId; }
    public String getEtName() { return etName; }
    public void setEtName(String etName) { this.etName = etName; }
    public String getEtChiefComplaint() { return etChiefComplaint; }
    public void setEtChiefComplaint(String etChiefComplaint) { this.etChiefComplaint = etChiefComplaint; }
    public String getEtPresentIllness() { return etPresentIllness; }
    public void setEtPresentIllness(String etPresentIllness) { this.etPresentIllness = etPresentIllness; }
    public String getEtPastHistory() { return etPastHistory; }
    public void setEtPastHistory(String etPastHistory) { this.etPastHistory = etPastHistory; }
    public String getEtPhysicalExam() { return etPhysicalExam; }
    public void setEtPhysicalExam(String etPhysicalExam) { this.etPhysicalExam = etPhysicalExam; }
    public String getEtDiagnosis() { return etDiagnosis; }
    public void setEtDiagnosis(String etDiagnosis) { this.etDiagnosis = etDiagnosis; }
    public String getEtTreatmentPlan() { return etTreatmentPlan; }
    public void setEtTreatmentPlan(String etTreatmentPlan) { this.etTreatmentPlan = etTreatmentPlan; }
    public String getEtDept() { return etDept; }
    public void setEtDept(String etDept) { this.etDept = etDept; }
    public String getDId() { return dId; }
    public void setDId(String dId) { this.dId = dId; }
    public String getEtCreateTime() { return etCreateTime; }
    public void setEtCreateTime(String etCreateTime) { this.etCreateTime = etCreateTime; }
}
