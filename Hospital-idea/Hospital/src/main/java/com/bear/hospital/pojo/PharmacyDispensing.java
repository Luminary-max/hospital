package com.bear.hospital.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;

@TableName("pharmacy_dispensing")
public class PharmacyDispensing {
    @TableId(value = "pd_id")
    @JsonProperty("pdId")
    private Integer pdId;
    @JsonProperty("oId")
    private Integer oId;
    @JsonProperty("drId")
    private String drId;
    @JsonProperty("pdQuantity")
    private Integer pdQuantity;
    @JsonProperty("pdStatus")
    private Integer pdStatus;
    @JsonProperty("pdCreateTime")
    private String pdCreateTime;
    @JsonProperty("pdDispenseTime")
    private String pdDispenseTime;
    @JsonProperty("pdDispenseBy")
    private String pdDispenseBy;
    @JsonProperty("pdNote")
    private String pdNote;

    public PharmacyDispensing() {}

    public Integer getPdId() { return pdId; }
    public void setPdId(Integer pdId) { this.pdId = pdId; }
    public Integer getOId() { return oId; }
    public void setOId(Integer oId) { this.oId = oId; }
    public String getDrId() { return drId; }
    public void setDrId(String drId) { this.drId = drId; }
    public Integer getPdQuantity() { return pdQuantity; }
    public void setPdQuantity(Integer pdQuantity) { this.pdQuantity = pdQuantity; }
    public Integer getPdStatus() { return pdStatus; }
    public void setPdStatus(Integer pdStatus) { this.pdStatus = pdStatus; }
    public String getPdCreateTime() { return pdCreateTime; }
    public void setPdCreateTime(String pdCreateTime) { this.pdCreateTime = pdCreateTime; }
    public String getPdDispenseTime() { return pdDispenseTime; }
    public void setPdDispenseTime(String pdDispenseTime) { this.pdDispenseTime = pdDispenseTime; }
    public String getPdDispenseBy() { return pdDispenseBy; }
    public void setPdDispenseBy(String pdDispenseBy) { this.pdDispenseBy = pdDispenseBy; }
    public String getPdNote() { return pdNote; }
    public void setPdNote(String pdNote) { this.pdNote = pdNote; }
}
