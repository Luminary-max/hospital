package com.bear.hospital.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonProperty;

@TableName("prescription_detail")
public class PrescriptionDetail {
    @TableId(value = "pd_id")
    @JsonProperty("pdId")
    private int pdId;
    @JsonProperty("oId")
    private int oId;
    @JsonProperty("drId")
    private String drId;
    @JsonProperty("pdUsage")
    private String pdUsage;
    @JsonProperty("pdDosage")
    private String pdDosage;
    @JsonProperty("pdFrequency")
    private String pdFrequency;
    @JsonProperty("pdDays")
    private Integer pdDays;
    @JsonProperty("pdQuantity")
    private Integer pdQuantity;
    @JsonProperty("pdNote")
    private String pdNote;
    @JsonProperty("pdPrice")
    private Double pdPrice;

    @TableField(exist = false)
    @JsonProperty("drName")
    private String drName;

    public PrescriptionDetail() {}

    public int getPdId() { return pdId; }
    public void setPdId(int pdId) { this.pdId = pdId; }
    public int getOId() { return oId; }
    public void setOId(int oId) { this.oId = oId; }
    public String getDrId() { return drId; }
    public void setDrId(String drId) { this.drId = drId; }
    public String getPdUsage() { return pdUsage; }
    public void setPdUsage(String pdUsage) { this.pdUsage = pdUsage; }
    public String getPdDosage() { return pdDosage; }
    public void setPdDosage(String pdDosage) { this.pdDosage = pdDosage; }
    public String getPdFrequency() { return pdFrequency; }
    public void setPdFrequency(String pdFrequency) { this.pdFrequency = pdFrequency; }
    public Integer getPdDays() { return pdDays; }
    public void setPdDays(Integer pdDays) { this.pdDays = pdDays; }
    public Integer getPdQuantity() { return pdQuantity; }
    public void setPdQuantity(Integer pdQuantity) { this.pdQuantity = pdQuantity; }
    public String getPdNote() { return pdNote; }
    public void setPdNote(String pdNote) { this.pdNote = pdNote; }
    public Double getPdPrice() { return pdPrice; }
    public void setPdPrice(Double pdPrice) { this.pdPrice = pdPrice; }
    public String getDrName() { return drName; }
    public void setDrName(String drName) { this.drName = drName; }
}
