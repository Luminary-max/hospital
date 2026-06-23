package com.bear.hospital.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;

@TableName("prescription_template")
public class PrescriptionTemplate {
    @TableId(value = "pt_id")
    @JsonProperty("ptId")
    private Integer ptId;
    @JsonProperty("ptName")
    private String ptName;
    @JsonProperty("dId")
    private String dId;
    @JsonProperty("ptDiagnosis")
    private String ptDiagnosis;
    @JsonProperty("ptDept")
    private String ptDept;
    @JsonProperty("ptContent")
    private String ptContent;
    @JsonProperty("ptCreateTime")
    private String ptCreateTime;

    public PrescriptionTemplate() {}

    public Integer getPtId() { return ptId; }
    public void setPtId(Integer ptId) { this.ptId = ptId; }
    public String getPtName() { return ptName; }
    public void setPtName(String ptName) { this.ptName = ptName; }
    public String getDId() { return dId; }
    public void setDId(String dId) { this.dId = dId; }
    public String getPtDiagnosis() { return ptDiagnosis; }
    public void setPtDiagnosis(String ptDiagnosis) { this.ptDiagnosis = ptDiagnosis; }
    public String getPtDept() { return ptDept; }
    public void setPtDept(String ptDept) { this.ptDept = ptDept; }
    public String getPtContent() { return ptContent; }
    public void setPtContent(String ptContent) { this.ptContent = ptContent; }
    public String getPtCreateTime() { return ptCreateTime; }
    public void setPtCreateTime(String ptCreateTime) { this.ptCreateTime = ptCreateTime; }
}
