package com.bear.hospital.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;

@TableName("prescription_master")
public class PrescriptionMaster {
    @TableId(value = "pm_id")
    @JsonProperty("pmId")
    private Integer pmId;
    @JsonProperty("oId")
    private Integer oId;
    @JsonProperty("dId")
    private String dId;
    @JsonProperty("pmDiagnosis")
    private String pmDiagnosis;
    @JsonProperty("pmType")
    private String pmType;
    @JsonProperty("pmStatus")
    private Integer pmStatus;
    @JsonProperty("pmCreateTime")
    private String pmCreateTime;

    public PrescriptionMaster() {}

    public Integer getPmId() { return pmId; }
    public void setPmId(Integer pmId) { this.pmId = pmId; }
    public Integer getOId() { return oId; }
    public void setOId(Integer oId) { this.oId = oId; }
    public String getDId() { return dId; }
    public void setDId(String dId) { this.dId = dId; }
    public String getPmDiagnosis() { return pmDiagnosis; }
    public void setPmDiagnosis(String pmDiagnosis) { this.pmDiagnosis = pmDiagnosis; }
    public String getPmType() { return pmType; }
    public void setPmType(String pmType) { this.pmType = pmType; }
    public Integer getPmStatus() { return pmStatus; }
    public void setPmStatus(Integer pmStatus) { this.pmStatus = pmStatus; }
    public String getPmCreateTime() { return pmCreateTime; }
    public void setPmCreateTime(String pmCreateTime) { this.pmCreateTime = pmCreateTime; }
}
