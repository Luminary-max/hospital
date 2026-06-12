package com.bear.hospital.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonProperty;

@TableName("outpatient_emr")
public class OutpatientEmr {
    @TableId(value = "emr_id")
    @JsonProperty("emrId")
    private int emrId;
    @JsonProperty("oId")
    private int oId;
    @JsonProperty("pId")
    private int pId;
    @JsonProperty("dId")
    private String dId;
    @JsonProperty("chiefComplaint")
    private String chiefComplaint;
    @JsonProperty("presentIllness")
    private String presentIllness;
    @JsonProperty("pastHistory")
    private String pastHistory;
    @JsonProperty("physicalExam")
    private String physicalExam;
    @JsonProperty("diagnosis")
    private String diagnosis;
    @JsonProperty("treatmentPlan")
    private String treatmentPlan;
    @JsonProperty("createTime")
    private String createTime;
    @JsonProperty("updateTime")
    private String updateTime;

    @TableField(exist = false)
    @JsonProperty("pName")
    private String pName;
    @TableField(exist = false)
    @JsonProperty("dName")
    private String dName;
    @TableField(exist = false)
    @JsonProperty("oStart")
    private String oStart;

    public OutpatientEmr() {}

    public int getEmrId() { return emrId; }
    public void setEmrId(int emrId) { this.emrId = emrId; }
    public int getOId() { return oId; }
    public void setOId(int oId) { this.oId = oId; }
    public int getPId() { return pId; }
    public void setPId(int pId) { this.pId = pId; }
    public String getDId() { return dId; }
    public void setDId(String dId) { this.dId = dId; }
    public String getChiefComplaint() { return chiefComplaint; }
    public void setChiefComplaint(String chiefComplaint) { this.chiefComplaint = chiefComplaint; }
    public String getPresentIllness() { return presentIllness; }
    public void setPresentIllness(String presentIllness) { this.presentIllness = presentIllness; }
    public String getPastHistory() { return pastHistory; }
    public void setPastHistory(String pastHistory) { this.pastHistory = pastHistory; }
    public String getPhysicalExam() { return physicalExam; }
    public void setPhysicalExam(String physicalExam) { this.physicalExam = physicalExam; }
    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
    public String getTreatmentPlan() { return treatmentPlan; }
    public void setTreatmentPlan(String treatmentPlan) { this.treatmentPlan = treatmentPlan; }
    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }
    public String getUpdateTime() { return updateTime; }
    public void setUpdateTime(String updateTime) { this.updateTime = updateTime; }
    public String getPName() { return pName; }
    public void setPName(String pName) { this.pName = pName; }
    public String getDName() { return dName; }
    public void setDName(String dName) { this.dName = dName; }
    public String getOStart() { return oStart; }
    public void setOStart(String oStart) { this.oStart = oStart; }
}
